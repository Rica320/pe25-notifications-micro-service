package pt.up.fe.pe25.task.notification;

import pt.up.fe.pe25.task.notification.plugins.WhatsAppPlugin;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class NotificationFactoryImpl implements NotificationFactory{

        @Override
        public NotificationService create(List<String> types) throws IllegalArgumentException{
            NotificationService notificationService = null;
            for (String s : types) {
                switch (s) {
                    case "whatsapp" -> notificationService = new WhatsAppPlugin(notificationService);
                    default -> {
                    }
                }
            }

            if (notificationService == null)
                throw new IllegalArgumentException("No valid notification type was provided");

            return notificationService;
        }
}
