package org.farm.fireflyserver.senior.application.port.out;

import org.farm.fireflyserver.senior.domain.Senior;

public interface RegisterSeniorPort {
    void save(Senior senior);
}
