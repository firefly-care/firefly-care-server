package org.farm.fireflyserver.domain.senior.web.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.farm.fireflyserver.domain.account.persistence.entity.Account;
import org.farm.fireflyserver.domain.account.persistence.entity.Authority;
import org.farm.fireflyserver.domain.senior.persistence.entity.*;

import java.time.LocalDate;

@Getter
@Builder
public class SeniorDetailDto {

    private SeniorDetailResponse response;

    public static SeniorDetailDto fromEntities(Senior senior, SeniorStatus seniorStatus, Account account) {
        SeniorDetailResponse response = new SeniorDetailResponse(
            // Senior fields
            senior.getSeniorId(),
            senior.getName(),
            senior.getGender(),
            senior.isHighRisk(),
            senior.getBenefitType(),
            senior.getBirthday(),
            senior.getPhoneNum(),
            senior.getHomePhoneNum(),
            senior.getZipCode(),
            senior.getAddress(),
            senior.getTown(),
            senior.getGuardianName(),
            senior.getGuardianPhoneNum(),
            senior.getMemo(),
            senior.isActive(),
            senior.isLedUse(),
            senior.isAmiUse(),
            // SeniorStatus fields
            seniorStatus.getDangerRt(),
            seniorStatus.getSleepScr(),
            seniorStatus.getMemoryScr(),
            seniorStatus.getLowEngScr(),
            seniorStatus.getLastActTime(),
            seniorStatus.getState(),
            seniorStatus.getDangerLevel(),
            // Account fields
            account.getName(),
            account.getPhoneNum(),
            account.getId(),
            account.getAuthority()
        );
        return SeniorDetailDto.builder().response(response).build();
    }

    public record SeniorDetailResponse(
        // Senior fields
        Long seniorId,
        String seniorName,
        Gender gender,
        boolean isHighRisk,
        BenefitType benefitType,
        LocalDate birthday,
        String seniorPhoneNum,
        String homePhoneNum,
        String zipCode,
        String address,
        String town,
        String guardianName,
        String guardianPhoneNum,
        String memo,
        boolean isActive,
        boolean isLedUse,
        boolean isAmiUse,

        // SeniorStatus fields
        Double dangerRt,
        Double sleepScr,
        Double memoryScr,
        Double lowEngScr,
        Integer lastActTime,
        String state,
        DangerLevel dangerLevel,

        // Account fields
        String managerName, // Renamed from accountName
        String managerPhoneNum, // Renamed from accountPhoneNum
        String id,
        Authority authority
    ) {}
}