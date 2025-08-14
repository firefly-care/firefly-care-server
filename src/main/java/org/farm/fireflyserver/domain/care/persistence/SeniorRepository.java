package org.farm.fireflyserver.domain.care.persistence;

import org.farm.fireflyserver.domain.care.persistence.entity.Senior;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeniorRepository extends JpaRepository<Senior, Long> {
}
