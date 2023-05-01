package pt.up.fe.pe25.task.notification;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.transaction.Transactional;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

import org.quartz.SchedulerException;
import pt.up.fe.pe25.authentication.User;

/**
 * The notifier resource
 * <p>
 *     This class is used to create a new notifier
 *     <br>
 *     The notifier is created by sending a POST request to the /notifier endpoint
 *     <br>
 *     The notifier is created by sending a JSON object with the following structure:
 *     <pre>
 *         {
 *         "notificationServices": ["email", ...],
 *         "notificationData": {
 *              "subject": "Notification subject",
 *              "message": "Notification message",
 *              "dateToSend": "2023-04-06T00:32:50",
 *              ...
 *         }, ...
 *        }
 *      </pre>
 *
 *      <br>
 *      The notificationServices field is an array of strings that the factory will use to create the notification plugins
 *      <br>
 *      The notificationData field is an object that contains the data that will be used by the notification plugins
 *      <br>
 *      The dateToSend field is optional and is used to schedule the notification
 *      <br>
 *      The notificationData object can contain any field that is required by the notification plugins
 *
 *
 * @see NotificationFactory
 * @see NotificationScheduler
 * @see NotificationData
 */
@Path("/notifier")
public class NotifierResource {

    /**
     * The notification factory
     */
    @Inject
    NotificationFactory notificationFactory;

    /**
     * The notification scheduler
     */
    @Inject
    NotificationScheduler notificationScheduler;

    /**
     * Creates a new notifier
     * Needs authentication to be called
     *
     * @param securityContext the security context
     * @param notifier the notifier
     * @return Response to the request
     */
    @POST
    @RolesAllowed({"user"})
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
    public Response createNotifier(@Context SecurityContext securityContext, Notifier notifier) {

        notifier.user = User.findByUsername(securityContext.getUserPrincipal().getName());
        notifier.persist();

        // TODO: VER QUESTAO DO THROW ... se nao houver plugin válido, nao faz nada mas guarda ticket
        // TODO: talvez seja melhor fazer um throw e nao guardar o ticket
        try {

            if (notifier.getNotificationData().getDateToSend() == null)
                notificationFactory.create(notifier.getNotificationServices()).notify(notifier.getNotificationData());
            else
                notificationScheduler.scheduleNotification(notifier.getNotificationData(),
                        notificationFactory.create(notifier.getNotificationServices()));

        }
        catch (IllegalArgumentException | SchedulerException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        }

        return Response.status(Response.Status.CREATED).entity(notifier).build();
    }

    /**
     * Gets all the notifiers of the user
     * Needs authentication to be called
     *
     * @param securityContext the security context
     * @return Response to the request
     */
    @GET
    @RolesAllowed({"user"})
    @Produces(MediaType.APPLICATION_JSON)
    public Response getNotifiers(@Context SecurityContext securityContext) {
        User user = User.findByUsername(securityContext.getUserPrincipal().getName());
        return Response.ok(Notifier.findByUser(user)).build();
    }
}

