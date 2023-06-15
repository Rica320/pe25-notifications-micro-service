package pt.up.fe.pe25.task.notification.plugins.whatsapp;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
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
@Tag(name = "WhatsApp Resource", description = "Send notifications by WhatsApp")
public class WhatsAppResource {

    WhatsAppPlugin whatsappPlugin = new WhatsAppPlugin(null);

    @Path("/group/create")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(summary = "Create a WhatsApp group",
            description = "Creates a group with the given name and adds the given phone numbers to it.")
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            description = "Data to create the group. Must contain the group name and a list of phone numbers.",
                            requiredProperties = {"groupName", "phoneList"},
                            example = "{\"phoneList\": [\"+351961234567\", \"+351921234567\", \"+351931234567\", \"+351941234567\"]," +
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
    @Operation(summary = "Add person to group",
            description = "Adds a phone number to a WhatsApp group.")
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            description = "Data to update the group. Must contain the group id and a non-empty list of phone numbers.",
                            requiredProperties = {"receiverGroup", "phoneList"},
                            example = "{    \"receiverGroup\" : \"4\",\n" +
                                    "    \"phoneList\" : [\"905301234567\"]}"
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
    @Operation(summary = "Remove person from group",
            description = "Removes a phone number from a WhatsApp group.")
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            description = "Data to update the group. Must contain the group id and a non-empty list of phone numbers.",
                            requiredProperties = {"receiverGroup", "phoneList"},
                            example = "{    \"receiverGroup\" : \"4\",\n" +
                                    "    \"phoneList\" : [\"905301234567\"]}"
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
    @Operation(summary = "Send a text message",
            description = "Sends a text message to a group or to a phone number.")
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            description = "Data to send the message. Must contain the group id or the phone number and the message" +
                                    ". It will prioritize the group id. If both are present, the phone number will be ignored.",
                            requiredProperties = {"receiverGroup", "message", "phoneList"},
                            example = "{    \"receiverGroup\" : \"4\",\n" +
                                    "    \"message\" : \"ola tudo bem?\"}"
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
    @Operation(summary = "Send a media message",
            description = "Sends a media message to a group or to a phone number.")
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            description = "Data to send the message. Must contain the group id or the phone number and the media" +
                                    ". It will prioritize the group id. If both are present, the phone number will be ignored.",
                            requiredProperties = {"receiverGroup", "media", "phoneList"},
                            example = "{    \"receiverGroup\" : \"4\",\n" +
                                    "    \"message\" : \"Altice labs\",\n" +
                                    "    \"media\" : \"https://pt.wikipedia.org/wiki/Ficheiro:Altice.png\"}"
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
    @Operation(summary = "Send a location message",
            description = "Sends a location message to a group or to a phone number.")
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            description = "Data to send the message. Must contain the group id or the phone number and the latitude and longitude" +
                                    ". It will prioritize the group id. If both are present, the phone number will be ignored.",
                            requiredProperties = {"receiverGroup", "latitude", "longitude", "phoneList"},
                            example = "{    \"receiverGroup\" : \"4\",\n" +
                                    "    \"message\" : \"Estou aqui na FEUP\",\n" +
                                    "    \"latitude\" : \"41.1780\",\n" +
                                    "    \"longitude\" : \"-8.5980\"}"
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
    @Operation(summary = "Send a link message",
            description = "Sends a link message to a group or to a phone number.")
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            description = "Data to send the message. Must contain the group id or the phone number and the link" +
                                    ". It will prioritize the group id. If both are present, the phone number will be ignored.",
                            requiredProperties = {"receiverGroup", "link", "phoneList"},
                            example = "{    \"receiverGroup\" : \"4\",\n" +
                                    "    \"message\" : \"Altice labs\",\n" +
                                    "    \"link\" : \"https://www.alticelabs.com/\"}"
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
    @Operation(summary = "Get all groups",
            description = "Gets all groups that have been created by the user")
    @RolesAllowed({"user"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getGroups(@Context SecurityContext securityContext) {

        User user = User.findByUsername(securityContext.getUserPrincipal().getName());
        return Response.ok(WhatsAppGroup.listAll()).build();
    }

    @GET
    @RolesAllowed({"user"})
    @Operation(summary = "Get all whatsapps requests",
            description = "Gets all whatsapps requests that have been created by the user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getWhatsapps(@Context SecurityContext securityContext) {
        User user = User.findByUsername(securityContext.getUserPrincipal().getName());
        return Response.ok(Whatsapp.findByUser(user)).build();
    }
}