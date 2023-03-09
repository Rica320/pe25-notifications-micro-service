package pt.up.fe.pe25.task.notification;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import javax.enterprise.context.ApplicationScoped;
import java.time.ZoneId;
import java.util.Date;

@ApplicationScoped
public class NotificationScheduler {

    public void scheduleNotification(NotificationData data, NotificationService service) throws SchedulerException {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("notificationData", data);
        jobDataMap.put("notificationService", service);
        JobDetail job = JobBuilder.newJob(NotificationJob.class)
                .withIdentity("notificationJob" + data.hashCode(), "notificationGroup") // TODO: aqui o grupo podia ser o id do utilizador/produto
                .usingJobData(jobDataMap)
                .build();

        Date triggerDate = Date.from(data.getDateToSend().atZone(ZoneId.systemDefault()).toInstant());
        Trigger trigger = TriggerBuilder.newTrigger()
                .withIdentity("notificationTrigger" + data.hashCode(), "notificationGroup") // TODO: ver isto tb
                .startAt(triggerDate)
                .withSchedule(SimpleScheduleBuilder.simpleSchedule().withMisfireHandlingInstructionFireNow())
                .build();

        Scheduler scheduler = new StdSchedulerFactory().getScheduler();
        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }

    public static class NotificationJob implements Job {

        public NotificationJob() {
        }

        public void execute(JobExecutionContext context) {
            JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
            NotificationData data = (NotificationData) jobDataMap.get("notificationData");
            NotificationService service = (NotificationService) jobDataMap.get("notificationService");

            service.notify(data);
        }
    }
}
