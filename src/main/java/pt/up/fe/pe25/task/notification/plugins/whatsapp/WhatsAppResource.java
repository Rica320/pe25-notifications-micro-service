package pt.up.fe.pe25.task.notification.plugins.whatsapp;
import javax.ws.rs.core.Response;

import pt.up.fe.pe25.task.notification.NotificationData;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/whatsapp")
public class WhatsAppResource {


    @Path("/message/link")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /** Send a link message to a phone number
     * @param link - link to be sent
     * @param text - text to be sent
     * @param receiverPhone - phone number of the receiver
     * @return Response with status code and message
     **/
    public Response sendLinkMessage(NotificationData notificationData) {

        String link = notificationData.getLink();
        String message = notificationData.getMessage();
        String receiverPhone = notificationData.getReceiver();

        WhatsAppPlugin whatsappPlugin = new WhatsAppPlugin(null);

        try {
            whatsappPlugin.sendLinkMessage(link, message, receiverPhone);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}