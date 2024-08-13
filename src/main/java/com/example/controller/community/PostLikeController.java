package com.example.controller.community;

import com.example.api.ApiResult;
import com.example.service.community.PostLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "게시글 좋아요 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/community/posts")
public class PostLikeController {

    private final PostLikeService postLikeService;

    @Operation(summary = "게시글 좋아요 누르기", description = "")
    @PostMapping("/{postId}/like")
    public ApiResult<String> likePost(@PathVariable Long postId, HttpServletRequest request) {
        return postLikeService.likePost(request, postId);
    }

    @Operation(summary = "게시글 좋아요 취소하기", description = "")
    @DeleteMapping("/{postId}/like")
    public ApiResult<String> unlikePost(@PathVariable Long postId, HttpServletRequest request) {
        return postLikeService.unlikePost(request, postId);
    }
}