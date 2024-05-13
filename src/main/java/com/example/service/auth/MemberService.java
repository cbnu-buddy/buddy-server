package com.example.service.auth;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.member.Member;
import com.example.dto.request.PointModifyRequest;
import com.example.dto.response.MemberInfoResponse;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.member.MemberRepository;
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
public class MemberService {

    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    /*
    토큰에서 회원 ID(sub) 추출
    */
    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        Claims claims = tokenProvider.getTokenClaims(tokenProvider.resolveToken(request));
        return claims.getSubject();
    }

    /*
    회원 정보 조회
    */
    @Transactional(readOnly = true)
    public ApiResult<?> getMemberInfo(HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        MemberInfoResponse memberInfoResponse = new MemberInfoResponse(
                member.getUserId(),
                member.getEmail(),
                member.getUsername(),
                member.getPoint()
        );

        return ApiResult.success(memberInfoResponse);
    }

    /*
    회원 탈퇴
    */
    @Transactional
    public ApiResult<?> deleteMember(HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        memberRepository.delete(member);

        return ApiResult.success("회원 탈퇴가 성공적으로 처리되었습니다.");
    }

    /*
    포인트 수정
    */
    @Transactional
    public ApiResult<?> modifyPoint(HttpServletRequest request, PointModifyRequest pointModifyRequest) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 회원의 기존 포인트와 SignUpRequest에서 가져온 정보를 더하여 계산
        int totalPoint = member.getPoint() + pointModifyRequest.getPoint();
        member.setPoint(totalPoint);

        return ApiResult.success("포인트 수정이 완료되었습니다");
    }
}
