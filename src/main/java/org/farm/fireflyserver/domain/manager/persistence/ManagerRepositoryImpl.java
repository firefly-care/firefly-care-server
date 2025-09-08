package org.farm.fireflyserver.domain.manager.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;

import java.util.List;
import java.util.Optional;

import static org.farm.fireflyserver.domain.manager.persistence.entity.QManager.manager;

@RequiredArgsConstructor
public class ManagerRepositoryImpl implements ManagerRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    @Override
    public List<ManagerDto.SimpleInfo> findManagerSimpleInfoList() {
        return queryFactory
                .select(Projections.constructor(ManagerDto.SimpleInfo.class,
                        manager.managerId,
                        manager.name,
                        manager.seniorCnt,
                        manager.careCnt,
                        manager.recentCareDate
                ))
                .from(manager)
                .fetch();
    }

    @Override
    public Optional<ManagerDto.DetailInfo> findDetailInfoById(Long id) {
        ManagerDto.DetailInfo dto =  queryFactory
                .select(Projections.constructor(ManagerDto.DetailInfo.class,
                        manager.managerId,
                        manager.name,
                        manager.phoneNum,
                        manager.birth,
                        manager.affiliation,
                        manager.email,
                        manager.address
                ))
                .from(manager)
                .where(manager.managerId.eq(id))
                .fetchOne();

        return Optional.ofNullable(dto);
    }
}
