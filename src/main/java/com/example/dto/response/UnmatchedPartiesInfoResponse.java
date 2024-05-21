package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class UnmatchedPartiesInfoResponse {

    private UnmatchedPartiesInfoResponse.ServiceDto service;
    private UnmatchedPartiesInfoResponse.PlanDto plan;
    private UnmatchedPartiesInfoResponse.PartyDto party;

    @Data
    @Builder
    @AllArgsConstructor
    public static class ServiceDto {
        private String name;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class PlanDto {
        private String name;
        private int monthlyFee;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class PartyDto {
        private Long partyId;
        private Date startDate;
        private Integer durationMonth;
        private Date endDate;
        private int monthlyFee;
    }
}
