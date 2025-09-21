package org.farm.fireflyserver.domain.senior.persistence.repository;

import org.farm.fireflyserver.domain.senior.persistence.entity.SeniorStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeniorStatusRepository extends JpaRepository<SeniorStatus, Long> {
}
