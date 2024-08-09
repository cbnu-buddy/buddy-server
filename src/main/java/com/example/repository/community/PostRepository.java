package com.example.repository.community;

import com.example.domain.community.Post;
import com.example.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByMember(Member member);

    @Query("SELECT COUNT(p) FROM PostTag p WHERE p.tag.id = :tagId")
    Long countPostsByTagId(@Param("tagId") Long tagId);

//    @Query("SELECT p FROM Post p JOIN p.tags t WHERE t.tagName = :tag ORDER BY p.createdAt DESC")
//    List<Post> findByTagName(@Param("tag") String tag, Pageable pageable);

}