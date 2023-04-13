package pt.up.fe.pe25.task.notification.plugins.smtp;

import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.Notifier;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/email")
public class MailResource {

    @Inject
    ReactiveMailer mailer;

    @Inject
    Template template;

    @Path("/message")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response notify(NotificationData notificationData) {
        System.out.println("Email");
        Notifier notifier = new Notifier();
        notifier.setNotificationData(notificationData);
        notifier.setNotificationServices(List.of("email"));
        notifier.persist();
        MailPlugin mailPlugin = new MailPlugin(null, mailer, template);
        try {
            mailPlugin.notify(notificationData);
            return Response.status(Response.Status.CREATED).entity(notificationData).build();
        }
        catch (IllegalArgumentException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }
    }
}
