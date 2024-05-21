package com.example.dto.response;

import com.example.domain.service.Service;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlanInfoResponse {
    private ServiceInfoResponse service;
    private String name;
    private int monthlyFee;
    private int maxMemberNum;
}
