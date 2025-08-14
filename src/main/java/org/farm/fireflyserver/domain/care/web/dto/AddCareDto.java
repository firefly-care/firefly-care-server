package org.farm.fireflyserver.domain.care.web.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AddCareDto {
    private Long manager_id;
    private Long senior_id;
    private LocalDateTime date;
    private String type;
    private String content;
    private String result;

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "result"
    )
    @JsonSubTypes({
            @JsonSubTypes.Type(value = NormalCareDetailsDto.class, name = "COMPLETED"),
            @JsonSubTypes.Type(value = AbsentCareDetailsDto.class, name = "ABSENT")
    })
    private CareDetailsDto details;
}
