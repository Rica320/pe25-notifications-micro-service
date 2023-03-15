package pt.up.fe.pe25.task.notification;

import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import io.quarkus.scheduler.Scheduled;

@Provider
@ApplicationScoped
public class NotificationRateLimitingFilter implements ContainerRequestFilter {

    private static final int MAX_REQUESTS_PER_MINUTE = 1;

    private final ConcurrentHashMap<String, Integer> requestCounts = new ConcurrentHashMap<>();

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String ip = requestContext.getHeaderString("X-Real-IP");
        if (ip == null) {
            ip = requestContext.getHeaderString("X-Forwarded-For");
        }
        if (ip == null) {
            ip = requestContext.getUriInfo().getRequestUri().getHost();
        }

        int count = requestCounts.getOrDefault(ip, 0);
        if (count >= MAX_REQUESTS_PER_MINUTE) {
            requestContext.abortWith(Response.status(Response.Status.TOO_MANY_REQUESTS).build());
        } else {
            requestCounts.put(ip, count + 1);
        }
    }

    @Scheduled(every = "1m")
    void resetCounts() {
        requestCounts.clear();
    }
}