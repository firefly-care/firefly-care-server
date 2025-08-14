package org.farm.fireflyserver.domain.care.mapper;

import org.farm.fireflyserver.domain.care.persistence.entity.AbsentResult;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;
import org.farm.fireflyserver.domain.care.persistence.entity.CareResult;
import org.farm.fireflyserver.domain.care.web.dto.AbsentCareDetailsDto;
import org.farm.fireflyserver.domain.care.web.dto.NormalCareDetailsDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CareMapper {
    @Mapping(target = "careResultId", ignore = true)
    @Mapping(target = "care", source = "care")
    CareResult toCareResult(NormalCareDetailsDto normalCareDetailsDto, Care care);

    @Mapping(target = "absentResultId", ignore = true)
    @Mapping(target = "care", source = "care")
    AbsentResult toAbsentResult(AbsentCareDetailsDto absentCareDetailsDto, Care care);
}
