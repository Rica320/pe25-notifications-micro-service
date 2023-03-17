package pt.up.fe.pe25.task.notification.plugins.msteams;

import javax.ws.rs.core.Response;

import pt.up.fe.pe25.task.notification.NotificationData;

import java.net.URL;

import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/msteams")
public class MsTeamsResource {
    @POST
    @Consumes(MediaType.TEXT_PLAIN)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Path("/add")
    public Response createTeam(String url) {
        try {
            new URL(url).toURI();
        }
        catch (Exception e) {
            return Response.status(Response.Status.BAD_REQUEST).entity("The provided URL is not valid").build();
        }

        MsTeamsPlugin msTeamsPlugin = new MsTeamsPlugin(null);
        MsTeam team = msTeamsPlugin.addTeam(url);
        return Response.status(Response.Status.CREATED).entity(team).build();
    }

    @Path("/message") 
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response sendMessage(NotificationData notificationData) {
        System.out.print(notificationData);
        MsTeamsPlugin msTeamsPlugin = new MsTeamsPlugin(null);
        try {
            msTeamsPlugin.sendMessages(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
