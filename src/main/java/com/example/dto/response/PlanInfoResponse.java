package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class PlanInfoResponse {
    private ServiceDto service;
    private Long planId;
    private String name;
    private int monthlyFee;
    private int maxMemberNum;

    @Data
    @Builder
    @AllArgsConstructor
    public static class ServiceDto {
        private String name;
    }
}