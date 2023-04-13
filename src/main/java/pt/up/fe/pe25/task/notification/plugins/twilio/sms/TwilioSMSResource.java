package pt.up.fe.pe25.task.notification.plugins.twilio.sms;

import pt.up.fe.pe25.task.notification.NotificationData;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/twilio/")
public class TwilioSMSResource {

    @Inject
    TwilioConfig twilioConfig;

    @Path("/sms")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
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
