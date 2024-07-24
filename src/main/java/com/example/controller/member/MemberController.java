package com.example.controller.member;

import com.example.api.ApiResult;
import com.example.dto.request.AddPointRequest;
import com.example.dto.request.ChangeEmailRequest;
import com.example.dto.request.ChangePwdRequest;
import com.example.dto.request.ChangeUsernameRequest;
import com.example.dto.request.ChangeProfileImageRequest;
import com.example.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Tag(name = "멤버 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/member")
public class MemberController {
    private final MemberService memberService;

    /*
    사용자 정보 조희
     */
    @GetMapping("/member-info")
    @Operation(summary = "사용자 정보 조회", description = "토큰을 통해 사용자 정보를 조회합니다.")
    public ApiResult<?> getMemberInfo(@AuthenticationPrincipal UserDetails userDetails) {

        return memberService.getMemberInfo(userDetails.getUsername());
    }



    /*
    이메일 변경
     */
    @PatchMapping("/change-email")
    @Operation(summary = "이메일 변경", description = "")
    public ApiResult<?> changeEmail(@RequestBody ChangeEmailRequest changeEmailRequest,
                                    @AuthenticationPrincipal UserDetails userDetails){

        return memberService.changeEmail(changeEmailRequest, userDetails.getUsername());
    }

    /*
    비밀번호 변경
     */
    @PatchMapping("/change-pwd")
    @Operation(summary = "비밀번호 변경", description = "")
    public ApiResult<?> changePwd(@RequestBody ChangePwdRequest changePwdRequest,
                                       @AuthenticationPrincipal UserDetails userDetails){

        return memberService.changePwd(changePwdRequest, userDetails.getUsername());
    }

    /*
    이름 변경
     */
    @PatchMapping("/change-username")
    @Operation(summary = "이름 변경", description = "")
    public ApiResult<?> changeUsername(@RequestBody ChangeUsernameRequest changeUsernameRequest,
                                       @AuthenticationPrincipal UserDetails userDetails){

        return memberService.changeUsername(changeUsernameRequest,userDetails.getUsername());
    }
    /*
    프로필 이미지 변경
    */
    @PatchMapping("/change-profile-image")
    @Operation(summary = "프로필 이미지 변경", description = "")
    public ApiResult<?> changeProfileImage(@RequestBody ChangeProfileImageRequest changeProfileImageRequest,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        return memberService.changeProfileImage(changeProfileImageRequest, userDetails.getUsername());
    }




}
