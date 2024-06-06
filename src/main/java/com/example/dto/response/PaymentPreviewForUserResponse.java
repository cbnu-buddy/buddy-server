package com.example.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class PaymentPreviewForUserResponse {
    private int totalAmount;
    private List<PartyInfo> parties;

    @Getter
    @Builder
    public static class PartyInfo {
        private String name;
        private int monthlyFee;
    }
}