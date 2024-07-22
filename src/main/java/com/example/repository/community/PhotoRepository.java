package com.example.repository.community;

import com.example.domain.community.Photo;
import com.example.domain.community.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoRepository extends JpaRepository<Photo, Long> {
    List<Photo> findByPost(Post post);
}