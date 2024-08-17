package com.example.service.member;

import com.example.api.ApiResult;
import com.example.domain.member.Member;
import com.example.domain.point.Point;
import com.example.dto.request.*;
import com.example.dto.response.MemberInfoResponse;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.member.MemberRepository;
import com.example.repository.point.PointRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    /*
    회원 정보 조회
    */
    public ApiResult<?> getMemberInfo(String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

        MemberInfoResponse memberInfoResponse = new MemberInfoResponse(
                member.getMemberId(),
                member.getEmail(),
                member.getUsername(),
                member.getPoint(),
                member.getProfile_path()
        );

        return ApiResult.success(memberInfoResponse);
    }


    /*
    이메일 수정
     */
    @Transactional
    public ApiResult<?> changeEmail(ChangeEmailRequest changeEmailRequest, String userId){

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<Member> emailCheckMember = memberRepository.findByEmail(changeEmailRequest.getNewEmail());

        // 이미 존재하는 이메일이면 예외
        if(emailCheckMember.isPresent()){
            throw new CustomException(ErrorCode.ALREADY_EXIST_EMAIL);
        }

        member.changeEmail(changeEmailRequest.getNewEmail());

        return ApiResult.success("이메일이 변경되었습니다.");
    }

    /*
    비밀번호 수정
     */
    @Transactional
    public ApiResult<?> changePwd(ChangePwdRequest changePwdRequest, String userId){

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 기존 비밀번호 확인. 일치하지 않으면 예외
        if(!passwordEncoder.matches(changePwdRequest.getOldPwd(), member.getPwd())){
            throw new CustomException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        // 비밀번호 변경
        member.changePwd(passwordEncoder, changePwdRequest.getNewPwd());

        return ApiResult.success("비밀번호가 변경되었습니다.");
    }

    /*
    이름 수정
     */
    @Transactional
    public ApiResult<?> changeUsername(ChangeUsernameRequest changeUsernameRequest, String userId){

        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Optional<Member> usernameCheckMember = memberRepository.findByUsername(changeUsernameRequest.getNewUsername());

        // 이미 존재하는 이름이면 예외
        if(usernameCheckMember.isPresent()){
            throw new CustomException(ErrorCode.ALREADY_EXIST_USERNAME);
        }

        // 이름 변경
        member.changeUsername(changeUsernameRequest.getNewUsername());

        return ApiResult.success("이름이 변경되었습니다.");
    }

    /*
    프로필 이미지 변경
     */
    @Transactional
    public ApiResult<?> changeProfileImage(ChangeProfileImageRequest changeProfileImageRequest, String userId) {
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        member.setProfile_path(changeProfileImageRequest.getProfileImagePathUrl());
        memberRepository.save(member);

        return ApiResult.success("프로필 이미지가 변경되었습니다");
    }

}
