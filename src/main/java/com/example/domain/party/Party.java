package com.example.domain.party;

import com.example.domain.member.Member;
import com.example.domain.plan.Plan;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Party {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_id")
    private Long partyId;                               // PK


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;                            // FK

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id")
    private Plan plan;


    @Column(name = "leader_id")
    private String leaderId;
    @Column(name = "leader_pwd")
    private String leaderPwd;
    @Column(name = "rec_limit")
    private Integer recLimit;
    @Column(name = "current_rec_num")
    private Integer currentRecNum;
    @Column(name = "start_date")
    private Date startDate;
    @Column(name = "duration_month")
    private Integer durationMonth;
    @Column(name = "end_date")
    private Date endDate;
    @Column(name = "bill_date")
    private Date billDate;
    @Column(name = "progress_status")
    private Boolean progressStatus;

}
