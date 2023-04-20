package pt.up.fe.pe25.task.notification.plugins.twilio.voice;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Call;
import com.twilio.type.Twiml;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;
import pt.up.fe.pe25.task.notification.plugins.twilio.TwilioConfig;

import javax.inject.Singleton;


@Singleton
public class TwilioCallPlugin extends PluginDecorator {

    public TwilioCallPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    private TwilioConfig twilioConfig;

    public TwilioCallPlugin(NotificationService notificationService, TwilioConfig twilioConfig) {
        super(notificationService);
        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    }

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
