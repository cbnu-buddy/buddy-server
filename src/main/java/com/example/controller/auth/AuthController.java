package com.example.controller.auth;

import com.example.api.ApiResult;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.SignUpRequest;
import com.example.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API")
@Slf4j
@RestController
//@RequestMapping
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;


    /*
    회원가입 post 요청
     */
    @PostMapping("/public/auth/signup")
    @Operation(summary = "회원가입", description = "아이디, 이메일 중복 요청의 경우 409 에러를 반환한다.")
    public ApiResult<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {

        return authService.signUp(signUpRequest);
    }

    /*
    로그인 post 요청
     */
    @PostMapping("/public/auth/login")
    @Operation(summary = "로그인", description = "Http 응답 헤더에 " +
            "(Authorization : 엑세스 토큰 / refresh-token : 리프레시 토큰 / refresh-token-exp-time : 리프레시 토큰 만료시간) 삽입")
    public ApiResult<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {

        return authService.login(loginRequest, response);
    }

    /*
    로그아웃
     */
    @GetMapping("/private/auth/logout")
    @Operation(summary = "로그아웃", description = "로그아웃이 정상적으로 작동하지 않은 경우 500 에러를 반환한다.")
    public ApiResult<?> logout(HttpServletRequest request, HttpServletResponse response) {

        return authService.logout(request, response);
    }

    /*
    회원 탈퇴
    */
    @DeleteMapping("/private/auth/withdraw")
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴시킵니다.")
    public ApiResult<?> deleteMember(HttpServletRequest request, HttpServletResponse response, @AuthenticationPrincipal UserDetails userDetails){

        return authService.deleteMember(request, response, userDetails.getUsername());
    }

}



//    @GetMapping("/test")
//    public ApiResult<?> test(){
//        return ApiResult.success("성공");
//    }

//    @RequestMapping("/forbidden")
//    public String forbidden(){
//        throw new CustomException(ErrorCode.MEMBER_NO_PERMISSION);
//    }
//
//    @RequestMapping("/unauthorized")
//    public String unauthorized(){
//        throw new CustomException(ErrorCode.UNAUTHORIZED);
//        }
