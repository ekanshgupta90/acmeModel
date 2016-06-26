import java.util.ArrayList;
import java.util.List;

public class Node {

    List<Topic> subscribed = new ArrayList<>();
    List<Topic> published = new ArrayList<>();
    private String name;


    public Node(String name) {
        this.name = name + "node";
    }

    public void addSubscriber(Topic topic) {
        this.subscribed.add(topic);
    }


    public void addPublisher(Topic topic) {
        this.published.add(topic);
    }

    public String getName() {
        return this.name;
    }

    public List<Topic> getSubscribed() {
        return this.subscribed;
    }

    public List<Topic> getPublished() {
        return this.published;
    }

}
