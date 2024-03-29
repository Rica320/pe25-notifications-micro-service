package pt.up.fe.pe25.unit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;
import pt.up.fe.pe25.task.notification.NotificationData;
import pt.up.fe.pe25.task.notification.plugins.msteams.MsTeam;
import pt.up.fe.pe25.task.notification.plugins.msteams.MsTeamsPlugin;

@QuarkusTest
public class MsTeamsTest {
    @Transactional
    @Test
    public void testAddTeam() {
        MsTeamsPlugin msTeamsPlugin = new MsTeamsPlugin(null);

        msTeamsPlugin.addTeam("test_add");
        List<MsTeam> teams = MsTeam.listAll();
        MsTeam team = teams.get(teams.size() - 1);
        assertEquals("test_add", team.getUrl());
    } 

    @Test
    @Transactional
    public void testAddTeamTwice() {
        MsTeamsPlugin msTeamsPlugin = new MsTeamsPlugin(null);

        try {
            msTeamsPlugin.addTeam("test");
        } catch (IllegalArgumentException e) {
            assertEquals("That team already exists", e.getMessage());
        }
    }

    @Test
    @Transactional
    public void testSendMessages() {
        MsTeamsPlugin msTeamsPlugin = mock(MsTeamsPlugin.class);
        doNothing().when(msTeamsPlugin).sendMessage(any(String.class), any(String.class), any(String.class), any(String.class));
        when(msTeamsPlugin.addTeam(any(String.class))).thenCallRealMethod();
        doCallRealMethod().when(msTeamsPlugin).sendMessages(any(NotificationData.class));

        msTeamsPlugin.addTeam("test_message");

        NotificationData notificationData = new NotificationData();
        notificationData.setMessage("test");
        notificationData.setTicketId("test");
        List<MsTeam> teams = MsTeam.listAll();
        List<Long> idTeams = new ArrayList<>();
        teams.forEach(team -> {idTeams.add(team.getId());});
        notificationData.setReceiverTeams(idTeams);
        msTeamsPlugin.sendMessages(notificationData);

        verify(msTeamsPlugin, times(1)).sendMessage("test_message", "test", "test", null);
    }


}
