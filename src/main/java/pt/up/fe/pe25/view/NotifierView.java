package pt.up.fe.pe25.view;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple frontend view for the notification service
 * You can view the template in the resources/templates/view.html file
 * This is a simple view used to demonstrate the notification service
 *
 * @see Template
 * @see TemplateInstance
 */
@Path("/view")
public class NotifierView {
    @Inject
    Template view;

    @GET
    @Produces(MediaType.TEXT_HTML)
    public Response notifier() {
        List<String> notificationServices = new ArrayList<>();
        notificationServices.add("email");
        notificationServices.add("sms");
        notificationServices.add("whatsapp");
        notificationServices.add("tCall");
        view.data("service", notificationServices);

        TemplateInstance instance = view.instance();
        String renderedHtml = instance.render();
        return Response.ok(renderedHtml).build();
    }
}
