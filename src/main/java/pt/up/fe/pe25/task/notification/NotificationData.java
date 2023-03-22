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
    @Column(name = "phone_list")
    private List<String> phoneList;

    @Column(name = "message")
    private String message;

    @Column(name = "link")
    private String link;

    @Column(name = "receiver")
    private String receiver;

    @Column(name = "date_to_send")
    private LocalDateTime dateToSend;
    // TODO: Add more fields ... Some will be more specific to each service and will probably be added in another place


    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public List<String> getPhoneList() {
        return phoneList;
    }

    public void setPhoneList(List<String> phoneList) {
        this.phoneList = phoneList;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getDateToSend() {
        return dateToSend;
    }

    public void setDateToSend(LocalDateTime dateToSend) {
        this.dateToSend = dateToSend;
    }
}
