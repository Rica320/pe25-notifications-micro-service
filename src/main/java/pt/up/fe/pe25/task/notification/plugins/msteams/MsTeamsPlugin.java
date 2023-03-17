package pt.up.fe.pe25.task.notification.plugins.msteams;

import pt.up.fe.pe25.task.notification.plugins.whatsapp.WhatsAppProperties;
import org.json.JSONArray;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;


import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;


public class MsTeamsPlugin extends PluginDecorator {

    private Map<String, String> headers = new HashMap<>();

    public MsTeamsPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    @PostConstruct
    public void initialize() {
        headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
    }

    @Override
    public boolean notify(NotificationData notificationData){
        if (notificationService != null)
            super.notify(notificationData);

        sendMessages(notificationData);
        return false;
    }

    @Transactional
    public MsTeam addTeam(String url) {
        // TODO check if team already exists
       MsTeam msTeam = new MsTeam(url);
       msTeam.persist();
       return msTeam;
    }

    /**
     * Send a message to all teams.
     * @param notificationData Data to send including a list of teams
     */
    public void sendMessages(NotificationData notificationData) {
        for (Long teamId : notificationData.getTeams()) {
            MsTeam team = MsTeam.findById(teamId);
            if (team == null) throw new IllegalArgumentException("That team does not exist");
            sendMessage(team.getUrl(),
                notificationData.getMessage(),
                notificationData.getTicketId());
        }
    }

    /**
     * Send a message to a ms teams webhook.
     * @param url Url of the webhook (channel)
     * @param message Message to send
     * @param ticketId Id of the ticket
     */
    public void sendMessage(String url, String message, String ticketId) {
        String body = String.format("""
                {
            \"@type\": \"MessageCard\",
            \"@context\": \"http://schema.org/extensions\",
            \"themeColor\": \"0076D7\",
            \"summary\": \"New message\",
            \"sections\": [
                {
                    \"activityTitle\": \"Alert!\",
                    \"activitySubtitle\": \"Service Status Ticket #%s\",
                    \"text\": \"%s\"
                }
            ]
        }""", ticketId, message);
        /*\"activityImage\": \"https://image_url",*/


        if (sendRequest(url, body, "POST", headers) == null)
            throw new IllegalArgumentException("Error sending message");
    }
}
