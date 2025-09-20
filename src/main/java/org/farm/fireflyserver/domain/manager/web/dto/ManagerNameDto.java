package org.farm.fireflyserver.domain.manager.web.dto;

import org.farm.fireflyserver.domain.manager.persistence.entity.Manager;

public record ManagerNameDto(
        String managerName,
        String phoneNumber
) {
    public static ManagerNameDto from(Manager manager) {
        return new ManagerNameDto(
                manager.getName(),
                manager.getPhoneNum()
        );
    }
}
