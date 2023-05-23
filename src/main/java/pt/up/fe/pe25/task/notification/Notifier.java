package pt.up.fe.pe25.task.notification;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;
import pt.up.fe.pe25.authentication.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * The notifier entity
 * This entity is used to store the notification data for each user
 *
 * @see NotificationData
 * @see NotificationService
 * @see NotificationFactory
 * @see NotificationScheduler
 */
@Entity
public class Notifier extends PanacheEntity {

    // https://quarkus.io/guides/hibernate-orm-panache

    /**
     * The notification services
    */
    @ElementCollection
    @CollectionTable(name = "notification_services")
    public List<String> notificationServices;

    /**
     * The notification data
    */
    @Embedded
    public NotificationData notificationData;

    /**
     * The creation date
    */
    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * The user that owns this notifier
    */
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

    public void setNotificationServices(List<String> notificationServices) {
        this.notificationServices = notificationServices;
    }

    public void setNotificationData(NotificationData notificationData) {
        this.notificationData = notificationData;
    }
}
