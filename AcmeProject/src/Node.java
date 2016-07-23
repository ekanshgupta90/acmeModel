import java.util.ArrayList;
import java.util.List;

public class Node {

    List<Topic> subscribed = new ArrayList<>();
    List<Topic> published = new ArrayList<>();
    List<Service> services = new ArrayList<>();
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

    public void addService(Service service) {this.services.add(service);}

    public String getName() {
        return this.name;
    }

    public List<Topic> getSubscribed() {
        return this.subscribed;
    }

    public List<Topic> getPublished() {
        return this.published;
    }

    public List<Service> getServices() { return this.services;}

    public String getOriginalName() {
        return this.name.substring(0, this.name.length()-4).replace("__", "/");
    }

}
