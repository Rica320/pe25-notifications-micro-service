package pt.up.fe.pe25.task.notification.plugins.msteams;

import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.NotificationService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.transaction.Transactional;

import pt.up.fe.pe25.task.notification.plugins.PluginDecorator;

/**
 * A plugin that sends notifications to Microsoft Teams.<br>
 * It uses a webhook to send messages to a team.<br>
 * To use, first add a team to the database with the webhook and then send messages with the given id.
 *
 * @see NotificationService
 * @see PluginDecorator
 * @see MsTeam
 * @see NotificationData
 */
public class MsTeamsPlugin extends PluginDecorator {

    private Map<String, String> headers = new HashMap<>();

    public MsTeamsPlugin(NotificationService notificationService) {
        super(notificationService);
    }

    public MsTeamsPlugin() {
        super(null);
    }

    @PostConstruct
    public void initialize() {
        headers = new HashMap<>();
        headers.put("Content-Type", "text/plain");
    }

    /**
     * Sends a message to a list of teams
     * @param notificationData the notification data
     * @return success or failure on sending the message
     */
    @Override
    public boolean notify(NotificationData notificationData){
        if (notificationService != null)
            super.notify(notificationData);

        sendMessages(notificationData);
        return true;
    }

    /**
     * Creates a new team and adds it to the database.
     * @param url The webhook url
     * @return The team
     */
    @Transactional
    public MsTeam addTeam(String url) {
        if (MsTeam.count("url", url) > 0)
            throw new IllegalArgumentException("That team already exists");
        MsTeam msTeam = new MsTeam(url);
        msTeam.persist();
        return msTeam;
    }

    /**
     * Send a message to all teams, specified in the notification data.
     * @param notificationData Data to send including a list of teams
     */
    public void sendMessages(NotificationData notificationData) {
        List<Long> errors = new ArrayList<>();
        if (notificationData.getReceiverTeams() == null) throw new IllegalArgumentException("No teams specified");
        if (notificationData.getMessage() == null) throw new IllegalArgumentException("No message specified");
        for (Long teamId : notificationData.getReceiverTeams()) {
            MsTeam team = MsTeam.findById(teamId);
            if (team == null) {
                errors.add(teamId);
                continue;
            }
            sendMessage(team.getUrl(),
                notificationData.getMessage(),
                notificationData.getTicketId(),
                notificationData.getTemplate());
        }
        if (errors.size() > 0) throw new IllegalArgumentException("Those teams do not exist: " + errors.toString());
    }

    /**
     * Send a message to a ms teams webhook.
     * @param url Url of the webhook (channel)
     * @param message Message to send
     * @param ticketId Id of the ticket
     */
    public void sendMessage(String url, String message, String ticketId, String template) {
        String templateText;
        if (template == null) {
            templateText = "";
        }
        else if (template.equals("ticket")) {
            templateText = String.format("""
                    \"activityTitle\": \"Alert!\",
                    \"activitySubtitle\": \"Service Status Ticket #%s\",
                """, ticketId);
        }
        else {
            templateText = "";
        }

        String body = String.format("""
                {
            \"@type\": \"MessageCard\",
            \"@context\": \"http://schema.org/extensions\",
            \"themeColor\": \"0076D7\",
            \"summary\": \"New message\",
            \"sections\": [
                {
                    %s
                    \"text\": \"%s\"
                }
            ]
        }""", templateText, message);
        /*\"activityImage\": \"https://image_url",*/


        if (sendRequest(url, body, "POST", headers) == null)
            throw new IllegalArgumentException("Error sending message");
    }
}
