package turtlebot.structures;

import java.util.HashMap;

/**
 * Created by ekansh_gupta on 11/20/16.
 */
public class RosModel {
    private Nodes nodes;
    private Nodes nodelets;
    private Topics topics;
    private HashMap<String, String> calls;

    public RosModel() {
        this.nodes = new Nodes();
        this.nodelets = new Nodes();
        this.topics = new Topics();
        this.calls = new HashMap<>();
    }

    public Nodes getNodes() {
        return nodes;
    }

    public void setNodes(Nodes nodes) {
        this.nodes = nodes;
    }

    public Nodes getNodelets() {
        return nodelets;
    }

    public void setNodelets(Nodes nodelets) {
        this.nodelets = nodelets;
    }

    public Topics getTopics() {
        return topics;
    }

    public void setTopics(Topics topics) {
        this.topics = topics;
    }

    public HashMap<String, String> getCalls() {
        return calls;
    }

    public void setCalls(HashMap<String, String> calls) {
        this.calls = calls;
    }
}
