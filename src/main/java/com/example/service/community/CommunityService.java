package com.example.service.community;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.community.*;
import com.example.domain.member.Member;
import com.example.domain.service.Service;
import com.example.dto.request.CreatePostRequest;
import com.example.dto.request.UpdatePostRequest;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.community.*;
import com.example.repository.member.MemberRepository;
import com.example.repository.service.ServiceRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@org.springframework.stereotype.Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class CommunityService {

    private final PostRepository postRepository;
    private final TagRepository tagRepository;
    private final PostTagRepository postTagRepository;
    private final PhotoRepository photoRepository;
    private final PostServiceRepository postServiceRepository;
    private final MemberRepository memberRepository;
    private final ServiceRepository serviceRepository;
    private final TokenProvider tokenProvider;

    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        Claims claims = tokenProvider.getTokenClaims(tokenProvider.resolveToken(request));
        return claims.getSubject();
    }

    @Transactional
    public ApiResult<?> createPost(CreatePostRequest postRequest, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Long memberId = member.getMemberId();

        Post post = new Post();
        post.setTitle(postRequest.getTitle());
        post.setContent(postRequest.getContent());
        post.setMemberId(memberId);
        post.setCreatedTime(LocalDateTime.now());
        post.setViews(0);

        post = postRepository.save(post);

        for (String tagName : postRequest.getTags()) {
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setTagName(tagName);
                        return tagRepository.save(newTag);
                    });

            PostTag postTag = new PostTag(post, tag);
            postTagRepository.save(postTag);
        }

        for (Long serviceId : postRequest.getServiceIds()) {
            Service service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_SERVICE_ID));
            PostService postService = new PostService(post, service);
            postServiceRepository.save(postService);
        }

        for (String photoPath : postRequest.getPostImagePathUrls()) {
            Photo photo = new Photo();
            photo.setPost(post);
            photo.setPhotoPath(photoPath);
            photoRepository.save(photo);
        }

        return ApiResult.success("게시글 작성이 완료되었습니다");
    }

    @Transactional
    public ApiResult<?> updatePost(Long postId, UpdatePostRequest updatePostRequest, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getMemberId().equals(member.getMemberId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        post.setTitle(updatePostRequest.getTitle());
        post.setContent(updatePostRequest.getContent());
        post.setCreatedTime(LocalDateTime.now());

        postTagRepository.deleteAll(post.getPostTags());
        postServiceRepository.deleteAll(post.getPostServices());
        photoRepository.deleteAll(post.getPhotos());

        for (String tagName : updatePostRequest.getTags()) {
            Tag tag = tagRepository.findByTagName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setTagName(tagName);
                        return tagRepository.save(newTag);
                    });

            PostTag postTag = new PostTag(post, tag);
            postTagRepository.save(postTag);
        }

        for (Long serviceId : updatePostRequest.getServiceIds()) {
            Service service = serviceRepository.findById(serviceId)
                    .orElseThrow(() -> new CustomException(ErrorCode.INVALID_SERVICE_ID));
            PostService postService = new PostService(post, service);
            postServiceRepository.save(postService);
        }

        for (String photoPath : updatePostRequest.getPostImagePathUrls()) {
            Photo photo = new Photo();
            photo.setPost(post);
            photo.setPhotoPath(photoPath);
            photoRepository.save(photo);
        }

        postRepository.save(post);
        return ApiResult.success("게시글 수정이 완료되었습니다");
    }

    @Transactional
    public ApiResult<?> deletePost(Long postId, HttpServletRequest request) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getMemberId().equals(member.getMemberId())) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        postTagRepository.deleteAll(post.getPostTags());
        postServiceRepository.deleteAll(post.getPostServices());
        photoRepository.deleteAll(post.getPhotos());

        postRepository.delete(post);

        return ApiResult.success("게시글 삭제가 완료되었습니다");
    }
}