package pt.up.fe.pe25.task.notification.plugins.smtp;

import io.quarkus.mailer.Attachment;
import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;

import javax.inject.Inject;
import java.io.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * A plugin that sends notifications by email<br>
 * You can configure the SMTP server in application.properties<br>
 * The email can contain a message, a subject and a list of receivers<br>
 *
 * @see NotificationService
 * @see PluginDecorator
 * @see Mail
 * @see ReactiveMailer
 * @see Template
 * @see NotificationData
 */
public class MailPlugin extends PluginDecorator {

    /**
     * The mailer
     */
    ReactiveMailer mailer;

    /**
     * The template to be used in the email
     */
    Template template;

    /**
     * Creates a new MailPlugin
     * @param notificationService the notification service
     * @param mailer the mailer
     * @param template the template
     */
    @Inject
    public MailPlugin(NotificationService notificationService, ReactiveMailer mailer, Template template) {
        super(notificationService);
        this.mailer = mailer;
        this.template = template;
    }

    public MailPlugin() {
        super(null);
    }

    /**
     * Gets the attachment from the base64 string
     * @param base64
     * @return
     * @throws IOException
     */
    private Attachment getAttachment(String base64) throws IOException {
        byte[] fileData = Base64.getDecoder().decode(base64);

        File tempFile = File.createTempFile("attachment", ".pdf");
        try (OutputStream os = new FileOutputStream(tempFile)){
            os.write(fileData);
        }

        return new Attachment("attachment.pdf", tempFile, "application/pdf");
    }

    /**
     * Adds the attachments to the list of attachments
     * @param attachments the list of attachments
     * @param base64s the list of base64 strings
     */
    private void addAttachments(List<Attachment> attachments, List<String> base64s) {
        for (String base64 : base64s) {
            try {
                attachments.add(getAttachment(base64));
            } catch (IOException ignored) {

            }
        }
    }

    /**
     * Sends a notification by email ... calling the wrapped notification service before
     * @param notificationData the notification data
     * @return true if the notification was sent successfully, false otherwise
     */
    @Override
    public boolean notify(NotificationData notificationData) {
        if (notificationService != null)
            super.notify(notificationData);

        List<Attachment> attachments = new ArrayList<>();

        if (notificationData.getAttachments() != null)
            addAttachments(attachments, notificationData.getAttachments());


        boolean valid = true;
        for (String email : notificationData.getReceiverEmails()) {

            if (!email.matches("^[a-z0-9]+@[a-z]+\\.[a-z]{2,3}")) {
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
                valid = false;
            }
        }

        return valid;
    }
}
