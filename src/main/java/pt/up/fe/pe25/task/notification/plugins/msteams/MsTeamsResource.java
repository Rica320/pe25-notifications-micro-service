package pt.up.fe.pe25.task.notification.plugins.msteams;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.core.Response;

import pt.up.fe.pe25.task.notification.NotificationData;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.metrics.MetricUnits;

import java.net.URL;

import org.json.JSONObject;

import com.oracle.svm.core.annotate.Inject;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * A separated resource that sends notifications by MS Teams and stores the notification in the database
 *
 * @see MsTeamsPlugin
 * @see NotificationData
 */
@Path("/msteams")
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
    @Path("/add")
    /**
     * Creates a new team.
     * If the team already exists, returns a 409 response with the id of the existing team.
     * @param url Url of the channel webhook.
     * @return Response with the created team.
     */
    public Response createTeam(String url) {
        try {
            new URL(url).toURI();
        } catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The provided URL is not valid").build();
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
    @Counted(name = "msTeamsNotificationsCount", description = "How many ms team notifications have been created with this resource.")
    @Timed(name = "msTeamsNotificationTimer", description = "A measure of how long it takes to send a ms teams notification",
            unit = MetricUnits.MILLISECONDS)
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
