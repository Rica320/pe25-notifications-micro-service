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

public class MailPlugin extends PluginDecorator {

    ReactiveMailer mailer;

    Template template;

    @Inject
    public MailPlugin(NotificationService notificationService, ReactiveMailer mailer, Template template) {
        super(notificationService);
        this.mailer = mailer;
        this.template = template;
    }

    public MailPlugin() {
        super(null);
    }

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
                    failure -> System.out.println("Failed to send email: " + failure.getMessage())
            );
        } catch (Exception e) {
            return false; // TODO: dps temos de arranjar maneira de devolver isto ao resource para que ele diga quais estão indisponíveis
        }



        return true;
    }
}
