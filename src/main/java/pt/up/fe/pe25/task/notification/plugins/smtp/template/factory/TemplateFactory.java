package pt.up.fe.pe25.task.notification.plugins.smtp.template.factory;

import io.quarkus.qute.TemplateInstance;

public interface TemplateFactory {
    TemplateInstance create(String id, String message);
}
