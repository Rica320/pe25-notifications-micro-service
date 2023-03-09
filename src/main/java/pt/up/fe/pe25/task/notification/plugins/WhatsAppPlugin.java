package pt.up.fe.pe25.task.notification.plugins;

import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import java.net.*;
import java.io.*;

public class WhatsAppPlugin extends PluginDecorator{

    public WhatsAppPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    public boolean notify(NotificationData notificationData) {
        if (notificationService != null)
            super.notify(notificationData);

        return false;
    }
}
