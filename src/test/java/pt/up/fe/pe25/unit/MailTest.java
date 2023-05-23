package pt.up.fe.pe25.unit;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.smtp.MailPlugin;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class MailTest {

    @Mock
    private NotificationService notificationService;

    @Mock
    private ReactiveMailer mailer;

    @Mock
    private Template template;

    private MailPlugin mailPlugin;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        mailPlugin = new MailPlugin(notificationService, mailer, template);
    }

    @Test
    public void testSendMail() {
        // Arrange
        NotificationData notificationData = new NotificationData();
        notificationData.setReceiverEmails(Collections.singletonList("test@test.com"));
        notificationData.setSubject("Test subject");
        notificationData.setMessage("Test message");

        TemplateInstance templateInstance = mock(TemplateInstance.class);
        when(template.data(anyString(), anyString())).thenReturn(templateInstance);
        when(templateInstance.render()).thenReturn("Test message");

        when(mailer.send(any(Mail.class))).thenReturn(Uni.createFrom().voidItem());

        // Act
        boolean result = mailPlugin.notify(notificationData);

        // Assert
        assertTrue(result);

        ArgumentCaptor<Mail> mailCaptor = ArgumentCaptor.forClass(Mail.class);
        verify(mailer).send(mailCaptor.capture());

        Mail mail = mailCaptor.getValue();
        assertEquals(Collections.singletonList("test@test.com"), mail.getTo());
        assertEquals("Test subject", mail.getSubject());
        assertEquals("Test message", mail.getHtml());
        verify(notificationService, times(1)).notify(notificationData);
    }

    @Test
    public void testTemplateFailure() {
        // Arrange
        NotificationData notificationData = new NotificationData();
        notificationData.setReceiverEmails(Collections.singletonList("test@test.com"));
        notificationData.setSubject("Test subject");
        notificationData.setMessage("Test message");

        TemplateInstance templateInstance = mock(TemplateInstance.class);
        when(template.data(anyString(), anyString())).thenReturn(templateInstance);
        when(templateInstance.render()).thenThrow(new RuntimeException());

        when(mailer.send(any(Mail.class))).thenReturn(Uni.createFrom().voidItem());

        // Act
        boolean result = mailPlugin.notify(notificationData);

        // Assert
        assertFalse(result);
    }

    @Test
    public void testMailerFailure() {
        // Arrange
        NotificationData notificationData = new NotificationData();
        notificationData.setReceiverEmails(Collections.singletonList("test@@test.com"));
        notificationData.setSubject("Test subject");
        notificationData.setMessage("Test message");

        TemplateInstance templateInstance = mock(TemplateInstance.class);
        when(template.data(anyString(), anyString())).thenReturn(templateInstance);
        when(templateInstance.render()).thenReturn("Test message");

        Uni<Void> failedUni = Uni.createFrom().failure(new Exception("Test exception"));
        when(mailer.send(any(Mail.class))).thenReturn(failedUni);

        // Act
        boolean result = mailPlugin.notify(notificationData);

        // Assert
        assertFalse(result);
    }
}