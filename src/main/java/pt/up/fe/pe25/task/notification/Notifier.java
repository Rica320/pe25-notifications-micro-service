package pt.up.fe.pe25.task.notification;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;


@Entity
public class Notifier extends PanacheEntity {

    // https://quarkus.io/guides/hibernate-orm-panache

    @ElementCollection
    @CollectionTable(name = "notification_services")
    public List<String> notificationServices;


    @Embedded
    public NotificationData notificationData;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
