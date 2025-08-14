package org.farm.fireflyserver.domain.senior.web.mapper;

import org.farm.fireflyserver.domain.senior.persistence.entity.Senior;
import org.farm.fireflyserver.domain.senior.web.dto.request.RegisterSeniorDto;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface SeniorMapper {
    Senior toEntity(RegisterSeniorDto dto);

}
