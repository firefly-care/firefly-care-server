package org.farm.fireflyserver.domain.manager.web.dto;

public class ManagerDto {

    public record SimpleInfo(
            Long managerId,
            String name,
            String seniorCnt,
            String careCnt,
            String recentCareDate
    ){}
}