import com.oracle.javafx.jmx.json.JSONException;
import org.acmestudio.acme.core.exception.AcmeException;
import org.acmestudio.acme.core.exception.AcmeVisitorException;
import org.acmestudio.acme.core.resource.IAcmeResource;
import org.acmestudio.acme.core.resource.ParsingFailureException;
import org.acmestudio.acme.element.IAcmePort;
import org.acmestudio.acme.element.IAcmeRole;
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
import java.util.List;

public class Model {

    private IAcmeModel model;
    private IAcmeSystem system;
    private ArrayList<Node> nodeArrayList = new ArrayList<>();
    private ArrayList<Topic> topicArrayList = new ArrayList<>();
    private ArrayList<Connector> connectorArrayList = new ArrayList<>();
    private ArrayList<Component> componentArrayList = new ArrayList<>();

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
        //System.out.println(model.toString());

        system = model.getSystem(systemName);
        //System.out.println(system.toString());
    }


    public void createModel() {
        System.out.println("Started");

        for (Topic topic : topicArrayList) {
            Connector connector = new Connector(system, topic.getName());
            //connector.addAdvertiserRole("ROSTopicAdvertiserRoleT0");
            //connector.addSubscriberRole("ROSTopicSubscriberRoleT0");
            //connector.addStringTypeProperty("msg_type", "msgs/odometry.msg");
//            System.out.println(connector.getName());
            connectorArrayList.add(connector);
        }


        for (Node node : nodeArrayList) {

            Component component = new Component(system, node.getName());
            List<Topic> subscribedTopics = node.getSubscribed();
            List<Topic> publishedTopics = node.getPublished();
            int portNumber = 0;
            int roleNumber = 0;

            for (Topic topic : subscribedTopics) {
                component.addSubscriberPort("sport" + portNumber);
                IAcmePort port = component.getPort("sport" + portNumber);
                for (Connector connector : connectorArrayList) {
                    if (connector.getName() == topic.getName()) {

                        while (connector.getRole("ROSTopicSubscriberRoleT" + roleNumber) != null)
                            roleNumber++;

                        connector.addSubscriberRole("ROSTopicSubscriberRoleT" + roleNumber);

                        IAcmeRole role = connector.getRole("ROSTopicSubscriberRoleT" + roleNumber);
                        try {
                            system.getCommandFactory().attachmentCreateCommand(port, role).execute();
                        } catch (AcmeException e) {
                            e.printStackTrace();
                        }
                    }
                }
                //component.addStringTypePropertytoPort("sport" + portNumber, "msg_type", "msg/odometety.msg");
                portNumber++;
            }

            portNumber = 0;
            roleNumber = 0;
            for (Topic topic : publishedTopics) {
                component.addPublisherPort("pport" + portNumber);
                IAcmePort port = component.getPort("pport" + portNumber);
                for (Connector connector : connectorArrayList) {
                    if (connector.getName() == topic.getName()) {
                        while (connector.getRole("ROSTopicAdvertiserRoleT" + roleNumber) != null)
                            roleNumber++;

                        connector.addAdvertiserRole("ROSTopicAdvertiserRoleT" + roleNumber);

                        IAcmeRole role = connector.getRole("ROSTopicAdvertiserRoleT" + roleNumber);
                        try {
                            system.getCommandFactory().attachmentCreateCommand(port, role).execute();
                        } catch (AcmeException e) {
                            e.printStackTrace();
                        }
                    }
                }

                //component.addStringTypePropertytoPort("pport" + portNumber, "msg_type", "msg/odometety.msg");
                portNumber++;
            }

//            System.out.println(component.getName());
            //component.printPortDetails();
            componentArrayList.add(component);
        }
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
            s.write(model.toString().getBytes(Charset.forName("UTF-8")));
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
    }


    public void createTopicList(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            Topic topic = new Topic(array.getString(i));
            topicArrayList.add(topic);
        }
    }


    public void createNodeList(JSONArray array) {

        for (int i = 0; i < array.length(); i++) {
            Node node = new Node(array.getString(i));
            nodeArrayList.add(node);
        }
    }


    public void addPublish(String t) throws JSONException {

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

    public void flush() {
        model.dispose();
    }

}


