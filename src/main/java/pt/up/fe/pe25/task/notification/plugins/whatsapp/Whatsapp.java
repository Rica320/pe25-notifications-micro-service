package pt.up.fe.pe25.task.notification.plugins.whatsapp;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import org.hibernate.annotations.CreationTimestamp;
import pt.up.fe.pe25.authentication.User;
import pt.up.fe.pe25.task.notification.NotificationData;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Whatsapp extends PanacheEntity{

    @Embedded
    public NotificationData notificationData;

    public String service;

    @Column(name = "created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @ManyToOne
    public User user;

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public NotificationData getNotificationData() {
        return notificationData;
    }

    public static List<Whatsapp> findByUser(User user) {
        return find("user", user).list();
    }

    public void setNotificationData(NotificationData notificationData) {
        this.notificationData = notificationData;
    }

    public void setService(String service) {
        this.service = service;
    }


}





