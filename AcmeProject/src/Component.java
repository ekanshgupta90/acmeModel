import org.acmestudio.acme.core.exception.AcmeException;
import org.acmestudio.acme.element.IAcmeComponent;
import org.acmestudio.acme.element.IAcmePort;
import org.acmestudio.acme.element.IAcmeRole;
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


    public void createSubscribers(IAcmeSystem system, List<Topic> topics, ArrayList<Connector> connectorArrayList ) {
        int portNumber = 0;
        int roleNumber = 0;
        String portName = "sport";
        String roleName = "ROSTopicSubscriberRoleT";

        for (Topic topic : topics) {
            addSubscriberPort(portName + portNumber);
            IAcmePort port = getPort(portName + portNumber);
            String msg_type = null;
            String topicName = null;
            for (Connector connector : connectorArrayList) {
                if (connector.getName() == topic.getName()) {

                    while (connector.getRole(roleName + roleNumber) != null)
                        roleNumber++;

                    connector.addSubscriberRole(roleName + roleNumber);

                    IAcmeRole role = connector.getRole(roleName + roleNumber);
                    Attachment attachment = new Attachment(system, port, role);
                    msg_type = topic.getMsg_Type();
                    topicName = topic.getOriginalName();
                }
            }
            addStringTypePropertytoPort(portName + portNumber, "msg_type", msg_type);
            addStringTypePropertytoPort(portName + portNumber, "topic",topicName);
            portNumber++;
        }
    }


    public void createPublishers(IAcmeSystem system, List<Topic> topics, ArrayList<Connector> connectorArrayList ) {
        int portNumber = 0;
        int roleNumber = 0;
        String portName = "pport";
        String roleName = "ROSTopicAdvertiserRoleT";

        for (Topic topic : topics) {
            addPublisherPort(portName + portNumber);
            IAcmePort port = getPort(portName + portNumber);
            String msg_type = null;
            String topicName = null;
            for (Connector connector : connectorArrayList) {
                if (connector.getName() == topic.getName()) {
                    while (connector.getRole(roleName + roleNumber) != null)
                        roleNumber++;

                    connector.addAdvertiserRole(roleName + roleNumber);
                    IAcmeRole role = connector.getRole(roleName + roleNumber);
                    Attachment attachment = new Attachment(system, port, role);
                    msg_type = topic.getMsg_Type();
                    topicName = topic.getOriginalName();
                }
            }
            addStringTypePropertytoPort(portName + portNumber, "msg_type",msg_type);
            addStringTypePropertytoPort(portName + portNumber, "topic",topicName);
            portNumber++;
        }
    }

    public void createServiceResponderPorts(IAcmeSystem system, List<Service> services, ArrayList<ServiceConnector> serviceConnectors) {
        int portNumber = 0;
        int roleNumber = 0;
        String portName = "serport";
        String roleName = "ROSServiceResponderRoleT";

        try {
            for (Service service : services) {
                addServiceProviderPort(portName + portNumber);
                IAcmePort port = getPort(portName + portNumber);
                String service_type = null;
                String args = null;
                String name = null;
                for (ServiceConnector connector : serviceConnectors) {
                    if (connector.getName().equals(service.getName())) {
                        while (connector.getRole(roleName + roleNumber) != null)
                            roleNumber++;

                        connector.addResponderRole(roleName + roleNumber);
                        IAcmeRole role = connector.getRole(roleName + roleNumber);
                        Attachment attachment = new Attachment(system, port, role);
                        service_type = service.getType();
                        StringBuilder sb = new StringBuilder();
                        for (String s : service.getArgs()) {
                            sb.append(s);
                            sb.append("\t");
                        }
                        args = sb.toString();
                        name = service.getName().split("/")[2];
                    }
                }

                addStringTypePropertytoPort(portName + portNumber, "svc_type", service_type);
                addStringTypePropertytoPort(portName + portNumber, "args", args);
                addStringTypePropertytoPort(portName + portNumber, "name", name);
                portNumber++;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void createServiceCallerPort(IAcmeSystem system, String serviceName, ArrayList<ServiceConnector> serviceConnectors) {
        int portNumber = 0;
        int roleNumber = 0;
        String portName = "callport";
        String roleName = "ROSServiceCallerRoleT";

        try {
                addServiceClientPort(portName + portNumber);
                IAcmePort port = getPort(portName + portNumber);
                for (ServiceConnector connector : serviceConnectors) {
                    if (connector.getName().equals(serviceName)) {
                        while (connector.getRole(roleName + roleNumber) != null)
                            roleNumber++;

                        connector.addCallerRole(roleName + roleNumber);
                        IAcmeRole role = connector.getRole(roleName + roleNumber);
                        Attachment attachment = new Attachment(system, port, role);
                    }
                }
                addStringTypePropertytoPort(portName + portNumber, "name", serviceName);
                portNumber++;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }




    public IAcmeComponent getComponent(){
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

}
