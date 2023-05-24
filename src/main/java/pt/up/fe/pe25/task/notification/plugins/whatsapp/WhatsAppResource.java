package pt.up.fe.pe25.task.notification.plugins.whatsapp;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import pt.up.fe.pe25.authentication.User;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.Notifier;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

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
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            example = "{\"phoneList\": [\"+351 967 325 360\", \"+351 924 017 794\", \"+351 967 108 975\", \"+351 910 384 072\"]," +
                                    " \"groupName\": \"Grupo Altice Labs\"" +
                                    "}"
                    )
            )
    )
    /**
     * Creates a group with the given name and adds the given phone numbers to it.
     * @param groupData Data to create the group
     * @return Response with the data sent.
     **/
    public Response createGroup(@Context SecurityContext securityContext, NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "createGroup";
        whatsapp.persist();

        try {
            WhatsAppGroup wppGroup = whatsappPlugin.createGroup(notificationData);
            return Response.status(Response.Status.CREATED).entity(wppGroup).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path("/group/add")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            example = "{\"phoneList\": [\"+351 967 325 360\"]," +
                                    " \"groupName\": \"Grupo Altice Labs\"" +
                                    "}"
                    )
            )
    )
    /**
     * Adds a phone number from a group.
     * @param notificationData Data to update the group
     * @return Response with the data sent.
     **/
    public Response addToGroup(@Context SecurityContext securityContext, NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "addToGroup";
        whatsapp.persist();

        try {
            whatsappPlugin.updateGroup(notificationData, true);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path("/group/remove")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            example = "{\"phoneList\": [\"+351 967 325 360\"]," +
                                    " \"groupName\": \"Grupo Altice Labs\"" +
                                    "}"
                    )
            )
    )
    /**
     * Removes a phone number from a group.
     * @param notificationData Data to update the group
     * @return Response with the data sent.
     **/
    public Response removeFromGroup(@Context SecurityContext securityContext, NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "createGroup";
        whatsapp.persist();

        try {
            whatsappPlugin.updateGroup(notificationData, false);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @Path("/message/text")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = "sendTextMessage", description = "How many whatsapp text messages have been sent.")
    @Timed(name = "sendTextMessageTimer", description = "A measure of how long it takes to send a whatsapp text message."
            , unit = MetricUnits.MILLISECONDS)
    @Transactional
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            example = "{" +
                                    " \"message\": \"A new ticket #1 has been assigned to you\"," +
                                    " \"groupName\": \"Grupo Altice Labs\"" +
                                    "}"
                    )
            )
    )
    /**
     * Sends a text message to a phone number
     * @param notificationData Data to send
     * @return Response with the data sent.
     **/
    public Response sendTextMessage(@Context SecurityContext securityContext, NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "sendTextMessage";
        whatsapp.persist();

        try {
            whatsappPlugin.sendTextMessage(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path("/message/media")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = "sendMediaMessage", description = "How many whatsapp media messages have been sent.")
    @Timed(name = "sendMediaMessageTimer", description = "A measure of how long it takes to send a whatsapp media message."
            , unit = MetricUnits.MILLISECONDS)
    @Transactional
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            example = "{" +
                                    " \"message\": \"Altice Labs\"," +
                                    " \"media\": [\"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MCBSL01lZGlhQm94WzAgMCA2MTIgNzkyXT4+CmVuZG9iagoxNyAwIG9iago8PC9EZXN0WzEgMCBSL1hZWiAwIDc0NCAwXS9UaXRsZShOb3RpZmljYXRpb25zIG1pY3JvLXNlcnZpY2U6IGludGVncmF0aW9uIHdpdGggV2hhdHNBcHAgYW5kIG90aGVyIGNvbGFib3JhdGlvbiB0b29scykvUGFyZW50IDE2IDAgUj4+CmVuZG9iagoxNiAwIG9iago8PC9UeXBlL091dGxpbmVzL0NvdW50IDEvRmlyc3QgMTcgMCBSL0xhc3QgMTcgMCBSPj4KZW5kb2JqCjIgMCBvYmoKPDwvU3VidHlwZS9UeXBlMS9UeXBlL0ZvbnQvQmFzZUZvbnQvSGVsdmV0aWNhLUJvbGQvRW5jb2RpbmcvV2luQW5zaUVuY29kaW5nPj4KZW5kb2JqCjQgMCBvYmoKPDwvU3VidHlwZS9UeXBlMS9UeXBlL0ZvbnQvQmFzZUZvbnQvSGVsdmV0aWNhL0VuY29kaW5nL1dpbkFuc2lFbmNvZGluZz4+CmVuZG9iago1IDAgb2JqCjw8L1N1YnR5cGUvVHlwZTEvVHlwZS9Gb250L0Jhc2VGb250L0hlbHZldGljYS1PYmxpcXVlL0VuY29kaW5nL1dpbkFuc2lFbmNvZGluZz4+CmVuZG9iago4IDAgb2JqCjw8L0tpZHNbMSAwIFIgMTUgMCBSXS9UeXBlL1BhZ2VzL0NvdW50IDIvSVRYVCg4LjAuMTApPj4KZW5kb2JqCjE4IDAgb2JqCjw8L1BhZ2VNb2RlL1VzZU91dGxpbmVzL1R5cGUvQ2F0YWxvZy9PdXRsaW5lcyAxNiAwIFIvUGFnZXMgOCAwIFI+PgplbmRvYmoKMTkgMCBvYmoKPDw+PgplbmRvYmoKeHJlZgowIDIwCjAwMDAwMDAwMDAgNjU1MzUgZiAKMDAwMDAyMzgxMCAwMDAwMCBuIAowMDAwMDI3MjUzIDAwMDAwIG4gCjAwMDAwMDAwMTUgMDAwMDAgbiAKMDAwMDAyNzM0NiAwMDAwMCBuIAowMDAwMDI3NDM0IDAwMDAwIG4gCjAwMDAwMjA0OTAgMDAwMDAgbiAKMDAwMDAyMDY3MiAwMDAwMCBuIAowMDAwMDI3NTMwIDAwMDAwIG4gCjAwMDAwMjQwMjIgMDAwMDAgbiAKMDAwMDAyNDIxOSAwMDAwMCBuIAowMDAwMDI0Mzk1IDAwMDAwIG4gCjAwMDAwMjQ1NTcgMDAwMDAgbiAKMDAwMDAyNDcwNyAwMDAwMCBuIAowMDAwMDI0ODU3IDAwMDAwIG4gCjAwMDAwMjY4MzQgMDAwMDAgbiAKMDAwMDAyNzE4NSAwMDAwMCBuIAowMDAwMDI3MDM1IDAwMDAwIG4gCjAwMDAwMjc2MDEgMDAwMDAgbiAKMDAwMDAyNzY4NCAwMDAwMCBuIAp0cmFpbGVyCjw8L0luZm8gMTkgMCBSL0lEIFs8Mjk1MTc0Nzk1YTYwMmY2OTEwNzc4ZjZjYWUxMjFjZmE+PGFhMWJjYTI1M2VhNWNiYWI5M2U3YTg0NTMyMWQxMWRhPl0vUm9vdCAxOCAwIFIvU2l6ZSAyMD4+CnN0YXJ0eHJlZgoyNzcwNQolJUVPRgo=\"]," +
                                    " \"groupName\": \"Grupo Altice Labs\"" +
                                    "}"
                    )
            )
    )
    /**
     * Sends a media message to a phone number
     * @param notificationData Data to send
     * @return Response with the data sent.
     **/
    public Response sendMediaMessage(@Context SecurityContext securityContext, NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "sendMediaMessage";
        whatsapp.persist();

        try {
            whatsappPlugin.sendMediaMessage(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }

    @Path("/message/location")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = "sendLocationMessage", description = "How many whatsapp location messages have been sent.")
    @Timed(name = "sendLocationMessageTimer", description = "A measure of how long it takes to send a whatsapp location message."
            , unit = MetricUnits.MILLISECONDS)
    @Transactional
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            example = "{" +
                                    " \"message\": \"Estou aqui na FEUP\"," +
                                    " \"latitude\": \"41.1780\"," +
                                    " \"longitude\": \"-8.5980\"," +
                                    " \"groupName\": \"Grupo Altice Labs\"" +
                                    "}"
                    )
            )
    )
    /**
     * Sends a location message to a phone number
     * @param notificationData Data to send
     * @return Response with the data sent.
     **/
    public Response sendLocationMessage(@Context SecurityContext securityContext, NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "sendLocationMessage";
        whatsapp.persist();

        try {
            whatsappPlugin.sendLocationMessage(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }


    @Path("/message/link")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Counted(name = "sendLinkMessage", description = "How many whatsapp link messages have been sent.")
    @Timed(name = "sendLinkMessageTimer", description = "A measure of how long it takes to send a whatsapp link message."
            , unit = MetricUnits.MILLISECONDS)
    @Transactional
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            example = "{," +
                                    " \"message\": \"Altice Labs website\"," +
                                    " \"link\": \"https://www.alticelabs.com/\"," +
                                    " \"groupName\": \"Grupo Altice Labs\"" +
                                    "}"
                    )
            )
    )

    /**
     * Sends a link message to a phone number
     * @param notificationData Data to send
     * @return Response with the data sent.
     **/
    public Response sendLinkMessage(@Context SecurityContext securityContext, NotificationData notificationData) {

        Whatsapp whatsapp = new Whatsapp();
        whatsapp.notificationData = notificationData;
        whatsapp.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        whatsapp.service = "sendLinkMessage";
        whatsapp.persist();

        try {
            whatsappPlugin.sendLinkMessage(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        } catch (IllegalArgumentException e) {
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