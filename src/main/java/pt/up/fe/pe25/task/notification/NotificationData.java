package pt.up.fe.pe25.task.notification;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import java.util.List;
import java.time.LocalDateTime;

@Embeddable
public class NotificationData {

    @Column(name = "ticket_id")
    private String ticketId;

    @ElementCollection
    @Column(name = "receiver_phone")
    private List<String> receiverPhone;

    @Column(name = "message")
    private String message;

    @Column(name = "date_to_send")
    private LocalDateTime dateToSend;
    // TODO: Add more fields ... Some will be more specific to each service and will probably be added in another place


    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public List<String> getReceiverPhone() {
        return receiverPhone;
    }

    public void setReceiverPhone(List<String> receiverPhone) {
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
