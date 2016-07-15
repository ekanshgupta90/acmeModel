import org.acmestudio.acme.core.exception.AcmeException;
import org.acmestudio.acme.element.IAcmeComponent;
import org.acmestudio.acme.element.IAcmeGroup;
import org.acmestudio.acme.element.IAcmeSystem;

import java.util.ArrayList;
import java.util.List;

public class Group {

    private IAcmeGroup group;

    Group(IAcmeSystem system, String groupName ) {
//        List<String> groupType = new ArrayList<>();
//        groupType.add("");
//        try {
//            system.getCommandFactory().groupCreateCommand(system,groupName,groupType,groupType).execute();
//        } catch (AcmeException e) {
//            e.printStackTrace();
//        }
//        group = system.getGroup(groupName);
        group = system.getGroup(groupName);
    }


    public void addMember(IAcmeSystem system, IAcmeComponent component) {
        try {
            system.getCommandFactory().memberAddCommand(group, component).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }
    }

}