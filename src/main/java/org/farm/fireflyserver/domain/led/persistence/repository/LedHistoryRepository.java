package org.farm.fireflyserver.domain.led.persistence.repository;

import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedHistoryRepository extends JpaRepository<LedHistory, Long> {
}
