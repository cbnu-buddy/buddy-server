package com.example.service.party;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.member.Member;
import com.example.domain.party.Party;
import com.example.domain.plan.Plan;
import com.example.dto.request.ChangePartyAccountRequest;
import com.example.dto.request.ChangePartyRecLimitRequest;
import com.example.dto.request.CreatePartyRequest;
import com.example.dto.response.UnmatchedPartiesInfoResponse;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.member.MemberRepository;
import com.example.repository.party.PartyRepository;
import com.example.repository.plan.PlanRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class PartyService {
    private final PartyRepository partyRepository;
    private final MemberRepository memberRepository;
    private final PlanRepository planRepository;
    private final TokenProvider tokenProvider;

    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        Claims claims = tokenProvider.getTokenClaims(tokenProvider.resolveToken(request));
        return claims.getSubject();
    }


    /*
    파티 생성하기
     */
    @Transactional
    public ApiResult<?> createParty(CreatePartyRequest createPartyRequest, HttpServletRequest request){
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        Long planId = createPartyRequest.getPlanId();
        Plan plan = planRepository.findById(planId)
                .orElse(null);

        if (plan == null) {
            throw new CustomException(ErrorCode.PlAN_NOT_FOUND);
        }

        Party joinParty = createPartyRequest.toEntity(member, plan);

        partyRepository.save(joinParty);

        return ApiResult.success("파티 생성이 성공적으로 처리되었습니다.");
    }

    /*
    파티 해산하기
    */
    @Transactional
    public ApiResult<?> deleteParty(Long partyId) {
        Party party = partyRepository.findByPartyId(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        partyRepository.delete(party);

        return ApiResult.success("파티 해산이 성공적으로 처리되었습니다.");
    }

    /*
    파티 로그인 정보 변경
    */
    @Transactional
    public ApiResult<?> changePartyAccount(ChangePartyAccountRequest changePartyAccountRequest) {
        Long partyId = changePartyAccountRequest.getPartyId();
        Party party = partyRepository.findByPartyId(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        party.changeLeaderId(changePartyAccountRequest.getNewLeaderId());
        party.changeLeaderPwd(changePartyAccountRequest.getNewLeaderPwd());

        return ApiResult.success("파티 로그인 정보 변경이 성공적으로 처리되었습니다.");
    }


    /*
    파티 모집 인원 변경
    */
    @Transactional
    public ApiResult<?> changePartyRecLimit(ChangePartyRecLimitRequest changePartyRecLimitRequest) {
        Long partyId = changePartyRecLimitRequest.getPartyId();
        Party party = partyRepository.findByPartyId(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        party.changeRecLimit(changePartyRecLimitRequest.getNewRecLimit());

        return ApiResult.success("파티 모집 인원 변경이 성공적으로 처리되었습니다.");
    }

    /*
    매칭이 완료되지 않은 파티 목록 정보 조회
    */
    @Transactional
    public ApiResult<?> getUnmatchedParties(Long serviceId) {
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



}
