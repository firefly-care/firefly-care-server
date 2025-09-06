package org.farm.fireflyserver.domain.led.service.batch;

import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;
import org.farm.fireflyserver.domain.led.web.dto.response.LedDataLogDto;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

// 10분 이내 LED 센서 로그를 읽어오는 ItemReader
public class LedItemReader {

    public static JdbcCursorItemReader<LedDataLogDto> reader(DataSource ledDataSource, Map<String, LocalDateTime> windowTimes) {

        Timestamp start = Timestamp.valueOf(windowTimes.get("windowStart"));
        Timestamp now = Timestamp.valueOf(windowTimes.get("now"));

        return new JdbcCursorItemReaderBuilder<LedDataLogDto>()
                .name("ledSensorLogReader")
                .dataSource(ledDataSource)
                .sql("SELECT LED_MTCHN_SN, LED_SENSOR_GBN, REG_DT " +
                        "FROM t_led_sensor_log " +
                        "WHERE REG_DT >= ? AND REG_DT < ? " +
                        "ORDER BY REG_DT DESC")
                .queryArguments(start, now)
                .rowMapper((rs, rowNum) -> {
                    String ledMtchnSn = rs.getString("LED_MTCHN_SN");
                    SensorGbn sensorGbn = SensorGbn.fromCode(rs.getString("LED_SENSOR_GBN"));
                    LocalDateTime eventTime = rs.getTimestamp("REG_DT").toLocalDateTime();
                    return new LedDataLogDto(ledMtchnSn, sensorGbn, eventTime,null);
                }
                )
                .build();
    }
}
