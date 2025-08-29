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
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class LedBatchConfig {

    private final PlatformTransactionManager transactionManager;
    private final LedHistoryRepository ledHistoryRepository;
    private final JobLauncher jobLauncher;
    private final ApplicationContext context;

    // 최신 10분 ON 로그를 저장하는 Map 결과 누적용
    @Bean
    public Map<String, LedDataLogDto> latestMap() {
        return new HashMap<>();
    }

    @Bean
    public LedItemProcessor ledItemProcessor(Map<String, LedDataLogDto> latestMap) {
        return new LedItemProcessor(latestMap);
    }

    @Bean
    public LedJobListener ledJobListener(Map<String, LedDataLogDto> latestMap) {
        return new LedJobListener(ledHistoryRepository, latestMap);
    }

    // LED 데이터 히스토리 저장 배치 Job
    // ledDataSource : 실제 LED 센서 로그 받아오는 DB
    @Bean
    public Job ledHistoryJob(JobRepository jobRepository,
                             @Qualifier("ledDataSource") DataSource ledDataSource,
                             LedJobListener ledJobListener,
                             LedItemProcessor ledItemProcessor) {
        return new JobBuilder("ledHistoryJob", jobRepository)
                .listener(ledJobListener)
                .start(ledHistoryStep(jobRepository, ledDataSource, ledItemProcessor))
                .build();
    }


    // LED 데이터 히스토리 저장 배치 Step
    // 1. reader에서 10분 이내 LED 센서 로그를 읽기
    // 2. processor에서 각 LED별로 가장 최신 로그만 남기기
    // 3. writer에서 최신 ON 이벤트를 LED 히스토리에 저장 & 업데이트
    // 4. jobListener에서 ON 상태였는데 processor(최신 10분 ON 로그)에 없으면 OFF 이벤트 추가
    @Bean
    public Step ledHistoryStep(JobRepository jobRepository,
                               DataSource ledDataSource,
                               LedItemProcessor ledItemProcessor) {
        return new StepBuilder("ledHistoryStep", jobRepository)
                .<LedDataLogDto, Map<String, LedDataLogDto>>chunk(500, transactionManager)
                .reader(LedItemReader.reader(ledDataSource))
                .processor(ledItemProcessor)
                .writer(new LedItemWriter(ledHistoryRepository))
                .build();
    }

    @Scheduled(cron = "0 0/10 * * * *")
    public void runScheduledLedHistoryJob() throws Exception {
        System.out.println("[Scheduled Batch] ledHistoryJob started at " + LocalDateTime.now());

        Map<String, LedDataLogDto> latestMap = context.getBean("latestMap", Map.class);
        latestMap.clear();

        Job ledHistoryJob = context.getBean("ledHistoryJob", Job.class);

        jobLauncher.run(
                ledHistoryJob,
                new JobParametersBuilder()
                        .addLong("time", System.currentTimeMillis())
                        .toJobParameters()
        );
        System.out.println("[Scheduled Batch] ledHistoryJob finished at " + LocalDateTime.now());
    }


   /*
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
