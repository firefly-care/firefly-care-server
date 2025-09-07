package org.farm.fireflyserver.domain.manager.web.dto;

import org.farm.fireflyserver.domain.care.persistence.entity.Result;
import org.farm.fireflyserver.domain.senior.persistence.entity.Gender;

import java.time.LocalDateTime;

public class ManagerDto {

    public record SimpleInfo(
            Long managerId,
            String name,
            String seniorCnt,
            String careCnt,
            String recentCareDate
    ){}

    public record DetailInfo(
            Long managerId,
            String name,
            String phone,
            String affiliation,
            String email,
            String address
    ){}

    public record SeniorInfo(
            Long seniorId,
            String name,
            Gender gender,
            Long age,
            String address
    ){}

    public record CareSeniorInfo(
            String seniorName,
            Gender seniorGender,
            Long seniorAge,
            LocalDateTime careDate,
            Result careResult
    ){}
}