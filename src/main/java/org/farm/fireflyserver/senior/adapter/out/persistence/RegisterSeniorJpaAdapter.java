package org.farm.fireflyserver.senior.adapter.out.persistence;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.senior.adapter.out.persistence.repository.SeniorRepository;
import org.farm.fireflyserver.senior.application.port.out.RegisterSeniorPort;
import org.farm.fireflyserver.senior.adapter.out.persistence.entity.SeniorEntity;
import org.farm.fireflyserver.senior.domain.Senior;
import org.springframework.stereotype.Component;
@Component
@RequiredArgsConstructor
public class RegisterSeniorJpaAdapter implements RegisterSeniorPort {

    private final SeniorRepository seniorRepository;

    @Override
    public void save(Senior senior) {

        SeniorEntity seniorEntity = SeniorEntity.builder()
                .name(senior.name())
                .gender(senior.gender())
                .birthday(senior.birthday())
                .address(senior.address())
                .town(senior.town())
                .phoneNum(senior.phoneNum())
                .homePhoneNum(senior.homePhoneNum())
                .zipCode(senior.zipCode())
                .guardianName(senior.guardianName())
                .guardianPhoneNum(senior.guardianPhoneNum())
                .isHighRisk(senior.isHighRisk())
                .benefitType(senior.benefitType())
                .memo(senior.memo())
                .build();

        seniorRepository.save(seniorEntity);

    }
}
