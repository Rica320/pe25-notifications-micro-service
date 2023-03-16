package pt.up.fe.pe25.task.notification.plugins;

import org.json.JSONArray;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;


import com.oracle.svm.core.annotate.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;

public class WhatsAppPlugin extends PluginDecorator{

    @Inject
    @ConfigProperty(name = "pt.fe.up.pe25.whatsapp.product_id")
    String PRODUCT_ID;

    @Inject
    @ConfigProperty(name = "pt.fe.up.pe25.whatsapp.phone_id")
    String PHONE_ID;

    @Inject
    @ConfigProperty(name = "pt.fe.up.pe25.whatsapp.maytapi_key")
    String MAYTAPI_KEY;

    private final String httpMethod = "POST";
    private final Map<String, String> headers = new HashMap<>();
    {{
        headers.put("Content-Type", "application/json");
        headers.put("x-maytapi-key", MAYTAPI_KEY);
    }}


    public WhatsAppPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    public boolean notify(NotificationData notificationData){
        if (notificationService != null)
            super.notify(notificationData);

        //print class fields
        System.out.println("PRODUCT_ID: " + PRODUCT_ID);
        System.out.println("PHONE_ID: " + PHONE_ID);
        System.out.println("MAYTAPI_KEY: " + MAYTAPI_KEY);

        sendLinkMessage("https://latitude.to/articles-by-country/pt/portugal/8013/estadio-do-dragao",
                "Estádio do Dragão","XXXXXXXXXXX");

        return false;
    }

    /**
     * Creates a new WhatsApp group with the given name and phone numbers.
     *
     * @param groupName the name of the group
     * @param phoneNumbers a list of phone numbers to add to the group
     * @return the ID of the new group if the request is successful, false otherwise
     */
    public Object createGroup(String groupName, List<String> phoneNumbers) {
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/createGroup";
        String requestBody = "{\"name\": \"" + groupName + "\", " +
                "\"numbers\": " + new JSONArray(phoneNumbers) + "}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        if (response == null) {
            System.out.println("Failed to send message. Response is null.");
            return false;
        }
        boolean success = response.getBoolean("success");
        if (success) {
            return response.getJSONObject("data").getString("id");
        } else {
            String message = response.getString("message");
            System.out.println("Failed to create group. Reason: " + message);
            return false;
        }
    }

    /**
     * Adds or removes a phone number from a WhatsApp group.
     *
     * @param groupId the ID of the group
     * @param phoneNumber the phone number to add or remove
     * @param isAddOperation true to add the phone number, false to remove it
     * @return true if the message was sent successfully; false otherwise
     */
    public  boolean updateGroup(String groupId, String phoneNumber, boolean isAddOperation){
        String endpoint = isAddOperation ? "add" : "remove";
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/group/" + endpoint;
        String requestBody = "{\"conversation_id\": \"" + groupId + "\", " +
                "\"number\": " + phoneNumber + "}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        if (response == null) {
            System.out.println("Failed to send message. Response is null.");
            return false;
        }
        boolean success = response.getBoolean("success");
        if (success) {
            System.out.println("Group member updated successfully");
            return true;
        } else {
            String message = response.getString("message");
            System.out.println("Failed to update group member. Reason: " + message);
            return false;
        }
    }


    /**
     * Used to send a text message to an individual person or group chat.
     *
     * @param text the text message to send
     * @param receiver the phone number or group ID of the message recipient
     * @return true if the message was sent successfully; false otherwise
     */
    public boolean sendTextMessage(String text, String receiver){
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage";
        String requestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"text\", " +
                "\"message\": \"" + text + "\"}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        if (response == null) {
            System.out.println("Failed to send message. Response is null.");
            return false;
        }
        boolean success = response.getBoolean("success");
        if (success) {
            System.out.println("Message sent successfully");
            return true;
        } else {
            String message = response.getString("message");
            System.out.println("Failed to send message. Reason: " + message);
            return false;
        }
    }

    /**
     * Used to send a media message to an individual person or group chat.
     *
     * @param media the Base64-encoded media content to send (e.g., an image or video) or a URL to that media
     * @param caption the optional caption text for the media message
     * @param receiver the phone number or group ID of the message recipient
     * @return true if the message was sent successfully; false otherwise
     */
    public boolean sendMediaMessage(String media, String caption, String receiver){
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage";
        String requestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"media\", " +
                "\"message\": \"" + media + "\", " +
                "\"text\": \"" + caption + "\"}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        if (response == null) {
            System.out.println("Failed to send message. Response is null.");
            return false;
        }
        boolean success = response.getBoolean("success");
        if (success) {
            System.out.println("Message sent successfully");
            return true;
        } else {
            String message = response.getString("message");
            System.out.println("Failed to send message. Reason: " + message);
            return false;
        }
    }

    /**
     * Used to send a location message to an individual person or group chat.
     *
     * @param latitude the latitude of the location in decimal degrees (DD)
     * @param longitude the longitude of the location in decimal degrees (DD)
     * @param locationText the name or description of the location
     * @param receiver the phone number or group ID of the message recipient
     * @return true if the message was sent successfully; false otherwise
     */
    public boolean sendLocationMessage(String latitude, String longitude, String locationText, String receiver){
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage";
        String requestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"location\", " +
                "\"text\": \"" + locationText + "\", " +
                "\"latitude\": \"" + latitude + "\", " +
                "\"longitude\": \"" + longitude + "\"}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        if (response == null) {
            System.out.println("Failed to send message. Response is null.");
            return false;
        }
        boolean success = response.getBoolean("success");
        if (success) {
            System.out.println("Message sent successfully");
            return true;
        } else {
            String message = response.getString("message");
            System.out.println("Failed to send message. Reason: " + message);
            return false;
        }
    }


    /**
     * Used to send a link message to an individual person or group chat.
     *
     * @param link     the link to send
     * @param text     optional text to include with the link
     * @param receiver the phone number or group ID of the message recipient
     * @return true if the message was sent successfully; false otherwise
     */
    public boolean sendLinkMessage(String link, String text, String receiver) {
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage";
        String requestBody = "{\"to_number\": \"" + receiver + "\"," +
                " \"type\": \"link\", " +
                "\"message\": \"" + link + "\", " +
                "\"text\": \"" + text + "\"}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        if (response == null) {
            System.out.println("Failed to send message. Response is null.");
            return false;
        }
        boolean success = response.getBoolean("success");
        if (success) {
            System.out.println("Message sent successfully");
            return true;
        } else {
            String message = response.getString("message");
            System.out.println("Failed to send message. Reason: " + message);
            return false;
        }
    }

}
