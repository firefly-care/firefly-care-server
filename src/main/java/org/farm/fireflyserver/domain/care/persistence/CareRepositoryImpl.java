package org.farm.fireflyserver.domain.care.persistence;

import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.domain.care.persistence.entity.Type;
import org.farm.fireflyserver.domain.manager.web.dto.ManagerDto;

import java.util.List;
import static org.farm.fireflyserver.domain.care.persistence.entity.QCare.care;
import static org.farm.fireflyserver.domain.senior.persistence.entity.QSenior.senior;

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

    @Override
    public List<ManagerDto.CareSeniorInfo> getCareSeniorInfoByManagerAndCareType(Long managerId, Type careType) {
        return queryFactory
                .select(Projections.constructor(
                        ManagerDto.CareSeniorInfo.class,
                        senior.name,
                        senior.gender,
                        Expressions.numberTemplate(
                                Long.class,
                                "TIMESTAMPDIFF(YEAR, {0}, CURDATE())",
                                senior.birthday
                        ),
                        Expressions.stringTemplate("DATE_FORMAT({0}, '%Y-%m-%d')", care.date),
                        care.result
                ))
                .from(care)
                .join(care.senior, senior)
                .where(
                        care.manager.managerId.eq(managerId),
                        care.type.eq(careType)
                )
                .fetch();
    }
}
