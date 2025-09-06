package org.farm.fireflyserver.domain.manager.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;

import java.util.List;

import static org.farm.fireflyserver.domain.manager.persistence.entity.QManager.manager;

@RequiredArgsConstructor
public class ManagerRepositoryImpl implements ManagerRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ManagerDto.SimpleInfo> findManagerSimpleInfoList() {
        return queryFactory
                .select(Projections.constructor(ManagerDto.SimpleInfo.class,
                        manager.name,
                        manager.seniorCnt,
                        manager.careCnt,
                        manager.recentCareDate
                ))
                .from(manager)
                .fetch();
    }
}
