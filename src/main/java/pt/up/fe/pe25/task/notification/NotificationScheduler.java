package pt.up.fe.pe25.task.notification;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.ZoneId;
import java.util.Date;

/**
 * The Notification Scheduler
 * <p>
 *     This class is used to schedule the notifications
 *     <br>
 *     The notifications are scheduled using the Quartz library
 *     <br>
 *     The notifications are scheduled based on the date to send
 *     <br>
 *     The notifications are scheduled using the NotificationJob class
 *     <br>
 *     The NotificationJob class is responsible for sending the notifications
 *     <br>
 * </p>
 */
@ApplicationScoped
public class NotificationScheduler {

    /**
     * Schedules a notification
     * @param data the notification data
     * @param service the notification service
     * @throws SchedulerException if the notification could not be scheduled
     */
    public void scheduleNotification(NotificationData data, NotificationService service) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("notificationData", data);
        jobDataMap.put("notificationService", service);
        JobDetail job = JobBuilder.newJob(NotificationJob.class)
                .withIdentity("notificationJob" + data.hashCode(), "notificationGroup")
                .usingJobData(jobDataMap)
                .build();

        Date triggerDate = Date.from(data.getDateToSend().atZone(ZoneId.systemDefault()).toInstant());
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("notificationTrigger" + data.hashCode(), "notificationGroup")
                .startAt(triggerDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }

    /**
     * The notification job
     * Executes the notification service after the notification is triggered
     */
    public static class NotificationJob implements Job {

        /**
         * The default constructor
         */
        public NotificationJob() {
        }

        /**
         * Executes the notification service
         * @param context the job context
         */
        public void execute(JobExecutionContext context) {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            NotificationData data = (NotificationData) jobDataMap.get("notificationData");
            NotificationService service = (NotificationService) jobDataMap.get("notificationService");

            service.notify(data);
        }
    }
}
