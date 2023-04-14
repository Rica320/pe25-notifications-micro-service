package pt.up.fe.pe25.task.notification;

import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import pt.up.fe.pe25.task.notification.plugins.smtp.MailPlugin;

import javax.enterprise.context.ApplicationScoped;


import pt.up.fe.pe25.task.notification.plugins.msteams.MsTeamsPlugin;

import pt.up.fe.pe25.task.notification.plugins.smpp.SmsPlugin;
import pt.up.fe.pe25.task.notification.plugins.whatsapp.WhatsAppPlugin;
import pt.up.fe.pe25.task.notification.plugins.whatsapp.WhatsAppProperties;

import javax.inject.Inject;
import java.util.List;

@ApplicationScoped
public class NotificationFactoryImpl implements NotificationFactory{

    @Inject
    ReactiveMailer mailer;

    @Inject
    Template template;

    @Inject
    WhatsAppProperties whatsAppProperties;

    @Override
    public NotificationService create(List<String> types) throws IllegalArgumentException{

        NotificationService notificationService = null;

        // TODO: para passarmos argumentos adicionais teremos de passar um novo objeto sem ser uma string (ver mais à frente)
        for (String s : types) {
            switch (s) {
                case "whatsapp":
                    // TODO: isto é uma solução temporária, por algum motivo o @Inject não está a funcionar dentro do WhatsAppPlugin
                    notificationService = new WhatsAppPlugin(notificationService)
                            .set_PRODUCT_ID(whatsAppProperties.get_PRODUCT_ID())
                            .set_PHONE_ID(whatsAppProperties.get_PHONE_ID())
                            .set_MAYTAPI_KEY(whatsAppProperties.get_MAYTAPI_KEY());
                    break;
                case "msteams":
                    notificationService = new MsTeamsPlugin(notificationService);
                case "email":
                    notificationService = new MailPlugin(notificationService, mailer, template);
                case "sms":
                    notificationService = new SmsPlugin(notificationService);
                default:
                    break;
            }
        }

        if (notificationService == null)
            throw new IllegalArgumentException("No valid notification type was provided");

        return notificationService;
    }
}