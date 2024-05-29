package com.example.repository.plan;

import com.example.domain.member.Member;
import com.example.domain.plan.Plan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
public interface PlanRepository extends JpaRepository<Plan, Long> {
    Optional<Plan> findById(Long id);
    List<Plan> findByService_Id(Long serviceId);
}