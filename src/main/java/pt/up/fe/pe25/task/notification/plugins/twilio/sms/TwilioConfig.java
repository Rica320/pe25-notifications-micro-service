package pt.up.fe.pe25.task.notification.plugins.twilio.sms;

import org.eclipse.microprofile.config.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TwilioConfig {
    @Inject
    Config config;

    public String getAccountSid() {
        return config.getValue("twilio.accountSid", String.class);
    }

    public String getAuthToken() {
        return config.getValue("twilio.authToken", String.class);
    }

    public String getPhoneNumber() {
        return config.getValue("twilio.phoneNumber", String.class);
    }

}