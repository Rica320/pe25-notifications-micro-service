package pt.up.fe.pe25.task.notification.plugins.twilio.voice;

import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.plugins.twilio.TwilioConfig;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/twilio/call/")
public class TwilioCallResource {

    @Inject
    TwilioConfig twilioConfig;

    @POST
    @Path("/send")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
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
