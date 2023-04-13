package pt.up.fe.pe25.task.notification.plugins.twilio.sms;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import org.eclipse.microprofile.config.Config;
import org.eclipse.microprofile.config.inject.ConfigProperties;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;

import javax.inject.Inject;
import javax.inject.Singleton;


@Singleton
public class TwilioSMSPlugin extends PluginDecorator {

    public TwilioSMSPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    private TwilioConfig twilioConfig;

    public TwilioSMSPlugin(NotificationService notificationService, TwilioConfig twilioConfig) {
        super(notificationService);
        this.twilioConfig = twilioConfig;
        Twilio.init(twilioConfig.getAccountSid(), twilioConfig.getAuthToken());
    }

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

            //smsList.add(sms.getSid());
        }


        return true;
    }
}
