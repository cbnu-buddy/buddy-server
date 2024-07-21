package com.example.service.service;

import com.example.api.ApiResult;
import com.example.domain.service.Service;
import com.example.dto.response.ServicesInfoResponse;
import com.example.repository.party.PartyRepository;
import com.example.repository.plan.PlanRepository;
import com.example.repository.service.ServiceRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ServiceService {

    private final ServiceRepository serviceRepository;
    private final PlanRepository planRepository;
    private final PartyRepository partyRepository;

    public ApiResult<?> getServicesInfo() {
        List<Service> services = serviceRepository.findAll();
        List<Object[]> topServices = partyRepository.findTop5HotServices();

        // Hot service IDs
        List<Long> hotServiceIds = topServices.stream()
                .map(result -> ((Number) result[0]).longValue())
                .collect(Collectors.toList());

        // Construct response
        List<Object> response = new ArrayList<>();
        for (Service service : services) {
            // Get plans for the current service
            List<ServicesInfoResponse.PlanDto> planDtos = planRepository.findByService_Id(service.getId())
                    .stream()
                    .map(plan -> ServicesInfoResponse.PlanDto.builder()
                            .name(plan.getPlanName())
                            .monthlyFee(plan.getMonthlyFee())
                            .build())
                    .collect(Collectors.toList());

            // Construct service DTO
            ServicesInfoResponse.ServiceDto serviceDto = ServicesInfoResponse.ServiceDto.builder()
                    .name(service.getServiceName())
                    .isHot(hotServiceIds.contains(service.getId()))
                    .build();

            // Construct service and plan response
            ServicesInfoResponse servicesInfoResponse = ServicesInfoResponse.builder()
                    .service(serviceDto)
                    .plans(planDtos)
                    .build();

            // Add to response list
            response.add(servicesInfoResponse);
        }

        return ApiResult.success(response);
    }
}
