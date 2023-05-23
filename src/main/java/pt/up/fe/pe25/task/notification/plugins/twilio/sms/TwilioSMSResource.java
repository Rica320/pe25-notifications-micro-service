package pt.up.fe.pe25.task.notification.plugins.twilio.sms;

import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.plugins.twilio.TwilioConfig;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A separated resource that sends notifications by SMS and stores the notification in the database
 *
 * @see TwilioSMSPlugin
 * @see NotificationData
 */
@Path("/twilio/sms/")
public class TwilioSMSResource {

    /**
     * The Twilio configuration
     */
    @Inject
    TwilioConfig twilioConfig;

    /**
     * Sends a notification by SMS<br>
     * Needs authentication to be called<br>
     *
     * @param notificationData the notification data
     * @return true if the notification was sent successfully, false otherwise
     */
    @Path("/send")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user"})
    @Transactional
    public Response sendMessage(NotificationData notificationData) {
        TwilioSMSPlugin twilioSMSPlugin = new TwilioSMSPlugin(null, twilioConfig);
        try {
            twilioSMSPlugin.notify(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }
}
