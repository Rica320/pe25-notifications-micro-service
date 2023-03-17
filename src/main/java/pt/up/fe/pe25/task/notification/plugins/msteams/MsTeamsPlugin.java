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

    // TODO notifier
    public MsTeamsPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    @PostConstruct
    public void initialize() {
        headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
    }

    /*@Override
    public boolean notify(NotificationData notificationData){
        if (notificationService != null)
            super.notify(notificationData);

        // TODO call message
        return false;
    }*/

    @Transactional
    public MsTeam addTeam(String url) {
        // TODO check if team already exists
       MsTeam msTeam = new MsTeam(url);
       msTeam.persist();
       return msTeam;
    }

    public String sendMessages(NotificationData notificationData) {
        for (Long teamId : notificationData.getTeams()) {
            MsTeam team = MsTeam.findById(teamId);
            if (team == null) return "That team does not exist";
            String status = sendMessage(team.getUrl(),
                notificationData.getMessage(),
                notificationData.getTicketId());
            if (status != "") return status;
        }
        return "";
    }

    public String sendMessage(String url, String message, String ticketId) {
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
            return "Error sending message";
        return "";
    }
}
