package org.farm.fireflyserver.domain.led.service.batch;

import lombok.AllArgsConstructor;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.UUID;


@Configuration
@AllArgsConstructor
public class LedBatchScheduler {
    private final JobLauncher jobLauncher;
    private final Job ledHistoryJob;

    //임시 : 5분 스케줄링
    @Scheduled(cron = "0 */5 * * * *", zone = "${app.timezone:Asia/Seoul}")
    public void runScheduledLedHistoryJob() throws Exception {
        long now = System.currentTimeMillis();
        jobLauncher.run(
                ledHistoryJob,
                new JobParametersBuilder()
                        .addString("run.id", UUID.randomUUID().toString()) //job 식별자
                        .addLong("window.now", now) // 배치 시간
                        .toJobParameters()
        );
    }

}



