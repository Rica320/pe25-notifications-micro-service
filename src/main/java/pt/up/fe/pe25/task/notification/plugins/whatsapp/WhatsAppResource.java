package pt.up.fe.pe25.task.notification.plugins.whatsapp;
import javax.ws.rs.core.Response;

import pt.up.fe.pe25.task.notification.NotificationData;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/whatsapp")
public class WhatsAppResource {

    @Path("/message/text")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Sends a text message to a phone number
     * @param notificationData Data to send
     * @return Response with the data sent.
     **/
    public Response sendTextMessage(NotificationData notificationData) {

        String message = notificationData.getMessage();
        String receiverPhone = notificationData.getReceiver();

        WhatsAppPlugin whatsappPlugin = new WhatsAppPlugin(null);

        try {
            whatsappPlugin.sendTextMessage(message, receiverPhone);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path("/message/media")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Sends a media message to a phone number
     * @param notificationData Data to send
     * @return Response with the data sent.
     **/
    public Response sendMediaMessage(NotificationData notificationData) {

        String media = notificationData.getMedia();
        String message = notificationData.getMessage();
        String receiverPhone = notificationData.getReceiver();

        WhatsAppPlugin whatsappPlugin = new WhatsAppPlugin(null);

        try {
            whatsappPlugin.sendMediaMessage(media, message, receiverPhone);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path ("/message/location")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Sends a location message to a phone number
     * @param notificationData Data to send
     * @return Response with the data sent.
     **/
    public Response sendLocationMessage(NotificationData notificationData) {

        String latitude = notificationData.getLatitude();
        String longitude = notificationData.getLongitude();
        String message = notificationData.getMessage();
        String receiverPhone = notificationData.getReceiver();

        WhatsAppPlugin whatsappPlugin = new WhatsAppPlugin(null);

        try {
            whatsappPlugin.sendLocationMessage(latitude, longitude, message, receiverPhone);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @Path("/message/link")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Sends a link message to a phone number
     * @param notificationData Data to send
     * @return Response with the data sent.
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