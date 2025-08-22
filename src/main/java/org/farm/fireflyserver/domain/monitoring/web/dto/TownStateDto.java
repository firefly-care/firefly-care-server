package org.farm.fireflyserver.domain.monitoring.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record TownStateDto(
        String townName,
        //숫자 처리용
        @JsonProperty("0-24") int count0_24,
        @JsonProperty("24-48") int count24_48,
        @JsonProperty("48-72") int count48_72,
        @JsonProperty("72-") int count72_,

        int sleepCount,
        int memoryCount,
        int lowEngCount,
        int inactSCount,

        int normalCount,
        int attentionCount,
        int cautionCount,
        int dangerCount

) {
    public static TownStateDto of(String townName, int count0_24, int count24_48, int count48_72, int count72_,
                                  int sleepCount, int memoryCount, int lowEngCount, int inactSCount,
                                  int normalCount, int attentionCount, int cautionCount, int dangerCount) {
        return new TownStateDto(townName, count0_24, count24_48, count48_72, count72_,
                sleepCount, memoryCount, lowEngCount, inactSCount,
                normalCount, attentionCount, cautionCount, dangerCount);
    }
}