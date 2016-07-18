import org.acmestudio.acme.core.exception.AcmeException;
import org.acmestudio.acme.element.*;

import java.util.ArrayList;
import java.util.List;

public class ServiceConnector {

    private IAcmeConnector connector;

    public ServiceConnector(IAcmeSystem system, String name) {

        List<String> serviceTypeList = new ArrayList<>();
        serviceTypeList.add("ServiceConnT");

        try {
            system.getCommandFactory().connectorCreateCommand(system, name, serviceTypeList, serviceTypeList).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }

        connector = system.getConnector(name);
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



}