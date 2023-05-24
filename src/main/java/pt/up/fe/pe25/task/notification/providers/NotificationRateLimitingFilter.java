package pt.up.fe.pe25.task.notification.providers;

import java.util.concurrent.ConcurrentHashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import com.oracle.svm.core.annotate.Inject;
import io.quarkus.scheduler.Scheduled;
import org.eclipse.microprofile.config.inject.ConfigProperty;

/**
 * A filter that limits the number of requests per minute
 * <p>
 *     This filter is used to limit the number of requests per minute to the notification service.
 *     <br>
 *     The maximum number of requests per minute can be configured in the configuration file.
 *     <br>
 * </p>
 *
 */
@Provider
@ApplicationScoped
public class NotificationRateLimitingFilter implements ContainerRequestFilter {

    /**
     * The maximum number of requests per minute
     */
    @Inject
    @ConfigProperty(name = "pt.fe.up.pe25.max_requests_per_minute", defaultValue = "10")
    int MAX_REQUESTS_PER_MINUTE;

    /**
     * The request counts per client
     */
    private final ConcurrentHashMap<String, Integer> requestCounts = new ConcurrentHashMap<>();

    /**
     * Filters the request to check if the number of requests per minute is exceeded
     * @param requestContext the request context
     */
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

    /**
     * Resets the request counts every minute
     */
    @Scheduled(every = "1m")
    void resetCounts() {
        requestCounts.clear();
    }
}