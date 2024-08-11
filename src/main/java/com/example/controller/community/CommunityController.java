package com.example.controller.community;

import com.example.api.ApiResult;
import com.example.dto.request.CreatePostRequest;
import com.example.dto.request.UpdatePostRequest;
import com.example.dto.response.MyPostResponse;
import com.example.service.community.CommunityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "커뮤니티 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "커뮤니티 게시글 생성하기", description = "")
    @PostMapping
    public ApiResult<?> createPost(@RequestBody CreatePostRequest postRequest, HttpServletRequest request) {
        return communityService.createPost(postRequest, request);
    }

    @Operation(summary = "커뮤니티 게시글 수정하기", description = "")
    @PutMapping("/private/community/{postId}")
    public ApiResult<?> updatePost(@PathVariable Long postId, @RequestBody UpdatePostRequest updatePostRequest, HttpServletRequest request) {
        return communityService.updatePost(postId, updatePostRequest, request);
    }

    @Operation(summary = "커뮤니티 게시글 삭제하기", description = "")
    @DeleteMapping("/private/community/{postId}")
    public ApiResult<?> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        return communityService.deletePost(postId, request);
    }

    @Operation(summary = "내가 쓴 커뮤니티 게시글 목록 조회", description = "")
    @GetMapping("/private/community/posts/my")
    public ApiResult<List<MyPostResponse>> getMyCommunityPosts(HttpServletRequest request) {
        return communityService.getMyCommunityPosts(request);
    }

    @Operation(summary = "태그 기반 커뮤니티 게시글 목록 정보 조회", description = "")
    @GetMapping("/public/community/posts")
    public ApiResult<?> getPostsByTag(@RequestParam String tag, @RequestParam int limit) {
        return communityService.getPostsByTag(tag, limit);
    }

    @Operation(summary = "추천 태그 목록 정보 조회", description = "태그 등록 수가 가장 높은 상위 10개의 태그 목록 정보를 조회한다.")
    @GetMapping("/public/community/recommendation-tags")
    public ApiResult<?> getTop10Tags() {
        return communityService.getTop10Tags();
    }

    @Operation(summary = "연관 검색 태그 목록 정보 조회", description = "특정 검색어를 포함하는 태그 목록 정보를 조회한다.")
    @GetMapping("/public/community/search")
    public ApiResult<?> getRelatedTags(@RequestParam String query) {
        return communityService.getRelatedTags(query);
    }

    @Operation(summary = "커뮤니티 게시글 정보 조회", description = "")
    @GetMapping("/public/community/posts/{postId}")
    public ApiResult<?> getPostById(@PathVariable Long postId) {
        return communityService.getPostById(postId);
    }
}