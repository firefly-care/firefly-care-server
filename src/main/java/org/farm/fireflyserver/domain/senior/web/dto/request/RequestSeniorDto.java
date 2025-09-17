package org.farm.fireflyserver.domain.senior.web.dto.request;

public class RequestSeniorDto {
    public record Deactivate (
            Long seniorId
    ){}

    public record UpdateScore(
            String ledMtchnSn,
            Double sleepScr,
            Double memoryScr,
            Double lowEngScr,
            Double dangerRt
    ) {}
}

