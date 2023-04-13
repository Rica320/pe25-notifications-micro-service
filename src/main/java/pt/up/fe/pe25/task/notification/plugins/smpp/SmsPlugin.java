package pt.up.fe.pe25.task.notification.plugins.smpp;

import org.eclipse.microprofile.config.ConfigProvider;
import org.jsmpp.InvalidResponseException;
import org.jsmpp.PDUException;
import org.jsmpp.bean.*;
import org.jsmpp.extra.NegativeResponseException;
import org.jsmpp.extra.ResponseTimeoutException;
import org.jsmpp.session.BindParameter;
import org.jsmpp.session.SMPPSession;
import org.jsmpp.session.SubmitSmResult;
import org.jsmpp.util.AbsoluteTimeFormatter;
import org.jsmpp.util.TimeFormatter;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;

import java.io.IOException;

public class SmsPlugin extends PluginDecorator {
    private static final TimeFormatter TIME_FORMATTER = new AbsoluteTimeFormatter();
    private String host;
    private int port;
    private String systemId;
    private String password;
    private String sender;

    public SmsPlugin(NotificationService notificationService) {
        super(notificationService);
        this.host = ConfigProvider.getConfig().getValue("pt.fe.up.pe25.smpp.host", String.class);
        this.port = Integer.parseInt(ConfigProvider.getConfig().getValue("pt.fe.up.pe25.smpp.port", String.class));
        this.systemId = ConfigProvider.getConfig().getValue("pt.fe.up.pe25.smpp.systemId", String.class);
        this.password = ConfigProvider.getConfig().getValue("pt.fe.up.pe25.smpp.password", String.class);
        this.sender = ConfigProvider.getConfig().getValue("pt.fe.up.pe25.smpp.sender", String.class);
    }

    @Override
    public boolean notify(NotificationData notificationData){
        if (notificationService != null)
            super.notify(notificationData);

        sendMessage(notificationData);
        return false;
    }

    public void sendMessage(NotificationData notificationData) {

        SMPPSession session = new SMPPSession();
        try {
            session.connectAndBind(host, port, new BindParameter(BindType.BIND_TX,
                    systemId, password, null, TypeOfNumber.UNKNOWN, NumberingPlanIndicator.UNKNOWN,
                    null));
            try {
                for (String destPhone: notificationData.getPhoneList()) {
                    SubmitSmResult submitSmResult = session.submitShortMessage("CMT",
                            TypeOfNumber.ALPHANUMERIC, NumberingPlanIndicator.UNKNOWN, sender,
                            TypeOfNumber.INTERNATIONAL, NumberingPlanIndicator.ISDN, destPhone,
                            new ESMClass(), (byte) 0, (byte) 1, null, null,
                            new RegisteredDelivery(SMSCDeliveryReceipt.DEFAULT), (byte) 0,
                            new GeneralDataCoding(Alphabet.ALPHA_DEFAULT, MessageClass.CLASS1, false),
                            (byte) 0, (notificationData.getTicketId() + "\n" + notificationData.getMessage()).getBytes());
                    String messageId = submitSmResult.getMessageId();
                    System.out.println("Message successfully submitted (message_id = " + messageId + ")");
                }

            } catch (PDUException | ResponseTimeoutException | InvalidResponseException | NegativeResponseException | IOException e) {
                throw new IllegalArgumentException(e);
            }


            session.unbindAndClose();

        } catch (IOException e) {
            // Failed connect and bind to host
            throw new IllegalArgumentException(e);
        }
    }
}
