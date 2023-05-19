package pt.up.fe.pe25.task.notification.plugins.whatsapp;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.time.LocalDateTime;

/**
 * A entity that represents a Whatsapp Group
 *
 * @see PanacheEntity
 */
@Entity
public class WhatsAppGroup extends PanacheEntity {
    WhatsAppGroup() {}

    public WhatsAppGroup(String name, String groupId) {
        this.name = name;
        this.groupId = groupId;
    }

    private String name;

    private String groupId;

    public String getName() {
        return name;
    }

    public String getGroupId() {
        return groupId;
    }

    public Long getId() {
        return id;
    }

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
