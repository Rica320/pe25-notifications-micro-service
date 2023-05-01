package pt.up.fe.pe25.task.notification.plugins.smtp;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;

import javax.inject.Inject;

/**
 * A plugin that sends notifications by email<br>
 * You can configure the SMTP server in application.properties<br>
 * The email can contain a message, a subject and a list of receivers<br>
 *
 * @see NotificationService
 * @see PluginDecorator
 * @see Mail
 * @see ReactiveMailer
 * @see Template
 * @see NotificationData
 */
public class MailPlugin extends PluginDecorator {

    /**
     * The mailer
     */
    ReactiveMailer mailer;

    /**
     * The template to be used in the email
     */
    Template template;

    /**
     * Creates a new MailPlugin
     * @param notificationService the notification service
     * @param mailer the mailer
     * @param template the template
     */
    @Inject
    public MailPlugin(NotificationService notificationService, ReactiveMailer mailer, Template template) {
        super(notificationService);
        this.mailer = mailer;
        this.template = template;
    }

    public MailPlugin() {
        super(null);
    }

    /**
     * Sends a notification by email ... calling the wrapped notification service before
     * @param notificationData the notification data
     * @return true if the notification was sent successfully, false otherwise
     */
    @Override
    public boolean notify(NotificationData notificationData) {
        if (notificationService != null)
            super.notify(notificationData);

        try {
            TemplateInstance templateInstance = template.data("message", notificationData.getMessage());

            Uni<Void> x = mailer.send(
                    Mail.withHtml(String.join(",", notificationData.getReceiverEmails()),
                            notificationData.getSubject(),
                            templateInstance.render()
                    ));

            x.subscribe().with(
                    item -> System.out.println("Sent"),
                    failure -> {throw new IllegalArgumentException("Failed to send email: " + failure.getMessage());}
            );
        } catch (Exception e) {
            return false;
        }
        return true;
    }
}
