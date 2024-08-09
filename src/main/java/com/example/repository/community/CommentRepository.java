package com.example.repository.community;

import com.example.domain.community.Comment;
import com.example.domain.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    int countByPostId(Long postId);

    List<Comment> findByPost(Post post);
}