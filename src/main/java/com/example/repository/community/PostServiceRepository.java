package com.example.repository.community;

import com.example.domain.community.Post;
import com.example.domain.community.PostService;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostServiceRepository extends JpaRepository<PostService, Long> {
    List<PostService> findByPost(Post post);
}