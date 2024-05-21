package com.example.controller.member;

import com.example.api.ApiResult;
import com.example.dto.request.ChangeEmailRequest;
import com.example.dto.request.ChangePwdRequest;
import com.example.dto.request.ChangeUsernameRequest;
import com.example.dto.request.PointModifyRequest;
import com.example.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "멤버 API")
@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    /*
    사용자 정보 조희
     */
    @GetMapping("/private/member/member-info")
    @Operation(summary = "회원 정보 조회", description = "토큰을 통해 사용자 정보를 조회합니다.")
    public ApiResult<?> getMemberInfo(HttpServletRequest request) {
        return memberService.getMemberInfo(request);
    }

     /*
    회원 탈퇴
     */
    @DeleteMapping("/private/member/withdraw")
    @Operation(summary = "회원 탈퇴", description = "회원을 탈퇴시킵니다.")
    public ApiResult<?> deleteMember(HttpServletRequest request){

        return memberService.deleteMember(request);
    }

    /*
    포인트 수정
     */
    @PatchMapping("/private/member/point")
    @Operation(summary = "포인트 수정", description = "")
    public ApiResult<?> modifyPoint(HttpServletRequest request, @RequestBody PointModifyRequest pointModifyRequest){

        return memberService.modifyPoint(request, pointModifyRequest);
    }

    /*
    이메일 변경
     */
    @PatchMapping("/private/member/change-email")
    @Operation(summary = "이메일 변경", description = "")
    public ApiResult<?> changeEmail(@RequestBody ChangeEmailRequest changeEmailRequest,
                                    @AuthenticationPrincipal UserDetails userDetails){

        return memberService.changeEmail(changeEmailRequest, userDetails.getUsername());
    }

    /*
    비밀번호 변경
     */
    @PatchMapping("/private/member/change-pwd")
    @Operation(summary = "비밀번호 변경", description = "")
    public ApiResult<?> changePwd(@RequestBody ChangePwdRequest changePwdRequest,
                                       @AuthenticationPrincipal UserDetails userDetails){

        return memberService.changePwd(changePwdRequest, userDetails.getUsername());
    }

    /*
    이름 변경
     */
    @PatchMapping("/private/member/change-username")
    @Operation(summary = "이름 변경", description = "")
    public ApiResult<?> changeUsername(@RequestBody ChangeUsernameRequest changeUsernameRequest,
                                       @AuthenticationPrincipal UserDetails userDetails){

        return memberService.changeUsername(changeUsernameRequest,userDetails.getUsername());
    }


}
