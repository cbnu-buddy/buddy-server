package com.example.config.jwt.errorHandler;

import jakarta.servlet.ServletException; // 서블릿 예외 처리를 위한 import 문
import jakarta.servlet.http.HttpServletRequest; // HTTP 요청 객체를 사용하기 위한 import 문
import jakarta.servlet.http.HttpServletResponse; // HTTP 응답 객체를 사용하기 위한 import 문
import lombok.extern.slf4j.Slf4j; // Lombok을 사용하여 로깅을 위한 import 문
import org.springframework.security.access.AccessDeniedException; // Spring Security의 권한 거부 예외 클래스
import org.springframework.security.web.access.AccessDeniedHandler; // Spring Security의 권한 거부 처리 핸들러 인터페이스
import org.springframework.stereotype.Component; // Spring의 컴포넌트로 등록하기 위한 import 문

import java.io.IOException; // 입출력 예외 처리를 위한 import 문

@Component
@Slf4j
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    // 권한이 없는 사용자가 자원에 접근할 때 호출되는 메서드
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

//        // 로그에 권한 없는 사용자의 접근을 기록
//        log.error("JwtAccessDeniedHandler.class / 권한 없는 유저의 접근");
//
//        // 접근 거부 시 사용자를 원하는 페이지로 리다이렉트
//        response.sendRedirect("/forbidden");
    }
}
