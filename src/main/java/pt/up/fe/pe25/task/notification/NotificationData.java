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
    @Schema(example = "#1")
    private String ticketId;

    @ElementCollection
    @Column(name = "phone_list")
    @Schema(example = "[\"+351961234567\", \"+351921234567\", \"+351931234567\", \"+351941234567\"]")
    private List<String> phoneList;

    @ElementCollection
    @Column(name = "receiver_emails")
    @Schema(example = "[\"up202000000@edu.fe.up.pt\", \"up202000000@edu.fe.up.pt\", \"up202000000@edu.fe.up.pt\", \"up202000000@edu.fe.up.pt\"]")
    private List<String> receiverEmails;

    @Transient
    @Schema(example = "")
    private List<String> attachments;

    @Column(name = "subject")
    @Schema(example = "Ticket #1")
    private String subject;

    @Column(name = "message")
    @Schema(example = "A new ticket #1 has been assigned to you")
    private String message;

    @ElementCollection
    @Column(name = "teams")
    @Schema(example = "[0, 1]")
    public List<Long> teams;

    @Column(name = "link")
    @Schema(example = "https://www.alticelabs.com/")
    private String link;

    @Column(name = "latitude")
    @Schema(example = "41.1780")
    private String latitude;

    @Column(name = "longitude")
    @Schema(example = "-8.5980")
    private String longitude;

    @Column(name = "media", columnDefinition = "TEXT")
    @Schema(example = "")
    private String media;

    @Column(name = "group_name")
    @Schema(example = "Grupo Altice Labs")
    private String groupName;

    @Column(name = "receiver_group")
    @Schema(example = "1")
    private Long receiverGroup;

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

    public List<Long> getTeams() {
        return teams;
    }

    public void setTeams(List<Long> teams) {
        this.teams = teams;
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
