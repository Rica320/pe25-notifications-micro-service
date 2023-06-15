package pt.up.fe.pe25.task.notification;

import org.eclipse.microprofile.openapi.annotations.media.Schema;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.time.LocalDateTime;
import java.util.List;

@Embeddable
public class NotificationData {

    @Column(name = "ticket_id")
    @Schema(description = "The id of the ticket", example = "KS93KJ")
    private String ticketId;

    @Column(name = "template")
    @Schema(description = "Optional template to be used for the message. Currently supported templates: ticket", example = "ticket")
    private String template;

    @ElementCollection
    @Column(name = "phone_list")
    @Schema(description = "List of phones that should receive this message", example = "[\"+351967325360\", \"+351924017794\", \"+351967108975\", \"+351910384072\"]")
    @Schema(example = "[\"+351961234567\", \"+351921234567\", \"+351931234567\", \"+351941234567\"]")
    private List<String> phoneList;

    @ElementCollection
    @Column(name = "receiver_emails")
    @Schema(description = "List of emails that should receive this message", example = "[\"up202007962@edu.fe.up.pt\", \"up202004926@edu.fe.up.pt\", \"up202008462@edu.fe.up.pt\", \"up202005108@edu.fe.up.pt\"]")
    @Schema(example = "[\"up202000000@edu.fe.up.pt\", \"up202000000@edu.fe.up.pt\", \"up202000000@edu.fe.up.pt\", \"up202000000@edu.fe.up.pt\"]")
    private List<String> receiverEmails;

    @Transient
    @Schema(description = "Attachments for email messages", example = "")
    private List<String> attachments;

    @Column(name = "subject")
    @Schema(example = "Ticket #1")
    private String subject;

    @Column(name = "message")
    @Schema(description = "The text to include as a message", example = "A new ticket #1 has been assigned to you")
    private String message;

    @ElementCollection
    @Column(name = "receiver_teams")
    @Schema(description = "List of teams that should receive this message", example = "[0, 1]")
    private List<Long> receiverTeams;

    @Column(name = "link")
    @Schema(description = "Link to be included in the message", example = "https://www.alticelabs.com/")
    private String link;

    @Column(name = "latitude")
    @Schema(description = "Latitude geographic coordinates in decimal degrees", example = "41.1780")
    private String latitude;

    @Column(name = "longitude")
    @Schema(description = "Longitude geographic coordinates in decimal degrees", example = "-8.5980")
    private String longitude;

    @Column(name = "media", columnDefinition = "TEXT")
    @Schema(description = "Either a url for a medial file or a base64 formatted file")
    private String media;

    @Column(name = "group_name")
    @Schema(description = "Name of the WhatsApp group", example = "Grupo Altice Labs")
    private String groupName;

    @Column(name = "receiver_group")
    @Schema(example = "1")
    private Long receiverGroup;

    @Column(name = "date_to_send")
    @Schema(description = "Date and time to send the message. This can be used to schedule messages", example = "2021-05-20T10:00:00")
    private LocalDateTime dateToSend;

    public String getTicketId() {
        return ticketId;
    }

    public void setTicketId(String ticketId) {
        this.ticketId = ticketId;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    public List<String> getPhoneList() {
        return phoneList;
    }
    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
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

    public List<Long> getReceiverTeams() {
        return receiverTeams;
    }

    public void setReceiverTeams(List<Long> receiverTeams) {
        this.receiverTeams = receiverTeams;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public LocalDateTime getDateToSend() {
        return dateToSend;
    }

    public void setDateToSend(LocalDateTime dateToSend) {
        this.dateToSend = dateToSend;
    }

    public List<String> getReceiverEmails() {
        return receiverEmails;
    }

    public void setReceiverEmails(List<String> receiverEmails) {
        this.receiverEmails = receiverEmails;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public Long getReceiverGroup() {
        return receiverGroup;
    }

    public void setReceiverGroup(Long receiverGroup) {
        this.receiverGroup = receiverGroup;
    }


}
