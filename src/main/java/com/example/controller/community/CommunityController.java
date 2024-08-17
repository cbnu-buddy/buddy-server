package com.example.controller.community;

import com.example.api.ApiResult;
import com.example.dto.request.CreatePostRequest;
import com.example.dto.request.UpdatePostRequest;
import com.example.dto.response.CommunityPostResponse;
import com.example.dto.response.MyPostResponse;
import com.example.dto.response.PostsByTagInfoResponse;
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
    @PostMapping("/private/community/post")
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

    @Operation(summary = "추천 태그 목록 정보 조회", description = "태그 등록 수가 가장 높은 상위 10개의 태그 목록 정보를 조회한다.")
    @GetMapping("/public/community/recommendation-tags")
    public ApiResult<?> getTop10Tags() {
        return communityService.getTop10Tags();
    }

    @Operation(summary = "연관 검색 태그 목록 정보 조회", description = "특정 검색어를 포함하는 태그 목록 정보를 조회한다.")
    @GetMapping("/public/community/search/related-tags")
    public ApiResult<?> getRelatedTags(@RequestParam String query) {
        return communityService.getRelatedTags(query);
    }

    @Operation(summary = "내가 쓴 커뮤니티 게시글 목록 정보 조회", description = "")
    @GetMapping("/private/community/posts/my")
    public ApiResult<List<PostsByTagInfoResponse>> getMyCommunityPosts(HttpServletRequest request) {
        return communityService.getMyCommunityPosts(request);
    }

    @Operation(summary = "태그 기반 커뮤니티 게시글 목록 정보 조회", description = "태그명(tag)과 조회될 게시글의 최댓값(limit)을 Query String으로 전달하여\n" +
            "게시글 목록 정보를 조회한다.")
    @GetMapping("/public/community/posts")
    public ApiResult<?> getPostsByTag(@RequestParam String tag, @RequestParam int limit) {
        return communityService.getPostsByTag(tag, limit);
    }

    @Operation(summary = "커뮤니티 게시글 정보 조회", description = "postId(Path Variable)에 해당하는 게시글 정보를 조회한다.")
    @GetMapping("/public/community/posts/{postId}")
    public ApiResult<?> getPostById(@PathVariable Long postId) {
        return communityService.getPostById(postId);
    }

    @Operation(summary = "커뮤니티 최신 게시글 목록 정보 조회", description = "조회될 게시글의 최댓값(limit)을 Query String으로 전달하여 게시글 목록 정보를 조회한다.")
    @GetMapping("/public/community/posts/latest")
    public ApiResult<List<PostsByTagInfoResponse>> getLatestPosts(@RequestParam int limit) {
        return communityService.getLatestPosts(limit);
    }

    @Operation(summary = "인기 커뮤니티 게시글 목록 정보 조회", description = " 커뮤니티 내 전체 게시글 중에서 게시글의 조회수(views)가 가장 높은 상위 5개의\n" +
            "게시글 정보를 조회한다.")
    @GetMapping("/public/community/posts/hot")
    public ApiResult<List<PostsByTagInfoResponse>> getHotPosts() {
        return communityService.getHotPosts();
    }


    @Operation(summary = "게시글 검색 정보 조회", description = " 검색된 query값에 대한 연관 커뮤니티 태그 목록 정보 및 (게시글 제목, 내용, 게시글 내 등록된 태그, 작성자명)에 하나라도\n" +
            "일치하는 게시글(들)에 대한 정보가 포함됨. limit는 조회된 게시글들 중에서 날짜가 최신인\n" +
            "게시글들의 조회 개수의 제한을 두는 역할을 함(조정이 가능해야 함).")
    @GetMapping("/public/community/search/posts")
    public ApiResult<?> searchQuery(@RequestParam String q, @RequestParam int limit) {
        return communityService.searchQuery(q,limit);
    }

    @Operation(summary = "태그 관련 통합 검색 정보 조회", description = "검색된 query값과 일치하는 커뮤니티 태그 및 연관 커뮤니티 태그 목록 정보, 그리고\n" +
            "(게시글 제목, 내용, 게시글 내 등록된 태그, 작성자명)에 하나라도 일치하는 게시글(들)에\n" +
            "대한 정보가 포함됨. limit는 조회된 게시글들 중에서 날짜가 최신인 게시글들의 조회 개수의\n" +
            "제한을 두는 역할을 함(조정이 가능해야 함). relatedSearchTag object에는 검색된 태그를\n" +
            "제외한 나머지 연관 커뮤니티 태그 목록 정보가 포함되어야 함.")
    @GetMapping("/public/community/search/tags")
    public ApiResult<?> searchTag(@RequestParam String q, @RequestParam int limit) {
        return communityService.searchTag(q,limit);
    }
}