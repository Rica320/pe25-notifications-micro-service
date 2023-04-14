package pt.up.fe.pe25.task.notification.plugins.whatsapp;
import javax.ws.rs.core.Response;

import pt.up.fe.pe25.task.notification.NotificationData;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/whatsapp")
public class WhatsAppResource {

    WhatsAppPlugin whatsappPlugin = new WhatsAppPlugin(null).set_PRODUCT_ID("xxxx")
            .set_PHONE_ID("xxxx")
            .set_MAYTAPI_KEY("xxxxx");


    @Path("/group/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Creates a group with the given name and adds the given phone numbers to it.
     * @param groupData Data to create the group
     * @return Response with the data sent.
     **/
    public Response createGroup(NotificationData notificationData) {

        whatsappPlugin.initialize();
        String groupName = notificationData.getGroupName();
        List<String> phoneNumbers = notificationData.getPhoneList();

        try {
            String groupId = whatsappPlugin.createGroup(groupName, phoneNumbers);
            return Response.status(Response.Status.CREATED).entity(groupId).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path("/group/add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Adds a phone number from a group.
     * @param notificationData Data to update the group
     * @return Response with the data sent.
     **/
    public Response addToGroup(NotificationData notificationData) {

            whatsappPlugin.initialize();
            String groupId = notificationData.getGroupId();
            String phoneNumber = notificationData.getReceiver();

            try {
                whatsappPlugin.updateGroup(groupId, phoneNumber, true);
                return Response.status(Response.Status.CREATED).entity(notificationData).build();
            }
            catch (IllegalArgumentException e) {
                return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
            }
    }

    @Path("/group/remove")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Removes a phone number from a group.
     * @param notificationData Data to update the group
     * @return Response with the data sent.
     **/
    public Response removeFromGroup(NotificationData notificationData) {

        whatsappPlugin.initialize();
        String groupId = notificationData.getGroupId();
        String phoneNumber = notificationData.getReceiver();

        try {
            whatsappPlugin.updateGroup(groupId, phoneNumber, false);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }



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

        whatsappPlugin.initialize();
        String message = notificationData.getMessage();
        String receiverPhone = notificationData.getReceiver();

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

        whatsappPlugin.initialize();
        String media = notificationData.getMedia();
        String message = notificationData.getMessage();
        String receiverPhone = notificationData.getReceiver();

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

        whatsappPlugin.initialize();
        String latitude = notificationData.getLatitude();
        String longitude = notificationData.getLongitude();
        String message = notificationData.getMessage();
        String receiverPhone = notificationData.getReceiver();

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

        whatsappPlugin.initialize();
        String link = notificationData.getLink();
        String message = notificationData.getMessage();
        String receiverPhone = notificationData.getReceiver();

        try {
            whatsappPlugin.sendLinkMessage(link, message, receiverPhone);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}