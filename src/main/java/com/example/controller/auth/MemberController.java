package com.example.controller.auth;

import com.example.api.ApiResult;
import com.example.dto.request.PointModifyRequest;
import com.example.service.auth.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "멤버 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/auth/member")
public class MemberController {
    private final MemberService memberService;

    /*
    사용자 정보 조희
     */
    @GetMapping
    @Operation(summary = "사용자 정보 조회", description = "토큰을 통해 사용자 정보를 조회합니다.")
    public ApiResult<?> getMemberInfo(HttpServletRequest request) {
        return memberService.getMemberInfo(request);
    }

     /*
    회원 탈퇴
     */
    @DeleteMapping
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴시킵니다.")
    public ApiResult<?> deleteMember(HttpServletRequest request){

        return memberService.deleteMember(request);
    }

    /*
    포인트수정
     */
    @PatchMapping("/point")
    @Operation(summary = "포인트 수정", description = "")
    public ApiResult<?> modifyPoint(HttpServletRequest request, @RequestBody PointModifyRequest pointModifyRequest){

        return memberService.modifyPoint(request, pointModifyRequest);
    }

}
