package com.example.service.auth;

import com.example.api.ApiResult;
import com.example.config.auth.Authenticator;
import com.example.config.cookie.CookieManager;
import com.example.config.jwt.TokenProvider;
import com.example.config.redis.RedisUtil;
import com.example.domain.member.Member;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.SignUpRequest;
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

import java.util.Optional;

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

        Optional<Member> member = memberRepository.findByEmail(signUpRequest.getEmail());

        // 이미 가입한 이메일 주소이면
        if(member.isPresent()){
            throw new CustomException(ErrorCode.ALREADY_EXIST);
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
            Authentication authentication = authenticator.createAuthenticationByIdPassword(loginRequest.getEmail(), loginRequest.getPwd());

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
