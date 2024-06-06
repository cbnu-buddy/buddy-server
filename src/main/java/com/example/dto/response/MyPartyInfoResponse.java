package com.example.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class MyPartyInfoResponse {
    private ServiceDto service;
    private PlanDto plan;
    private PartyDto party;

    @Getter
    @Setter
    @Builder
    public static class ServiceDto {
        private String name;
    }

    @Getter
    @Setter
    @Builder
    public static class PlanDto {
        private String name;
    }

    @Getter
    @Setter
    @Builder
    public static class PartyDto {
        private Long partyId;
        private String startDate;
        private int durationMonth;
        private String endDate;
        private boolean progressStatus;
    }
}
