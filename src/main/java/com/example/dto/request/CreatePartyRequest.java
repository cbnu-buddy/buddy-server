package com.example.dto.request;

import com.example.domain.member.Member;
import com.example.domain.party.Party;
import com.example.domain.plan.Plan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartyRequest {
    private Long planId;
    private String leaderId;
    private String leaderPwd;
    private Integer recLimit;
    private Date startDate;
    private Integer durationMonth;
    private Date endDate;

    public Party toEntity(Member member, Plan plan) {
        return Party.builder()
                .member(member)
                .plan(plan)
                .leaderId(this.leaderId)
                .leaderPwd(this.leaderPwd)
                .recLimit(this.recLimit)
                .currentRecNum(0)
                .startDate(this.startDate)
                .durationMonth(this.durationMonth)
                .endDate(this.endDate)
                .progressStatus(false)
                .build();
    }
}
