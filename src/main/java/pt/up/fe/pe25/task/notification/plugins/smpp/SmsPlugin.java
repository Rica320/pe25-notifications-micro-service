package pt.up.fe.pe25.task.notification.plugins.smpp;

import org.eclipse.microprofile.config.Config;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.*;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.SubmitMultiResult;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Date;

public class SmsPlugin extends PluginDecorator {
    @Inject
    Config config;
    private static final TimeFormatter TIME_FORMATTER = new AbsoluteTimeFormatter();
    private final String host = config.getValue("pt.fe.up.pe25.smpp.host", String.class);
    private final int port = Integer.parseInt(config.getValue("pt.fe.up.pe25.smpp.port", String.class));
    private final String systemId = config.getValue("pt.fe.up.pe25.smpp.systemId", String.class);
    private final String password = config.getValue("pt.fe.up.pe25.smpp.password", String.class);
    private final String sender = config.getValue("pt.fe.up.pe25.smpp.sender", String.class);

    public SmsPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    public boolean notify(NotificationData notificationData){
        if (notificationService != null)
            super.notify(notificationData);

        sendSMS(notificationData);
        return false;
    }

    private void sendSMS(NotificationData notificationData) {

        SMPPSession session = new SMPPSession();
        try {
            session.connectAndBind(host, Integer.parseInt(String.valueOf(port)), new BindParameter(BindType.BIND_TX,
                    systemId, password, "cp", TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN,
                    null));

            Address[] addresses = new Address[notificationData.getReceiverPhone().size()];
            int i = 0;
            for (String addr: notificationData.getReceiverPhone()) {
                Address address = new Address(TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, addr);
                addresses[i] = address;
                i++;
            }

            try {
                SubmitMultiResult submitMultiResult = session.submitMultiple("CMT",
                        TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.UNKNOWN, sender,
                        addresses,
                        new ESMClass(), (byte)0, (byte)1,  TIME_FORMATTER.format(new Date()), null,
                        new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), ReplaceIfPresentFlag.REPLACE,
                        new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false),
                        (byte)0, notificationData.getMessage().getBytes());
                String messageId = submitMultiResult.getMessageId();
                System.out.println("Message successfully submitted (message_id = " + messageId + ")");
            } catch (PDUException e) {
                // Invalid PDU parameter
                System.out.println("Invalid PDU parameter");
            } catch (ResponseTimeoutException e) {
                // Response timeout
                System.out.println("Response timeout");
            } catch (InvalidResponseException e) {
                // Invalid response
                System.out.println("Invalid response");
            } catch (NegativeResponseException e) {
                // Receiving negative response (non-zero command_status)
                System.out.println("Receiving negative response");
            } catch (IOException e) {
                System.out.println("IO Exception");
            }

            session.unbindAndClose();

        } catch (IOException e) {
            System.out.println("Failed connect and bind to host");
        }
    }
}
