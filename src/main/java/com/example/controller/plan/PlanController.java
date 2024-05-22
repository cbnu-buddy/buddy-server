package com.example.controller.plan;

import com.example.api.ApiResult;
import com.example.service.plan.PlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "플랜 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/plan")
public class PlanController {

    private final PlanService planService;

    /*
    플랜 정보 조희
     */
    @GetMapping("/{planId}")
    @Operation(summary = "플랜 정보 조회", description = "플랜 정보를 조회합니다.")
    public ApiResult<?> getPlanInfo(@PathVariable Long planId) {

        return planService.getPlanInfo(planId);
    }
}
