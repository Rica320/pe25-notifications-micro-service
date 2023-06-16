package pt.up.fe.pe25.unit;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import io.quarkus.test.junit.QuarkusTest;
import org.mockito.internal.matchers.Not;
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

        try {
            WhatsAppGroup wppGroup = spyWhatsAppPlugin.createGroup(notificationData);
            verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                    eq("POST"), anyMap());
            assertEquals("test-group-id", wppGroup.getGroupId());
        }
        catch (Exception e) {
            assertTrue(e.getMessage().contains("That group already exists"));
        }
    }

    @Test
    public void testUpdateGroup() {
        try {
            testCreateGroup();
        } catch (Exception ignored) {

        }
        String groupId = "test-group-id";
        String phoneNumber = "111111111";
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/group/add";
        String expectedRequestBody = "{\"conversation_id\": \"" + groupId + "\", \"number\": " + phoneNumber + "}";
        JSONObject expectedResponse = new JSONObject("{\"message\": {\"success\": true}}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        NotificationData notificationData = new NotificationData();
        notificationData.setPhoneList(Arrays.asList(phoneNumber));
        notificationData.setReceiverGroup(3L);

        spyWhatsAppPlugin.updateGroup(notificationData, true);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());
    }

    @Test
    public void testUpdateGroupNotExists() {

        String groupId = "test-group-id";
        String phoneNumber = "111111111";
        String expectedUrl = "https://api.maytapi.com/api/test-product-id/test-phone-id/group/add";
        String expectedRequestBody = "{\"conversation_id\": \"" + groupId + "\", \"number\": " + phoneNumber + "}";
        JSONObject expectedResponse = new JSONObject("{\"message\": {\"success\": true}}");

        WhatsAppPlugin spyWhatsAppPlugin = spy(whatsappPlugin);
        doReturn(expectedResponse).when(spyWhatsAppPlugin).sendRequest(eq(expectedUrl), eq(expectedRequestBody),
                eq("POST"), anyMap());

        NotificationData notificationData = new NotificationData();
        notificationData.setPhoneList(Arrays.asList(phoneNumber));
        notificationData.setReceiverGroup(3L);

        try {
            spyWhatsAppPlugin.updateGroup(notificationData, true);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            assertTrue(e.getMessage().contains("That group does not exist"));
        }
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

        NotificationData notificationData = new NotificationData();
        notificationData.setPhoneList(List.of(receiver));
        notificationData.setMessage(text);

        spyWhatsAppPlugin.sendTextMessage(notificationData);

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

        NotificationData notificationData = new NotificationData();
        notificationData.setMedia(media);
        notificationData.setMessage(caption);
        notificationData.setPhoneList(List.of(receiver));

        spyWhatsAppPlugin.sendMediaMessage(notificationData);

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

        NotificationData notificationData = new NotificationData();
        notificationData.setLatitude(latitude);
        notificationData.setLongitude(longitude);
        notificationData.setMessage(locationText);
        notificationData.setPhoneList(List.of(receiver));

        spyWhatsAppPlugin.sendLocationMessage(notificationData);

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

        NotificationData notificationData = new NotificationData();
        notificationData.setLink(link);
        notificationData.setMessage(text);
        notificationData.setPhoneList(List.of(receiver));

        spyWhatsAppPlugin.sendLinkMessage(notificationData);

        verify(spyWhatsAppPlugin, times(1)).sendRequest(eq(expectedUrl), eq(expectedRequestBody), eq("POST"), anyMap());

    }

}