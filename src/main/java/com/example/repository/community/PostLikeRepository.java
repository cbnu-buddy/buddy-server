package com.example.repository.community;

import com.example.domain.community.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    boolean existsByMemberMemberIdAndPostId(Long memberId, Long postId);

    PostLike findByMemberMemberIdAndPostId(Long memberId, Long postId);
    int countByPostId(Long postId);
}