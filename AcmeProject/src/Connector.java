import org.acmestudio.acme.core.exception.AcmeException;
import org.acmestudio.acme.element.IAcmeConnector;
import org.acmestudio.acme.element.IAcmeRole;
import org.acmestudio.acme.element.IAcmeSystem;
import org.acmestudio.acme.model.DefaultAcmeModel;
import org.acmestudio.acme.model.util.core.UMIntValue;
import org.acmestudio.acme.model.util.core.UMStringValue;

import java.util.ArrayList;
import java.util.List;

public class Connector {

    private IAcmeConnector connector;

    public Connector(IAcmeSystem system, String name) {

        List<String> topicTypeList = new ArrayList<>();
        topicTypeList.add("TopicConnectorT");

        try {
            system.getCommandFactory().connectorCreateCommand(system, name, topicTypeList, topicTypeList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

        connector = system.getConnector(name);
    }

    public void addSubscriberRole(String name) {

        List<String> topicSubscriber = new ArrayList<>();
        topicSubscriber.add("ROSTopicAdvertiserRoleT");

        try {
            connector.getCommandFactory().roleCreateCommand(connector, name, topicSubscriber, topicSubscriber).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

    }

    public void addAdvertiserRole(String name) {

        List<String> topicAdvertiser = new ArrayList<>();
        topicAdvertiser.add("ROSTopicAdvertiserRoleT");

        try {
            connector.getCommandFactory().roleCreateCommand(connector, name, topicAdvertiser, topicAdvertiser).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

    }

    public void addStringTypeProperty(String name, String value) {

        try {
            connector.getCommandFactory().propertyCreateCommand(connector, name, DefaultAcmeModel.defaultStringType(), new UMStringValue(value)).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

    }

    public void addIntTypeProperty(String name, Integer value) {

        try {
            connector.getCommandFactory().propertyCreateCommand(connector, name, DefaultAcmeModel.defaultStringType(), new UMIntValue(value)).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

    }

    public String getName() {
        return connector.getName();
    }

    public IAcmeRole getRole(String name) {
        return connector.getRole(name);
    }


}
