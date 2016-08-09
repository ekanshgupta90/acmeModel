import com.oracle.javafx.jmx.json.JSONException;
import org.acmestudio.acme.core.exception.AcmeVisitorException;
import org.acmestudio.acme.core.resource.IAcmeResource;
import org.acmestudio.acme.core.resource.ParsingFailureException;
import org.acmestudio.acme.element.IAcmeSystem;
import org.acmestudio.acme.model.IAcmeModel;
import org.acmestudio.armani.ArmaniExportVisitor;
import org.acmestudio.standalone.resource.StandaloneResourceProvider;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.nio.charset.Charset;
import java.util.*;

public class Model {

    private IAcmeModel model;
    private IAcmeSystem system;
    private ArrayList<Node> nodeArrayList = new ArrayList<>();
    private ArrayList<Topic> topicArrayList = new ArrayList<>();
    private ArrayList<Component> componentArrayList = new ArrayList<>();
    private ArrayList<Connector> connectorArrayList = new ArrayList<>();
    private HashMap<Node, ArrayList<Node>> nodeletList = new HashMap<>();
    private ArrayList<Group> groupArrayList = new ArrayList<>();
    private ArrayList<Service> serviceArrayList = new ArrayList<>();
    private HashMap<String, ServiceConnector> serviceConnectors = new HashMap<>();
    private HashMap<String, String> callsMap = new HashMap<>();

    public Model(String familyPath, String fileName, String systemName) {
        System.setProperty(StandaloneResourceProvider.FAMILY_SEARCH_PATH, familyPath);
        IAcmeResource resource = null;
        try {
            resource = StandaloneResourceProvider.instance().acmeResourceForString(fileName);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParsingFailureException e) {
            e.printStackTrace();
        }
        model = resource.getModel();
        system = model.getSystem(systemName);
    }


    public void createServiceCalls() throws Exception {

        Component client = null;
        Component server = null;
        ServiceConnector serviceConnector = null;

        if (! callsMap.isEmpty()) {
            System.out.print(callsMap);
            for (Component component : componentArrayList) {
                if (("/" + component.getName().split("__")[1]).equals(callsMap.get("client") + "node")) {
                    client = component;
                }

                if (("/" + component.getName().split("__")[1]).equals(callsMap.get("server") + "node")) {
                    server = component;
                }
            }

            if (serviceConnectors.get(callsMap.get("service_name")) == null) {
                serviceConnector = new ServiceConnector(system, callsMap.get("service_name"));
                serviceConnectors.put(callsMap.get("service_name"), serviceConnector);
            } else {
                serviceConnector = serviceConnectors.get(callsMap.get("service_name"));
            }

            server.connectToServiceConnector(system, callsMap.get("service_name"), serviceConnector);
            client.createServiceCallerPort(system, callsMap.get("service_name"), serviceConnector);
        }
    }


    public void createComponents() throws Exception{

        for (Node node : nodeArrayList) {
            Component component = new Component(system, node.getName());
            component.addStringTypeProperty("name",node.getOriginalName());
            component.createSubscribers(system,node.getSubscribed(),connectorArrayList);
            component.createPublishers(system,node.getPublished(),connectorArrayList);
            component.createServiceResponderPorts(system, node.getServices());
            componentArrayList.add(component);
        }
        int groupId = 0;

        for(Node key: nodeletList.keySet()) {
            groupId++;
            Group group = new Group(system, "group" + String.valueOf(groupId));
            group.addStringTypeProperty("name", key.getOriginalName());
            List<String> nodeManager = new ArrayList<>();
            nodeManager.add("ROSNodeManagerCompT");
            Component component = new Component(system, key.getName(), nodeManager);
            component.addStringTypeProperty("name", key.getOriginalName());
            component.createSubscribers(system, key.getSubscribed(), connectorArrayList);
            component.createPublishers(system, key.getPublished(), connectorArrayList);
            component.createServiceResponderPorts(system, key.getServices());
            componentArrayList.add(component);
            group.addMember(system, component.getComponent());

            for (Node node : nodeletList.get(key)) {
                List<String> nodeletType = new ArrayList<>();
                nodeletType.add("ROSNodeletCompT");
                Component nodelet = new Component(system, node.getName(), nodeletType);
                nodelet.addStringTypeProperty("name", node.getOriginalName());
                nodelet.createSubscribers(system, node.getSubscribed(), connectorArrayList);
                nodelet.createPublishers(system, node.getPublished(), connectorArrayList);
                nodelet.createServiceResponderPorts(system, node.getServices());
                componentArrayList.add(nodelet);
                group.addMember(system, nodelet.getComponent());
            }
            groupArrayList.add(group);
        }

    }



    public void createConnectors() throws  Exception{
        for (Topic topic : topicArrayList) {
            Connector connector = new Connector(system, topic.getName());
            connector.addStringTypeProperty("topic", topic.getOriginalName());
            connector.addStringTypeProperty("msg_type", topic.getMsg_Type());
            connectorArrayList.add(connector);
        }
    }


    public void createModel() {
        System.out.println("Started");
        try {
            createConnectors();
            createComponents();
            createServiceCalls();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Finished");
    }


    public void writeToFile() {

        OutputStream s = null;
        try {
            s = new FileOutputStream("turtle.acme");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            s.write(model.toString().getBytes(Charset.forName("UTF-8")),10,0);
            s.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArmaniExportVisitor visitor = new ArmaniExportVisitor(s);
        try {
            model.visit(visitor, null);
        } catch (AcmeVisitorException e) {
            e.printStackTrace();
        }

        try {
            s.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    public void createTopicList(String t) throws Exception {
        topicArrayList = new ArrayList<>();
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = (String) jObject.get(key);
            Topic topic = new Topic(key, value);
            topicArrayList.add(topic);
        }
    }

    public void createNodeList(String t) throws JSONException {
        nodeletList = new HashMap<>();
        nodeArrayList = new ArrayList<>();
        HashMap<String, ArrayList<String>> map = createMap(t);
        ArrayList<String> array = map.get("nodes");
        for (int i = 0; i < array.size(); i++) {
            Node node = new Node(array.get(i));
            nodeArrayList.add(node);
        }


        for(String name: map.keySet()) {
            if(!name.equals("nodes")) {
                Node node = new Node(name);
                ArrayList<String> nodeletArray = map.get(name);
                ArrayList<Node> nodlets = new ArrayList<>();
                for (int i = 0; i < nodeletArray.size(); i++) {
                    Node node1 = new Node(nodeletArray.get(i));
                    nodlets.add(node1);
                }
                nodeletList.put(node,nodlets);
            }
        }
    }


    public HashMap<String, ArrayList<String>> createMap(String t) {
        HashMap<String, ArrayList<String>> map = new HashMap<>();
        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            JSONArray array = jObject.getJSONArray(key);
            ArrayList<String> list = new ArrayList();
            for (int i = 0; i < array.length(); list.add(array.getString(i++))) ;
            map.put(key, list);
        }
        return map;
    }


    public void addPublish(String t) throws JSONException {

        HashMap<String, ArrayList<String>> map = createMap(t);
        for (String nodeName : map.keySet()) {
            for (Node node : nodeArrayList) {
                if (node.getName().equals(nodeName + "node")) {
                    for (String topicName : map.get(nodeName)) {
                        for (Topic topic : topicArrayList) {
                            if (topic.getName().equals(topicName + "topic"))
                                node.addSubscriber(topic);
                        }
                    }
                }
            }
        }
    }


    public void addSubscribe(String t) throws JSONException {

        HashMap<String, ArrayList<String>> map = createMap(t);
        for (String nodeName : map.keySet()) {
            for (Node node : nodeArrayList) {
                if (node.getName().equals(nodeName + "node")) {
                    for (String topicName : map.get(nodeName)) {
                        for (Topic topic : topicArrayList) {
                            if (topic.getName().equals(topicName + "topic"))
                                node.addPublisher(topic);
                        }
                    }
                }
            }
        }

    }


    public void addServices(String t) throws JSONException {
        try {
            HashMap<String, ArrayList<String>> map = createMap(t);
            for (Node node : nodeArrayList) {
                for (String serviceName : map.keySet()) {
                    if (node.getOriginalName().equals( "/" + (serviceName.split("/"))[1])) {
                        ArrayList<String> data = map.get(serviceName);
                        ArrayList<String> args = new ArrayList<>(Arrays.asList(data.get(1).split(" ")));
                        Service service = new Service(serviceName, data.get(0), args);
                        node.addService(service);
                        serviceArrayList.add(service);
                    }
                }
            }

            for(Node key: nodeletList.keySet()) {
                for (String serviceName : map.keySet()) {
                    if (key.getOriginalName().equals("/" + (serviceName.split("/"))[1])) {
                        ArrayList<String> data = map.get(serviceName);
                        ArrayList<String> args = new ArrayList<>(Arrays.asList(data.get(1).split(" ")));
                        Service service = new Service(serviceName, data.get(0), args);
                        key.addService(service);
                        serviceArrayList.add(service);
                    }
                }

                for (Node node : nodeletList.get(key)) {
                    for (String serviceName : map.keySet()) {
                        if (node.getOriginalName().equals("/" + (serviceName.split("/"))[1])) {
                            ArrayList<String> data = map.get(serviceName);
                            ArrayList<String> args = new ArrayList<>(Arrays.asList(data.get(1).split(" ")));
                            Service service = new Service(serviceName, data.get(0), args);
                            node.addService(service);
                            serviceArrayList.add(service);
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void addCalls(String t) {
        JSONArray jsonarray = new JSONArray(t);
        callsMap = new HashMap<>();
        for(Object jsonObject : jsonarray) {
            JSONObject jObject = new JSONObject((String) jsonObject);
            Iterator<?> keys = jObject.keys();

            while (keys.hasNext()) {
                String key = (String) keys.next();
                String value = jObject.getString(key);
                callsMap.put(key, value);
            }
        }
    }


    public void initialize( JSONObject jObject) {

        nodeArrayList = new ArrayList<>();
        topicArrayList = new ArrayList<>();
        nodeletList = new HashMap<>();
        groupArrayList = new ArrayList<>();
        serviceArrayList = new ArrayList<>();
        callsMap = new HashMap<>();

        try {
            createTopicList(jObject.get("topics").toString());
            createNodeList(jObject.get("nodes").toString());
            addPublish(jObject.get("pub").toString());
            addSubscribe(jObject.get("sub").toString());
            addServices(jObject.get("service").toString());
            addCalls(jObject.get("calls").toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void flush() {
        model.dispose();
    }

}


