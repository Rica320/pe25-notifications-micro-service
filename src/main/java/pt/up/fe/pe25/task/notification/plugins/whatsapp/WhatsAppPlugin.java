package pt.up.fe.pe25.task.notification.plugins.whatsapp;

import org.eclipse.microprofile.config.ConfigProvider;
import org.json.JSONArray;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.json.JSONObject;
import javax.transaction.Transactional;

import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;

public class WhatsAppPlugin extends PluginDecorator {

    private final String httpMethod = "POST";
    private Map<String, String> headers = new HashMap<>();

    private String PRODUCT_ID;
    private String PHONE_ID;
    private String MAYTAPI_KEY;

    public void set_PRODUCT_ID(String PRODUCT_ID) {
        this.PRODUCT_ID = PRODUCT_ID;
    }

    public void set_PHONE_ID(String PHONE_ID) {
        this.PHONE_ID = PHONE_ID;
    }

    public void set_MAYTAPI_KEY(String MAYTAPI_KEY) {
        this.MAYTAPI_KEY = MAYTAPI_KEY;
    }

    public String getPRODUCT_ID() {
        return PRODUCT_ID;
    }

    public String getPHONE_ID() {
        return PHONE_ID;
    }

    public String getMAYTAPI_KEY() {
        return MAYTAPI_KEY;
    }

    public WhatsAppPlugin(NotificationService notificationService) {
        super(notificationService);
        this.PHONE_ID = ConfigProvider.getConfig().getValue("pt.fe.up.pe25.whatsapp.phone_id", String.class);
        this.PRODUCT_ID = ConfigProvider.getConfig().getValue("pt.fe.up.pe25.whatsapp.product_id", String.class);
        this.MAYTAPI_KEY = ConfigProvider.getConfig().getValue("pt.fe.up.pe25.whatsapp.maytapi_key", String.class);
        headers.put("Content-Type", "application/json");
        headers.put("x-maytapi-key",  MAYTAPI_KEY);
    }


    @Override
    public boolean notify(NotificationData notificationData){
        if (notificationService != null)
            super.notify(notificationData);

        return true;
    }

    /**
     * Creates a new WhatsApp group with the given name and phone numbers.
     * @param notificationData the notification data
     * @return the WhatsApp group; throws an exception if the group already exists
     */
    @Transactional
    public WhatsAppGroup createGroup(NotificationData notificationData) {
        String groupName = notificationData.getGroupName();
        List<String> phoneList = notificationData.getPhoneList();

        if (groupName == null || phoneList == null || phoneList.isEmpty())
            throw new IllegalArgumentException("Group name and phone number list must be given");

        if (WhatsAppGroup.count("name", groupName) > 0)
            throw new IllegalArgumentException("That group already exists");

        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/createGroup";
        String requestBody = "{\"name\": \"" + groupName + "\", " +
                "\"numbers\": " + new JSONArray(phoneList) + "}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        AtomicReference<String> groupIdRef = new AtomicReference<>("");
        groupIdRef.set(groupName.toLowerCase(Locale.ROOT));
        //processResponse(response, groupIdRef);
        WhatsAppGroup wppGroup = new WhatsAppGroup(groupName, groupIdRef.get());
        wppGroup.persist();
        return wppGroup;
    }

    /**
     * Adds or removes a phone number from a WhatsApp group.
     * @param notificationData the notification data
     *
     */
    public void updateGroup(NotificationData notificationData, boolean isAddOperation){
        String groupName = notificationData.getGroupName();
        List<String> phoneList = notificationData.getPhoneList();
        if (groupName == null || phoneList == null || phoneList.isEmpty())
            throw new IllegalArgumentException("Group name and phone list must be given");

        WhatsAppGroup wppGroup = WhatsAppGroup.find("name", groupName).firstResult();

        if (wppGroup == null)
            throw new IllegalArgumentException("That group does not exist");

        String endpoint = isAddOperation ? "add" : "remove";
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/group/" + endpoint;

        for (String phoneNumber : phoneList) {
            String requestBody = "{\"conversation_id\": \"" + wppGroup.getGroupId() + "\", " +
                    "\"number\": " + phoneNumber + "}";
            System.out.println(requestBody);
            JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
            processResponse(response,null);
        }

    }

    /**
     * Used to send a text message to an individual person or group chat.
     *
     * @param text the text message to send
     * @param phoneNr the phone number of the message recipient (if sending to an individual)
     * @param groupName the name of the group (if sending to a group)
     */
    public void sendTextMessage(String text, String phoneNr, String groupName){

        // Throw exceptions if any of the conditions are not met
        if (text == null) {
            throw new IllegalArgumentException("Message text must be given");
        }
        // phoneNr and groupName xor
        if ((phoneNr == null) == (groupName == null)) {
            throw new IllegalArgumentException("Either only one phone number or only one group name must be given");
        }
        if (text.length() > 4096) {
            // Message text must be less than 4096 characters
            throw new IllegalArgumentException("Message text must be less than 4096 characters");
        }

        String receiver;
        if (phoneNr != null) {
            receiver = phoneNr;
        } else {
            WhatsAppGroup wppGroup = WhatsAppGroup.find("name", groupName).firstResult();
            if (wppGroup == null)
                throw new IllegalArgumentException("That group does not exist");
            receiver = wppGroup.getGroupId();
        }

        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage";
        String requestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"text\", " +
                "\"message\": \"" + text + "\"}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        processResponse(response,null);
    }

    /**
     * Used to send a media message to an individual person or group chat.
     *
     * @param media the Base64-encoded media content to send (e.g., an image or video) or a URL to that media
     * @param caption the optional caption text for the media message
     * @param receiver the phone number or group ID of the message recipient
     */
    public void sendMediaMessage(String media, String caption, String receiver){
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage";
        String requestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"media\", " +
                "\"message\": \"" + media + "\", " +
                "\"text\": \"" + caption + "\"}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        processResponse(response,null);
    }

    /**
     * Used to send a location message to an individual person or group chat.
     *
     * @param latitude the latitude of the location in decimal degrees (DD)
     * @param longitude the longitude of the location in decimal degrees (DD)
     * @param locationText the name or description of the location
     * @param receiver the phone number or group ID of the message recipient
     */
    public void sendLocationMessage(String latitude, String longitude, String locationText, String receiver){
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage";
        String requestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"location\", " +
                "\"text\": \"" + locationText + "\", " +
                "\"latitude\": \"" + latitude + "\", " +
                "\"longitude\": \"" + longitude + "\"}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        processResponse(response,null);
    }


    /**
     * Used to send a link message to an individual person or group chat.
     *
     * @param link     the link to send
     * @param text     optional text to include with the link
     * @param receiver the phone number or group ID of the message recipient
     */
    public void sendLinkMessage(String link, String text, String receiver) {
        String url = "https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage";
        String requestBody = "{\"to_number\": \"" + receiver + "\"," +
                " \"type\": \"link\", " +
                "\"message\": \"" + link + "\", " +
                "\"text\": \"" + text + "\"}";
        JSONObject response = sendRequest(url, requestBody, httpMethod, headers);
        processResponse(response,null);
    }

    /**
     * Used to process the response from the API.
     * @param response the response from the API
     * @param groupId the ID of the group if the request for creating it is successful
     */
    private void processResponse(JSONObject response, AtomicReference<String> groupId) {
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
        } else {
            String message = messageJson.getString("message");
            throw new IllegalArgumentException("Failure - Reason: " + message);
        }
    }




}
