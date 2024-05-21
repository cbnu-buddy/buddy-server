package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ServicesInfoResponse {
    private ServiceDto service;
    private List<PlanDto> plans;

    @Data
    @Builder
    @AllArgsConstructor
    public static class ServiceDto {
        private String name;
        private Boolean isHot;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class PlanDto {
        private String name;
        private int monthlyFee;
    }
}

