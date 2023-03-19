package pt.up.fe.pe25.task.notification.plugins;

import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;

public abstract class PluginDecorator implements NotificationService {

        protected final NotificationService notificationService;

        public PluginDecorator(NotificationService notificationService) {
            this.notificationService = notificationService;
        }

        @Override
        public boolean notify(NotificationData notificationData) {
            return notificationService.notify(notificationData);
        }
}
