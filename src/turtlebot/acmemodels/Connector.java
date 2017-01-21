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

public class Connector {

    private IAcmeConnector connector;

    private String connectorName;

    private String subscriberRoleName;

    private String advertizerRoleName;

    public Connector(IAcmeSystem system, String name, String connectorName, String subscriberRoleName, String advertizerRoleName) {
        this.connectorName = connectorName;
        this.subscriberRoleName = subscriberRoleName;
        this.advertizerRoleName = advertizerRoleName;

        List<String> topicTypeList = new ArrayList<>();
        topicTypeList.add(this.connectorName);

        name = name + "_conn";
        try {
            system.getCommandFactory().connectorCreateCommand(system, name, topicTypeList, topicTypeList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

        connector = system.getConnector(name);
    }

    public void addSubscriberRole(String name) {

        List<String> topicSubscriber = new ArrayList<>();
        topicSubscriber.add(this.subscriberRoleName);

        try {
            connector.getCommandFactory().roleCreateCommand(connector, name, topicSubscriber, topicSubscriber).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

    }

    public void addAdvertiserRole(String name) {

        List<String> topicAdvertiser = new ArrayList<>();
        topicAdvertiser.add(this.advertizerRoleName);

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
        String name = connector.getName().substring(0, connector.getName().length() - 5);
        return name;
    }

    public IAcmeRole getRole(String name) {
        return connector.getRole(name);
    }


}
