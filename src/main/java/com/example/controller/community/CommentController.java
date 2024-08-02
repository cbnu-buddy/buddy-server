package com.example.controller.community;

import com.example.api.ApiResult;
import com.example.dto.request.CommentRequest;
import com.example.service.community.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ApiResult<?> createComment(@PathVariable Long postId, @RequestBody CommentRequest request) {
        return commentService.createComment(postId, request.getContent());
    }

    @Operation(summary = "댓글 삭제하기", description = "")
    @DeleteMapping("/comments/{commentId}")
    public ApiResult<?> deleteComment(@PathVariable Long commentId) {
        return commentService.deleteComment(commentId);
    }

    @Operation(summary = "댓글 수정하기", description = "")
    @PatchMapping("/comments/{commentId}")
    public ApiResult<?> updateComment(@PathVariable Long commentId, @RequestBody CommentRequest request) {
        return commentService.updateComment(commentId, request.getContent());
    }
}