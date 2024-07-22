package com.example.controller.community;

import com.example.api.ApiResult;
import com.example.dto.request.CreatePostRequest;
import com.example.dto.request.UpdatePostRequest;
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
@RequestMapping("/private/community")
public class CommunityController {

    private final CommunityService communityService;

    @Operation(summary = "커뮤니티 게시글 생성하기", description = "")
    @PostMapping
    public ApiResult<?> createPost(@RequestBody CreatePostRequest postRequest, HttpServletRequest request) {
        return communityService.createPost(postRequest, request);
    }

    @Operation(summary = "커뮤니티 게시글 수정하기", description = "")
    @PutMapping("/{postId}")
    public ApiResult<?> updatePost(@PathVariable Long postId, @RequestBody UpdatePostRequest updatePostRequest, HttpServletRequest request) {
        return communityService.updatePost(postId, updatePostRequest, request);
    }

    @Operation(summary = "커뮤니티 게시글 삭제하기", description = "")
    @DeleteMapping("/{postId}")
    public ApiResult<?> deletePost(@PathVariable Long postId, HttpServletRequest request) {
        return communityService.deletePost(postId, request);
    }

}