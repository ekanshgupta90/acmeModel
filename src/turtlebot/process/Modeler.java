package turtlebot.process;

import org.acmestudio.acme.core.resource.IAcmeResource;
import org.acmestudio.acme.core.resource.ParsingFailureException;
import org.acmestudio.acme.element.IAcmeSystem;
import org.acmestudio.acme.model.IAcmeModel;
import org.acmestudio.standalone.resource.StandaloneResourceProvider;
import turtlebot.acmemodels.ActionConnector;
import turtlebot.acmemodels.Component;
import turtlebot.acmemodels.Group;
import turtlebot.acmemodels.TopicConnector;
import turtlebot.structures.Constants;
import turtlebot.structures.Nodes;
import turtlebot.structures.RosModel;
import turtlebot.structures.Topics;

import java.io.IOException;
import java.util.*;

/**
 * Created by ekansh_gupta on 11/20/16.
 */
public class Modeler {
    private IAcmeModel model;
    private IAcmeSystem system;
    private RosModel rosModel;

    Set<String> addedTopics = new HashSet<>();
    Set<String> addedActionTopics = new HashSet<>();
    Set<TopicConnector> topicConnectors = new HashSet<>();
    Set<ActionConnector> actionConnectors = new HashSet<>();

    Set<Component> components = new HashSet<>();

    Set<Group> groups = new HashSet<>();

    public Modeler(String familyPath, String fileName, String systemName) {
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

    private void createConnectors() {

        Nodes nodes = rosModel.getNodes();
        Topics topics = rosModel.getTopics();

        // Create topics, excluding action topics

        Iterator<String> nodesIterator = nodes.getNodes();
        addTopicConectors(nodesIterator, nodes, topics);

        // Create Action topics connectors

        nodesIterator = nodes.getNodes();
        addActionConnector(nodesIterator, nodes, topics);


    }

    private void addActionConnector(Iterator<String> nodesIterator, Nodes nodes, Topics topics) {
        while (nodesIterator.hasNext()) {
            String nodeName = nodesIterator.next();

            if (nodes.isActionServer(nodeName)) {
                ActionConnector connector = new ActionConnector(system, nodeName);
                actionConnectors.add(connector);
                addedActionTopics.add(nodeName);
            }

            if (nodes.isActionClient(nodeName)) {
                Set<String> actionServerNodeNames = nodes.getAllConnectingActionServers(nodeName);
                for (String serverName : actionServerNodeNames) {
                    ActionConnector connector = new ActionConnector(system, nodeName);
                    actionConnectors.add(connector);
                    addedActionTopics.add(serverName);
                }
            }
        }
    }

    private void addTopicConectors(Iterator<String> nodesIterator, Nodes nodes, Topics topics) {
        while (nodesIterator.hasNext()) {
            String nodeName = nodesIterator.next();
            Set<String> allTopics = nodes.getAllSubscriptionsWithoutActions(nodeName);
            allTopics.addAll(nodes.getAllPublicationsWithoutActions(nodeName));

            for (String topic : allTopics) {
                if (addedTopics.contains(topic)) {
                    continue;
                }
                TopicConnector connector = new TopicConnector(system, topic);
                connector.addStringTypeProperty("topic", topics.getTopicOriginalName(topic));
                connector.addStringTypeProperty("msg_type", topics.getTopicType(topic));
                addedTopics.add(topic);
                topicConnectors.add(connector);
            }
        }

    }

    private void createComponents() {

        Nodes nodes = rosModel.getNodes();
        Topics topics = rosModel.getTopics();
        Nodes nodelets = rosModel.getNodelets();

        Iterator<String> nodeNames = nodes.getNodes();

        while (nodeNames.hasNext()) {
            String nodeName = nodeNames.next();
            if (nodes.isNodeletManager(nodeName)) {
                continue;
            }

            Component component = new Component(system, nodeName);
            component.addStringTypeProperty("name", nodes.getOriginalName(nodeName));
            component.createSubscribers(system, topics, nodes.getAllSubscriptionsWithoutActions(nodeName), topicConnectors);
            component.createPublishers(system, topics, nodes.getAllPublicationsWithoutActions(nodeName), topicConnectors);
            component.createServiceResponderPorts(system, nodeName, nodes);

            if (nodes.isActionServer(nodeName)) {
                Set<String> set = new HashSet<>();
                set.add(nodeName);
                component.createActionServer(system, set, actionConnectors);
            }

            if (nodes.isActionClient(nodeName)) {
                Set<String> set = nodes.getAllConnectingActionServers(nodeName);
                component.createActionClient(system, set, actionConnectors);
            }

            components.add(component);
        }

        int groupId = 0;
        nodeNames = nodes.getNodes();

        while (nodeNames.hasNext()) {
            String nodeName = nodeNames.next();

            if (nodes.isNodeletManager(nodeName)) {
                groupId++;
                Group group = new Group(system, "group" + String.valueOf(groupId));
                group.addStringTypeProperty("name", nodes.getOriginalName(nodeName));
                List<String> nodeManager = new ArrayList<>();
                nodeManager.add("ROSNodeManagerCompT");
                Component component = new Component(system, nodeName, nodeManager);
                component.addStringTypeProperty("name", nodes.getOriginalName(nodeName));

                component.createSubscribers(system, topics, nodes.getAllSubscriptionsWithoutActions(nodeName), topicConnectors);
                component.createPublishers(system, topics, nodes.getAllPublicationsWithoutActions(nodeName), topicConnectors);
                component.createServiceResponderPorts(system, nodeName, nodes);

                if (nodes.isActionServer(nodeName)) {
                    Set<String> set = new HashSet<>();
                    set.add(nodeName);
                    component.createActionServer(system, set, actionConnectors);
                }

                if (nodes.isActionClient(nodeName)) {
                    Set<String> set = nodes.getAllConnectingActionServers(nodeName);
                    component.createActionClient(system, set, actionConnectors);
                }

                components.add(component);

                group.addMember(system, component.getComponent());

                Set<String> nodeletNames = nodes.getAllNodelets(nodeName);

                for (String node : nodeletNames) {
                    List<String> nodeletType = new ArrayList<>();
                    nodeletType.add("ROSNodeletCompT");
                    Component nodelet = new Component(system, node, nodeletType);
                    nodelet.addStringTypeProperty("name", nodelets.getOriginalName(node));
                    nodelet.createSubscribers(system, topics, nodelets.getAllSubscriptionsWithoutActions(node), topicConnectors);
                    nodelet.createPublishers(system, topics, nodelets.getAllPublicationsWithoutActions(node), topicConnectors);
                    nodelet.createServiceResponderPorts(system, node, nodelets);
                    components.add(nodelet);
                    group.addMember(system, nodelet.getComponent());
                }
                groups.add(group);
            }
        }


    }

    private void createServiceCalls() {

    }

    public IAcmeModel create(RosModel rosModel) {
        this.rosModel = rosModel;

        System.out.println("Started");
        try {
            createConnectors();
            createComponents();
            createServiceCalls();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Finished");
        return this.model;
    }
}
