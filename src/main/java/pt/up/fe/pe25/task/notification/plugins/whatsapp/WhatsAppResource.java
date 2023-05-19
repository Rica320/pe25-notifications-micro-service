package pt.up.fe.pe25.task.notification.plugins.whatsapp;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import pt.up.fe.pe25.authentication.User;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.Notifier;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import java.util.List;

/**
 * A separated resource that sends notifications by WhatsApp and stores the notification in the database
 *
 * @see WhatsAppPlugin
 * @see NotificationData
 * @see Notifier
 * @see Whatsapp
 */
@Path("/whatsapp")
public class WhatsAppResource {

    WhatsAppPlugin whatsappPlugin = new WhatsAppPlugin(null);

    @Path("/group/create")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Creates a group with the given name and adds the given phone numbers to it.
     * @param groupData Data to create the group
     * @return Response with the data sent.
     **/
    public Response createGroup(@Context SecurityContext securityContext,NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "createGroup";
        whatsapp.persist();

        try {
            WhatsAppGroup wppGroup = whatsappPlugin.createGroup(notificationData);
            return Response.status(Response.Status.CREATED).entity(wppGroup).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path("/group/add")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Adds a phone number from a group.
     * @param notificationData Data to update the group
     * @return Response with the data sent.
     **/
    public Response addToGroup(@Context SecurityContext securityContext,NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "addToGroup";
        whatsapp.persist();

        try {
            whatsappPlugin.updateGroup(notificationData, true);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path("/group/remove")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Removes a phone number from a group.
     * @param notificationData Data to update the group
     * @return Response with the data sent.
     **/
    public Response removeFromGroup(@Context SecurityContext securityContext,NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "createGroup";
        whatsapp.persist();

        try {
            whatsappPlugin.updateGroup(notificationData, false);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }



    @Path("/message/text")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Sends a text message to a phone number
     * @param notificationData Data to send
     * @return Response with the data sent.
     **/
    public Response sendTextMessage(@Context SecurityContext securityContext,NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "sendTextMessage";
        whatsapp.persist();

        try {
            whatsappPlugin.sendTextMessage(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path("/message/media")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Sends a media message to a phone number
     * @param notificationData Data to send
     * @return Response with the data sent.
     **/
    public Response sendMediaMessage(@Context SecurityContext securityContext,NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "sendMediaMessage";
        whatsapp.persist();

        try {
            whatsappPlugin.sendMediaMessage(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path ("/message/location")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Sends a location message to a phone number
     * @param notificationData Data to send
     * @return Response with the data sent.
     **/
    public Response sendLocationMessage(@Context SecurityContext securityContext,NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "sendLocationMessage";
        whatsapp.persist();

        try {
            whatsappPlugin.sendLocationMessage(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @Path("/message/link")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    /**
     * Sends a link message to a phone number
     * @param notificationData Data to send
     * @return Response with the data sent.
     **/
    public Response sendLinkMessage(@Context SecurityContext securityContext,NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "sendLinkMessage";
        whatsapp.persist();

        try {
            whatsappPlugin.sendLinkMessage(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path("/groups")
    @GET
    @RolesAllowed({"user"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroups(@Context SecurityContext securityContext) {

        User user = User.findByUsername(securityContext.getUserPrincipal().getName());
        return Response.ok(WhatsAppGroup.listAll()).build();
    }

    @GET
    @RolesAllowed({"user"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWhatsapps(@Context SecurityContext securityContext) {
        User user = User.findByUsername(securityContext.getUserPrincipal().getName());
        return Response.ok(Whatsapp.findByUser(user)).build();
    }
}