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

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Model {

    private IAcmeModel model;
    private IAcmeSystem system;
    private ArrayList<Node> nodeArrayList = new ArrayList<>();
    private ArrayList<Topic> topicArrayList = new ArrayList<>();
    private ArrayList<Component> componentArrayList = new ArrayList<>();
    private ArrayList<Connector> connectorArrayList = new ArrayList<>();

    public Model(String familyPath, String fileName, String systemName) {
        System.setProperty(StandaloneResourceProvider.FAMILY_SEARCH_PATH, familyPath);
        IAcmeResource resource = null;
        try {
            resource = StandaloneResourceProvider.instance().acmeResourceForString("src/turtle.acme");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParsingFailureException e) {
            e.printStackTrace();
        }
        model = resource.getModel();
        system = model.getSystem(systemName);
    }

    public void createComponents() {
        for (Node node : nodeArrayList) {
            Component component = new Component(system, node.getName());
            component.addStringTypeProperty("name",node.getOriginalName());
            component.createSubscribers(system,node.getSubscribed(),connectorArrayList);
            component.createPublishers(system,node.getPublished(),connectorArrayList);
            componentArrayList.add(component);
        }
    }

    public void createConnectors() {
        for (Topic topic : topicArrayList) {
            Connector connector = new Connector(system, topic.getName());
            connector.addStringTypeProperty("name", topic.getOriginalName());
            connector.addStringTypeProperty("msg_type", topic.getMsg_Type());
            connectorArrayList.add(connector);
        }
    }


    public void createModel() {
        System.out.println("Started");
        createConnectors();
        createComponents();
        System.out.println("Finished");
    }


    public void writeToFile() {

        OutputStream s = null;
        try {
            s = new FileOutputStream("src/new.acme");
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


    public void createTopicList(String t) {

        JSONObject jObject = new JSONObject(t);
        Iterator<?> keys = jObject.keys();

        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = (String) jObject.get(key);
            Topic topic = new Topic(key, value);
            topicArrayList.add(topic);
        }
    }

    public void createNodeList(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            Node node = new Node(array.getString(i));
            nodeArrayList.add(node);
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

    public void initialize( JSONObject jObject) {
        createTopicList(jObject.get("topics").toString());
        createNodeList(jObject.getJSONArray("nodes"));
        addPublish(jObject.get("pub").toString());
        addSubscribe(jObject.get("sub").toString());
    }


    public void flush() {
        model.dispose();
    }

}


