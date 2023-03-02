package pt.up.fe.pe25.task.notification.plugins;

import org.json.JSONArray;
import org.json.JSONException;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import java.net.*;
import java.io.*;
import java.util.List;
import org.json.JSONObject;

public class WhatsAppPlugin extends PluginDecorator{

    private static final String PRODUCT_ID = "XXXXXXX";
    private static final String PHONE_ID = "XXXXX";
    private static final String MAYTAPI_KEY = "XXXXXXX";

    public WhatsAppPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    public boolean notify(NotificationData notificationData){
        if (notificationService != null)
            super.notify(notificationData);

        try {
            String group_id = createGroup(notificationData.getTicketId(),notificationData.getReceiverPhone());
            System.out.println("Group id:" + group_id);
            sendMessage(notificationData.getMessage(),group_id);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static String createGroup(String ticketId, List<String> numbers) throws IOException, JSONException {
        String groupName = "TICKET #" + ticketId;
        String requestBody = "{\"name\": \"" + groupName + "\", " +
                "\"numbers\": " + new JSONArray(numbers) + "}";
        JSONObject jsonResponse = sendPostRequest("https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/createGroup", requestBody);
        return jsonResponse.getJSONObject("data").getString("id");
    }

    public static Boolean sendMessage(String message, String group_id) throws IOException, JSONException {
        String requestBody = "{\"to_number\": \"" + group_id + "\"," +
                " \"type\": \"text\", " +
                "\"message\": \"" + message + "\"}";
        sendPostRequest("https://api.maytapi.com/api/" + PRODUCT_ID + "/" + PHONE_ID + "/sendMessage", requestBody);
        return true;
    }

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
