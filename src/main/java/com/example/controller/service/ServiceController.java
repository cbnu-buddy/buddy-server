package com.example.controller.service;

import com.example.api.ApiResult;
import com.example.service.service.ServiceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "서비스 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/public/services")
public class ServiceController {

    private final ServiceService serviceService;

    /*
    서비스 목록 조희
     */
    @GetMapping
    @Operation(summary = "서비스 목록 조회", description = "서비스 목록을 조회합니다.")
    public ApiResult<?> getServicesInfo() {

        return serviceService.getServicesInfo();
    }
}
