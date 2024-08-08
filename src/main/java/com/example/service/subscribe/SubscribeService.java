package com.example.service.subscribe;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.community.Tag;
import com.example.domain.member.Member;
import com.example.domain.subscribe.TagSub;
import com.example.dto.response.TagSubInfoResponse;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.community.PostRepository;
import com.example.repository.community.TagRepository;
import com.example.repository.member.MemberRepository;
import com.example.repository.subscribe.TagSubRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@Transactional
@RequiredArgsConstructor
@Slf4j

public class SubscribeService {

    private final MemberRepository memberRepository;
    private final TagRepository tagRepository;
    private final TagSubRepository tagSubRepository;
    private final PostRepository postRepository;
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

        if (tagSubRepository.findByMember_MemberIdAndTag_Id(member.getMemberId(), tag.getId()).isPresent()) {
            throw new CustomException(ErrorCode.ALREADY_SUBSCRIBED);
        }

        TagSub tagSub = new TagSub(member, tag);
        tagSubRepository.save(tagSub);

        return ApiResult.success("태그 구독이 성공적으로 처리되었습니다.");
    }

    /*
    태그 구독 취소하기
    */
    @Transactional
    public ApiResult<?> unsubscribeTag(HttpServletRequest request, Long tagId) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Tag tag = tagRepository.findById(tagId)
                .orElseThrow(() -> new CustomException(ErrorCode.TAG_NOT_FOUND));

        TagSub tagSub = tagSubRepository.findByMember_MemberIdAndTag_Id(member.getMemberId(), tag.getId())
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        tagSubRepository.delete(tagSub);

        return ApiResult.success("태그 구독이 성공적으로 취소되었습니다.");
    }


    /*
    회원의 구독 태그 목록 조회
    */
    @Transactional(readOnly = true)
    public ApiResult<?> getSubscribedTags(HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        List<TagSub> subscribedTags = tagSubRepository.findByMember_MemberId(member.getMemberId());

        List<TagSubInfoResponse> tagResponses = subscribedTags.stream()
                .map(tagSub -> TagSubInfoResponse.builder()
                        .tagId(tagSub.getTag().getId())
                        .tags(tagSub.getTag().getTagName())
                        .isReceiveNotification(tagSub.getSubNotify())
                        .postCount(postRepository.countPostsByTagId(tagSub.getTag().getId()))
                        .build())
                .collect(Collectors.toList());

        return ApiResult.success(tagResponses);
    }

    /*
    태그 신규 게시글 알림 받기
    */
    @Transactional
    public ApiResult<?> enableTagNotification(HttpServletRequest request, Long tagId) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        TagSub tagSub = tagSubRepository.findByMember_MemberIdAndTag_Id(member.getMemberId(), tagId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        if (Boolean.TRUE.equals(tagSub.getSubNotify())) {
            throw new CustomException(ErrorCode.ALREADY_ON_NOTIFY);
        }

        tagSub.setSubNotify(true);
        tagSubRepository.save(tagSub);

        return ApiResult.success("해당 태그에 대한 신규 게시글 알림이 켜졌습니다.");
    }

    /*
    태그 신규 게시글 알림 끄기
    */
    @Transactional
    public ApiResult<?> disableTagNotification(HttpServletRequest request, Long tagId) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        TagSub tagSub = tagSubRepository.findByMember_MemberIdAndTag_Id(member.getMemberId(), tagId)
                .orElseThrow(() -> new CustomException(ErrorCode.SUBSCRIPTION_NOT_FOUND));

        if (Boolean.FALSE.equals(tagSub.getSubNotify())) {
            throw new CustomException(ErrorCode.ALREADY_OFF_NOTIFY);
        }

        tagSub.setSubNotify(false);
        tagSubRepository.save(tagSub);

        return ApiResult.success("해당 태그에 대한 신규 게시글 알림이 꺼졌습니다.");
    }
}