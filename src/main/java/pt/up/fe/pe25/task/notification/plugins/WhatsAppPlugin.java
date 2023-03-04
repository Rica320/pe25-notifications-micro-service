package pt.up.fe.pe25.task.notification.plugins;

import org.json.JSONArray;
import org.json.JSONException;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import java.net.*;
import java.io.*;
import java.util.List;
import org.json.JSONObject;
import io.github.cdimascio.dotenv.Dotenv;

public class WhatsAppPlugin extends PluginDecorator{

    private static final Dotenv dotenv = Dotenv.load();
    private static final String PRODUCT_ID = dotenv.get("PRODUCT_ID");
    private static final String PHONE_ID = dotenv.get("PHONE_ID");
    private static final String MAYTAPI_KEY = dotenv.get("MAYTAPI_KEY");


    public WhatsAppPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    public boolean notify(NotificationData notificationData){
        if (notificationService != null)
            super.notify(notificationData);

        try {
            JSONObject jsonResponse = sendLinkMessage("https://latitude.to/articles-by-country/pt/portugal/8013/estadio-do-dragao",
                    "Estádio do Dragão","XXXXXXXXXXX");
            if(jsonResponse.getBoolean("success"))
                System.out.println("successful operation");
            else System.out.println(jsonResponse.getString("message"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Creates a new WhatsApp group with the given name and phone numbers.
     *
     * @param groupName the name of the group
     * @param phoneNumbers a list of phone numbers to add to the group
     * @return a JSONObject representing the request's response
     * @throws IOException if there is an error sending the request
     * @throws JSONException if there is an error parsing the JSON response
     */
    public static JSONObject createGroup(String groupName, List<String> phoneNumbers) throws IOException, JSONException {
        String requestBody = "{\"name\": \"" + groupName + "\", " +
                "\"numbers\": " + new JSONArray(phoneNumbers) + "}";
        return sendPostRequest("https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/createGroup", requestBody);
    }

    /**
     * Adds or removes a phone number from a WhatsApp group.
     *
     * @param groupId the ID of the group
     * @param phoneNumber the phone number to add or remove
     * @param isAddOperation true to add the phone number, false to remove it
     * @return a JSONObject representing the request's response
     * @throws IOException if there is an error sending the request
     * @throws JSONException if there is an error parsing the JSON response
     */
    public static JSONObject updateGroup(String groupId, String phoneNumber, boolean isAddOperation) throws IOException, JSONException {
        String endpoint = isAddOperation ? "add" : "remove";
        String requestBody = "{\"conversation_id\": \"" + groupId + "\", " +
                "\"number\": " + phoneNumber + "}";
        return sendPostRequest("https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/group/" + endpoint, requestBody);
    }


    /**
     * Used to send a text message to an individual person or group chat.
     *
     * @param message the text message to send
     * @param receiver the phone number or group ID of the message recipient
     * @return a JSONObject representing the request's response
     * @throws IOException if there is an error sending the request
     * @throws JSONException if there is an error parsing the JSON response
     */
    public static JSONObject sendTextMessage(String message, String receiver) throws IOException, JSONException {
        String requestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"text\", " +
                "\"message\": \"" + message + "\"}";
        return sendPostRequest("https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage", requestBody);
    }

    /**
     * Used to send a media message to an individual person or group chat.
     *
     * @param media the Base64-encoded media content to send (e.g., an image or video) or a URL to that media
     * @param caption the optional caption text for the media message
     * @param receiver the phone number or group ID of the message recipient
     * @return a JSONObject representing the request's response
     * @throws IOException if there is an error sending the request
     * @throws JSONException if there is an error parsing the JSON response
     */
    public static JSONObject sendMediaMessage(String media, String caption, String receiver) throws IOException, JSONException {
        String requestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"media\", " +
                "\"message\": \"" + media + "\", " +
                "\"text\": \"" + caption + "\"}";
        return sendPostRequest("https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage", requestBody);
    }

    /**
     * Used to send a location message to an individual person or group chat.
     *
     * @param latitude the latitude of the location in decimal degrees (DD)
     * @param longitude the longitude of the location in decimal degrees (DD)
     * @param locationText the name or description of the location
     * @param receiver the phone number or group ID of the message recipient
     * @return a JSONObject representing the request's response
     * @throws IOException if there is an error sending the request
     * @throws JSONException if there is an error parsing the JSON response
     */
    public static JSONObject sendLocationMessage(String latitude, String longitude, String locationText, String receiver) throws IOException, JSONException {
        String requestBody = "{\"to_number\": \"" + receiver + "\", " +
                "\"type\": \"location\", " +
                "\"text\": \"" + locationText + "\", " +
                "\"latitude\": \"" + latitude + "\", " +
                "\"longitude\": \"" + longitude + "\"}";
        return sendPostRequest("https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage", requestBody);
    }

    /**
     * Used to send a link message to an individual person or group chat.
     *
     * @param url the link to send
     * @param text optional text to include with the link
     * @param receiver the phone number or group ID of the message recipient
     * @return a JSONObject representing the request's response
     * @throws IOException if there is an error sending the request
     * @throws JSONException if there is an error parsing the JSON response
     */
    public static JSONObject sendLinkMessage(String url, String text, String receiver) throws IOException, JSONException {
        String requestBody = "{\"to_number\": \"" + receiver + "\"," +
                " \"type\": \"link\", " +
                "\"message\": \"" + url + "\", " +
                "\"text\": \"" + text + "\"}";
        return sendPostRequest("https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage", requestBody);
    }




    /**
     * Sends an HTTP POST request to the specified URL with the given request body.
     *
     * @param urlString the URL to send the request to
     * @param requestBody the request body to send
     * @return a JSONObject representing the response from the server
     * @throws IOException if there is an error sending or receiving the request
     * @throws JSONException if there is an error parsing the JSON response
     */
    public static JSONObject sendPostRequest(String urlString, String requestBody) throws IOException, JSONException {
        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setRequestProperty("x-maytapi-key", MAYTAPI_KEY);
        conn.setDoOutput(true);

        OutputStream os = conn.getOutputStream();
        os.write(requestBody.getBytes());
        os.flush();
        os.close();

        int responseCode = conn.getResponseCode();
        System.out.println("Response code: " + responseCode);

        InputStream is = conn.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuilder response = new StringBuilder();
        while ((line = br.readLine()) != null) {
            response.append(line);
        }
        br.close();

        System.out.println("Response body: " + response);

        return new JSONObject(response.toString());
    }
}
