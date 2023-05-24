package pt.up.fe.pe25.task.notification.plugins.smtp.template.factory;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import pt.up.fe.pe25.task.notification.NotificationData;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class TemplateFactoryImpl implements TemplateFactory {

    /**
     * The template instance to be injected into the mail plugin
     */
    @Inject
    Template template;

    @Inject
    Template defaultTemplate;

    @Override
    public TemplateInstance create(String id, String message) {
        if (id.equals("1")) {
            return  template.data("message", message);
        }

        return defaultTemplate.data("message", message);
    }

}
