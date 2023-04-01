package pt.up.fe.pe25.task.notification;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;
import pt.up.fe.pe25.authentication.User;

import javax.persistence.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.SecurityContext;
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

    @ManyToOne
    public User user;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public List<String> getNotificationServices() {
        return notificationServices;
    }

    public NotificationData getNotificationData() {
        return notificationData;
    }

    public static List<Notifier> findByUser(User user) {
        return find("user", user).list();
    }
}
