package pt.up.fe.pe25.task.notification;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/notifier")
public class NotifierResource {

    @Inject
    NotificationFactory notificationFactory;

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createNotifier(Notifier notifier) {
        notifier.persist();
        //System.out.println(notifier.getNotificationData().getReceiverPhone());
        // TODO: VER QUESTAO DO THROW ... se nao houver plugin válido, nao faz nada mas guarda ticket
        // TODO: talvez seja melhor fazer um throw e nao guardar o ticket
        try {
            notificationFactory.create(notifier.getNotificationServices()).notify(notifier.getNotificationData());
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.CREATED).entity(notifier).build();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifiers() {
        return Response.ok(Notifier.listAll()).build();
    }
}

