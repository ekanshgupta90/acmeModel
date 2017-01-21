package turtlebot.process;

import org.json.JSONArray;
import org.json.JSONObject;

import turtlebot.structures.Constants;
import turtlebot.structures.Nodes;
import turtlebot.structures.RosModel;
import turtlebot.structures.Topics;

import java.util.*;

/**
 * Created by ekansh_gupta on 11/20/16.
 */
public class Initializer {
    private RosModel model;

    public RosModel getRosModel() {
        return this.model;
    }

    private void addToNodes(JSONObject object) {
        Nodes nodes = model.getNodes();
        Nodes nodelets = model.getNodelets();

        Iterator<?> iterator = object.keySet().iterator();

        while (iterator.hasNext()) {
            String nodeName = String.valueOf(iterator.next());

            if (Constants.NODES.toString().equalsIgnoreCase(nodeName)) {
                JSONArray list =  object.getJSONArray(nodeName);
                for (int i = 0; i < list.length(); nodes.addNode(list.getString(i++)));
            } else {
                nodes.addNode(nodeName);
                JSONArray list =  object.getJSONArray(nodeName);
                for (int i = 0; i < list.length(); i++) {
                    nodes.addNodelets(nodeName, list.getString(i));
                    nodelets.addNode(list.getString(i));
                }
            }
        }

        model.setNodes(nodes);
        model.setNodelets(nodelets);

    }

    private void addSubcriptions(JSONObject object) {
        Nodes nodes = model.getNodes();
        Nodes nodelets = model.getNodelets();

        Iterator<?> iterator = object.keySet().iterator();
        while (iterator.hasNext()) {
            String nodeName = String.valueOf(iterator.next());

            JSONArray list = object.getJSONArray(nodeName);
            for (int i = 0; i < list.length(); i++) {
                if (nodes.hasNode(nodeName)) {
                    nodes.addSubscription(nodeName, list.getString(i));
                }

                if (nodelets.hasNode(nodeName)) {
                    nodelets.addSubscription(nodeName, list.getString(i));
                }
            }
        }

        model.setNodes(nodes);
        model.setNodelets(nodelets);
    }

    private void addPublications(JSONObject object) {
        Nodes nodes = model.getNodes();
        Nodes nodelets = model.getNodelets();

        Iterator<?> iterator = object.keySet().iterator();
        while (iterator.hasNext()) {
            String nodeName = String.valueOf(iterator.next());

            JSONArray list = object.getJSONArray(nodeName);
            for (int i = 0; i < list.length(); i++) {
                if (nodes.hasNode(nodeName)) {
                    nodes.addPublication(nodeName, list.getString(i));
                }

                if (nodelets.hasNode(nodeName)) {
                    nodelets.addPublication(nodeName, list.getString(i));
                }
            }
        }

        model.setNodes(nodes);
        model.setNodelets(nodelets);
    }

    private void addToTopics(JSONObject object) {
        Topics topics = model.getTopics();

        Iterator<?> iterator = object.keySet().iterator();
        while (iterator.hasNext()) {
            String topicName = String.valueOf(iterator.next());
            String topicType = String.valueOf(object.get(topicName));
            topics.addTopic(topicName, topicType);
        }

        model.setTopics(topics);
    }

    private void addToServices(JSONObject object) {
        Nodes nodes = model.getNodes();
        Nodes nodelets = model.getNodelets();

        Iterator<?> iterator = object.keySet().iterator();
        while (iterator.hasNext()) {
            String serviceName = String.valueOf(iterator.next());
            String tempName = "__" + serviceName.split("/")[1];

            // Index at 0 is type and index at 1 are args space separated
            JSONArray list = object.getJSONArray(serviceName);
            String[] args = list.getString(1).split(" ");
            Set<String> argsSet = new HashSet<>();
            for (int i = 0; i < args.length; argsSet.add(args[i++]));

            if (nodes.hasNode(tempName)) {
                nodes.addService(tempName, serviceName, list.getString(0), argsSet);
            }

            if (nodelets.hasNode(tempName)) {
                nodelets.addService(tempName, serviceName, list.getString(0), argsSet);
            }
        }

        model.setNodes(nodes);
        model.setNodelets(nodelets);
    }

    private void addCalls(JSONArray array) {
        HashMap<String, String> calls = model.getCalls();

        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);

            Iterator<String> iterator = object.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = object.getString(key);

                calls.put(key, value);
            }
        }

        model.setCalls(calls);
    }

    public RosModel initialize(JSONObject jObject) {
        model = new RosModel();

        addToNodes(jObject.getJSONObject(Constants.NODES.toString()));
        addSubcriptions(jObject.getJSONObject(Constants.SUBCRIBE.toString()));
        addPublications(jObject.getJSONObject(Constants.PUBLISH.toString()));
        addToTopics(jObject.getJSONObject(Constants.TOPICS.toString()));
        addToServices(jObject.getJSONObject(Constants.SERVICES.toString()));
        addCalls(jObject.getJSONArray(Constants.CALLS.toString()));

        return this.model;
    }
}
