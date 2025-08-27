package org.farm.fireflyserver.domain.led.persistence.repository;

import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.farm.fireflyserver.domain.led.persistence.entity.SensorGbn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LedHistoryRepository extends JpaRepository<LedHistory, Long> {
    LedHistory findTopByLedMtchnSnAndSensorGbnOrderByEventTimeDesc(String s, SensorGbn sensorGbn);

    @Query("""
                SELECT l FROM LedHistory l
                WHERE l.ledHistoryId IN (
                    SELECT MAX(l2.ledHistoryId) FROM LedHistory l2 GROUP BY l2.ledMtchnSn, l2.sensorGbn
                )
            """)
    List<LedHistory> findLatestHistories();
}