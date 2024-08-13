package com.example.service.community;

import com.example.api.ApiResult;
import com.example.config.jwt.TokenProvider;
import com.example.domain.community.Post;
import com.example.domain.community.PostLike;
import com.example.domain.community.PostLikeId;
import com.example.domain.member.Member;
import com.example.exception.CustomException;
import com.example.exception.ErrorCode;
import com.example.repository.community.PostLikeRepository;
import com.example.repository.community.PostRepository;
import com.example.repository.member.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;
    private final TokenProvider tokenProvider;

    public String getUserIdFromToken(HttpServletRequest request) {
        Authentication authentication = tokenProvider.getAuthentication(tokenProvider.resolveToken(request));
        return authentication.getName();
    }

    /*
    게시글 좋아요 누르기
     */
    @Transactional
    public ApiResult<String> likePost(HttpServletRequest request, Long postId) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PostLikeId postLikeId = new PostLikeId(member.getMemberId(), postId);
        if (postLikeRepository.existsById(postLikeId)) {
            throw new CustomException(ErrorCode.ALREADY_LIKED);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        PostLike postLike = new PostLike(member, post);
        postLikeRepository.save(postLike);
        return ApiResult.success("좋아요");
    }

    /*
    게시글 좋아요 취소하기
     */
    @Transactional
    public ApiResult<String> unlikePost(HttpServletRequest request, Long postId) {
        String userId = getUserIdFromToken(request);
        Member member = memberRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));

        PostLikeId postLikeId = new PostLikeId(member.getMemberId(), postId);
        if (!postLikeRepository.existsById(postLikeId)) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        postLikeRepository.deleteById(postLikeId);

        return ApiResult.success("좋아요 취소");
    }
}