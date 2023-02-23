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
}
