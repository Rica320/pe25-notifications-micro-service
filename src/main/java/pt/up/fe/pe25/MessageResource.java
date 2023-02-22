/**
 * This is a REST endpoint that allows to send messages.
 */
package pt.up.fe.pe25;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import io.github.cdimascio.dotenv.Dotenv;

@Path("/message")
public class MessageResource {
    // Helper function to convert a Map to a URL-encoded query string
    private static String getQuery(Map<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }
            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }
        return result.toString();
    }

    public String sendRequest(String text) {
        // Set up the URL and the request parameters
        Dotenv dotenv = Dotenv.load(); // load .env file

        URL url;
        try {
            String phoneId = dotenv.get("WHATSAPP_PHONE_ID");
            String accessToken = dotenv.get("WHATSAPP_TOKEN");
            String testingNumber = dotenv.get("TEST_PHONE_NUMBER");

            url = new URL("https://graph.facebook.com/v15.0/" + phoneId + "/messages");

            Map<String, String> postData = new HashMap<>();
            postData.put("messaging_product", "whatsapp");
            postData.put("to", "");
            postData.put("type", "template");
            postData.put("template[name]", "testing");
            postData.put("template[language][code]", "pt_PT");

            // Set up the HttpURLConnection and configure the request method, content type,
            // and headers
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "Bearer " + accessToken);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setDoOutput(true);

            // Write the request parameters to the output stream and close it
            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(postData));
            writer.flush();
            writer.close();
            os.close();

            // Read the response from the server and print it to the console
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();
            return response.toString();
        } catch (Exception e) {
            // todo auto-generated catch block
            return e.getMessage();
        }
    }

    @POST
    @Produces(MediaType.TEXT_PLAIN)
    public String message(@FormParam("text") String text) {
        return sendRequest(text);
    }

}