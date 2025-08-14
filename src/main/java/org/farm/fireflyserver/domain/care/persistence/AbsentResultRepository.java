package org.farm.fireflyserver.domain.care.persistence;

import org.farm.fireflyserver.domain.care.persistence.entity.AbsentResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AbsentResultRepository extends JpaRepository<AbsentResult, Long> {
}
