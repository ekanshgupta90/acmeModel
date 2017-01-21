package tests.structures;

import turtlebot.structures.Constants;
import turtlebot.structures.Nodes;
import org.junit.Assert;
import org.junit.Test;

import java.util.Iterator;

/**
 * Created by ekansh_gupta on 11/20/16.
 */
public class NodesTest {

    private int defaultSize = 0;

    private Nodes createNodes() {
        Nodes nodes = new Nodes();
        nodes.addNode("node1");
        nodes.addNode("node2");

        defaultSize = 2;

        return nodes;
    }

    private String getFirstNode(Nodes nodes) {
        String nodeName = null;
        Iterator<String> nodesIterator = nodes.getNodes();
        while (nodesIterator.hasNext()) {
            nodeName = nodesIterator.next();
        }
        return nodeName;
    }

    @Test
    public void getNodes() throws Exception {
        Nodes nodes = createNodes();

        Iterator<String> nodesIterator = nodes.getNodes();

        int count = 0;
        while (nodesIterator.hasNext()) {
            nodesIterator.next();
            count++;
        }

        Assert.assertEquals(count, defaultSize);

        nodes = new Nodes();
        nodes.addNode("new node");

        nodesIterator = nodes.getNodes();

        String nodeName = null;
        while (nodesIterator.hasNext()) {
            nodeName = nodesIterator.next();
        }

        Assert.assertEquals("new node", nodeName);
    }

    @Test
    public void addNode() throws Exception {
        Nodes nodes = createNodes();

        Assert.assertEquals(nodes.size(), defaultSize);

        nodes.addNode("node2");

        Assert.assertEquals(nodes.size(), defaultSize);
    }

    @Test
    public void addSubscription() throws Exception {
        Nodes nodes = createNodes();
        String nodeName = getFirstNode(nodes);

        if (nodeName != null) {
            nodes.addSubscription(nodeName, "subcription1");
            nodes.addSubscription(nodeName, "subcription2");
            Assert.assertEquals(2, nodes.getAllSubscriptions(nodeName).size());
        } else {
            throw new RuntimeException("Test case is not properly configured.");
        }
    }

    @Test
    public void addPublication() throws Exception {
        Nodes nodes = createNodes();
        String nodeName = getFirstNode(nodes);

        if (nodeName != null) {
            nodes.addPublication(nodeName, "publication1");
            nodes.addPublication(nodeName, "publication2");

            Assert.assertEquals(2, nodes.getAllPublications(nodeName).size());
        } else {
            throw new RuntimeException("Test case is not properly configured.");
        }
    }

    @Test
    public void hasNode() throws Exception {
        Nodes nodes = createNodes();
        String nodeName = getFirstNode(nodes);

        if (nodeName != null) {
            Assert.assertEquals(nodes.hasNode(nodeName), true);
            Assert.assertNotEquals(nodes.hasNode("1234"), true);
        } else {
            throw new RuntimeException("Test case is not properly configured.");
        }
    }

    @Test
    public void isActionServer() throws Exception {
        Nodes nodes = createNodes();
        String nodeName = getFirstNode(nodes);

        if (nodeName != null) {
            nodes.addSubscription(nodeName, nodeName + Constants.GOAL);
            nodes.addSubscription(nodeName, nodeName + Constants.CANCEL);
            nodes.addPublication(nodeName, nodeName + Constants.FEEDBACK);
            nodes.addPublication(nodeName, nodeName + Constants.STATUS);
            nodes.addPublication(nodeName, nodeName + Constants.RESULT);

            Assert.assertEquals(true, nodes.isActionServer(nodeName));

            nodes = createNodes();
            nodeName = getFirstNode(nodes);
            Assert.assertNotEquals(true, nodes.isActionServer(nodeName));

        } else {
            throw new RuntimeException("Test case is not properly configured.");
        }
    }

    @Test
    public void isActionClient() throws Exception {
        Nodes nodes = createNodes();
        String nodeName = getFirstNode(nodes);

        if (nodeName != null) {
            nodes.addPublication(nodeName, nodeName + Constants.GOAL);
            nodes.addPublication(nodeName, nodeName + Constants.CANCEL);

            nodes.addPublication(nodeName, "node2" + Constants.GOAL);
            nodes.addPublication(nodeName, "node2" + Constants.CANCEL);

            Assert.assertEquals(true, nodes.isActionClient(nodeName));

            nodes = createNodes();
            nodeName = getFirstNode(nodes);
            Assert.assertNotEquals(true, nodes.isActionClient(nodeName));

        } else {
            throw new RuntimeException("Test case is not properly configured.");
        }
    }

    @Test
    public void getAllConnectingActionServers() throws Exception {
        Nodes nodes = createNodes();
        String nodeName = getFirstNode(nodes);

        if (nodeName != null) {
            nodes.addPublication(nodeName, nodeName + Constants.GOAL);
            nodes.addPublication(nodeName, nodeName + Constants.CANCEL);

            nodes.addPublication(nodeName, "node2" + Constants.GOAL);
            nodes.addPublication(nodeName, "node2" + Constants.CANCEL);

            Assert.assertEquals(2, nodes.getAllConnectingActionServers(nodeName).size());

        } else {
            throw new RuntimeException("Test case is not properly configured.");
        }
    }

    @Test
    public void getAllSubscriptions() throws Exception {
        Nodes nodes = createNodes();
        String nodeName = getFirstNode(nodes);

        if (nodeName != null) {
            nodes.addSubscription(nodeName, "topic 1");
            nodes.addSubscription(nodeName, "topic 2");


            Assert.assertEquals(2, nodes.getAllSubscriptions(nodeName).size());

        } else {
            throw new RuntimeException("Test case is not properly configured.");
        }
    }

    @Test
    public void getAllPublications() throws Exception {
        Nodes nodes = createNodes();
        String nodeName = getFirstNode(nodes);

        if (nodeName != null) {
            nodes.addPublication(nodeName, "topic 1");
            nodes.addPublication(nodeName, "topic 2");


            Assert.assertEquals(2, nodes.getAllPublications(nodeName).size());

        } else {
            throw new RuntimeException("Test case is not properly configured.");
        }
    }

    @Test
    public void size() throws Exception {
        Nodes nodes = createNodes();

        Assert.assertEquals(2, nodes.size());
    }

}