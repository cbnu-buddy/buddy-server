package com.example.config.cookie;

import jakarta.servlet.http.HttpServletResponse; // HttpServletResponse를 사용하기 위한 import 문

import org.springframework.beans.factory.annotation.Value; // Spring Framework에서 값을 주입하기 위한 import 문
import org.springframework.http.ResponseCookie; // Spring의 ResponseCookie 클래스를 사용하기 위한 import 문
import org.springframework.stereotype.Component; // Spring의 컴포넌트로 등록하기 위한 import 문

@Component
public class CookieManager {

    @Value("#{environment['jwt.access-exp-time']}")
    private int accessTokenExpTime; // 접근 토큰의 만료 시간을 주입받는 변수

    @Value("#{environment['jwt.refresh-exp-time']}")
    private int refreshTokenExpTime; // 리프레시 토큰의 만료 시간을 주입받는 변수

    /*
     * 주어진 이름과 값으로 쿠키를 생성하여 HttpServletResponse에 추가합니다.
     * @param name 쿠키의 이름
     * @param value 쿠키의 값
     * @param isLogout 로그아웃 여부 (true: 로그아웃 상태, false: 로그인 상태)
     * @param response HTTP 응답 객체
     */
    public void setCookie(String name, String value, boolean isLogout, HttpServletResponse response){

        int expTime = 0; // 쿠키의 만료 시간(초)을 저장할 변수 초기화

        // 로그아웃 상태가 아니고 이름이 "Authorization"인 경우 접근 토큰의 만료 시간 설정
        if(!isLogout && name.equals("Authorization")){
            expTime = accessTokenExpTime / 1000; // 밀리초를 초로 변환하여 설정
        }
        // 로그아웃 상태가 아니고 이름이 "refresh-token"인 경우 리프레시 토큰의 만료 시간 설정
        else if(!isLogout && name.equals("refresh-token")){
            expTime = refreshTokenExpTime / 1000; // 밀리초를 초로 변환하여 설정
        }

        // ResponseCookie 객체를 생성하여 쿠키 속성 설정
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .path("/") // 쿠키의 유효 경로 설정
                .maxAge(expTime) // 쿠키의 만료 시간 설정
                .httpOnly(true) // JavaScript에서 쿠키 접근 제한 설정 (보안 강화)
                .secure(true) // 쿠키는 HTTPS를 통해서만 전송되어야 함
                .sameSite("Strict") // 쿠키는 같은 사이트에서만 보내짐 (CSRF 방지)
                .build(); // ResponseCookie 객체 생성 완료

        // 생성된 쿠키를 HTTP 응답 헤더에 추가
        response.addHeader("Set-Cookie", cookie.toString());
    }
}