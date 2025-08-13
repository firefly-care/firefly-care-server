package org.farm.fireflyserver.senior.application.port.in;

import org.farm.fireflyserver.senior.application.command.RegisterSeniorCommand;

public interface RegisterSeniorUseCase {
    void registerSenior(RegisterSeniorCommand command);
}