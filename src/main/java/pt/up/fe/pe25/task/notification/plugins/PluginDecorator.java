package pt.up.fe.pe25.task.notification.plugins;

import org.json.JSONException;
import org.json.JSONObject;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;


import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;


public abstract class PluginDecorator implements NotificationService {

        protected final NotificationService notificationService;

        public PluginDecorator(NotificationService notificationService) {
            this.notificationService = notificationService;
        }

        @Override
        public boolean notify(NotificationData notificationData) {
            return notificationService.notify(notificationData);
        }

    /**
     * Sends an HTTP request to the specified URL with the given request body, HTTP method, and headers.
     *
     * @param urlString the URL to send the request to
     * @param requestBody the request body to send (can be null if not applicable)
     * @param httpMethod the HTTP method to use (e.g. "GET", "POST", etc.)
     * @param headers a map of headers to include in the request (can be null if not applicable)
     * @return a JSONObject representing the response from the server
     */
    public JSONObject sendRequest(String urlString, String requestBody, String httpMethod, Map<String, String> headers) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(httpMethod);
            if (headers != null) {
                for (Map.Entry<String, String> entry : headers.entrySet()) {
                    conn.setRequestProperty(entry.getKey(), entry.getValue());
                }
            }
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(requestBody.getBytes());
            os.flush();
            os.close();

            int responseCode = conn.getResponseCode();

            if (responseCode >= 200 && responseCode <= 299) {
                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuilder response = new StringBuilder();
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                System.out.println("Response body: " + response);

                return new JSONObject("{message:" + response.toString() + "}");
            } else {
                System.out.println("Request failed with response code: " + responseCode);
                return null;
            }
        } catch (IOException | JSONException e) {
            System.out.println("Error sending request: " + e.getMessage());
            return null;
        }
    }



}
