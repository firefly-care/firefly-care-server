package org.farm.fireflyserver.domain.led.service.calculator;

import org.farm.fireflyserver.domain.led.persistence.AnomalyType;
import org.farm.fireflyserver.domain.led.persistence.entity.LedHistory;

import java.util.List;

public interface ScoreCalculator {
    Double calculate(List<LedHistory> events);
    AnomalyType getAnomalyType();
}
