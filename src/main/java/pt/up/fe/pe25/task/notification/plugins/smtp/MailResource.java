package pt.up.fe.pe25.task.notification.plugins.smtp;

import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.Notifier;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.eclipse.microprofile.metrics.MetricUnits;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * A separated resource that sends notifications by email and stores the notification in the database
 *
 * @see MailPlugin
 * @see NotificationData
 * @see Notifier
 */
@Path("/email")
public class MailResource {

    /**
     * The mailer
     */
    @Inject
    ReactiveMailer mailer;

    /**
     * The template to be used in the email
     */
    @Inject
    Template template;

    /**
     * Sends a notification by email<br>
     * Needs authentication to be called<br>
     *
     * @param notificationData the notification data
     * @return true if the notification was sent successfully, false otherwise
     */
    @Path("/message")
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    @Counted(name = "emailNotifications", description = "How many email notifications have been sent.")
    @Timed(name = "emailNotificationsTimer", description = "A measure of how long it takes to send an email notification.",
            unit = MetricUnits.MILLISECONDS)
    public Response notify(NotificationData notificationData) {
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
