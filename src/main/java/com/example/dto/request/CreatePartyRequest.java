package com.example.dto.request;

import com.example.domain.party.Party;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePartyRequest {

    private Long memberId;
    private Long planId;
    private String leaderId;
    private String leaderPwd;
    private Integer recLimit;
    private Date startDate;
    private Integer durationMonth;
    private Date endDate;

    public Party toEntity(){
        return Party.builder()
                .memberId(this.memberId)
                .planId(this.planId)
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
