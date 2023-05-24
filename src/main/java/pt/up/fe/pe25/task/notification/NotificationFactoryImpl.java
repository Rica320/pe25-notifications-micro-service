package pt.up.fe.pe25.task.notification;

import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import javax.inject.Inject;
import javax.enterprise.context.ApplicationScoped;
import java.util.List;

import pt.up.fe.pe25.task.notification.plugins.smtp.MailPlugin;
import pt.up.fe.pe25.task.notification.plugins.msteams.MsTeamsPlugin;
import pt.up.fe.pe25.task.notification.plugins.smpp.SmsPlugin;
import pt.up.fe.pe25.task.notification.plugins.smtp.template.factory.TemplateFactory;
import pt.up.fe.pe25.task.notification.plugins.twilio.TwilioConfig;
import pt.up.fe.pe25.task.notification.plugins.twilio.sms.TwilioSMSPlugin;
import pt.up.fe.pe25.task.notification.plugins.twilio.voice.TwilioCallPlugin;
import pt.up.fe.pe25.task.notification.plugins.whatsapp.WhatsAppPlugin;

/**
 * The notification factory
 * <p>
 *     This class is used to create the notification service chain
 *     <br>
 *     The notification service chain is created based on the types of notifications
 *     that are passed as an argument to the create method
 *     <br>
 * </p>
 *
 */
@ApplicationScoped
public class NotificationFactoryImpl implements NotificationFactory{

    /**
     * The mailer instance to be injected into the mail plugin
     */
    @Inject
    ReactiveMailer mailer;

    /**
     * The twilio configuration to be injected into the twilio plugins
     */
    @Inject
    TwilioConfig twilioConfig;

    /**
     * The template factory to be injected into the mail plugin
     */
    @Inject
    TemplateFactory templateFactory;

    /**
     * Creates the notification service chain
     * @param types the types of notifications to be sent
     * @return the notification service chained
     * @throws IllegalArgumentException if the types are not valid
     */
    @Override
    public NotificationService create(List<String> types) throws IllegalArgumentException{

        NotificationService notificationService = null;

        for (String s : types) {
            switch (s) {
                case "whatsapp" -> notificationService = new WhatsAppPlugin(notificationService);
                case "msteams" -> notificationService = new MsTeamsPlugin(notificationService);
                case "email" -> notificationService = new MailPlugin(notificationService, mailer, templateFactory);
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