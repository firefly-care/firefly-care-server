package org.farm.fireflyserver.domain.care.persistence;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.util.List;
import static org.farm.fireflyserver.domain.care.persistence.entity.QCare.care;

@RequiredArgsConstructor
public class CareRepositoryImpl implements CareRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<Long> findDistinctSeniorIdsByManagerId(Long managerId) {
        return queryFactory
                .select(care.senior.seniorId).distinct()
                .from(care)
                .where(care.manager.managerId.eq(managerId))
                .fetch();
    }
}
