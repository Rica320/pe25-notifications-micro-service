package pt.up.fe.pe25.task.notification.plugins.twilio.voice;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.Twiml;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;
import pt.up.fe.pe25.task.notification.plugins.twilio.TwilioConfig;

import javax.inject.Singleton;

/**
 * A plugin that phones a list of numbers using Twilio. <br>
 * You can configure the Twilio account in application.properties.<br>
 * The call can contain a message and a list of receivers.<br>
 *
 * @see NotificationService
 * @see PluginDecorator
 * @see NotificationData
 * @see TwilioConfig
 */
@Singleton
public class TwilioCallPlugin extends PluginDecorator {

    public TwilioCallPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    private TwilioConfig twilioConfig;

    /**
     * Creates a new TwilioCallPlugin
     * @param notificationService the notification service to be wrapped
     * @param twilioConfig the Twilio configuration
     */
    public TwilioCallPlugin(NotificationService notificationService, TwilioConfig twilioConfig) {
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

        for (String destPhone: notificationData.getPhoneList()) {
            Call call = Call.creator(
                    new com.twilio.type.PhoneNumber(destPhone),
                    new com.twilio.type.PhoneNumber(twilioConfig.getPhoneNumber()),
                    new Twiml(notificationData.getMessage()))
                    .create();
        }
        // add log messaging

        return true;
    }
}
