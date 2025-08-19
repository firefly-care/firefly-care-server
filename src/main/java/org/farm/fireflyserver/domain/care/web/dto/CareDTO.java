package org.farm.fireflyserver.domain.care.web.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import org.farm.fireflyserver.domain.care.Result;
import org.farm.fireflyserver.domain.care.Type;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;

import java.time.LocalDateTime;

public class CareDTO {
    @Getter
    public class Register {
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

    public record Response (
        LocalDateTime date,
        String managerId,
        String managerName,
        Type type,
        String content,
        String seniorId,
        String seniorName,
        Result result
    ) {
        public static Response from(Care care) {
            return new Response(
                    care.getDate(),
                    care.getManagerAccount().getId(),
                    care.getManagerAccount().getName(),
                    care.getType(),
                    care.getContent(),
                    String.valueOf(care.getSenior().getId()),
                    care.getSenior().getName(),
                    care.getResult()
            );
        }
    }
}