package pt.up.fe.pe25.task.notification;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.quartz.SchedulerException;
import pt.up.fe.pe25.authentication.User;

@Path("/notifier")
public class NotifierResource {

    @Inject
    NotificationFactory notificationFactory;

    @Inject
    NotificationScheduler notificationScheduler;

    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createNotifier(@Context SecurityContext securityContext, Notifier notifier) {

        notifier.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        notifier.persist();

        // TODO: VER QUESTAO DO THROW ... se nao houver plugin válido, nao faz nada mas guarda ticket
        // TODO: talvez seja melhor fazer um throw e nao guardar o ticket
        try {

            if (notifier.getNotificationData().getDateToSend() == null)
                notificationFactory.create(notifier.getNotificationServices()).notify(notifier.getNotificationData());
            else
                notificationScheduler.scheduleNotification(notifier.getNotificationData(),
                        notificationFactory.create(notifier.getNotificationServices()));

        }
        catch (IllegalArgumentException | SchedulerException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.CREATED).entity(notifier).build();
    }

    @GET
    @RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifiers(@Context SecurityContext securityContext) {
        User user = User.findByUsername(securityContext.getUserPrincipal().getName());
        return Response.ok(Notifier.findByUser(user)).build();
    }
}

