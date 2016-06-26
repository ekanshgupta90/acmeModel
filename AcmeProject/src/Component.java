import org.acmestudio.acme.core.exception.AcmeException;
import org.acmestudio.acme.element.IAcmeComponent;
import org.acmestudio.acme.element.IAcmePort;
import org.acmestudio.acme.element.IAcmeSystem;
import org.acmestudio.acme.element.property.IAcmeProperty;
import org.acmestudio.acme.model.DefaultAcmeModel;
import org.acmestudio.acme.model.util.core.UMIntValue;
import org.acmestudio.acme.model.util.core.UMStringValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class Component {

    private IAcmeComponent component;


    public Component(IAcmeSystem system, String name) {

        List<String> componentType = new ArrayList<>();
        componentType.add("ROSNodeCompT");


        try {
            system.getCommandFactory().componentCreateCommand(system, name, componentType, componentType).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

        component = system.getComponent(name);

    }

    public void addSubscriberPort(String name) {

        List<String> portTypeList = new ArrayList<>();
        portTypeList.add("TopicSubscribePortT");

        try {
            component.getCommandFactory().portCreateCommand(component, name, portTypeList, portTypeList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }
    }

    public void addPublisherPort(String name) {

        List<String> portTypeList = new ArrayList<>();
        portTypeList.add("TopicPublisherPortT");

        try {
            component.getCommandFactory().portCreateCommand(component, name, portTypeList, portTypeList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }
    }


    public void addStringTypePropertytoPort(String portName, String name, String value) {

        try {
            IAcmePort port = component.getPort(portName);
            port.getCommandFactory().propertyCreateCommand(port, name, DefaultAcmeModel.defaultStringType(), new UMStringValue(value)).execute();
            //System.out.println(property.toString() +  " " + property.getType().toString() + " " +   ((AcmeStringValue) property.getValue()).getValue());
        } catch (AcmeException e) {
            e.printStackTrace();
        }

    }

    public void addIntTypePropertytoPort(String portName, String name, Integer value) {

        try {
            IAcmePort port = component.getPort(portName);
            port.getCommandFactory().propertyCreateCommand(port, name, DefaultAcmeModel.defaultStringType(), new UMIntValue(value)).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

    }


    public void addStringTypeProperty(String name, String value) {

        try {
            component.getCommandFactory().propertyCreateCommand(component, name, DefaultAcmeModel.defaultStringType(), new UMStringValue(value)).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

    }

    public void addIntTypeProperty(String name, Integer value) {

        try {
            component.getCommandFactory().propertyCreateCommand(component, name, DefaultAcmeModel.defaultStringType(), new UMIntValue(value)).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

    }

    public String getName() {
        return component.getName();
    }

    public void printPortDetails() {

        Set<? extends IAcmePort> ports = component.getPorts();
        for (IAcmePort port : ports) {
            Set<? extends IAcmeProperty> properties = port.getProperties();
            for (IAcmeProperty property : properties) {
                System.out.println(property.toString() + " " + property.getType().toString() + " " + property.getValue());
            }
        }
    }

    public IAcmePort getPort(String name) {
        return component.getPort(name);
    }


}
