package org.farm.fireflyserver.domain.care.persistence;

import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareRepository extends JpaRepository<Care, Long> {
}
