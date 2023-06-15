package pt.up.fe.pe25.task.notification.plugins.msteams;

import com.oracle.svm.core.annotate.Inject;

import io.vertx.core.cli.annotations.Description;

import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.media.SchemaProperty;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.json.JSONObject;
import pt.up.fe.pe25.task.notification.NotificationData;

import javax.annotation.security.RolesAllowed;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URL;


/**
 * A separated resource that sends notifications by MS Teams and stores the notification in the database
 *
 * @see MsTeamsPlugin
 * @see NotificationData
 */
@Path("/msteams")
@Tag(name = "Microsoft Teams Resource", description = "Send notifications to MS Teams")
public class MsTeamsResource {
    @Inject
    private MsTeamsPlugin msTeamsPlugin = new MsTeamsPlugin();

    public void setMsTeamsPlugin(MsTeamsPlugin msTeamsPlugin) {
        this.msTeamsPlugin = msTeamsPlugin;
    }

    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(summary="Add a new team to the database",
     description = "Add a new team to the database by providing its webhook url. To get the webhook url, go <a href=\"https://docs.microsoft.com/en-us/microsoftteams/platform/webhooks-and-connectors/how-to/add-incoming-webhook\">here</a>.")
     @RequestBody(
        content = @Content(mediaType = MediaType.APPLICATION_JSON,
            schema = @Schema(
                properties = {
                    @SchemaProperty(name = "url", description = "The webhook url", example = "https://team.webhook.ms")
                },
                requiredProperties = {"url"}
            )
        )
    )
    @Path("/add")
    /**
     * Creates a new team.
     * If the team already exists, returns a 409 response with the id of the existing team.
     * @param url Url of the channel webhook.
     * @return Response with the created team.
     */
    public Response createTeam(@Parameter(description="Webhook url given by Teams", example = "https://team.webhook.ms") String url) {
        try {
            new URL(url).toURI();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The provided Webhook URL is not valid").build();
        }

        try {
            MsTeam team = msTeamsPlugin.addTeam(url);
            return Response.status(Response.Status.CREATED).entity(team).build();
        } catch (IllegalArgumentException e) {
            MsTeam previous = MsTeam.find("url", url).firstResult();
            JSONObject response = new JSONObject("{\"message\": \"That team already exists\", \"id\": " + previous.id + "}");
            return Response.status(Response.Status.CONFLICT).entity(response.toString()).build();
        }
    }

    @Path("/message")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Operation(summary="Send a message to a team",
            description = "Send a message to a team by providing the ticket id, the message and the id of the team. The message will be sent to all the teams specified in the list. To get an id, go to <a href=\"/msteams/add\">/msteams/add</a>")
    @Counted(name = "msTeamsNotificationsCount", description = "How many ms team notifications have been created with this resource.")
    @Timed(name = "msTeamsNotificationTimer", description = "A measure of how long it takes to send a ms teams notification",
            unit = MetricUnits.MILLISECONDS)
    @RequestBody(
            content = @Content(mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(implementation = NotificationData.class,
                            requiredProperties = {"ticketId", "message", "teams"},
                            example = "{\"ticketId\": \"#1\"," +
                                    " \"message\": \"A new ticket #1 has been assigned to you\"," +
                                    " \"teams\": \"[0, 1]\"" +
                                    "}"
                        )
                    )
            )
    /**
     * Sends a message to the specified team.
     * @param notificationData Data to send including a list of teams
     * @return Response with the sent data.
     */
    public Response sendMessage(NotificationData notificationData) {
        try {
            msTeamsPlugin.sendMessages(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        } catch (IllegalArgumentException e) {
            //return Response.status(Response.Status.CREATED).build();
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
