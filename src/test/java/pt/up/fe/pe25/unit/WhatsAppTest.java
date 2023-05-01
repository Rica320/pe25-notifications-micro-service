package pt.up.fe.pe25.unit;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.quarkus.test.junit.QuarkusTest;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.whatsapp.WhatsAppGroup;
import pt.up.fe.pe25.task.notification.plugins.whatsapp.WhatsAppPlugin;

import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class WhatsAppTest {

    private WhatsAppPlugin whatsappPlugin;

    private WhatsAppGroup whatsappGroup;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        whatsappPlugin = new WhatsAppPlugin(notificationService);
        whatsappPlugin.set_MAYTAPI_KEY("test-key");
        whatsappPlugin.set_PHONE_ID("test-phone-id");
        whatsappPlugin.set_PRODUCT_ID("test-product-id");
        whatsappGroup = new WhatsAppGroup("test-group", "test-group-id");
    }

    @Test
    @Transactional
    public void testCreateGroup() {
        NotificationData notificationData = new NotificationData();
        notificationData.setPhoneList(Arrays.asList("111111111", "222222222"));
        notificationData.setGroupName("test-group");
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/createGroup";
        String expectedRequestBody = "{\"name\": \"" + "test-group" + "\", \"numbers\": [\"111111111\",\"222222222\"]}";
        JSONObject expectedResponse = new JSONObject("{\"message\": {\"success\":true,\"data\":{\"id\":\"test-group-id\"}}}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        WhatsAppGroup wppGroup = spyWhatsAppPlugin.createGroup(notificationData);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());
        assertEquals("test-group-id", wppGroup.getGroupId());
    }
}