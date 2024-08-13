package com.example.service.community;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.community.Comment;
import com.example.domain.community.Reply;
import com.example.domain.community.ReplyLike;
import com.example.domain.community.ReplyLikeId;
import com.example.domain.member.Member;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.community.CommentRepository;
import com.example.repository.community.ReplyRepository;
import com.example.repository.community.ReplyLikeRepository;
import com.example.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final ReplyLikeRepository replyLikeRepository;
    private final TokenProvider tokenProvider;


    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        return authentication.getName();
    }

    /*
    답글 작성하기
    */
    @Transactional
    public ApiResult<?> createReply(Long commentId, HttpServletRequest request, String content) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        Reply reply = new Reply(comment, member, content);
        replyRepository.save(reply);
        return ApiResult.success("답글이 작성되었습니다.");
    }

    /*
    답글 삭제하기
    */
    @Transactional
    public ApiResult<?> deleteReply(Long replyId) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
        replyRepository.delete(reply);
        return ApiResult.success("답글이 삭제되었습니다.");
    }

    /*
    답글 수정하기
    */
    @Transactional
    public ApiResult<?> updateReply(Long replyId, String content) {
        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));
        reply.updateContent(content);
        replyRepository.save(reply);
        return ApiResult.success("답글이 수정되었습니다");
    }

    /*
    답글 좋아요 누르기
    */
    @Transactional
    public ApiResult<String> likeReply(HttpServletRequest request, Long replyId) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ReplyLikeId replyLikeId = new ReplyLikeId(member.getMemberId(), replyId);
        if (replyLikeRepository.existsById(replyLikeId)) {
            throw new CustomException(ErrorCode.ALREADY_REPLY_LIKED);
        }

        Reply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPLY_NOT_FOUND));

        ReplyLike replyLike = new ReplyLike(member, reply);
        replyLikeRepository.save(replyLike);
        return ApiResult.success("답글에 좋아요를 눌렀습니다.");
    }

    /*
    답글 좋아요 취소하기
    */
    @Transactional
    public ApiResult<String> unlikeReply(HttpServletRequest request, Long replyId) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        ReplyLikeId replyLikeId = new ReplyLikeId(member.getMemberId(), replyId);
        if (!replyLikeRepository.existsById(replyLikeId)) {
            throw new CustomException(ErrorCode.REPLY_NOT_FOUND);
        }

        replyLikeRepository.deleteById(replyLikeId);

        return ApiResult.success("답글에 좋아요를 취소했습니다.");
    }
}
