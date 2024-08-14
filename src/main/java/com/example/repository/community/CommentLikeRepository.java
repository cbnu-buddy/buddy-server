package com.example.repository.community;

import com.example.domain.community.CommentLike;
import com.example.domain.community.CommentLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, CommentLikeId> {
    int countByCommentId(Long commentId);
}