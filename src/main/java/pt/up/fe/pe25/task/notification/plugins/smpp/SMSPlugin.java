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

/**
 * A plugin that sends notifications by sms (SMPP)<br>
 * You can configure the SMPP server in application.properties<br>
 *
 * @see NotificationService
 * @see PluginDecorator
 * @see SMPPSession
 * @see SubmitSmResult
 */
public class SMSPlugin extends PluginDecorator {
    private static final TimeFormatter TIME_FORMATTER = new AbsoluteTimeFormatter();
    private String host;
    private int port;
    private String systemId;
    private String password;
    private String sender;
    private String systemType;
    private BindType bindType;
    private TypeOfNumber systemTon;
    private NumberingPlanIndicator systemNpi;
    private TypeOfNumber sourceAddrTon;
    private NumberingPlanIndicator sourceAddrNpi;
    private TypeOfNumber destAddrTon;
    private NumberingPlanIndicator destAddrNpi;
    private byte esmClass;
    private byte protocolId;
    private byte priorityFlag;
    private String scheduleDeliveryTime;
    private String validityPeriod;
    private SMSCDeliveryReceipt registeredDelivery;
    private byte replaceIfPresentFlag;
    private Alphabet dataCoding;
    private byte smDefaultMsgId;

    public SMSPlugin(NotificationService notificationService) {
        super(notificationService);
        getConfig();
    }

    /**
     * Sends a notification by sms using the SMPP protocol
     * @param notificationData the notification data
     * @return true if the notification was sent successfully
     */
    @Override
    public boolean notify(NotificationData notificationData){
        if (notificationService != null)
            super.notify(notificationData);

        return sendMessage(notificationData);
    }

    /**
     * Sends a message to a list of phone numbers
     * @param notificationData
     * @return success or failure on sending the message
     */
    public boolean sendMessage(NotificationData notificationData) {
        String templateText;
        if (notificationData.getTemplate() == null) {
            templateText = "";
        }
        else if (notificationData.getTemplate().equals("ticket")) {
            templateText = "TICKET #" + notificationData.getTicketId() + "\n";
        }
        else {
            templateText = ""; //more templates can be added here
        }
        String message = templateText + notificationData.getMessage();

        SMPPSession session = new SMPPSession();
        try {
            session.connectAndBind(host, port, new BindParameter(bindType,
                    systemId, password, systemType, systemTon, systemNpi,
                    null));

            for (String destPhone: notificationData.getPhoneList()) {
                try {
                    SubmitSmResult submitSmResult = session.submitShortMessage("CMT",
                            sourceAddrTon, sourceAddrNpi, sender,
                            destAddrTon, destAddrNpi, destPhone,
                            new ESMClass(esmClass), protocolId, priorityFlag, scheduleDeliveryTime, validityPeriod,
                            new RegisteredDelivery(registeredDelivery), replaceIfPresentFlag,
                            new GeneralDataCoding(dataCoding, MessageClass.CLASS1, false),
                            smDefaultMsgId, (message).getBytes());


                } catch (PDUException | ResponseTimeoutException | InvalidResponseException |
                         NegativeResponseException | IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }


            session.unbindAndClose();

        } catch (IOException e) {
            // Failed connect and bind to host
            return false;
        }
        return true;
    }

    /**
     * Gets the configuration from application.properties
     */
    private void getConfig() throws NullPointerException{
        this.host = ConfigProvider.getConfig().getValue("smpp.host", String.class);
        this.port = Integer.parseInt(ConfigProvider.getConfig().getValue("smpp.port", String.class));
        this.systemId = ConfigProvider.getConfig().getValue("smpp.systemId", String.class);
        this.password = ConfigProvider.getConfig().getValue("smpp.password", String.class);
        this.sender = ConfigProvider.getConfig().getValue("smpp.sender", String.class);
        this.systemType = ConfigProvider.getConfig().getValue("smpp.systemType", String.class).equals("null")
                ? null : ConfigProvider.getConfig().getValue("smpp.systemType", String.class);
        this.bindType = ConfigProvider.getConfig().getValue("smpp.bindType", BindType.class);
        this.systemTon = ConfigProvider.getConfig().getValue("smpp.systemTon", TypeOfNumber.class);
        this.systemNpi = ConfigProvider.getConfig().getValue("smpp.systemNpi", NumberingPlanIndicator.class);
        this.sourceAddrTon = ConfigProvider.getConfig().getValue("smpp.submitSm.sourceAddrTon", TypeOfNumber.class);
        this.sourceAddrNpi = ConfigProvider.getConfig().getValue("smpp.submitSm.sourceAddrNpi", NumberingPlanIndicator.class);
        this.destAddrTon = ConfigProvider.getConfig().getValue("smpp.submitSm.destAddrTon", TypeOfNumber.class);
        this.destAddrNpi = ConfigProvider.getConfig().getValue("smpp.submitSm.destAddrNpi", NumberingPlanIndicator.class);
        this.esmClass = ConfigProvider.getConfig().getValue("smpp.submitSm.esmClass", Byte.class);
        this.protocolId = ConfigProvider.getConfig().getValue("smpp.submitSm.protocolId", Byte.class);
        this.priorityFlag = ConfigProvider.getConfig().getValue("smpp.submitSm.priorityFlag", Byte.class);
        this.scheduleDeliveryTime = ConfigProvider.getConfig().getValue("smpp.submitSm.scheduleDeliveryTime", String.class).equals("null")
                ? null : ConfigProvider.getConfig().getValue("smpp.submitSm.scheduleDeliveryTime", String.class);
        this.validityPeriod = ConfigProvider.getConfig().getValue("smpp.submitSm.validityPeriod", String.class).equals("null")
                ? null : ConfigProvider.getConfig().getValue("smpp.submitSm.validityPeriod", String.class);
        this.registeredDelivery = ConfigProvider.getConfig().getValue("smpp.submitSm.registeredDelivery", SMSCDeliveryReceipt.class);
        this.replaceIfPresentFlag = ConfigProvider.getConfig().getValue("smpp.submitSm.replaceIfPresentFlag", Byte.class);
        this.dataCoding = ConfigProvider.getConfig().getValue("smpp.submitSm.dataCoding", Alphabet.class);
        this.smDefaultMsgId = ConfigProvider.getConfig().getValue("smpp.submitSm.smDefaultMsgId", Byte.class);
    }
}
