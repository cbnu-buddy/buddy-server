package com.example.domain.party;

import com.example.domain.member.Member;
import com.example.domain.plan.Plan;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    private LocalDateTime startDate;

    @Column(name = "duration_month")
    private Integer durationMonth;

    @Column(name = "end_date")
    private LocalDateTime endDate;
    @Column(name = "bill_date")
    private Integer billDate;
    @Column(name = "progress_status")
    private Boolean progressStatus;

    //== 비지니스 로직 ==//

    /*
    리더 아이디 변경
     */
    public void changeLeaderId(String leaderId){
        this.leaderId = leaderId;
    }

    /*
    리더 비밀번호 변경
    */
    public void changeLeaderPwd(String leaderPwd){
        this.leaderPwd = leaderPwd;
    }

    public void setProgressStatus(Boolean progressStatus) {
        this.progressStatus = progressStatus;
    }

    /*
    현재 회원 수 설정
    */
    public void setCurrentRecNum(Integer currentRecNum) {
        this.currentRecNum = currentRecNum;
    }


    /*
    모집 인원 변경
    */
    public void changeRecLimit(Integer recLimit){
        if (recLimit <= 0) {
            throw new CustomException(ErrorCode.INVALID_REC_LIMIT);
        }
        if (recLimit > this.plan.getMaxMemberNum()-1) {
            throw new CustomException(ErrorCode.EXCEEDS_MAX_MEMBER_NUM);
        }
        this.recLimit = recLimit;
    }

    public String getStartDateISOString() {
        return startDate.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    public String getEndDateISOString() {
        return endDate.format(DateTimeFormatter.ISO_DATE_TIME);
    }

}
