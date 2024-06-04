package com.example.dto.request;

import com.example.domain.member.Member;
import com.example.domain.party.Party;
import com.example.domain.plan.Plan;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartyRequest {
    private Long planId;
    private String leaderId;
    private String leaderPwd;
    private Integer recLimit;
    private LocalDateTime startDate;
    private Integer durationMonth;
    private LocalDateTime endDate;

    public Party toEntity(Member member, Plan plan) {
        if (this.recLimit <= 0) {
            throw new CustomException(ErrorCode.INVALID_REC_LIMIT);
        }
        if (this.recLimit > plan.getMaxMemberNum() - 1) {
            throw new CustomException(ErrorCode.EXCEEDS_MAX_MEMBER_NUM);
        }

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
                .billDate(1)
                .progressStatus(false)
                .build();
    }
}
