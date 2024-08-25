package com.example.service.party;

import com.example.api.ApiResult;

import java.time.LocalDate;
import java.time.LocalDateTime;
import com.example.config.jwt.TokenProvider;
import com.example.domain.member.Member;
import com.example.domain.party.Party;
import com.example.domain.party.PartyMember;
import com.example.domain.plan.Plan;
import com.example.dto.request.ChangePartyAccountRequest;
import com.example.dto.request.ChangePartyRecLimitRequest;
import com.example.dto.request.CreatePartyRequest;
import com.example.dto.response.MyPartyInfoResponse;
import com.example.dto.response.PartyInfoResponse;
import com.example.dto.response.UnmatchedPartiesInfoResponse;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.member.MemberRepository;
import com.example.repository.party.PartyMemberRepository;
import com.example.repository.party.PartyRepository;
import com.example.repository.plan.PlanRepository;
import com.example.service.email.EmailService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PartyService {
    private final PartyRepository partyRepository;
    private final MemberRepository memberRepository;
    private final PartyMemberRepository partyMemberRepository;
    private final PlanRepository planRepository;
    private final TokenProvider tokenProvider;
    private final EmailService emailService;


    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        Claims claims = tokenProvider.getTokenClaims(tokenProvider.resolveToken(request));
        return claims.getSubject();
    }

    private Party verifyMemberAndGetParty(String userId, Long partyId) {
        Party party = partyRepository.findByPartyId(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        if (!userId.equals(party.getMember().getUserId())) {
            throw new CustomException(ErrorCode.MEMBER_NO_PERMISSION);
        }

        return party;
    }

    /*
    파티 생성하기
     */
    @Transactional
    public ApiResult<?> createParty(CreatePartyRequest createPartyRequest, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Long planId = createPartyRequest.getPlanId();
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        Party joinParty = createPartyRequest.toEntity(member, plan);

        partyRepository.save(joinParty);

        PartyMember partyLeader = PartyMember.builder()
                .party(joinParty)
                .member(member)
                .build();
        partyMemberRepository.save(partyLeader);

        return ApiResult.success("파티 생성이 성공적으로 처리되었습니다.");
    }

    /*
    파티 해산하기
    */
    @Transactional
    public ApiResult<?> deleteParty(Long partyId, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Party party = verifyMemberAndGetParty(userId, partyId);

        partyMemberRepository.deleteByParty(party);
        partyRepository.delete(party);

        return ApiResult.success("파티 해산이 성공적으로 처리되었습니다.");
    }

    /*
    파티 로그인 정보 변경
    */
    @Transactional
    public ApiResult<?> changePartyAccount(Long partyId, ChangePartyAccountRequest changePartyAccountRequest, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Party party = verifyMemberAndGetParty(userId, partyId);

        party.changeLeaderId(changePartyAccountRequest.getNewLeaderId());
        party.changeLeaderPwd(changePartyAccountRequest.getNewLeaderPwd());

        return ApiResult.success("파티 로그인 정보 변경이 성공적으로 처리되었습니다.");
    }

    /*
    파티 모집 인원 변경
    */
    @Transactional
    public ApiResult<?> changePartyRecLimit(Long partyId, ChangePartyRecLimitRequest changePartyRecLimitRequest, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Party party = verifyMemberAndGetParty(userId, partyId);

        if (party.getProgressStatus() == true) {
            throw new CustomException(ErrorCode.PARTY_ALREADY_STARTED);
        }
        party.changeRecLimit(changePartyRecLimitRequest.getNewRecLimit());

        return ApiResult.success("파티 모집 인원 변경이 성공적으로 처리되었습니다.");
    }

    /*
    특정 플랜 내 매칭이 완료되지 않은 파티 목록 정보 조회
    */
    @Transactional
    public ApiResult<?> getUnmatchedParties(Long planId, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Plan plan = planRepository.findById(planId)
                .orElseThrow(() -> new CustomException(ErrorCode.PLAN_NOT_FOUND));

        List<Party> parties = partyRepository.findUnmatchedPartiesByPlanIdAndNotMember(planId, member);
        LocalDateTime now = LocalDateTime.now();

        List<UnmatchedPartiesInfoResponse.PartyDto> responseList = parties.stream()
                .filter(party -> party.getStartDate().isAfter(now) || party.getStartDate().isEqual(now))
                .map(party -> UnmatchedPartiesInfoResponse.PartyDto.builder()
                    .partyId(party.getPartyId())
                    .startDate(party.getStartDateISOString())
                    .durationMonth(party.getDurationMonth())
                    .endDate(party.getEndDateISOString())
                    .individualMonthlyFee(party.getPlan().getMonthlyFee() / (party.getRecLimit() + 1))
                    .build()).collect(Collectors.toList());

        UnmatchedPartiesInfoResponse.PlanDto planDto = UnmatchedPartiesInfoResponse.PlanDto.builder()
                .name(plan.getPlanName())
                .monthlyFee(plan.getMonthlyFee())
                .build();

        UnmatchedPartiesInfoResponse.Response response = UnmatchedPartiesInfoResponse.Response.builder()
                .plan(planDto)
                .count(responseList.size())
                .party(responseList)
                .build();

        return ApiResult.success(response);
    }

    /*
    나의 파티 목록 조회
    */
    @Transactional(readOnly = true)
    public ApiResult<List<MyPartyInfoResponse>> getMyParties(HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<PartyMember> partyMembers = partyMemberRepository.findByMember(member);
        LocalDateTime now = LocalDateTime.now();

        List<Party> parties = partyMembers.stream()
                .map(PartyMember::getParty)
                .filter(party -> !(party.getProgressStatus() == false && party.getStartDate().isBefore(now)))
                .collect(Collectors.toList());

        List<MyPartyInfoResponse> response = parties.stream().map(party -> MyPartyInfoResponse.builder()
                .service(MyPartyInfoResponse.ServiceDto.builder()
                        .name(party.getPlan().getService().getServiceName())
                        .build())
                .plan(MyPartyInfoResponse.PlanDto.builder()
                        .name(party.getPlan().getPlanName())
                        .planId(party.getPlan().getId())
                        .build())
                .party(MyPartyInfoResponse.PartyDto.builder()
                        .partyId(party.getPartyId())
                        .startDate(party.getStartDateISOString())
                        .durationMonth(party.getDurationMonth())
                        .endDate(party.getEndDateISOString())
                        .progressStatus(party.getProgressStatus())
                        .partyLeaderMemberId(party.getMember().getMemberId())
                        .build())
                .build()).collect(Collectors.toList());

        return ApiResult.success(response);
    }

    /*
    파티 가입하기
    */
    @Transactional
    public ApiResult<?> joinParty(Long partyId, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Party party = partyRepository.findByPartyId(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        if (partyMemberRepository.existsByPartyAndMember(party, member)) {
            throw new CustomException(ErrorCode.ALREADY_JOINED_PARTY);
        }

        if (party.getProgressStatus() && party.getCurrentRecNum() >= party.getRecLimit()) {
            throw new CustomException(ErrorCode.PARTY_FULL);
        }

        PartyMember partyMember = PartyMember.builder()
                .party(party)
                .member(member)
                .build();
        partyMemberRepository.save(partyMember);

        party.setCurrentRecNum(party.getCurrentRecNum() + 1);

        if (party.getCurrentRecNum() >= party.getRecLimit()) {
            party.setProgressStatus(true);
            notifyRecruitmentCompletion(partyId); // 파티 모집 완료 시 이메일 발송
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", "파티 가입이 성공적으로 처리되었습니다.");
        response.put("party_id", partyId);

        return ApiResult.success(response);
    }


    /*
    파티 탈퇴하기
    */
    @Transactional
    public ApiResult<?> leaveParty(Long partyId, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Party party = partyRepository.findByPartyId(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        if (userId.equals(party.getMember().getUserId())) {
            throw new CustomException(ErrorCode.LEADER_CANNOT_LEAVE_PARTY);
        }

        if (!partyMemberRepository.existsByPartyAndMember(party, member)) {
            throw new CustomException(ErrorCode.NOT_JOINED_PARTY);
        }

        partyMemberRepository.deleteByPartyAndMember(party, member);

        party.setCurrentRecNum(party.getCurrentRecNum() - 1);

        if (party.getCurrentRecNum() < party.getRecLimit()) {
            party.setProgressStatus(false);
        }

        return ApiResult.success("파티 탈퇴가 성공적으로 처리되었습니다.");
    }


    /*
    파티 구성 완료 여부 이메일 일괄 발송
    */
    @Transactional
    public ApiResult<?> notifyRecruitmentCompletion(Long partyId) {
        Party party = partyRepository.findByPartyId(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        List<PartyMember> partyMembers = partyMemberRepository.findByParty(party);
        int numberOfMembers = partyMembers.size();
        for (PartyMember partyMember : partyMembers) {
            Member member = partyMember.getMember();
            String email = member.getEmail();
            String subject = "파티 구성 완료";

            Plan plan = party.getPlan();
            String planName = plan.getPlanName();
            String partyPeriod = party.getDurationMonth() + "개월";
            int individualFee = plan.getMonthlyFee() / numberOfMembers;
            String monthlyFee = individualFee + "P";

            String htmlContent = "<div>"
                    + "      <table"
                    + "        cellspacing=\"0\""
                    + "        cellpadding=\"0\""
                    + "        border=\"0\""
                    + "        style=\"width: 100%; max-width: 750px; min-width: 320px\""
                    + "      >"
                    + "        <tbody>"
                    + "          <tr>"
                    + "            <td>"
                    + "              <table"
                    + "                cellspacing=\"0\""
                    + "                cellpadding=\"0\""
                    + "                border=\"0\""
                    + "                style=\"width: 100%; max-width: 750px; min-width: 320px\""
                    + "              >"
                    + "                <tbody>"
                    + "                  <tr>"
                    + "                    <td>"
                    + "                      <div style=\"margin: 25px 20px 55px 10px\">"
                    + "                        <a"
                    + "                          href=\"https://buddy-client.vercel.app/\""
                    + "                          target=\"_blank\""
                    + "                          rel=\"noreferrer noopener\""
                    + "                          style=\""
                    + "                            display: flex;"
                    + "                            align-items: center;"
                    + "                            text-decoration: none;"
                    + "                            column-gap: 0.25rem;"
                    + "                          \""
                    + "                          ><img"
                    + "                            src=\"https://avatars.githubusercontent.com/u/162964118?s=200&v=4\""
                    + "                            alt=\"SINABRO\""
                    + "                            height=\"50px; border:0;\""
                    + "                            loading=\"lazy\""
                    + "                          />"
                    + "                          <span"
                    + "                            style=\""
                    + "                              letter-spacing: -0.025em;"
                    + "                              font-size: 1.25rem;"
                    + "                              line-height: 1.75rem;"
                    + "                              font-weight: 600;"
                    + "                              color: black;"
                    + "                              text-transform: uppercase;"
                    + "                            \""
                    + "                          >"
                    + "                            buddy"
                    + "                          </span>"
                    + "                        </a>"
                    + "                      </div>"
                    + "                      <div"
                    + "                        style=\""
                    + "                          width: 100%;"
                    + "                          padding: 0 20px;"
                    + "                          margin: 0 auto;"
                    + "                          font-family: Malgun Gothic;"
                    + "                          box-sizing: border-box;"
                    + "                        \""
                    + "                      >"
                    + "                        <div"
                    + "                          style=\""
                    + "                            font-size: 28px;"
                    + "                            font-weight: bold;"
                    + "                            color: #000;"
                    + "                            padding-bottom: 15px;"
                    + "                            border-bottom: 4px solid #000;"
                    + "                            height: 30px;"
                    + "                            line-height: 28px;"
                    + "                          \""
                    + "                        >"
                    + "                          파티 매칭 완료"
                    + "                        </div>"
                    + "                        <div"
                    + "                          style=\""
                    + "                            font-size: 14px;"
                    + "                            color: #666;"
                    + "                            line-height: 22px;"
                    + "                            margin: 20px 0 0 0;"
                    + "                          \""
                    + "                        >"
                    + "                          ⚠️ 파티가 시작되면, 파티 이용 금액에 해당하는"
                    + "                          사용자의<br />"
                    + "                          버디 포인트가 차감되오니 잔액이 부족하지 않도록 주의해"
                    + "                          주세요!"
                    + "                        </div>"
                    + "                        <div"
                    + "                          style=\""
                    + "                            font-size: 18px;"
                    + "                            font-weight: bold;"
                    + "                            background: #eee;"
                    + "                            padding: 11px 12px 11px 20px;"
                    + "                            margin-top: 40px;"
                    + "                          \""
                    + "                        >"
                    + "                          <span"
                    + "                            style=\""
                    + "                              display: inline-block;"
                    + "                              height: 30px;"
                    + "                              line-height: 30px;"
                    + "                            \""
                    + "                            >파티 정보</span"
                    + "                          >"
                    + "                        </div>"
                    + "                        <table"
                    + "                          cellspacing=\"0\""
                    + "                          cellpadding=\"0\""
                    + "                          border=\"0\""
                    + "                          style=\""
                    + "                            width: 100%;"
                    + "                            border: 1px solid #eee;"
                    + "                            border-bottom: 0;"
                    + "                          \""
                    + "                        >"
                    + "                          <tbody>"
                    + "                            <tr>"
                    + "                              <th"
                    + "                                colspan=\"1\""
                    + "                                rowspan=\"1\""
                    + "                                style=\""
                    + "                                  width: 150px;"
                    + "                                  font-size: 14px;"
                    + "                                  font-weight: bold;"
                    + "                                  border-bottom: 1px solid #eee;"
                    + "                                  padding: 16px 0px 15px 20px;"
                    + "                                  text-align: left;"
                    + "                                \""
                    + "                              >"
                    + "                                플랜명"
                    + "                              </th>"
                    + "                              <td"
                    + "                                style=\""
                    + "                                  font-size: 14px;"
                    + "                                  color: #999;"
                    + "                                  line-height: 22px;"
                    + "                                  border-bottom: 1px solid #eee;"
                    + "                                  padding: 16px 0 15px 0;"
                    + "                                \""
                    + "                              >"
                    + "                                " + planName + ""
                    + "                              </td>"
                    + "                            </tr>"
                    + "                            <tr>"
                    + "                              <th"
                    + "                                colspan=\"1\""
                    + "                                rowspan=\"1\""
                    + "                                style=\""
                    + "                                  width: 150px;"
                    + "                                  font-size: 14px;"
                    + "                                  font-weight: bold;"
                    + "                                  border-bottom: 1px solid #eee;"
                    + "                                  padding: 16px 0px 15px 20px;"
                    + "                                  text-align: left;"
                    + "                                \""
                    + "                              >"
                    + "                                파티 기간"
                    + "                              </th>"
                    + "                              <td"
                    + "                                style=\""
                    + "                                  font-size: 14px;"
                    + "                                  color: #999;"
                    + "                                  line-height: 22px;"
                    + "                                  border-bottom: 1px solid #eee;"
                    + "                                  padding: 16px 0 15px 0;"
                    + "                                \""
                    + "                              >"
                    + "                                " + partyPeriod + ""
                    + "                              </td>"
                    + "                            </tr>"
                    + "                            <tr>"
                    + "                              <th"
                    + "                                colspan=\"1\""
                    + "                                rowspan=\"1\""
                    + "                                style=\""
                    + "                                  width: 150px;"
                    + "                                  font-size: 14px;"
                    + "                                  font-weight: bold;"
                    + "                                  border-bottom: 1px solid #eee;"
                    + "                                  padding: 16px 0px 15px 20px;"
                    + "                                  text-align: left;"
                    + "                                \""
                    + "                              >"
                    + "                                매월 요금"
                    + "                              </th>"
                    + "                              <td"
                    + "                                style=\""
                    + "                                  font-size: 14px;"
                    + "                                  color: #999;"
                    + "                                  line-height: 22px;"
                    + "                                  border-bottom: 1px solid #eee;"
                    + "                                  padding: 16px 0 15px 0;"
                    + "                                \""
                    + "                              >"
                    + "                                 " + monthlyFee + ""
                    + "                              </td>"
                    + "                            </tr>"
                    + "                          </tbody>"
                    + "                        </table>"
                    + "                        <div"
                    + "                          style=\""
                    + "                            font-size: 14px;"
                    + "                            color: #999;"
                    + "                            margin: 35px 0 60px;"
                    + "                            line-height: 22px;"
                    + "                          \""
                    + "                        >"
                    + "                          COPYRIGHTS (C)BUDDY ALL RIGHTS RESERVED."
                    + "                        </div>"
                    + "                      </div>"
                    + "                    </td>"
                    + "                  </tr>"
                    + "                </tbody>"
                    + "              </table>"
                    + "            </td>"
                    + "          </tr>"
                    + "        </tbody>"
                    + "      </table>"
                    + "    </div>";

            emailService.sendEmail(email, subject, htmlContent);
            log.info("Sent HTML email to: {}", email);  // 로그 확인용
        }

        return ApiResult.success("파티 구성 완료 이메일이 성공적으로 발송되었습니다.");
    }


    /*
    파티 정보 조회
    */
    public ApiResult<PartyInfoResponse> getPartyInfo(HttpServletRequest request, Long partyId) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        Party party = partyRepository.findByPartyId(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        List<PartyMember> partyMembers = partyMemberRepository.findByParty(party);

        List<PartyInfoResponse.MemberDto> memberDtos = partyMembers.stream()
                .map(partyMember -> PartyInfoResponse.MemberDto.builder()
                        .memberId(partyMember.getMember().getMemberId())
                        .username(partyMember.getMember().getUsername())
                        .build())
                .collect(Collectors.toList());

        PartyInfoResponse.ServiceDto serviceDto = PartyInfoResponse.ServiceDto.builder()
                .name(party.getPlan().getService().getServiceName())
                .build();

        PartyInfoResponse.PlanDto planDto = PartyInfoResponse.PlanDto.builder()
                .planId(party.getPlan().getId())
                .name(party.getPlan().getPlanName())
                .monthlyFee(party.getPlan().getMonthlyFee())
                .build();

        LocalDate today = LocalDate.now();
        PartyInfoResponse.AccountDto accountDto;

        boolean isLeader = userId.equals(party.getMember().getUserId());

        if (party.getProgressStatus() == false && !isLeader && today.isBefore(party.getStartDate().toLocalDate())) {
            accountDto = PartyInfoResponse.AccountDto.builder()
                    .id(null)
                    .pwd(null)
                    .build();
        } else {
            accountDto = PartyInfoResponse.AccountDto.builder()
                    .id(party.getLeaderId())
                    .pwd(party.getLeaderPwd())
                    .build();
        }

        PartyInfoResponse.PartyDto partyDto = PartyInfoResponse.PartyDto.builder()
                .partyId(party.getPartyId())
                .recLimit(party.getRecLimit())
                .currentRecNum(party.getCurrentRecNum())
                .progressStatus(party.getProgressStatus())
                .startDate(party.getStartDateISOString())
                .durationMonth(party.getDurationMonth())
                .endDate(party.getEndDateISOString())
                .partyLeaderMemberId(party.getMember().getMemberId())
                .myMemberId(member.getMemberId()) // 현재 로그인한 회원의 아이디
                .account(accountDto)
                .members(memberDtos)
                .maxMemberNum(party.getPlan().getMaxMemberNum())
                .build();

        return ApiResult.success(PartyInfoResponse.builder()
                .service(serviceDto)
                .plan(planDto)
                .party(partyDto)
                .build());
    }

    /*
    현재 매칭 대기 중인 모든 파티의 파티원 총 인원수 합산 값 조회
     */
    public ApiResult<?> getWaitingMembersCount() {
        LocalDateTime now = LocalDateTime.now();
        Integer count = partyRepository.getWaitingMembersCount(now);
        return ApiResult.success(count != null ? count : 0);
    }
}