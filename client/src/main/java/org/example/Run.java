package org.example;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

public class Run {

    public static void job() throws Exception{
        final JobKey jobKey = new JobKey("HelloName", "group1");
        final JobDetail job = JobBuilder.newJob(GetInformationFileJob.class).withIdentity(jobKey).build();

        final Trigger trigger = TriggerBuilder.newTrigger().withIdentity("HelloTriggerName", "group1")
                .withSchedule(CronScheduleBuilder.cronSchedule("0/7 * * * * ?")).build();

        final Scheduler scheduler = new StdSchedulerFactory().getScheduler();

        scheduler.start();
        scheduler.scheduleJob(job, trigger);
    }

    public static void main(String[] args0) {
        args0[0] =
        try {
            FileMonitor.usingFileAlterationMonitor();
//            job();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
