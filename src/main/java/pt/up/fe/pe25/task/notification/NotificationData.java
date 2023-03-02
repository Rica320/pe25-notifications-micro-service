package pt.up.fe.pe25.task.notification;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class NotificationData {

    @Column(name = "sender_phone")
    private String senderPhone;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    @Column(name = "message")
    private String message;

    // TODO: Add more fields ... Some will be more specific to each service and will probably be added in another place


    public String getSenderPhone() {
        return senderPhone;
    }

    public void setSenderPhone(String senderPhone) {
        this.senderPhone = senderPhone;
    }

    public String getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(String receiverPhone) {
        this.receiverPhone = receiverPhone;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
