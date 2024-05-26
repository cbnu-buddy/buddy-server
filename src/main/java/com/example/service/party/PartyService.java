package com.example.service.party;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.member.Member;
import com.example.domain.party.Party;
import com.example.domain.party.PartyMember;
import com.example.domain.plan.Plan;
import com.example.dto.request.ChangePartyAccountRequest;
import com.example.dto.request.ChangePartyRecLimitRequest;
import com.example.dto.request.CreatePartyRequest;
import com.example.dto.response.MyPartyInfoResponse;
import com.example.dto.response.UnmatchedPartiesInfoResponse;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.member.MemberRepository;
import com.example.repository.party.PartyMemberRepository;
import com.example.repository.party.PartyRepository;
import com.example.repository.plan.PlanRepository;
import com.example.repository.service.ServiceRepository;
import com.example.service.email.EmailService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PartyService {
    private final PartyRepository partyRepository;
    private final ServiceRepository serviceRepository;
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
                .orElseThrow(() -> new CustomException(ErrorCode.PlAN_NOT_FOUND));

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

        partyRepository.delete(party);

        return ApiResult.success("파티 해산이 성공적으로 처리되었습니다.");
    }

    /*
    파티 로그인 정보 변경
    */
    @Transactional
    public ApiResult<?> changePartyAccount(ChangePartyAccountRequest changePartyAccountRequest, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Long partyId = changePartyAccountRequest.getPartyId();
        Party party = verifyMemberAndGetParty(userId, partyId);

        party.changeLeaderId(changePartyAccountRequest.getNewLeaderId());
        party.changeLeaderPwd(changePartyAccountRequest.getNewLeaderPwd());

        return ApiResult.success("파티 로그인 정보 변경이 성공적으로 처리되었습니다.");
    }

    /*
    파티 모집 인원 변경
    */
    @Transactional
    public ApiResult<?> changePartyRecLimit(ChangePartyRecLimitRequest changePartyRecLimitRequest, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Long partyId = changePartyRecLimitRequest.getPartyId();
        Party party = verifyMemberAndGetParty(userId, partyId);

        party.changeRecLimit(changePartyRecLimitRequest.getNewRecLimit());

        return ApiResult.success("파티 모집 인원 변경이 성공적으로 처리되었습니다.");
    }

    /*
    특정 서비스 내 매칭이 완료되지 않은 파티 목록 정보 조회
    */
    @Transactional
    public ApiResult<?> getUnmatchedParties(Long serviceId) {
        serviceRepository.findById(serviceId)
                .orElseThrow(() -> new CustomException(ErrorCode.PlAN_NOT_FOUND));
        List<Party> parties = partyRepository.findUnmatchedPartiesByServiceId(serviceId);
        List<UnmatchedPartiesInfoResponse> response = parties.stream().map(party -> UnmatchedPartiesInfoResponse.builder()
                .service(UnmatchedPartiesInfoResponse.ServiceDto.builder()
                        .name(party.getPlan().getService().getServiceName())
                        .build())
                .plan(UnmatchedPartiesInfoResponse.PlanDto.builder()
                        .name(party.getPlan().getPlanName())
                        .monthlyFee(party.getPlan().getMonthlyFee())
                        .build())
                .party(UnmatchedPartiesInfoResponse.PartyDto.builder()
                        .partyId(party.getPartyId())
                        .startDate(party.getStartDate())
                        .durationMonth(party.getDurationMonth())
                        .endDate(party.getEndDate())
                        .monthlyFee(party.getPlan().getMonthlyFee())
                        .build())
                .build()).collect(Collectors.toList());

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
        List<Party> parties = partyMembers.stream()
                .map(PartyMember::getParty)
                .collect(Collectors.toList());

        List<MyPartyInfoResponse> response = parties.stream().map(party -> MyPartyInfoResponse.builder()
                .service(MyPartyInfoResponse.ServiceDto.builder()
                        .name(party.getPlan().getService().getServiceName())
                        .build())
                .plan(MyPartyInfoResponse.PlanDto.builder()
                        .name(party.getPlan().getPlanName())
                        .build())
                .party(MyPartyInfoResponse.PartyDto.builder()
                        .partyId(party.getPartyId())
                        .startDate(party.getStartDate())
                        .durationMonth(party.getDurationMonth())
                        .endDate(party.getEndDate())
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

        return ApiResult.success("파티 가입이 성공적으로 처리되었습니다.");
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
        for (PartyMember partyMember : partyMembers) {
            Member member = partyMember.getMember();
            String email = member.getEmail();
            emailService.sendEmail(email, "파티 구성 완료", "당신이 가입한 파티의 구성이 완료되었습니다.");
            log.info("Sent email to: {}", email);  // 로그 확인용임
        }

        return ApiResult.success("파티 구성 완료 이메일이 성공적으로 발송되었습니다.");
    }
}
