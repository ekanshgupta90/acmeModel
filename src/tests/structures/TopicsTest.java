package tests.structures;

import org.junit.Assert;
import org.junit.Test;
import turtlebot.structures.Topics;

import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * Created by ekansh_gupta on 11/20/16.
 */
public class TopicsTest {
    private int defaultSize = 0;

    private Topics createTopics() {
        Topics topics = new Topics();
        topics.addTopic("__topic1__subtopic1","type1");
        topics.addTopic("__topic1__subtopic2","type2");

        defaultSize = 2;

        return topics;
    }

    private String getFirstTopicName(Topics topics) {
        Iterator<String> iterator = topics.getTopics();
        String topicName = null;
        while (iterator.hasNext()) {
            topicName = iterator.next();
            break;
        }
        return topicName;
    }

    @Test
    public void addTopic() throws Exception {
        Topics topics = createTopics();

        Assert.assertEquals(defaultSize, topics.size());
    }

    @Test
    public void changeType() throws Exception {
        Topics topics = createTopics();
        String topicName = getFirstTopicName(topics);

        topics.changeType(topicName, "hello1");

        Assert.assertEquals("hello1", topics.getTopicType(topicName));
    }

    @Test
    public void hasTopic() throws Exception {
        Topics topics = createTopics();
        String topicName = getFirstTopicName(topics);

        Assert.assertEquals(true, topics.hasTopic(topicName));
    }

    @Test
    public void getTopics() throws Exception {
        Topics topics = createTopics();
        Iterator<String> iterator = topics.getTopics();

        int count = 0;

        while (iterator.hasNext()) {
            iterator.next();
            count++;
        }

        Assert.assertEquals(defaultSize, count);
    }

    @Test
    public void getTopicType() throws Exception {
        Topics topics = new Topics();
        topics.addTopic("topic1", "type1");

        Assert.assertEquals("type1", topics.getTopicType("topic1"));
    }

    @Test
    public void size() throws Exception {
        Topics topics = createTopics();

        Assert.assertEquals(defaultSize, topics.size());
    }

}