package pt.up.fe.pe25.task.notification.plugins.msteams;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
/**
 * This class represents a Microsoft Teams team.
 * To communicate with a team, first get a webhook and then use it to send messages.
 */
public class MsTeam extends PanacheEntity {
    MsTeam() {}

    MsTeam(String url) {
        this.url = url;
    }

    private String url;

    public String getUrl() {
        return url;
    }

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}