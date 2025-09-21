package org.farm.fireflyserver.domain.senior.web.dto.response;

import lombok.Builder;
import lombok.Getter;
import org.farm.fireflyserver.domain.manager.persistence.entity.Manager;
import org.farm.fireflyserver.domain.senior.persistence.entity.*;

import java.time.LocalDate;

@Getter
@Builder
public class SeniorDetailDto {

    private SeniorDetailResponse response;

    public static SeniorDetailDto fromEntities(Senior senior, SeniorStatus seniorStatus, Manager manager) {
        SeniorDetailResponse response = new SeniorDetailResponse(
                // Senior fields
                senior.getSeniorId(), senior.getName(), senior.getGender(), senior.getBenefitType(), senior.getBirthday(), senior.getPhoneNum(), senior.getSubPhoneNum(), senior.getZipCode(), senior.getAddress(), senior.getGuardianName(), senior.getGuardianPhoneNum(), senior.getMemo(), senior.isActive(), senior.isLedUse(),
                // SeniorStatus fields
                seniorStatus.getDangerRt(), seniorStatus.getSleepScr(), seniorStatus.getMemoryScr(), seniorStatus.getLowEngScr(), seniorStatus.getInactScr(), seniorStatus.getLastActTime(), seniorStatus.getState(), seniorStatus.getDangerLevel(),

                manager.getName(), manager.getPhoneNum(), manager.getManagerId());
        return SeniorDetailDto.builder().response(response).build();
    }

    public record SeniorDetailResponse(
            // Senior fields
            Long seniorId, String seniorName, Gender gender,

            BenefitType benefitType, LocalDate birthday, String seniorPhoneNum, String subPhoneNum, String zipCode,
            String address, String guardianName, String guardianPhoneNum, String memo, boolean isActive,
            boolean isLedUse,

            // SeniorStatus fields
            Double dangerRt, Double sleepScr, Double memoryScr, Double lowEngScr, Double inactScr, Integer lastActTime,
            String state, DangerLevel dangerLevel,

            // Account fields
            String managerName, String managerPhoneNum, Long managerId) {
    }
}