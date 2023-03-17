package pt.up.fe.pe25.task.notification;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.time.LocalDateTime;

@Embeddable
public class NotificationData {

    @Column(name = "sender_phone")
    private String senderPhone;

    @Column(name = "receiver_phone")
    private String receiverPhone;

    @Column(name = "message")
    private String message;

    @Column(name = "date_to_send")
    private LocalDateTime dateToSend;
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

    public LocalDateTime getDateToSend() {
        return dateToSend;
    }

    public void setDateToSend(LocalDateTime dateToSend) {
        this.dateToSend = dateToSend;
    }
}
