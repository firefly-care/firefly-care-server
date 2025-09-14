package org.farm.fireflyserver.domain.manager.persistence;

import org.farm.fireflyserver.domain.manager.persistence.entity.Manager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ManagerRepository extends JpaRepository<Manager, Long>, ManagerRepositoryCustom {
    List<Manager> findAllByOrderByNameAsc();
}
