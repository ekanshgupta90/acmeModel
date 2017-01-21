package turtlebot.structures;

import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by ekansh_gupta on 11/19/16.
 */
public class Topics {
    private HashMap<String, Topic> topics;

    /**
     * Constructor to initialize the topics hashmap.
     */
    public Topics() {
        this.topics = new HashMap<>();
    }

    /**
     * Adds a new topic, if it doesn't exist.
     * The method will not override the topic, in case it
     * already exist.
     * @param topicName
     * @param topicType
     */
    public void addTopic(String topicName, String topicType) {
        if (!hasTopic(topicName)) {
            topics.put(topicName, new Topic(topicName, topicType));
        }
    }

    /**
     * Can be used to change topic type. Only changes in case
     * the topic exists.
     * @param topicName
     * @param newTopicType
     */
    public void changeType(String topicName, String newTopicType) {
        if (hasTopic(topicName)) {
            Topic topic = topics.get(topicName);
            topic.setType(newTopicType);
            topics.put(topicName, topic);
        }
    }

    /**
     * Checks if the topic is already present in the map.
     * @param topicName
     * @return
     */
    public boolean hasTopic(String topicName) {
        return topics.containsKey(topicName);
    }

    /**
     * Returns name of each topic
     * @return Iterator of String -> topic name
     */
    public Iterator<String> getTopics() {
        return topics.keySet().iterator();
    }

    /**
     * Returns the type of a given topic
     * @param topicName
     * @return String topic type
     */
    public String getTopicType(String topicName) {
        if (hasTopic(topicName)) {
            return topics.get(topicName).getType();
        }
        return null;
    }

    public String getTopicOriginalName(String topicName) {
        return topicName.replace("__", "/");
    }

    /**
     * Returns total number of topics in the map
     * @return int - total number
     */
    public int size() {
        return topics.size();
    }

    /**
     * The class is intentionally wrapped inside and is not accessible outside.
     * The comparator only compares topic name for equality.
     */
    class Topic implements Comparable<Topic> {
        private String name;
        private String type;

        public Topic(String name, String type) {
            this.name = name;
            this.type = type;
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

        @Override
        public int compareTo(Topic o) {
            return this.name.compareTo(o.getName());
        }
    }
}
