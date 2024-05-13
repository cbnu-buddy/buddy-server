package com.example.service.member;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.member.Member;
import com.example.dto.request.ChangeEmailRequest;
import com.example.dto.request.ChangePwdRequest;
import com.example.dto.request.ChangeUsernameRequest;
import com.example.dto.request.PointModifyRequest;
import com.example.dto.response.MemberInfoResponse;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.member.MemberRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
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
    private final TokenProvider tokenProvider;
    private final PasswordEncoder passwordEncoder;

    /*
    토큰에서 회원 ID(sub) 추출
    */
    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        Claims claims = tokenProvider.getTokenClaims(tokenProvider.resolveToken(request));
        return claims.getSubject();
    }

    /*
    회원 정보 조회
    */
    public ApiResult<?> getMemberInfo(HttpServletRequest request) {
            String userId = getUserIdFromToken(request);
            Member member = memberRepository.findByUserId(userId)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회원입니다."));

            MemberInfoResponse memberInfoResponse = new MemberInfoResponse(
                    member.getUserId(),
                    member.getEmail(),
                    member.getUsername(),
                    member.getPoint()
            );

            return ApiResult.success(memberInfoResponse);
    }

    /*
    회원 탈퇴
    */
    @Transactional
    public ApiResult<?> deleteMember(HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        memberRepository.delete(member);

        return ApiResult.success("회원 탈퇴가 성공적으로 처리되었습니다.");
    }

    /*
    포인트 수정
    */
    @Transactional
    public ApiResult<?> modifyPoint(HttpServletRequest request, PointModifyRequest pointModifyRequest) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        // 회원의 기존 포인트와 SignUpRequest에서 가져온 정보를 더하여 계산
        int totalPoint = member.getPoint() + pointModifyRequest.getPoint();
        member.setPoint(totalPoint);

        return ApiResult.success("포인트 수정이 완료되었습니다");
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

        // 이름 변경
        member.changeUsername(changeUsernameRequest.getNewUsername());

        return ApiResult.success("이름이 변경되었습니다.");
    }

}
