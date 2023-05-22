package pt.up.fe.pe25.task.notification.plugins.twilio.voice;

import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.plugins.twilio.TwilioConfig;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * A separated resource that sends notifications by SMS and stores the notification in the database
 *
 * @see TwilioCallPlugin
 * @see NotificationData
 */
@Path("/twilio/call/")
public class TwilioCallResource {

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
    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"user"})
    @Transactional
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            example = "{\"ticketId\": \"#1\"," +
                                    " \"phoneList\": [\"+351 967 325 360\", \"+351 924 017 794\", \"+351 967 108 975\", \"+351 910 384 072\"]," +
                                    " \"message\": \"A new ticket #1 has been assigned to you\"" +
                                    "}"
                    )
            )
    )
    public Response sendMessage(NotificationData notificationData) {
        TwilioCallPlugin twilioCallPlugin = new TwilioCallPlugin(null, twilioConfig);
        try {
            twilioCallPlugin.notify(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

    }
}
