/**
 * This is a simple REST endpoint that returns a greeting.
 */
package pt.up.fe.pe25;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/hello")
public class GreetingResource {

    /**
     * This method returns a greeting.
     * @return a greeting.
     */
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }
}
