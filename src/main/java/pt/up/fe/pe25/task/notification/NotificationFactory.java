package pt.up.fe.pe25.task.notification;

import java.util.List;

/**
 * The notification factory interface
 */
public interface NotificationFactory {
    NotificationService create(List<String> types);
}
