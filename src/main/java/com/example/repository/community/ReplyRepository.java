package com.example.repository.community;

import com.example.domain.community.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository extends JpaRepository<Reply, Long> {
    int countByCommentPostId(Long postId);
    List<Reply> findByCommentId(Long commentId);
}