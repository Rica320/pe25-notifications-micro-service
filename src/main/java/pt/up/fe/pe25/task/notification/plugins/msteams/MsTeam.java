package pt.up.fe.pe25.task.notification.plugins.msteams;

import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.hibernate.annotations.CreationTimestamp;

import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
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