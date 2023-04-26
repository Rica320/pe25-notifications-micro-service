package pt.up.fe.pe25.task.notification.plugins.whatsapp;

import org.json.JSONArray;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONObject;
import javax.annotation.PostConstruct;
import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;

public class WhatsAppPlugin extends PluginDecorator {

    private final String httpMethod = "POST";
    private Map<String, String> headers = new HashMap<>();

    private String PRODUCT_ID;
    private String PHONE_ID;
    private String MAYTAPI_KEY;

    @PostConstruct
    public void initialize() {
        headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("x-maytapi-key",  MAYTAPI_KEY);
    }

    public WhatsAppPlugin set_PRODUCT_ID(String PRODUCT_ID) {
        this.PRODUCT_ID = PRODUCT_ID;
        return this;
    }

    public WhatsAppPlugin set_PHONE_ID(String PHONE_ID) {
        this.PHONE_ID = PHONE_ID;
        return this;
    }

    public WhatsAppPlugin set_MAYTAPI_KEY(String MAYTAPI_KEY) {
        this.MAYTAPI_KEY = MAYTAPI_KEY;
        return this;
    }



    public WhatsAppPlugin(NotificationService notificationService) {
        super(notificationService);
    }


    @Override
    public boolean notify(NotificationData notificationData){
        if (notificationService != null)
            super.notify(notificationData);


        return false;
    }

    /**
     * Creates a new WhatsApp group with the given name and phone numbers.
     *
     * @param groupName the name of the group
     * @param phoneNumbers a list of phone numbers to add to the group
     * @return the ID of the new group if the request is successful ; throws an exception otherwise
     */
    public String createGroup(String groupName, List<String> phoneNumbers) {
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/createGroup";
        String requestBody = "{\"name\": \"" + groupName + "\", " +
                "\"numbers\": " + new JSONArray(phoneNumbers) + "}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        AtomicReference<String> groupIdRef = new AtomicReference<>("");
        processResponse(response, groupIdRef);
        return groupIdRef.get();
    }

    /**
     * Adds or removes a phone number from a WhatsApp group.
     *
     * @param groupId the ID of the group
     * @param phoneNumber the phone number to add or remove
     * @param isAddOperation true to add the phone number, false to remove it
     * @return true if the message was sent successfully; throws an exception otherwise
     */
    public boolean updateGroup(String groupId, String phoneNumber, boolean isAddOperation){
        String endpoint = isAddOperation ? "add" : "remove";
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/group/" + endpoint;
        String requestBody = "{\"conversation_id\": \"" + groupId + "\", " +
                "\"number\": " + phoneNumber + "}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        return processResponse(response);
    }

    /**
     * Used to send a text message to an individual person or group chat.
     *
     * @param text the text message to send
     * @param receiver the phone number or group ID of the message recipient
     * @return true if the message was sent successfully; throws an exception otherwise
     */
    public boolean sendTextMessage(String text, String receiver){
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage";
        String requestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"text\", " +
                "\"message\": \"" + text + "\"}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        return processResponse(response);
    }

    /**
     * Used to send a media message to an individual person or group chat.
     *
     * @param media the Base64-encoded media content to send (e.g., an image or video) or a URL to that media
     * @param caption the optional caption text for the media message
     * @param receiver the phone number or group ID of the message recipient
     * @return true if the message was sent successfully; throws an exception otherwise
     */
    public boolean sendMediaMessage(String media, String caption, String receiver){
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage";
        String requestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"media\", " +
                "\"message\": \"" + media + "\", " +
                "\"text\": \"" + caption + "\"}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        return processResponse(response);
    }

    /**
     * Used to send a location message to an individual person or group chat.
     *
     * @param latitude the latitude of the location in decimal degrees (DD)
     * @param longitude the longitude of the location in decimal degrees (DD)
     * @param locationText the name or description of the location
     * @param receiver the phone number or group ID of the message recipient
     * @return true if the message was sent successfully; throws an exception otherwise
     */
    public boolean sendLocationMessage(String latitude, String longitude, String locationText, String receiver){
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage";
        String requestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"location\", " +
                "\"text\": \"" + locationText + "\", " +
                "\"latitude\": \"" + latitude + "\", " +
                "\"longitude\": \"" + longitude + "\"}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        return processResponse(response);
    }


    /**
     * Used to send a link message to an individual person or group chat.
     *
     * @param link     the link to send
     * @param text     optional text to include with the link
     * @param receiver the phone number or group ID of the message recipient
     * @return true if the message was sent successfully; throws an exception otherwise
     */
    public boolean sendLinkMessage(String link, String text, String receiver) {
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage";
        String requestBody = "{\"to_number\": \"" + receiver + "\"," +
                " \"type\": \"link\", " +
                "\"message\": \"" + link + "\", " +
                "\"text\": \"" + text + "\"}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        return processResponse(response);
    }

    /**
     * Used to process the response from the API.
     * @param response the response from the API
     * @return true if the request was successful; throws an exception otherwise
     */
    private boolean processResponse(JSONObject response) {
        return processResponse(response, null);
    }

    /**
     * Used to process the response from the API.
     * @param response the response from the API
     * @param groupId the ID of the group if the request for creating it is successful
     * @return true if the request was successful; throws an exception otherwise
     */
    private boolean processResponse(JSONObject response, AtomicReference<String> groupId) {
        if (response == null) {
            throw new IllegalArgumentException("Failure - Reason : Response is null.");
        }
        System.out.println(response);
        JSONObject messageJson = response.getJSONObject("message");
        boolean success = messageJson.getBoolean("success");
        if (success) {
            if (groupId != null) {
                groupId.set(messageJson.getJSONObject("data").getString("id"));
            }
            return true;
        } else {
            String message = messageJson.getString("message");
            throw new IllegalArgumentException("Failure - Reason: " + message);
        }
    }




}
