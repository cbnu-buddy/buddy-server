package com.example.controller.auth;

import com.example.api.ApiResult;
import com.example.dto.request.LoginRequest;
import com.example.dto.request.PointModifyRequest;
import com.example.dto.request.SignUpRequest;
import com.example.config.jwt.TokenProvider;
import com.example.service.auth.AuthService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "인증 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;


    /*
    회원가입 post 요청
     */
    @PostMapping("/signup")
    @Operation(summary = "회원가입", description = "아이디, 이메일 중복 요청의 경우 409 에러를 반환한다.")
    public ApiResult<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest){

        return authService.signUp(signUpRequest);
    }

    /*
    로그인 post 요청
     */
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "Http 응답 헤더에 " +
            "(Authorization : 엑세스 토큰 / refresh-token : 리프레시 토큰 / refresh-token-exp-time : 리프레시 토큰 만료시간) 삽입")
    public ApiResult<?> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response){

        return authService.login(loginRequest, response);
    }

    /*
    로그아웃
     */
    @GetMapping("/logout")
    @Operation(summary = "로그아웃", description = "로그아웃이 정상적으로 작동하지 않은 경우 500 에러를 반환한다.")
    public ApiResult<?> logout(HttpServletRequest request, HttpServletResponse response){

        return authService.logout(request, response);
    }

    /*
    포인트수정
     */
    @PatchMapping("/point")
    @Operation(summary = "포인트 수정", description = "")
    public ApiResult<?> modifyPoint(@RequestBody PointModifyRequest pointModifyRequest){

        return authService.modifyPoint(pointModifyRequest);
    }

    /*
    사용자 정보 조희
     */
    @GetMapping("/member")
    @Operation(summary = "사용자 정보 조회", description = "토큰을 통해 사용자 정보를 조회합니다.")
    public ApiResult<?> getMemberInfo(HttpServletRequest request) {
        return authService.getMemberInfo(request);
    }



    /*
    회원 탈퇴
     */
    @DeleteMapping("/member")
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴시킵니다.")
    public ApiResult<?> deleteMember(@RequestParam Long memberId){

        return authService.deleteMember(memberId);
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

}
