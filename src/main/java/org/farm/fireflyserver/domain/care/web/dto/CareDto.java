package org.farm.fireflyserver.domain.care.web.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import org.farm.fireflyserver.domain.care.persistence.entity.Result;
import org.farm.fireflyserver.domain.care.persistence.entity.Type;
import org.farm.fireflyserver.domain.care.persistence.entity.Care;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class CareDto {
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
                    String.valueOf(care.getSenior().getSeniorId()),
                    care.getSenior().getName(),
                    care.getResult()
            );
        }
    }

    public record SearchRequest (
            Type type,
            Result result,
            LocalDate startDate,
            LocalDate endDate,
            String searchTerm
    ) {
    }

    public record MonthlyCare (
            Long callCnt,
            Long visitCnt,
            Long emergCnt,
            List<CareTuple> cares
    ) {
        public static record CareTuple (
                LocalDateTime careDate,
                Type careType,
                Result result
        ) {}
    }
}