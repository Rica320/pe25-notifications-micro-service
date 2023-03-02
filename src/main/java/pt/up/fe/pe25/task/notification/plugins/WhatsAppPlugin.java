package pt.up.fe.pe25.task.notification.plugins;

import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import java.net.*;
import java.io.*;

public class WhatsAppPlugin extends PluginDecorator{

    public WhatsAppPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    @Override
    public boolean notify(NotificationData notificationData) {
        if (notificationService != null)
            super.notify(notificationData);
        System.out.println("Sending WhatsApp notification");
        try {
            String product_id = "5ea95684-b279-4cc9-aba9-872a5bdf4a52";
            String phone_id = "27088";
            String maytapiKey = "e1a4c1bf-90ca-43c5-8563-86ea773455dd";

            URL url = new URL("https://api.maytapi.com/api/" + product_id + "/" + phone_id + "/sendMessage");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("x-maytapi-key", maytapiKey);
            conn.setDoOutput(true);

            String to_number = notificationData.getReceiverPhone();
            String message = notificationData.getMessage();

            String requestBody = "{\"to_number\": \"" + to_number + "\"," +
                    " \"type\": \"text\", " +
                    "\"message\": \"" + message + "\"}";

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
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
