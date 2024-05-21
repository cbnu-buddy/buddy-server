package com.example.service.party;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.member.Member;
import com.example.domain.party.Party;
import com.example.domain.plan.Plan;
import com.example.dto.request.CreatePartyRequest;
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

import java.util.NoSuchElementException;

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

    @Transactional
    public ApiResult<?> deleteParty(Long partyId) {
        Party party = partyRepository.findByPartyId(partyId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARTY_NOT_FOUND));

        partyRepository.delete(party);

        return ApiResult.success("파티 해산이 성공적으로 처리되었습니다.");
    }
}
