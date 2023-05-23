package pt.up.fe.pe25.task.notification.plugins.twilio;

import org.eclipse.microprofile.config.Config;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

/**
 * The Twilio configuration
 * <p>
 *     This class is used to retrieve the Twilio configuration from the configuration file
 *     and inject it into the Twilio plugins
 *     <br>
 *     The configuration file must contain the following properties:
 *     <ul>
 *         <li>twilio.accountSid</li>
 *         <li>twilio.authToken</li>
 *         <li>twilio.phoneNumber</li>
 *     </ul>
 *     <br>
 *     The configuration file can be a properties file or a yaml file
 *     <br>
 *     The configuration file can is located in the resources folder.
 *     <br>
 *
 */
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