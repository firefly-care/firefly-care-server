package org.farm.fireflyserver.domain.care.persistence;

import org.farm.fireflyserver.domain.care.persistence.entity.CareResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareResultRepository extends JpaRepository<CareResult, Long> {
}
