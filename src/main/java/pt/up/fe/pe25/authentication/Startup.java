package pt.up.fe.pe25.authentication;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.transaction.Transactional;

import io.quarkus.runtime.StartupEvent;
import org.quartz.SchedulerException;
import pt.up.fe.pe25.task.notification.NotificationFactory;
import pt.up.fe.pe25.task.notification.NotificationScheduler;
import pt.up.fe.pe25.task.notification.Notifier;

import java.util.Collections;
import java.util.List;


@Singleton
public class Startup {

    @Inject
    NotificationFactory notificationFactory;

    @Inject
    NotificationScheduler notificationScheduler;

    /**
     * Loads the users into the database, for development purposes. Not recommended for production.
     * @param evt the startup event
     */
    @Transactional
    public void loadUsers(@Observes StartupEvent evt) {
        User.deleteAll();
        User.add("admin", "admin", List.of("user", "admin"));
        User.add("user", "user", Collections.singletonList("user"));
    }

    @Transactional
    public void loadToSendSchedulers(@Observes StartupEvent evt) throws SchedulerException {
        for (Notifier notifier : Notifier.getToSendNotifications()) {
            notificationScheduler.scheduleNotification(notifier.getNotificationData(),
                    notificationFactory.create(notifier.getNotificationServices()));
        }

    }

}
