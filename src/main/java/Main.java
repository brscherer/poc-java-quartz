import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Main {
    public static void main(String[] args) throws SchedulerException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        JobDetail job = JobBuilder.newJob(MyJob.class)
                .withIdentity("myJob", "group1")
                .usingJobData("jobSays", "Hello World!")
                .usingJobData("myFloatValue", 3.141f)
                .build();

        JobDetail job2 = JobBuilder.newJob(MyJob.class)
                .withIdentity("myJob2", "group1")
                .usingJobData("jobSays", "Cron Job Works")
                .usingJobData("myFloatValue", 99.99f)
                .build();

        // SimpleTrigger is used when neeed to execute job at a specific moment in time
        Trigger simpleTrigger = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger", "group1")
                .startNow()
                .withSchedule(SimpleScheduleBuilder.simpleSchedule()
                        .withIntervalInSeconds(10) // Execute every 10 seconds
                        .repeatForever())
                .build();

        // CronTrigger is used when we need schedules based on calendar-like statements
        // examples: every other friday, last day on the month at 9:30 am
        CronTrigger cronTrigger = TriggerBuilder.newTrigger()
                .withIdentity("myTrigger2", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("* * * * * ?"))
                .forJob("myJob2", "group1")
                .build();

        scheduler.scheduleJob(job, simpleTrigger);
        scheduler.scheduleJob(job2, cronTrigger);

        scheduler.start();
    }
}
