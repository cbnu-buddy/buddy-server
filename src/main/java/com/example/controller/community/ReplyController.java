package com.example.controller.community;

import com.example.api.ApiResult;
import com.example.dto.request.CommentRequest;
import com.example.service.community.ReplyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Tag(name = "답글 API")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/private/comments")
public class ReplyController {

    private final ReplyService replyService;

    @Operation(summary = "답글 작성하기", description = "")
    @PostMapping("/{commentId}/reply")
    public ApiResult<?> createReply(@PathVariable Long commentId, HttpServletRequest request, @RequestBody CommentRequest commentRequest) {
        return replyService.createReply(commentId, request, commentRequest.getContent());
    }

    @Operation(summary = "답글 삭제하기", description = "")
    @DeleteMapping("/replies/{replyId}")
    public ApiResult<?> deleteReply(@PathVariable Long replyId) {
        return replyService.deleteReply(replyId);
    }

    @Operation(summary = "답글 수정하기", description = "")
    @PatchMapping("/replies/{replyId}")
    public ApiResult<?> updateReply(@PathVariable Long replyId, @RequestBody CommentRequest commentRequest) {
        return replyService.updateReply(replyId, commentRequest.getContent());
    }

    @Operation(summary = "답글 좋아요 누르기", description = "")
    @PostMapping("/replies/{replyId}/like")
    public ApiResult<String> likeReply(@PathVariable Long replyId, HttpServletRequest request) {
        return replyService.likeReply(request, replyId);
    }

    @Operation(summary = "답글 좋아요 취소하기", description = "")
    @DeleteMapping("/replies/{replyId}/like")
    public ApiResult<String> unlikeReply(@PathVariable Long replyId, HttpServletRequest request) {
        return replyService.unlikeReply(request, replyId);
    }
}