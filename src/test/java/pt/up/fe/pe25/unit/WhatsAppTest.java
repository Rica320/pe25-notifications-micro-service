package pt.up.fe.pe25.unit;

import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import io.quarkus.test.junit.QuarkusTest;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import pt.up.fe.pe25.task.notification.plugins.whatsapp.WhatsAppGroup;
import pt.up.fe.pe25.task.notification.plugins.whatsapp.WhatsAppPlugin;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    /*
    @Test
    public void testUpdateGroup() {
        NotificationData notificationData = new NotificationData();
        notificationData.setPhoneList(Arrays.asList("111111111"));
        notificationData.setGroupName("test-group");
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/group/add";
        String expectedRequestBody = "{\"conversation_id\": \"" + groupId + "\", \"number\": " + phoneNumber + "}";
        JSONObject expectedResponse = new JSONObject("{\"message\": {\"success\": true}}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        spyWhatsAppPlugin.updateGroup(groupId, phoneNumber, true);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());
    }

    @Test
    public void testSendTextMessage() {
        String text = "test-message";
        String receiver = "111111111";
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/sendMessage";
        String expectedRequestBody = "{\"to_number\": \"" + receiver + "\", \"type\": \"text\", \"message\": \"" + text + "\"}";
        JSONObject expectedResponse = new JSONObject("{\"message\": {\"success\": true}}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        spyWhatsAppPlugin.sendTextMessage(text, receiver, null);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());

    }

    @Test
    public void testSendMediaMessage() {
        String media = "test-media";
        String caption = "test-caption";
        String receiver = "111111111";
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/sendMessage";
        String expectedRequestBody = "{\"to_number\": \"" + receiver + "\", \"type\": \"media\", \"message\": \"" + media + "\", \"text\": \"" + caption + "\"}";
        JSONObject expectedResponse = new JSONObject("{\"message\": {\"success\": true}}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        spyWhatsAppPlugin.sendMediaMessage(media, caption, receiver);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());
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
        JSONObject expectedResponse = new JSONObject("{\"message\": {\"success\": true}}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        spyWhatsAppPlugin.sendLocationMessage(latitude, longitude, locationText, receiver);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());
    }

    @Test
    public void testSendLinkMessage() {
        String link = "https://www.example.com";
        String text = "Check out this link!";
        String receiver = "111111111";
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/sendMessage";
        String expectedRequestBody = "{\"to_number\": \"" + receiver + "\", \"type\": \"link\", \"message\": \"" + link + "\", \"text\": \"" + text + "\"}";
        JSONObject expectedResponse = new JSONObject("{\"message\": {\"success\": true}}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        spyWhatsAppPlugin.sendLinkMessage(link, text, receiver);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());
    }
    */
}