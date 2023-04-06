package pt.up.fe.pe25.unit;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.quarkus.test.junit.QuarkusTest;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.whatsapp.WhatsAppPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
public class WhatsAppTest {

    private WhatsAppPlugin whatsappPlugin;

    @Mock
    private NotificationService notificationService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        whatsappPlugin = new WhatsAppPlugin(notificationService);
        whatsappPlugin.set_MAYTAPI_KEY("test-key");
        whatsappPlugin.set_PHONE_ID("test-phone-id");
        whatsappPlugin.set_PRODUCT_ID("test-product-id");
    }

    @Test
    public void testCreateGroup() {
        String groupName = "test-group";
        List<String> phoneNumbers = Arrays.asList("111111111", "222222222");
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/createGroup";
        String expectedRequestBody = "{\"name\": \"" + groupName + "\", \"numbers\": [\"111111111\",\"222222222\"]}";
        JSONObject expectedResponse = new JSONObject("{\"success\":true,\"data\":{\"id\":\"test-group-id\"}}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        String groupId = spyWhatsAppPlugin.createGroup(groupName, phoneNumbers);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());
        assertTrue(groupId.equals("test-group-id"));
    }

    @Test
    public void testUpdateGroup() {
        String groupId = "test-group-id";
        String phoneNumber = "111111111";
        boolean isAddOperation = true;
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/group/add";
        String expectedRequestBody = "{\"conversation_id\": \"" + groupId + "\", \"number\": " + phoneNumber + "}";
        JSONObject expectedResponse = new JSONObject("{\"success\":true}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        boolean isSuccess = spyWhatsAppPlugin.updateGroup(groupId, phoneNumber, isAddOperation);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());
        assertTrue(isSuccess);
    }

    @Test
    public void testSendTextMessage() {
        String text = "test-message";
        String receiver = "111111111";
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/sendMessage";
        String expectedRequestBody = "{\"to_number\": \"" + receiver + "\", \"type\": \"text\", \"message\": \"" + text + "\"}";
        JSONObject expectedResponse = new JSONObject("{\"success\":true}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        boolean isSuccess = spyWhatsAppPlugin.sendTextMessage(text, receiver);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());
        assertTrue(isSuccess);
    }

    @Test
    public void testSendMediaMessage() {
        String media = "test-media";
        String caption = "test-caption";
        String receiver = "111111111";
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/sendMessage";
        String expectedRequestBody = "{\"to_number\": \"" + receiver + "\", \"type\": \"media\", \"message\": \"" + media + "\", \"text\": \"" + caption + "\"}";
        JSONObject expectedResponse = new JSONObject("{\"success\":true}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        boolean isSuccess = spyWhatsAppPlugin.sendMediaMessage(media, caption, receiver);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());
        assertTrue(isSuccess);
    }

    @Test
    public void testSendLocationMessage() {
        String latitude = "40.7128";
        String longitude = "-74.0060";
        String locationText = "New York City";
        String receiver = "111111111";
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/sendMessage";
        String expectedRequestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"location\", " +
                "\"text\": \"" + locationText + "\", " +
                "\"latitude\": \"" + latitude + "\", " +
                "\"longitude\": \"" + longitude + "\"}";
        JSONObject expectedResponse = new JSONObject("{\"success\":true}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        boolean isSuccess = spyWhatsAppPlugin.sendLocationMessage(latitude, longitude, locationText, receiver);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());
        assertTrue(isSuccess);
    }

    @Test
    public void testSendLinkMessage() {
        String link = "https://www.example.com";
        String text = "Check out this link!";
        String receiver = "111111111";
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/sendMessage";
        String expectedRequestBody = "{\"to_number\": \"" + receiver + "\", \"type\": \"link\", \"message\": \"" + link + "\", \"text\": \"" + text + "\"}";
        JSONObject expectedResponse = new JSONObject("{\"success\":true}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        boolean isSuccess = spyWhatsAppPlugin.sendLinkMessage(link, text, receiver);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());
        assertTrue(isSuccess);
    }

}