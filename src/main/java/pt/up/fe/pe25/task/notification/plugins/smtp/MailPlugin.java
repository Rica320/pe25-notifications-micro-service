package pt.up.fe.pe25.task.notification.plugins.smtp;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.smallrye.mutiny.Uni;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

@RequestScoped
public class MailPlugin extends PluginDecorator {

    ReactiveMailer mailer;

    @Inject
    public MailPlugin(NotificationService notificationService, ReactiveMailer mailer) {
        super(notificationService);
        this.mailer = mailer;
    }

    public MailPlugin() {
        super(null);
    }

    @Override
    public boolean notify(NotificationData notificationData) {
        if (notificationService != null)
            super.notify(notificationData);

        Uni<Void> x = mailer.send(
                Mail.withHtml(String.join(",", notificationData.getReceiverEmails()),
                        notificationData.getSubject(),
                        notificationData.getMessage()
                ));

        x.subscribe().with(
                item -> System.out.println("Sent"),
                failure -> System.out.println("Failed to send email: " + failure.getMessage())
        );

        return true;
    }
}
