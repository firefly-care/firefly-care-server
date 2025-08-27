package org.farm.fireflyserver.domain.senior.web.dto.request;

public class RequestSeniorDto {
    public record Deactivate (
            Long seniorId
    ){}
}
