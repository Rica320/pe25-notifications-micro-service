package pt.up.fe.pe25.task.notification.plugins.smtp;

import com.sun.istack.ByteArrayDataSource;
import io.quarkus.mailer.Attachment;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import lombok.SneakyThrows;
import org.reactivestreams.Publisher;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.sql.DataSource;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@RequestScoped
public class MailPlugin extends PluginDecorator {

    ReactiveMailer mailer;

    Template template;

    @Inject
    public MailPlugin(NotificationService notificationService, ReactiveMailer mailer, Template template) {
        super(notificationService);
        this.mailer = mailer;
        this.template = template;
    }

    public MailPlugin() {
        super(null);
    }

    private Attachment getAttachment(String base64) throws IOException {
        byte[] fileData = Base64.getDecoder().decode(base64);

        File tempFile = File.createTempFile("attachment", ".pdf");
        try (OutputStream os = new FileOutputStream(tempFile)){
            os.write(fileData);
        }

        return new Attachment("attachment.pdf", tempFile, "application/pdf");
    }

    private void addAttachments(List<Attachment> attachments, List<String> base64s) {
        for (String base64 : base64s) {
            try {
                attachments.add(getAttachment(base64));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean notify(NotificationData notificationData) {
        if (notificationService != null)
            super.notify(notificationData);

        List<Attachment> attachments = new ArrayList<>();
        System.out.println(notificationData.getAttachments());
        if (notificationData.getAttachments() != null)
            addAttachments(attachments, notificationData.getAttachments());


        boolean valid = true;
        for (String email : notificationData.getReceiverEmails()) {
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                valid = false;
                continue;
            }
            try {
                TemplateInstance templateInstance = template.data("message", notificationData.getMessage());

                Mail mail = Mail.withHtml(email, notificationData.getSubject(), templateInstance.render());

                if (!attachments.isEmpty())
                    mail.setAttachments(attachments);

                mail.setSubject(notificationData.getSubject());


                Uni<Void> x = mailer.send(mail);

                x.subscribe().with(
                        item -> System.out.println("Sent"),
                        failure -> System.out.println("Failed to send email: " + failure.getMessage())
                );
            } catch (Exception e) {
                valid = false; // TODO: dps temos de arranjar maneira de devolver isto ao resource para que ele diga quais estão indisponíveis
            }
        }

        return valid;
    }
}
