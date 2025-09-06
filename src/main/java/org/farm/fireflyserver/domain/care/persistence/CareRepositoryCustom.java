package org.farm.fireflyserver.domain.care.persistence;

import java.util.List;

public interface CareRepositoryCustom {
    List<Long> findDistinctSeniorIdsByManagerId(Long managerId);
}
