import org.acmestudio.acme.core.exception.AcmeException;
import org.acmestudio.acme.element.IAcmeConnector;
import org.acmestudio.acme.element.IAcmeRole;
import org.acmestudio.acme.element.IAcmeSystem;

import java.util.ArrayList;
import java.util.List;

public class ServiceConnector {

    private IAcmeConnector connector;
    private String name;
    private String newName;

    public ServiceConnector(IAcmeSystem system, String name) {

        List<String> serviceTypeList = new ArrayList<>();
        serviceTypeList.add("ServiceConnT");
        this.name = name;

        String newName = new String(name.substring(0, name.length()).replace("/", "__"));
        this.newName = newName;

        try {
            system.getCommandFactory().connectorCreateCommand(system, newName, serviceTypeList, serviceTypeList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

        connector = system.getConnector(newName);
    }


    public void addCallerRole(String name) {

        List<String> callerList = new ArrayList<>();
        callerList.add("ROSServiceCallerRoleT");

        try {
            connector.getCommandFactory().roleCreateCommand(connector, name, callerList, callerList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

    }

    public void addResponderRole(String name) {
        List<String> responderList = new ArrayList<>();
        responderList.add("ROSServiceResponderRoleT");

        try {
            connector.getCommandFactory().roleCreateCommand(connector, name, responderList, responderList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getConnectorName() {
        return newName;
    }

    public IAcmeRole getRole(String name) {
        return connector.getRole(name);
    }


}