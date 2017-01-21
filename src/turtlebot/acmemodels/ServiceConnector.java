package turtlebot.acmemodels;

import org.acmestudio.acme.element.IAcmeSystem;

/**
 * Created by ekansh_gupta on 11/21/16.
 */
public class ServiceConnector extends Connector {
    private String name;
    private String newName;

    public ServiceConnector(IAcmeSystem system, String name) {
        super(system, new String(name.substring(0, name.length()).replace("/", "__")), "ServiceConnT", "ROSServiceCallerRoleT", "ROSServiceResponderRoleT");
        this.name = name;
        this.newName = new String(name.substring(0, name.length()).replace("/", "__"));
    }

    @Override
    public String getName() {
        return name;
    }

    public String getNewName() {
        return newName;
    }
}
