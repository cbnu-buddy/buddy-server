package com.example.service.community;

import com.example.api.ApiResult;
import com.example.domain.community.Comment;
import com.example.domain.community.Post;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.community.CommentRepository;
import com.example.repository.community.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    /*
    댓글 작성
    */
    @Transactional
    public ApiResult<?> createComment(Long postId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));
        Comment comment = new Comment();
        comment.setPost(post);
        comment.setPostContent(content);
        comment.setCreatedTime(LocalDateTime.now());
        comment.setModifiedTime(LocalDateTime.now());
        commentRepository.save(comment);
        return ApiResult.success("댓글이 작성되었습니다.");
    }

    /*
    댓글 삭제
    */
    @Transactional
    public ApiResult<?> deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        commentRepository.delete(comment);
        return ApiResult.success("댓글이 삭제되었습니다.");
    }

    /*
    댓글 수정
    */
    @Transactional
    public ApiResult<?> updateComment(Long commentId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        comment.setPostContent(content);
        comment.setModifiedTime(LocalDateTime.now());
        commentRepository.save(comment);
        return ApiResult.success("댓글이 수정되었습니다.");
    }
}