package turtlebot.acmemodels;

import org.acmestudio.acme.core.exception.AcmeException;
import org.acmestudio.acme.element.IAcmeAttachment;
import org.acmestudio.acme.element.IAcmePort;
import org.acmestudio.acme.element.IAcmeRole;
import org.acmestudio.acme.element.IAcmeSystem;

public class Attachment {

    private IAcmeAttachment attachment;

    public Attachment(IAcmeSystem system, IAcmePort port, IAcmeRole role) {
        try {
            system.getCommandFactory().attachmentCreateCommand(port, role).execute();
        } catch (AcmeException e) {
            e.printStackTrace();
        }
        attachment = system.getAttachment(port, role);
    }

    public IAcmeAttachment getAttachement() {
        return attachment;
    }
}
