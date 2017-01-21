package turtlebot.acmemodels;

import org.acmestudio.acme.core.exception.AcmeException;
import org.acmestudio.acme.element.IAcmeComponent;
import org.acmestudio.acme.element.IAcmePort;
import org.acmestudio.acme.element.IAcmeRole;
import org.acmestudio.acme.element.IAcmeSystem;
import org.acmestudio.acme.element.property.IAcmeProperty;
import org.acmestudio.acme.model.DefaultAcmeModel;
import org.acmestudio.acme.model.util.core.UMIntValue;
import org.acmestudio.acme.model.util.core.UMStringValue;
import turtlebot.structures.Nodes;
import turtlebot.structures.Topics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * Created by ekansh_gupta on 11/21/16.
 */
public class Component implements  Comparable<Component> {
    private IAcmeComponent component;
    private HashMap<String, String> servicePortmap = new HashMap<>();


    public Component(IAcmeSystem system, String name) {
        List<String> componentType = new ArrayList<>();
        componentType.add("ROSNodeCompT");

        name = name + "_comp";
        try {
            system.getCommandFactory().componentCreateCommand(system, name, componentType, componentType).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }
        component = system.getComponent(name);
        servicePortmap = new HashMap<>();
    }


    public Component(IAcmeSystem system, String name, List<String> componentType) {
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
        portTypeList.add("TopicAdvertisePortT");

        try {
            component.getCommandFactory().portCreateCommand(component, name, portTypeList, portTypeList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }
    }

    public void addActionClientPort(String name) {
        List<String> portTypeList = new ArrayList<>();
        portTypeList.add("ActionClientPortT");

        try {
            component.getCommandFactory().portCreateCommand(component, name, portTypeList, portTypeList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }
    }

    public void addActionServerPort(String name) {
        List<String> portTypeList = new ArrayList<>();
        portTypeList.add("ActionServerPortT");

        try {
            component.getCommandFactory().portCreateCommand(component, name, portTypeList, portTypeList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }
    }

    public void addActionSubscriberPort(String name) {
        List<String> portTypeList = new ArrayList<>();
        portTypeList.add("ActionSubscribePortT");

        try {
            component.getCommandFactory().portCreateCommand(component, name, portTypeList, portTypeList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }
    }

    public void addActionAdvertizerPort(String name) {
        List<String> portTypeList = new ArrayList<>();
        portTypeList.add("ActionAdvertisePortT");

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


    public void createSubscribers(IAcmeSystem system, Topics topicsModel, Set<String> topics, Set<TopicConnector> connectors) {
        int portNumber = 0;
        int roleNumber = 0;
        String portName = "sport";
        String roleName = "ROSTopicSubscriberRoleT";

        for (String topic : topics) {
            addSubscriberPort(portName + portNumber);
            IAcmePort port = getPort(portName + portNumber);
            String msg_type = null;
            String topicName = null;
            for (Connector connector : connectors) {
                if (connector.getName().equals(topic)) {

                    while (connector.getRole(roleName + roleNumber) != null)
                        roleNumber++;

                    connector.addSubscriberRole(roleName + roleNumber);

                    IAcmeRole role = connector.getRole(roleName + roleNumber);
                    Attachment attachment = new Attachment(system, port, role);
                    msg_type = topicsModel.getTopicType(topic);
                    topicName = topicsModel.getTopicOriginalName(topic);
                }
            }
            addStringTypePropertytoPort(portName + portNumber, "msg_type", msg_type);
            addStringTypePropertytoPort(portName + portNumber, "topic", topicName);
            portNumber++;
        }
    }

    public void createActionClient(IAcmeSystem system, Set<String> topics, Set<ActionConnector> connectors) {
        int portNumber = 0;
        int roleNumber = 0;
        String portName = "client_port";
        String roleName = "ROSActionCallerRoleT";

        for (String topic : topics) {
            addActionClientPort(portName + portNumber);
            IAcmePort port = getPort(portName + portNumber);
            for (Connector connector : connectors) {
                if (connector.getName().equals(topic)) {

                    while (connector.getRole(roleName + roleNumber) != null)
                        roleNumber++;

                    connector.addSubscriberRole(roleName + roleNumber);

                    IAcmeRole role = connector.getRole(roleName + roleNumber);
                    Attachment attachment = new Attachment(system, port, role);
                }
            }
            portNumber++;
        }
    }


    public void createPublishers(IAcmeSystem system, Topics topicsModel, Set<String> topics, Set<TopicConnector> connectors) {
        int portNumber = 0;
        int roleNumber = 0;
        String portName = "pport";
        String roleName = "ROSTopicAdvertiserRoleT";

        for (String topic : topics) {
            addPublisherPort(portName + portNumber);
            IAcmePort port = getPort(portName + portNumber);
            String msg_type = null;
            String topicName = null;
            for (Connector connector : connectors) {
                if (connector.getName().equals(topic)) {
                    while (connector.getRole(roleName + roleNumber) != null)
                        roleNumber++;

                    connector.addAdvertiserRole(roleName + roleNumber);
                    IAcmeRole role = connector.getRole(roleName + roleNumber);
                    Attachment attachment = new Attachment(system, port, role);
                    msg_type = topicsModel.getTopicType(topic);
                    topicName = topicsModel.getTopicOriginalName(topic);
                }
            }
            addStringTypePropertytoPort(portName + portNumber, "msg_type", msg_type);
            addStringTypePropertytoPort(portName + portNumber, "topic", topicName);
            portNumber++;
        }
    }

    public void createActionServer(IAcmeSystem system, Set<String> topics, Set<ActionConnector> connectors) {
        int portNumber = 0;
        int roleNumber = 0;
        String portName = "action_port";
        String roleName = "ROSActionResponderRoleT";

        for (String topic : topics) {
            addActionServerPort(portName + portNumber);
            IAcmePort port = getPort(portName + portNumber);
            for (Connector connector : connectors) {
                if (connector.getName().equals(topic)) {
                    while (connector.getRole(roleName + roleNumber) != null)
                        roleNumber++;

                    connector.addAdvertiserRole(roleName + roleNumber);
                    IAcmeRole role = connector.getRole(roleName + roleNumber);
                    Attachment attachment = new Attachment(system, port, role);
                }
            }
            portNumber++;
        }
    }

    public void createServiceResponderPorts(IAcmeSystem system, String nodeName, Nodes nodes) {
        int portNumber = 0;
        String portName = "serport";

        try {
            Set<String> services = nodes.getAllServiceNames(nodeName);
            for (String service : services) {
                addServiceProviderPort(portName + portNumber);
                //IAcmePort port = getPort(portName + portNumber);
                String service_type = null;
                String args = null;
                String name = null;
                service_type = nodes.getServiceType(nodeName, service);
                StringBuilder sb = new StringBuilder();
                for (String s : nodes.getServiceArgs(nodeName, service)) {
                    sb.append(s);
                    sb.append("\t");
                }
                args = sb.toString();
                name = service.split("/")[2];

                addStringTypePropertytoPort(portName + portNumber, "svc_type", service_type);
                addStringTypePropertytoPort(portName + portNumber, "args", args);
                addStringTypePropertytoPort(portName + portNumber, "name", name);

                if (service != null && (portName + portNumber) != null)
                    servicePortmap.put(service, portName + portNumber);
                portNumber++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void connectToServiceConnector(IAcmeSystem system, String serviceName, ServiceConnector connector) {
        int roleNumber = 0;
        String roleName = "ROSServiceResponderRoleT";

        while (connector.getRole(roleName + roleNumber) != null)
            roleNumber++;

        connector.addAdvertiserRole(roleName + roleNumber);
        IAcmeRole role = connector.getRole(roleName + roleNumber);
        IAcmePort port = getPort(servicePortmap.get(serviceName));
        Attachment attachment = new Attachment(system, port, role);
    }


    public void createServiceCallerPort(IAcmeSystem system, String serviceName, ServiceConnector connector) {
        int portNumber = 0;
        int roleNumber = 0;
        String portName = "callport";
        String roleName = "ROSServiceCallerRoleT";

        try {
            addServiceClientPort(portName + portNumber);
            IAcmePort port = getPort(portName + portNumber);
            while (connector.getRole(roleName + roleNumber) != null)
                roleNumber++;

            connector.addSubscriberRole(roleName + roleNumber);
            IAcmeRole role = connector.getRole(roleName + roleNumber);
            Attachment attachment = new Attachment(system, port, role);
            addStringTypePropertytoPort(portName + portNumber, "name", serviceName);
            portNumber++;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public IAcmeComponent getComponent() {
        return component;
    }


    public void addServiceProviderPort(String name) {
        List<String> portTypeList = new ArrayList<>();
        portTypeList.add("ServiceProviderPortT");
        try {
            component.getCommandFactory().portCreateCommand(component, name, portTypeList, portTypeList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }
    }

    public void addServiceClientPort(String name) {
        List<String> portTypeList = new ArrayList<>();
        portTypeList.add("ServiceClientPortT");
        try {
            component.getCommandFactory().portCreateCommand(component, name, portTypeList, portTypeList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int compareTo(Component o) {
        return this.component.getName().compareTo(o.getName());
    }
}
