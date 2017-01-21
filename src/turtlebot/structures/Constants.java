package turtlebot.structures;

/**
 * Created by ekansh_gupta on 11/19/16.
 */
public enum Constants {
    GOAL ("__goal"),
    STATUS("__status"),
    RESULT("__result"),
    FEEDBACK("__feedback"),
    CANCEL("__cancel"),
    NODES("nodes"),
    PUBLISH("pub"),
    SUBCRIBE("sub"),
    TOPICS("topics"),
    SERVICES("service"),
    CALLS("calls");

    private String dataName;

    Constants(String dataName) {
        this.dataName = dataName;
    }

    @Override
    public String toString() {
        return this.dataName;
    }
}
