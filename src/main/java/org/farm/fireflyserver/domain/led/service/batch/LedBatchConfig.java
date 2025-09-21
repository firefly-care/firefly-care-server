package org.farm.fireflyserver.domain.led.service.batch;

import lombok.AllArgsConstructor;
import org.farm.fireflyserver.domain.led.persistence.repository.LedHistoryRepository;
import org.farm.fireflyserver.domain.led.persistence.repository.LedStateRepository;
import org.farm.fireflyserver.domain.led.web.dto.response.LedDataLogDto;
import org.springframework.batch.core.Job;
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

    // 공통 시간 Map
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
    public LedJobListener ledJobListener(
            LedHistoryRepository ledHistoryRepository,
            LedStateRepository ledStateRepository,
            Map<String, LedDataLogDto> latestMap,
            Map<String, LocalDateTime> windowTimes
    ) {
        return new LedJobListener(ledHistoryRepository, ledStateRepository, latestMap, windowTimes);
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
            LedStateRepository ledStateRepository,
            Map<String, LocalDateTime> windowTimes
    ) {
        return new LedItemWriter(ledHistoryRepository, ledStateRepository, windowTimes);
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

    /*
      LED 데이터 히스토리 저장 배치 Step
      1) Reader: 실행 시각 기준 최근 10분 이내의 LED 센서 로그 조회
      2) Processor: 센서별 최신 로그 1건만 남김
      3) Writer: LedState(현재 상태)에 따라
                    - OFF → ON : LedHistory에 ON 이벤트 추가 + LedState를 ON으로 전환
                    - ON 유지 시: LedState의 updatedAt만 갱신 (히스토리 추가 없음)
      4) Listener : 직전 상태가 ON인데 이번 윈도우에 로그가 없으면 LedHistory에 OFF 이벤트 추가 + LedState를 OFF로 전환
     */
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

    /*
    //바로 실행용
    @Bean
    public CommandLineRunner runLedHistoryJob(Job ledHistoryJob) {
        return args -> {
            System.out.println("[Batch] ledHistoryJob started at " + LocalDateTime.now());

            long now = System.currentTimeMillis();
            jobLauncher.run(:q
                    ledHistoryJob,
                    new JobParametersBuilder()
                            .addString("run.id", UUID.randomUUID().toString())
                            .addLong("window.now", now)
                            .toJobParameters()
            );
            System.out.println("[Batch] ledHistoryJob finished at " + LocalDateTime.now());
        };
    }
    */

}
