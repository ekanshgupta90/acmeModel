package turtlebot.acmemodels;

import org.acmestudio.acme.element.IAcmeSystem;

/**
 * Created by ekansh_gupta on 11/20/16.
 */
public class ActionConnector extends Connector implements Comparable<ActionConnector> {

    public ActionConnector(IAcmeSystem system, String name) {
        super(system, name, "ActionServerConnT", "ROSActionTopicSubscriberRoleT", "ROSActionTopicAdvertiserRoleT");
    }

    @Override
    public int compareTo(ActionConnector o) {
        return this.getName().compareTo(o.getName());
    }
}
