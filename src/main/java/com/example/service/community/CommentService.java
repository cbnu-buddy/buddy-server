package com.example.service.community;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.community.Comment;
import com.example.domain.community.CommentLike;
import com.example.domain.community.CommentLikeId;
import com.example.domain.community.Post;
import com.example.domain.member.Member;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.community.CommentRepository;
import com.example.repository.community.PostRepository;
import com.example.repository.community.CommentLikeRepository;
import com.example.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final CommentLikeRepository commentLikeRepository;

    private final TokenProvider tokenProvider;

    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        return authentication.getName();
    }

    /*
    댓글 작성하기
    */
    @Transactional
    public ApiResult<?> createComment(Long postId, HttpServletRequest request, String content) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        Comment comment = new Comment();
        comment.setPost(post);
        comment.setMember(member);
        comment.setPostContent(content);
        comment.setCreatedTime(LocalDateTime.now());
        comment.setModifiedTime(LocalDateTime.now());

        commentRepository.save(comment);
        return ApiResult.success("댓글이 작성되었습니다.");
    }

    /*
    댓글 삭제하기
    */
    @Transactional
    public ApiResult<?> deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));
        commentRepository.delete(comment);
        return ApiResult.success("댓글이 삭제되었습니다.");
    }

    /*
    댓글 수정하기
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

    /*
    댓글 좋아요 누르기
     */
    @Transactional
    public ApiResult<String> likeComment(HttpServletRequest request, Long commentId) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommentLikeId commentLikeId = new CommentLikeId(member.getMemberId(), commentId);
        if (commentLikeRepository.existsById(commentLikeId)) {
            throw new CustomException(ErrorCode.ALREADY_COMMENT_LIKED);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        CommentLike commentLike = new CommentLike(member, comment);
        commentLikeRepository.save(commentLike);
        return ApiResult.success("댓글에 좋아요를 눌렀습니다.");
    }

    /*
    댓글 좋아요 취소하기
     */
    @Transactional
    public ApiResult<String> unlikeComment(HttpServletRequest request, Long commentId) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        CommentLikeId commentLikeId = new CommentLikeId(member.getMemberId(), commentId);
        if (!commentLikeRepository.existsById(commentLikeId)) {
            throw new CustomException(ErrorCode.COMMENT_NOT_FOUND);
        }

        commentLikeRepository.deleteById(commentLikeId);

        return ApiResult.success("댓글에 좋아요를 취소했습니다.");
    }
}
