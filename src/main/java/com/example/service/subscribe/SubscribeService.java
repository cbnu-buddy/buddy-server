package com.example.service.subscribe;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.community.Tag;
import com.example.domain.member.Member;
import com.example.domain.subscribe.TagSub;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.community.TagRepository;
import com.example.repository.member.MemberRepository;
import com.example.repository.subscribe.TagSubRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

@org.springframework.stereotype.Service
@Transactional
@RequiredArgsConstructor
@Slf4j

public class SubscribeService {

    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final TagSubRepository tagSubRepository;
    private final TokenProvider tokenProvider;

    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        Claims claims = tokenProvider.getTokenClaims(tokenProvider.resolveToken(request));
        return claims.getSubject();
    }

    /*
    태그 구독하기
     */
    @Transactional
    public ApiResult<?> subscribeTag(HttpServletRequest request, Long tagId) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));

        boolean alreadySubscribed = tagSubRepository.findByMember_MemberIdAndTag_Id(member.getMemberId(), tag.getId()).isPresent();
        if (alreadySubscribed) {
            return ApiResult.success("이미 해당 태그를 구독 중입니다.");
        }

        TagSub tagSub = new TagSub(member, tag);
        tagSubRepository.save(tagSub);

        return ApiResult.success("태그 구독이 성공적으로 처리되었습니다.");
    }
}
