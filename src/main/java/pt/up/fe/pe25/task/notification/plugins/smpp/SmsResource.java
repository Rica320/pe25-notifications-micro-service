package pt.up.fe.pe25.task.notification.plugins.smpp;

import pt.up.fe.pe25.task.notification.NotificationData;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A separated resource that sends notifications by SMS (SMPP) and stores the notification in the database
 */
@Path("/sms")
public class SmsResource {
    @Path("/message")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user"})
    @Transactional
    /**
     * Sends a text message to a list of phone numbers
     * @param notificationData Data to send
     * @return Response with the data sent.
     **/
    public Response sendMessage(NotificationData notificationData) {

        SmsPlugin smsPlugin = new SmsPlugin(null);

        try {
            smsPlugin.sendMessage(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
