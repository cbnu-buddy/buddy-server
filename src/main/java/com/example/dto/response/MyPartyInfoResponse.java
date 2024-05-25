package com.example.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

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
        private Date startDate;
        private int durationMonth;
        private Date endDate;
    }
}
