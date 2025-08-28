package org.farm.fireflyserver.domain.led.service.batch;

import lombok.AllArgsConstructor;
import org.farm.fireflyserver.domain.led.persistence.repository.LedHistoryRepository;
import org.farm.fireflyserver.domain.led.web.dto.response.LedDataLogDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class LedBatchConfig {

    private final PlatformTransactionManager transactionManager;
    private final LedHistoryRepository ledHistoryRepository;
    private final JobLauncher jobLauncher;

    // LED 데이터 저장 배치 잡
    @Bean
    public Job ledHistoryJob(JobRepository jobRepository,
                             @Qualifier("ledDataSource") DataSource ledDataSource) {
        return new JobBuilder("ledHistoryJob", jobRepository)
                .start(ledHistoryStep(jobRepository, ledDataSource))
                .build();
    }

    @Bean
    public Step ledHistoryStep(JobRepository jobRepository, DataSource ledDataSource) {
        return new StepBuilder("ledHistoryStep", jobRepository)
                .<LedDataLogDto, Map<String, LedDataLogDto>>chunk(500, transactionManager)
                .reader(LedItemReader.reader(ledDataSource))
                .processor(new LedItemProcessor())
                .writer(new LedItemWriter(ledHistoryRepository))
                .build();
    }


    // 10분마다 실행
    //@Scheduled(cron = "0 */10 * * * *")
    /*
    public void runScheduledLedHistoryJob() throws Exception {
        System.out.println("[Scheduled Batch] ledHistoryJob started at " + LocalDateTime.now());
        jobLauncher.run(
                ledHistoryJob(null, null),
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters()
        );
        System.out.println("[Scheduled Batch] ledHistoryJob finished at " + LocalDateTime.now());
    }
    */

    /* 바로 실행
    @Bean
    public CommandLineRunner runLedHistoryJob(Job ledHistoryJob) {
        return args -> {
            System.out.println("[Batch] ledHistoryJob started at " + LocalDateTime.now());
            jobLauncher.run(
                    ledHistoryJob,
                    new JobParametersBuilder()
                            .addLong("time", System.currentTimeMillis())
                            .toJobParameters()
            );
            System.out.println("[Batch] ledHistoryJob finished at " + LocalDateTime.now());
        };
    }
    */
}
