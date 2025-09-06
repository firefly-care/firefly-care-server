package org.farm.fireflyserver.domain.led.service.batch;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;


@Configuration
@AllArgsConstructor
public class LedBatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job ledHistoryJob;

    @Scheduled(cron = "0 0/10 * * * *", zone = "${app.timezone:Asia/Seoul}")
    public void runScheduledLedHistoryJob() throws Exception {

        long now = System.currentTimeMillis();
        jobLauncher.run(
                ledHistoryJob,
                new JobParametersBuilder()
                        .addLong("run.id", now)
                        .addLong("window.now", now)
                        .toJobParameters()
        );
    }

}



