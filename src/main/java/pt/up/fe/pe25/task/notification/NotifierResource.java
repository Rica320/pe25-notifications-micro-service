package pt.up.fe.pe25.task.notification;

import javax.inject.Inject;
import javax.ws.rs.core.Response;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import org.quartz.SchedulerException;

@Path("/notifier")
public class NotifierResource {

    @Inject
    NotificationFactory notificationFactory;

    @Inject
    NotificationScheduler notificationScheduler;

    @Inject
    ReactiveMailer mailer;

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
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifiers() {
        return Response.ok(Notifier.listAll()).build();
    }

    @GET
    @Path("/reactive")
    public Uni<Void> sendEmailUsingReactiveMailer() {
        // https://quarkus.io/guides/mailer
        return mailer.send(
                Mail.withText("quarkus@quarkus.io",
                        "Ahoy from Quarkus",
                        "A simple email sent from a Quarkus application using the reactive API."
                )
        );
    }

}

