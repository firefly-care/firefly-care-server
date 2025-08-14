package org.farm.fireflyserver.domain.care.persistence;

import org.farm.fireflyserver.domain.care.persistence.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {
}
