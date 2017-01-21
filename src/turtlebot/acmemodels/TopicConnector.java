package turtlebot.acmemodels;

import org.acmestudio.acme.core.exception.AcmeException;
import org.acmestudio.acme.element.IAcmeConnector;
import org.acmestudio.acme.element.IAcmeRole;
import org.acmestudio.acme.element.IAcmeSystem;
import org.acmestudio.acme.model.DefaultAcmeModel;
import org.acmestudio.acme.model.util.core.UMIntValue;
import org.acmestudio.acme.model.util.core.UMStringValue;

import java.util.ArrayList;
import java.util.List;

public class TopicConnector extends Connector implements Comparable<TopicConnector> {

    public TopicConnector(IAcmeSystem system, String name) {
        super(system, name, "TopicConnectorT", "ROSTopicSubscriberRoleT", "ROSTopicAdvertiserRoleT");
    }

    @Override
    public int compareTo(TopicConnector o) {
        return this.getName().compareTo(o.getName());
    }
}
