package pt.up.fe.pe25.task.notification;

import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;

import pt.up.fe.pe25.task.notification.plugins.smtp.MailPlugin;
import pt.up.fe.pe25.task.notification.plugins.msteams.MsTeamsPlugin;
import pt.up.fe.pe25.task.notification.plugins.smpp.SmsPlugin;
import pt.up.fe.pe25.task.notification.plugins.twilio.TwilioConfig;
import pt.up.fe.pe25.task.notification.plugins.twilio.sms.TwilioSMSPlugin;
import pt.up.fe.pe25.task.notification.plugins.twilio.voice.TwilioCallPlugin;
import pt.up.fe.pe25.task.notification.plugins.whatsapp.WhatsAppPlugin;

@ApplicationScoped
public class NotificationFactoryImpl implements NotificationFactory{

    @Inject
    ReactiveMailer mailer;

    @Inject
    Template template;

    @Inject
    TwilioConfig twilioConfig;

    @Override
    public NotificationService create(List<String> types) throws IllegalArgumentException{

        NotificationService notificationService = null;

        for (String s : types) {
            switch (s) {
                case "whatsapp" -> notificationService = new WhatsAppPlugin(notificationService);
                case "msteams" -> notificationService = new MsTeamsPlugin(notificationService);
                case "email" -> notificationService = new MailPlugin(notificationService, mailer, template);
                case "sms" -> notificationService = new SmsPlugin(notificationService);
                case "tSMS" -> notificationService = new TwilioSMSPlugin(notificationService, twilioConfig);
                case "tCall" -> notificationService = new TwilioCallPlugin(notificationService, twilioConfig);
                default -> {
                }
            }
        }

        if (notificationService == null)
            throw new IllegalArgumentException("No valid notification type was provided");

        return notificationService;
    }
}