package com.example.service.plan;

import com.example.api.ApiResult;
import com.example.domain.plan.Plan;
import com.example.dto.response.PlanInfoResponse;
import com.example.dto.response.ServiceInfoResponse;
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

        String serviceName = plan.getService().getServiceName();

        ServiceInfoResponse serviceInfoResponse = new ServiceInfoResponse(serviceName);

        PlanInfoResponse planInfoResponse = new PlanInfoResponse(
                serviceInfoResponse, // 서비스 정보
                plan.getPlanName(),
                plan.getMonthlyFee(),
                plan.getMaxMemberNum()
        );

        return ApiResult.success(planInfoResponse);
    }
}
