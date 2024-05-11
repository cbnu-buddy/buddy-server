package com.example.service.auth;

import com.example.api.ApiResult;
import com.example.config.auth.Authenticator;
import com.example.config.cookie.CookieManager;
import com.example.config.jwt.TokenProvider;
import com.example.config.redis.RedisUtil;
import com.example.domain.member.Member;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.PointModifyRequest;
import com.example.dto.request.SignUpRequest;
import com.example.dto.response.MemberInfoResponse;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final CookieManager cookieManager;
    private final Authenticator authenticator;
    private final RedisUtil redisUtil;

    @Value("#{environment['jwt.refresh-exp-time']}")
    private long refreshTokenExpTime;              //리프레쉬 토큰 유효기간


    /*
    회원가입
     */
    @Transactional
    public ApiResult<?> signUp(SignUpRequest signUpRequest){

        Optional<Member> memberByEmail = memberRepository.findByEmail(signUpRequest.getEmail());
        Optional<Member> memberByUserId = memberRepository.findByUserId(signUpRequest.getUserId());

        // 이미 가입한 사용자 ID이면
        if (memberByUserId.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_USERID);
        }

        // 이미 가입한 이메일 주소이면
        if (memberByEmail.isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_EXIST_EMAIL);
        }

        Member joinMember = signUpRequest.toEntity();
        joinMember.encodePassword(passwordEncoder);         //비밀번호 인코딩해서 저장

        memberRepository.save(joinMember);

        log.info("MemberService.class / signUp : 회원가입 신청");

        return ApiResult.success("회원가입 신청이 성공적으로 처리되었습니다.");
    }

    /*
    로그인
     */
    @Transactional
    public ApiResult<?> login(LoginRequest loginRequest, HttpServletResponse response){

        try{
            Authentication authentication = authenticator.createAuthenticationByIdPassword(loginRequest.getUserId(), loginRequest.getPwd());

            String accessToken = tokenProvider.createAccessToken(authentication);
            String refreshToken = tokenProvider.createRefreshToken(authentication);

            //레디스에 리프레시 토큰 저장
            redisUtil.setData(refreshToken, "refresh-token", refreshTokenExpTime);

            cookieManager.setCookie("Authorization", accessToken, false, response);
            cookieManager.setCookie("refresh-token", refreshToken, false, response);

        } catch (Exception e){
            throw new CustomException(ErrorCode.ID_PASSWORD_NOT_MATCH);
        }

        return ApiResult.success("로그인 되었습니다.");
    }

    /*
    로그아웃
     */
    public ApiResult<?> logout(HttpServletRequest request, HttpServletResponse response){

        tokenProvider.invalidateAccessToken(request);
        tokenProvider.invalidateRefreshToken(request);
        cookieManager.setCookie("Authorization", null, true, response);
        cookieManager.setCookie("refresh-token", null, true, response);

        return ApiResult.success("로그아웃 되었습니다");
    }

    /*
    포인트수정
     */
    @Transactional
    public ApiResult<?> modifyPoint(PointModifyRequest pointModifyRequest) {
        Member member = memberRepository.findById(pointModifyRequest.getMemberId())
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 회원의 기존 포인트와 SignUpRequest에서 가져온 정보를 더하여 계산
        int totalPoint = member.getPoint() + pointModifyRequest.getPoint();
        member.setPoint(totalPoint);
        return ApiResult.success("포인트 수정이 완료되었습니다");
    }

    /*
    회원 정보 수정
     */
    @Transactional(readOnly = true)
    public ApiResult<?> getMemberInfo(Long memberId) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

            MemberInfoResponse memberInfoResponse = new MemberInfoResponse(
                    member.getUserId(),
                    member.getEmail(),
                    member.getUsername(),
                    member.getPoint()
            );

            return ApiResult.success(memberInfoResponse);
    }

    @Transactional
    public ApiResult<?> deleteMember(Long memberId) {
        // 회원 ID로 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 회원 삭제
        memberRepository.delete(member);

        return ApiResult.success("회원 탈퇴가 성공적으로 처리되었습니다.");
    }
//    // 인증 정보 발급 및 security context 등록 + 새로운 토큰 발급
//    public TokenDto setAuthenticationSecurityContext(String loginId, HttpServletRequest request){
//
//        Authentication authentication = authenticator.createAuthentication(loginId);
//
//        String newAccessToken = tokenProvider.createAccessToken(authentication);
//        String newRefreshToken = tokenProvider.createRefreshToken(authentication);
//
//        tokenProvider.invalidateRefreshToken(request);
//
//        //레디스에 리프레시 토큰 저장
//        redisUtil.setData(newRefreshToken, "refresh-token", refreshTokenExpTime);
//
//        return new TokenDto(newAccessToken, newRefreshToken);
//    }


}
