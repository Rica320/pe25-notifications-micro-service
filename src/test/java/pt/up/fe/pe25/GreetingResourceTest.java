package pt.up.fe.pe25;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("Hello RESTEasy"));
    }

    @Test
    public void testHelloEndpoint2() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(400)
             .body(is("Hello RESTEas444y"));
    }
}