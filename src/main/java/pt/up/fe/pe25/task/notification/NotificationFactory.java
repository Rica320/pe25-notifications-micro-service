package pt.up.fe.pe25.task.notification;

import java.util.List;

public interface NotificationFactory {
    NotificationService create(List<String> types);
}
