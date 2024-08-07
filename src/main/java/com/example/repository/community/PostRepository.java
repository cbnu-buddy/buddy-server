package com.example.repository.community;

import com.example.domain.community.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByMemberId(Long memberId);

    @Query("SELECT COUNT(p) FROM PostTag p WHERE p.tag.id = :tagId")
    Long countPostsByTagId(@Param("tagId") Long tagId);


}