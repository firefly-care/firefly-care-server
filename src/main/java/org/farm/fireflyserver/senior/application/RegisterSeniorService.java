package org.farm.fireflyserver.senior.application;

import lombok.RequiredArgsConstructor;
import org.farm.fireflyserver.senior.application.command.RegisterSeniorCommand;
import org.farm.fireflyserver.senior.application.port.in.RegisterSeniorUseCase;
import org.farm.fireflyserver.senior.application.port.out.RegisterSeniorPort;
import org.farm.fireflyserver.senior.domain.Senior;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegisterSeniorService implements RegisterSeniorUseCase {

    private final RegisterSeniorPort seniorRepositoryPort;

    @Override
    @Transactional
    public void registerSenior(RegisterSeniorCommand command) {
        Senior senior = new Senior(
                command.name(),
                command.gender(),
                command.birthday(),
                command.address(),
                command.town(),
                command.phoneNum(),
                command.homePhoneNum(),
                command.zipCode(),
                command.guardianName(),
                command.guardianPhoneNum(),
                command.isHighRisk(),
                command.benefitType(),
                command.memo()
        );

        seniorRepositoryPort.save(senior);
    }

}
