package pt.up.fe.pe25.task.notification.plugins.twilio.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;
import pt.up.fe.pe25.task.notification.plugins.twilio.TwilioConfig;

import javax.inject.Singleton;

/**
 * A plugin that sends notifications by SMS using Twilio<br>
 * You can configure the Twilio account in application.properties<br>
 * The SMS can contain a message and a list of receivers<br>
 *
 * @see NotificationService
 * @see PluginDecorator
 * @see NotificationData
 * @see TwilioConfig
 */
@Singleton
public class TwilioSMSPlugin extends PluginDecorator {

    public TwilioSMSPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    private TwilioConfig twilioConfig;

    /**
     * Creates a new TwilioSMSPlugin
     * @param notificationService the notification service to be wrapped
     * @param twilioConfig the Twilio configuration
     */
    public TwilioSMSPlugin(NotificationService notificationService, TwilioConfig twilioConfig) {
        super(notificationService);
        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    }

    /**
     * Sends a notification by SMS ... calling the wrapped notification service before
     * @param notificationData the notification data
     * @return true if the notification was sent successfully, false otherwise
     */
    @Override
    public boolean notify(NotificationData notificationData) {
        if (notificationService != null)
            super.notify(notificationData);

        //List<String> smsList = new ArrayList<>();
        for (String destPhone: notificationData.getPhoneList()) {
            Message sms = Message.creator(
                    new com.twilio.type.PhoneNumber(destPhone),
                    new com.twilio.type.PhoneNumber(twilioConfig.getPhoneNumber()),
                    notificationData.getMessage())
                    .create();

            //smsList.add(sms.getSid()); .... TODO: add Loggers
        }


        return true;
    }
}
