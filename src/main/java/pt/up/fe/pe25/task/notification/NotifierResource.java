package pt.up.fe.pe25.task.notification;

import javax.ws.rs.core.Response;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/notifier")
public class NotifierResource {

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createNotifier(Notifier notifier) {
        notifier.persist();
        return Response.status(Response.Status.CREATED).entity(notifier).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifiers() {
        return Response.ok(Notifier.listAll()).build();
    }
}

