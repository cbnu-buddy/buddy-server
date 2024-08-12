package com.example.controller.community;

import com.example.api.ApiResult;
import com.example.dto.request.CommentRequest;
import com.example.service.community.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/posts")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "댓글 작성하기", description = "")
    @PostMapping("/{postId}/comment")
    public ApiResult<?> createComment(@PathVariable Long postId, HttpServletRequest request, @RequestBody CommentRequest commentRequest) {
        return commentService.createComment(postId, request, commentRequest.getContent());
    }

    @Operation(summary = "댓글 삭제하기", description = "")
    @DeleteMapping("/comments/{commentId}")
    public ApiResult<?> deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }

    @Operation(summary = "댓글 수정하기", description = "")
    @PatchMapping("/comments/{commentId}")
    public ApiResult<?> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest commentRequest) {
        return commentService.updateComment(commentId, commentRequest.getContent());
    }

    @Operation(summary = "댓글 좋아요 누르기", description = "")
    @PostMapping("/comments/{commentId}/like")
    public ApiResult<String> likeComment(@PathVariable Long commentId, HttpServletRequest request) {
        return commentService.likeComment(request, commentId);
    }

    @Operation(summary = "댓글 좋아요 취소하기", description = "")
    @DeleteMapping("/comments/{commentId}/like")
    public ApiResult<String> unlikeComment(@PathVariable Long commentId, HttpServletRequest request) {
        return commentService.unlikeComment(request, commentId);
    }
}