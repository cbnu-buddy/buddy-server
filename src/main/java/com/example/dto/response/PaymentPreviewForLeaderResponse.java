package com.example.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PaymentPreviewForLeaderResponse {
    private int totalAmount;
    private List<PlanInfo> plans;

    @Getter
    @Builder
    public static class PlanInfo {
        private String name;
        private int totalMonthlyFee;
    }
}
