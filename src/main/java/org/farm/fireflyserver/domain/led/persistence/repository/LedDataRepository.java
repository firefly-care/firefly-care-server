package org.farm.fireflyserver.domain.led.persistence.repository;

import org.farm.fireflyserver.domain.led.persistence.entity.LedData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LedDataRepository extends JpaRepository<LedData, Long> {
}