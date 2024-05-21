package com.example.service.plan;

import com.example.api.ApiResult;
import com.example.domain.plan.Plan;
import com.example.dto.response.PlanInfoResponse;
import com.example.repository.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PlanService {

    private final PlanRepository planRepository;

    public ApiResult<?> getPlanInfo(Long planId) {
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 플랜입니다."));

        PlanInfoResponse.ServiceDto serviceDto = new PlanInfoResponse.ServiceDto(plan.getService().getServiceName());
        PlanInfoResponse planInfoResponse = PlanInfoResponse.builder()
                .service(serviceDto)
                .name(plan.getPlanName())
                .monthlyFee(plan.getMonthlyFee())
                .maxMemberNum(plan.getMaxMemberNum())
                .build();

        return ApiResult.success(planInfoResponse);
    }
}
