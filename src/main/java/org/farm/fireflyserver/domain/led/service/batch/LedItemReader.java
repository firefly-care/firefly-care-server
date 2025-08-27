package org.farm.fireflyserver.domain.led.service.batch;

import org.farm.fireflyserver.domain.led.web.dto.response.LedDataLogDto;
import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;


// 10분 이내 LED 센서 로그를 읽어오는 ItemReader
public class LedItemReader {

    public static ItemReader<LedDataLogDto> reader(DataSource ledDataSource) {
        LocalDateTime now = LocalDateTime.now();
        Timestamp tenMinutesAgo = Timestamp.valueOf(now.minusMinutes(10));

        return new JdbcCursorItemReaderBuilder<LedDataLogDto>()
                .name("ledSensorLogReader")
                .dataSource(ledDataSource)
                .sql("SELECT LED_MTCHN_SN, LED_SENSOR_GBN, REG_DT " +
                        "FROM t_led_sensor_log " +
                        "WHERE REG_DT >= ? " +
                        "ORDER BY REG_DT DESC")
                .queryArguments(tenMinutesAgo)
                .rowMapper((rs, rowNum) -> new LedDataLogDto(
                        rs.getString("LED_MTCHN_SN"),
                        SensorGbn.fromCode(rs.getString("LED_SENSOR_GBN")),
                        rs.getTimestamp("REG_DT").toLocalDateTime(),
                        null
                ))
                .build();
    }
}
