package com.example.repository.community;

import com.example.domain.community.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByMemberId(Long memberId);

    @Query("SELECT COUNT(p) FROM PostTag p WHERE p.tag.id = :tagId")
    Long countPostsByTagId(@Param("tagId") Long tagId);

//    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.tagName = :tag ORDER BY p.createdAt DESC")
//    List<Post> findByTagName(@Param("tag") String tag, Pageable pageable);

}