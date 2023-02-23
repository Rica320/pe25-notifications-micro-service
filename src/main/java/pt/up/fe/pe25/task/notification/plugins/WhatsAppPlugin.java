package pt.up.fe.pe25.task.notification.plugins;

import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;

public class WhatsAppPlugin extends PluginDecorator{

    public WhatsAppPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    public boolean notify(NotificationData notificationData) {
        super.notify(notificationData);
        // TODO
        return false;
    }
}
