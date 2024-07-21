package com.example.domain.plan;

import com.example.domain.service.Service;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Plan {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "plan_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private Service service;

    private String planName;            // 요금제

    private int maxMemberNum;           // 최대 사용자 수

    private int monthlyFee;             // 월 비용

    public Plan(Service service, String planName, int maxMemberNum, int monthlyFee){
        this.service = service;
        this.planName = planName;
        this.maxMemberNum = maxMemberNum;
        this.monthlyFee = monthlyFee;
    }
}
