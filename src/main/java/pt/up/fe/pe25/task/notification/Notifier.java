package pt.up.fe.pe25.task.notification;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
public class Notifier extends PanacheEntity {


    @ElementCollection
    @CollectionTable(name = "notification_services")
    private List<String> notificationServices;


    @Embedded
    private NotificationData notificationData;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;


    public Long getId() {
        return id;
    }

    public List<String> getNotificationServices() {
        return notificationServices;
    }

    public NotificationData getNotificationData() {
        return notificationData;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
