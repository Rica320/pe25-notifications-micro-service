package pt.up.fe.pe25.task.notification;

import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import pt.up.fe.pe25.task.notification.plugins.WhatsAppPlugin;
import pt.up.fe.pe25.task.notification.plugins.smtp.MailPlugin;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class NotificationFactoryImpl implements NotificationFactory{

        @Inject
        ReactiveMailer mailer;

        @Inject
        Template template;

        @Override
        public NotificationService create(List<String> types) throws IllegalArgumentException{
            NotificationService notificationService = null;
            // TODO: para passarmos argumentos adicionais teremos de passar um novo objeto sem ser uma string (ver mais à frente)
            for (String s : types) {
                switch (s) {
                    case "whatsapp" -> notificationService = new WhatsAppPlugin(notificationService);
                    case "email" -> notificationService = new MailPlugin(notificationService, mailer, template);
                    default -> {
                    }
                }
            }

            if (notificationService == null)
                throw new IllegalArgumentException("No valid notification type was provided");

            return notificationService;
        }
}
