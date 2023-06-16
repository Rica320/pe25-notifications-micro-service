package pt.up.fe.pe25.integration;

import io.quarkus.test.Mock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import pt.up.fe.pe25.task.notification.plugins.msteams.MsTeamsPlugin;
import pt.up.fe.pe25.task.notification.plugins.msteams.MsTeamsResource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.hamcrest.Matchers.equalTo;
import org.hamcrest.Matchers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Produces;
import javax.transaction.Transactional;
import javax.inject.Inject;

@QuarkusTest
public class MsTeamsResourceTest {
    String url = "https://my-channel-webhook-url.com";

    @Mock
    @Produces
    MsTeamsPlugin pluginMock;

    @Inject
    MsTeamsResource resource;

    @BeforeEach
    public void setup() {
        pluginMock = mock(MsTeamsPlugin.class);
        doNothing().when(pluginMock).sendMessage(any(String.class), any(String.class), any(String.class), any(String.class));
        when(pluginMock.addTeam(any(String.class))).thenCallRealMethod();

        resource.setMsTeamsPlugin(pluginMock);
    }

    @Test
    @Transactional
    public void testCreateTeam() {
        RestAssured
            .given()
                .contentType(ContentType.TEXT)
                .body(url)
                .auth().basic("admin", "admin")
            .when()
                .post("/msteams/add")
            .then()
                .statusCode(201)
                .body("url", equalTo(url))
                .body("id", Matchers.any(Integer.class))
                .body("id", equalTo(3));
    }
    
    @Test
    @Transactional
    public void testAddTeamTwice() {
        RestAssured
            .given()
                .contentType(ContentType.TEXT)
                .body(url)
                .auth().basic("admin", "admin")
                .when()
                .post("/msteams/add")
            .then()
                .statusCode(409)
                .body("message", equalTo("That team already exists"))
                .body("id", equalTo(3));
    }

    @Test
    @Transactional
    public void testSendMessage() {
        Map<String, Object>  jsonAsMap = new HashMap<>();
        jsonAsMap.put("message", "test_message");
        jsonAsMap.put("teams", List.of(1));
        jsonAsMap.put("ticketId", "test_ticket_id");

        // When
        RestAssured
            .given()
                .contentType(ContentType.JSON)
                .body(jsonAsMap)
                .auth().basic("admin", "admin")
                .when()
                .post("/msteams/message")
            .then()
                .statusCode(201)
                .body("message", equalTo("test_message"))
                .body("ticketId", equalTo("test_ticket_id"));
        
    }
}

