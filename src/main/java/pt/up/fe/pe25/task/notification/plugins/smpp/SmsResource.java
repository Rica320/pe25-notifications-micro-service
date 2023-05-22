package pt.up.fe.pe25.task.notification.plugins.smpp;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
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
    @Counted(name = "smppNotifications", description = "How many sms notifications have been sent through smpp.")
    @Timed(name = "smppNotificationsTimer", description = "A measure of how long it takes to send an sms notification through smpp.",
            unit = MetricUnits.MILLISECONDS)
    @RequestBody(
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
        schema = @Schema(implementation = NotificationData.class,
                example = "{\"ticketId\": \"#1\"," +
                        " \"phoneList\": [\"+351 910 384 072\"]," +
                        " \"message\": \"A new ticket #1 has been assigned to you\"" +
                        "}"
            )
        )
    )
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
