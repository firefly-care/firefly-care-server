package org.farm.fireflyserver.domain.led.service.batch;

import lombok.AllArgsConstructor;
import org.farm.fireflyserver.domain.led.persistence.repository.LedHistoryRepository;
import org.farm.fireflyserver.domain.led.web.dto.response.LedDataLogDto;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Configuration
@EnableBatchProcessing
@AllArgsConstructor
public class LedBatchConfig {

    private final PlatformTransactionManager transactionManager;
    private final LedHistoryRepository ledHistoryRepository;
    private final JobLauncher jobLauncher;

    // 공톹 시간 Map
    @Bean
    @JobScope
    public Map<String, LocalDateTime> windowTimes(
            @Value("#{jobParameters['window.now']}") Long nowEpochMillis,
            @Value("${app.timezone:Asia/Seoul}") String appTimezone
    ) {
        LocalDateTime now = Instant.ofEpochMilli(nowEpochMillis)
                .atZone(ZoneId.of(appTimezone))
                .toLocalDateTime();

        Map<String, LocalDateTime> map = new HashMap<>();
        map.put("now", now);
        map.put("windowStart", now.minusMinutes(10));
        return map;
    }

    // 최신 10분 ON 로그를 저장하는 Map 결과 누적용 (Processor ↔ Listener 공유)
    @Bean
    @JobScope
    public Map<String, LedDataLogDto> latestMap() {
        return new HashMap<>();
    }

    @Bean
    @StepScope
    public LedItemProcessor ledItemProcessor(Map<String, LedDataLogDto> latestMap) {
        return new LedItemProcessor(latestMap);
    }

    @Bean
    @JobScope
    public LedJobListener ledJobListener(Map<String, LedDataLogDto> latestMap, Map<String, LocalDateTime> windowTimes) {
        return new LedJobListener(ledHistoryRepository, latestMap, windowTimes);
    }


    // 실행(now) 기준 10분 윈도우를 계산하는 Reader
    @Bean
    @StepScope
    public JdbcCursorItemReader<LedDataLogDto> ledItemReader(
            @Qualifier("ledDataSource") DataSource ledDataSource,
            Map<String, LocalDateTime> windowTimes
    ) {
        return LedItemReader.reader(ledDataSource, windowTimes);
    }


    @Bean
    @StepScope
    public LedItemWriter ledItemWriter(
            LedHistoryRepository ledHistoryRepository,
            Map<String, LocalDateTime> windowTimes
    ) {
        return new LedItemWriter(ledHistoryRepository, windowTimes);
    }

    // LED 데이터 히스토리 저장 배치 Job
    // ledDataSource : 실제 LED 센서 로그 받아오는 DB
    @Bean
    public Job ledHistoryJob(JobRepository jobRepository,
                             LedJobListener ledJobListener,
                             Step ledHistoryStep) {
        return new JobBuilder("ledHistoryJob", jobRepository)
                .listener(ledJobListener)
                .start(ledHistoryStep)
                .build();
    }

    // LED 데이터 히스토리 저장 배치 Step
    // 1. reader에서 10분 이내 LED 센서 로그를 읽기
    // 2. processor에서 각 LED별로 가장 최신 로그만 남기기
    // 3. writer에서 최신 ON 이벤트를 LED 히스토리에 저장 & 업데이트
    // 4. jobListener에서 ON 상태였는데 processor(최신 10분 ON 로그)에 없으면 OFF 이벤트 추가
    @Bean
    public Step ledHistoryStep(JobRepository jobRepository,
                               LedItemProcessor ledItemProcessor,
                               JdbcCursorItemReader<LedDataLogDto> ledItemReader,
                               LedItemWriter ledItemWriter) {
        return new StepBuilder("ledHistoryStep", jobRepository)
                .<LedDataLogDto, Map<String, LedDataLogDto>>chunk(500, transactionManager)
                .reader(ledItemReader)
                .processor(ledItemProcessor)
                .writer(ledItemWriter)
                .build();
    }


    //바로 실행용
    @Bean
    public CommandLineRunner runLedHistoryJob(Job ledHistoryJob) {
        return args -> {
            System.out.println("[Batch] ledHistoryJob started at " + LocalDateTime.now());

            long now = System.currentTimeMillis();
            jobLauncher.run(
                    ledHistoryJob,
                    new JobParametersBuilder()
                            .addString("run.id", UUID.randomUUID().toString())
                            .addLong("window.now", now)
                            .toJobParameters()
            );
            System.out.println("[Batch] ledHistoryJob finished at " + LocalDateTime.now());
        };
    }



}
