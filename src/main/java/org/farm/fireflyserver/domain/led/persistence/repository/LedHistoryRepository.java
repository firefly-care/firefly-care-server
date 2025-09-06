package org.farm.fireflyserver.domain.led.persistence.repository;

import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.entity.OnOff;
import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LedHistoryRepository extends JpaRepository<LedHistory, Long> {

    //중복 체크
    boolean existsByLedMtchnSnAndSensorGbnAndOnOffAndEventTime(
            String ledMtchnSn, SensorGbn sensorGbn, OnOff onOff, LocalDateTime eventTime);

    // 특정 LED센서 최신 데이터 조회
    LedHistory findTopByLedMtchnSnAndSensorGbnOrderByEventTimeDescLedHistoryIdDesc(String ledMtchnSn, SensorGbn sensorGbn);

    // 모든 LED센서 최신 데이터 조회
    //JPQL에서 일부 기능 사용 불가하여 NativeQuery 사용
    @Query(value = """
    SELECT l.*
    FROM led_history l
    JOIN (
        SELECT t.led_mtchn_sn, t.sensor_gbn, MAX(t.led_history_id) AS max_id
        FROM led_history t
        GROUP BY t.led_mtchn_sn, t.sensor_gbn
    ) latest
      ON l.led_history_id = latest.max_id
    """, nativeQuery = true)
    List<LedHistory> findLatestHistories();
}