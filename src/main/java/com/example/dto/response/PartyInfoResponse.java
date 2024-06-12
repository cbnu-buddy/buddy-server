package com.example.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
public class PartyInfoResponse {
    private ServiceDto service;
    private PlanDto plan;
    private PartyDto party;

    @Getter
    @Setter
    @Builder
    public static class ServiceDto {
        private String name;
    }

    @Getter
    @Setter
    @Builder
    public static class PlanDto {
        private Long planId;
        private String name;
        private Integer monthlyFee;
    }

    @Getter
    @Setter
    @Builder
    public static class PartyDto {
        private Long partyId;
        private Integer recLimit;
        private Integer currentRecNum;
        private Boolean progressStatus;
        private String startDate;
        private String endDate;
        private Integer durationMonth;
        private Long partyLeaderMemberId;
        private Long myMemberId;
        private AccountDto account;
        private List<MemberDto> members;
        private Integer maxMemberNum;
    }

    @Getter
    @Setter
    @Builder
    public static class AccountDto {
        private String id;
        private String pwd;
    }

    @Getter
    @Setter
    @Builder
    public static class MemberDto {
        private Long memberId;
        private String username;
    }
}
