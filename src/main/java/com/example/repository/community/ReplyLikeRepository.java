package com.example.repository.community;

import com.example.domain.community.ReplyLike;
import com.example.domain.community.ReplyLikeId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyLikeRepository extends JpaRepository<ReplyLike, ReplyLikeId> {
    int countByReplyId(Long replyId);

}