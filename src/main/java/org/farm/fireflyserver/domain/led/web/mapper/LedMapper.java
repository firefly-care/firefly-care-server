package org.farm.fireflyserver.domain.led.web.mapper;

import org.farm.fireflyserver.domain.led.persistence.entity.LedData;
import org.farm.fireflyserver.domain.led.web.dto.request.SaveLedDataDto;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface LedMapper {
    LedData toEntity(SaveLedDataDto dto);
}