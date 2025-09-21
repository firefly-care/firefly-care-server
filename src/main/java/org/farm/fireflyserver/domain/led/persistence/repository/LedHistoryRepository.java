package org.farm.fireflyserver.domain.led.persistence.repository;

import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.entity.OnOff;
import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface LedHistoryRepository extends JpaRepository<LedHistory, Long> {

    //중복 체크
    boolean existsByLedMtchnSnAndSensorGbnAndOnOffAndEventTime(
            String ledMtchnSn, SensorGbn sensorGbn, OnOff onOff, LocalDateTime eventTime);

    // 모든 LED센서 최신 데이터 조회
    @Query("""
    SELECT l FROM LedHistory l
    WHERE l.ledHistoryId IN (
        SELECT MAX(l2.ledHistoryId)
        FROM LedHistory l2
        GROUP BY l2.ledMtchnSn, l2.sensorGbn
    )
    """)
    List<LedHistory> findLatestHistories();

    //특정 대상자의 지정된 시간 범위 내 모든 LED 기록을 시간 순으로 조회
    List<LedHistory> findByLedMtchnSnAndEventTimeBetweenOrderByEventTimeAsc(
            String ledMtchnSn, LocalDateTime start, LocalDateTime end
    );
}