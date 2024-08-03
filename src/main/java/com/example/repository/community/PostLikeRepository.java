package com.example.repository.community;

import com.example.domain.community.PostLike;
import com.example.domain.community.PostLikeId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostLikeRepository extends JpaRepository<PostLike, PostLikeId> {
    boolean existsById(PostLikeId id);
    void deleteById(PostLikeId id);
    int countByPostId(Long postId);
}