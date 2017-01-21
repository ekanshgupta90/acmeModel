package turtlebot.structures;

import turtlebot.acmemodels.TopicConnector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by ekansh_gupta on 11/19/16.
 */

/**
 * This a ROS Node data structure.
 * All public methods in the data structure return empties, but not null.
 */
public class Nodes implements Comparable<Nodes> {

    private HashMap<String, Node> nodes;

    /**
     * Public constructor, used to initialize the hashmap.
     */
    public Nodes() {
        nodes = new HashMap<>();
    }

    /**
     * Get all the node names.
     * @return nodeName Iterator<String>
     */
    public Iterator<String> getNodes() {
        return nodes.keySet().iterator();
    }

    /**
     * Add a new node. Ignores if the node already exists.
     * @param nodeName
     */
    public void addNode(String nodeName) {
        if (!hasNode(nodeName)) {
            nodes.put(nodeName, new Node(nodeName));
        }
    }

    /**
     * Add a subscription topic to a node.
     * Creates a new node if the node doesn't exist.
     * @param nodeName
     * @param topicName
     */
    public void addSubscription(String nodeName, String topicName) {
        Node node = null;
        if (hasNode(nodeName)) {
            node = nodes.get(nodeName);
        } else {
            node = new Node(nodeName);
        }
        node.addSubscriber(topicName);
        nodes.put(nodeName, node);
    }

    /**
     * Add a publication topic to a node.
     * Creates a new node if the node doesn't exist.
     * @param nodeName
     * @param topicName
     */
    public void addPublication(String nodeName, String topicName) {
        Node node = null;
        if (hasNode(nodeName)) {
            node = nodes.get(nodeName);
        } else {
            node = new Node(nodeName);
        }
        node.addPublisher(topicName);
        nodes.put(nodeName, node);
    }

    /**
     * Add a nodelet to a nodelet manager.
     * All nodes are considered equal by default. If a node contains a
     * nodelet, it is marked as a nodelet manager. Although, there is
     * no way to know, if the node is just a nodelet manager or a node too.
     * @param nodeName
     * @param nodeletName
     */
    public void addNodelets(String nodeName, String nodeletName) {
        Node node = null;
        if (hasNode(nodeName)) {
            node = nodes.get(nodeName);
        } else {
            node = new Node(nodeName);
        }
        node.addNodelet(nodeletName);
        nodes.put(nodeName, node);
    }

    /**
     * Add a service to a node with type and args as set
     * @param nodeName
     * @param serviceName
     * @param serviceType
     * @param serviceArgs
     */
    public void addService(String nodeName, String serviceName, String serviceType, Set<String> serviceArgs) {
        Node node = null;
        if (hasNode(nodeName)) {
            node = nodes.get(nodeName);
        } else {
            node = new Node(nodeName);
        }
        node.addService(serviceName, serviceType, serviceArgs);
        nodes.put(nodeName, node);
    }

    /**
     * Checks if the node name exists.
     * @param nodeName
     * @return boolean
     */
    public boolean hasNode(String nodeName) {
        return nodes.containsKey(nodeName);
    }

    /**
     * Does node has services
     * @param nodeName
     * @return boolean
     */
    public boolean hasServices(String nodeName) {
        if (hasNode(nodeName)) {
            return !nodes.get(nodeName).getServiceNames().isEmpty();
        }
        return false;
    }

    /**
     * Does a node has a specific service
     * @param nodeName
     * @param serviceName
     * @return boolean
     */
    public boolean hasSpecificServices(String nodeName, String serviceName) {
        if (hasNode(nodeName)) {
            return nodes.get(nodeName).hasService(serviceName);
        }
        return false;
    }

    /**
     * Checks if the node is an action server.
     * @param nodeName
     * @return boolean
     */
    public boolean isActionServer(String nodeName) {
        if (hasNode(nodeName)) {
            return nodes.get(nodeName).isActionServer();
        }
        return false;
    }

    /**
     * Checks if the node is an action client
     * @param nodeName
     * @return boolean
     */
    public boolean isActionClient(String nodeName) {
        if (hasNode(nodeName)) {
            return nodes.get(nodeName).isActionClient();
        }
        return false;
    }

    /**
     * Checks if the node is a nodelet manager
     * @param nodeName
     * @return boolean
     */
    public boolean isNodeletManager(String nodeName) {
        if (hasNode(nodeName)) {
            return nodes.get(nodeName).isNodeletManager();
        }
        return false;
    }

    /**
     * Returns all the action server node names, the input node is client too.
     * @param nodeName
     * @return Set of String - node names.
     */
    public Set<String> getAllConnectingActionServers(String nodeName) {
        if (isActionClient(nodeName)) {
            return nodes.get(nodeName).getAllActionServerNodeNames();
        }
        return new HashSet<String>();
    }

    /**
     * Returns all the subscription topics of the node
     * @param nodeName
     * @return Set of String - topic names.
     */
    public Set<String> getAllSubscriptions(String nodeName) {
        if (hasNode(nodeName)) {
            return nodes.get(nodeName).getSubscriptions();
        }
        return new HashSet<>();
    }

    /**
     * Returns all the subscription topics of the node
     * @param nodeName
     * @return Set of String - topic names.
     */
    public Set<String> getAllSubscriptionsWithoutActions(String nodeName) {
        if (hasNode(nodeName)) {
            Set<String> allTopics = nodes.get(nodeName).getSubscriptions();
            return removeActionTopics(allTopics);
        }
        return new HashSet<>();
    }


    /**
     * Returns all the publishing topics of the node
     * @param nodeName
     * @return Set of String - topic names.
     */
    public Set<String> getAllPublications(String nodeName) {
        if (hasNode(nodeName)) {
            return nodes.get(nodeName).getPublications();
        }
        return new HashSet<>();
    }

    /**
     * Returns all the subscription topics of the node
     * @param nodeName
     * @return Set of String - topic names.
     */
    public Set<String> getAllPublicationsWithoutActions(String nodeName) {
        if (hasNode(nodeName)) {
            Set<String> allTopics = nodes.get(nodeName).getPublications();
            return removeActionTopics(allTopics);
        }
        return new HashSet<>();
    }

    /**
     * Get all action topics
     * @param nodeName
     * @return
     */
    public Set<String> getAllActionTopics(String nodeName) {
        if (hasNode(nodeName)) {
            Set<String> allTopics = nodes.get(nodeName).getPublications();
            Set<String> outputTopics = new HashSet<>();
            for (String topic : allTopics) {
                if (topic.endsWith(Constants.GOAL.toString()) ||
                        topic.endsWith(Constants.CANCEL.toString()) ||
                        topic.endsWith(Constants.FEEDBACK.toString()) ||
                        topic.endsWith(Constants.RESULT.toString()) ||
                        topic.endsWith(Constants.STATUS.toString())) {
                    outputTopics.add(topic);
                }
            }
            return outputTopics;
        }
        return new HashSet<>();
    }

    private Set<String> removeActionTopics(Set<String> allTopics) {
        Set<String> outputTopics = new HashSet<>();
        for (String topic : allTopics) {
            if (topic.endsWith(Constants.GOAL.toString()) ||
                    topic.endsWith(Constants.CANCEL.toString()) ||
                    topic.endsWith(Constants.FEEDBACK.toString()) ||
                    topic.endsWith(Constants.RESULT.toString()) ||
                    topic.endsWith(Constants.STATUS.toString())) {
                continue;
            }
            outputTopics.add(topic);
        }
        return outputTopics;
    }

    /**
     * Returns all the nodelets for the node
     * @param nodeName
     * @return
     */
    public Set<String> getAllNodelets(String nodeName) {
        if (hasNode(nodeName)) {
            return nodes.get(nodeName).getNodelets();
        }
        return new HashSet<>();
    }

    /**
     * Returns all the services for a node
     * @param nodeName
     * @return
     */
    public Set<String> getAllServiceNames(String nodeName) {
        if (hasNode(nodeName)) {
            return nodes.get(nodeName).getServiceNames();
        }
        return new HashSet<>();
    }

    public String getServiceType(String nodeName, String serviceName) {
        if (hasNode(nodeName)) {
            if (nodes.get(nodeName).hasService(serviceName)) {
                return nodes.get(nodeName).getServiceType(serviceName);
            }
        }
        return "";
    }

    public Set<String> getServiceArgs(String nodeName, String serviceName) {
        if (hasNode(nodeName)) {
            if (nodes.get(nodeName).hasService(serviceName)) {
                return nodes.get(nodeName).getServiceArgs(serviceName);
            }
        }
        return new HashSet<>();
    }

    /**
     * Get total number of nodes
     * @return integer total
     */
    public int size() {
        return nodes.size();
    }

    public String getOriginalName(String nodeName) {
        return nodeName.replace("__", "/");
    }


    /**
     * Comparing two nodes objects. As nodelet can also have services.
     * @param o
     * @return
     */
    @Override
    public int compareTo(Nodes o) {
        if (size() == o.size()) {
            Iterator<String> nodeIterator = getNodes();

            int count = 0;
            while (nodeIterator.hasNext()) {
                String nodeName = nodeIterator.next();

                Iterator<String> oIterator = o.getNodes();
                while (oIterator.hasNext()) {
                    String oName = oIterator.next();

                    if (nodeName.equals(oName)) {
                        count++;
                        break;
                    }
                }
            }

            if (count == size()) {
                return 0;
            }
        }
        return -1;
    }


    /**
     * Implements the node data structure.
     * This class is intentionally placed inside the Nodes class to
     * abstract it away from the usage and implementation.
     *
     * The comparator only checks node name for equality. So, it is
     * important to do a check before adding a node with the name
     * same as an existing, as it will be overridden.
     */
    class Node implements Comparable<Node> {

        String name;
        Set<String> subscriptionTopics;
        Set<String> publicationTopics;
        Set<String> nodelets;
        HashMap<String, Service> services;

        Node(String name) {
            this.name = name;
            this.subscriptionTopics = new HashSet<>();
            this.publicationTopics = new HashSet<>();
            this.nodelets = new HashSet<>();
            this.services = new HashMap<>();
        }

        public String getName() {
            return name;
        }

        public boolean isActionServer() {

            // Check if the node publishes status, result, feedback and subscribes to cancel and goal
            if (publicationTopics.contains(this.name + Constants.FEEDBACK.toString()) &&
                publicationTopics.contains(this.name + Constants.STATUS.toString()) &&
                publicationTopics.contains(this.name + Constants.RESULT.toString()) &&
                subscriptionTopics.contains(this.name + Constants.GOAL.toString()) &&
                subscriptionTopics.contains(this.name + Constants.CANCEL.toString())) {
                return true;
            }
            return false;
        }

        public boolean isActionClient() {
            return (!getAllActionServerNodeNames().isEmpty());
        }

        public boolean isNodeletManager() {
            if (nodelets.size() > 0) {
                return true;
            }
            return false;
        }

        public Set<String> getAllActionServerNodeNames() {
            HashMap<String, Integer> nodesMap = new HashMap<>();

            // Checking for all the goal and cancel topics in the publication list
            for (String topic : publicationTopics) {
                String nodeName = null;
                if (topic.endsWith(Constants.GOAL.toString())) {
                    nodeName = topic.substring(0,topic.indexOf(Constants.GOAL.toString()));
                }

                if (topic.endsWith(Constants.CANCEL.toString())) {
                    nodeName = topic.substring(0, topic.indexOf(Constants.CANCEL.toString()));
                }

                if (nodeName != null) {
                    if (nodesMap.containsKey(nodeName)) {
                        nodesMap.put(nodeName, nodesMap.get(nodeName) + 1);
                    } else {
                        nodesMap.put(nodeName, 1);
                    }
                }
            }

            // Remove all the nodename which do not have both the topics
            Iterator<String> keySet  = nodesMap.keySet().iterator();
            while(keySet.hasNext()) {
                String nodeName = keySet.next();
                if (nodesMap.get(nodeName) != 2) {
                    nodesMap.remove(nodeName);
                }
            }

            return nodesMap.keySet();
        }

        public void addSubscriber(String topicName) {
            subscriptionTopics.add(topicName);
        }

        public void addPublisher(String topicName) {
            publicationTopics.add(topicName);
        }

        public void addNodelet(String nodeletName) {
            nodelets.add(nodeletName);
        }

        public void addService(String serviceName, String serviceType, Set<String> serviceArgs) {
            Service service = new Service(serviceName, serviceType, serviceArgs);
            services.put(serviceName, service);
        }

        public void removeSubscriber(String topicName) {
            subscriptionTopics.remove(topicName);
        }

        public void removePublisher(String topicName) {
            publicationTopics.remove(topicName);
        }

        public Set<String> getSubscriptions() {
            return this.subscriptionTopics;
        }

        public Set<String> getPublications() {
            return this.publicationTopics;
        }

        public Set<String> getNodelets() {
            return this.nodelets;
        }

        public Set<String> getServiceNames() {
            return services.keySet();
        }

        public boolean hasService(String serviceName) {
            return services.containsKey(serviceName);
        }

        public String getServiceType(String serviceName) {
            if (hasService(serviceName)) {
                return services.get(serviceName).getType();
            }
            return "";
        }

        public Set<String> getServiceArgs(String serviceName) {
            if (hasService(serviceName)) {
                return services.get(serviceName).getArgs();
            }
            return new HashSet<>();
        }

        /**
         * The equals for nodes only campares the names.
         * And even if two nodes have different subscriptions and
         * publications, the node will be considered the same and
         * can be overridden.
         * @param o
         * @return int
         */
        @Override
        public int compareTo(Node o) {
            return this.name.compareTo(o.getName());
        }

        class Service {
            String name;
            String type;
            Set<String> args;

            Service(String name) {
                this.name = name;
                this.type = null;
                this.args = new HashSet<>();
            }

            Service(String name, String type) {
                this.name = name;
                this.type = type;
                this.args = new HashSet<>();
            }

            Service (String name, String type, Set<String> args) {
                this.name = name;
                this.type = type;
                if (args != null) {
                    this.args = args;
                } else {
                    this.args = new HashSet<>();
                }

            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public Set<String> getArgs() {
                return args;
            }

            public void setArgs(Set<String> args) {
                this.args = args;
            }
        }
    }
}
