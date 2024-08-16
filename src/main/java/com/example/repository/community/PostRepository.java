package com.example.repository.community;

import com.example.domain.community.Post;
import com.example.domain.member.Member;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByMember(Member member);

    @Query("SELECT p FROM Post p ORDER BY p.views DESC, p.createdTime DESC")
    List<Post> findTop5ByViews(Pageable pageable);
    @Query("SELECT COUNT(p) FROM PostTag p WHERE p.tag.id = :tagId")
    Long countPostsByTagId(@Param("tagId") Long tagId);
    @Query("SELECT p FROM Post p JOIN p.postTags pt JOIN pt.tag t WHERE t.tagName = :tag ORDER BY p.createdTime DESC")
    List<Post> findByTagName(@Param("tag") String tag, Sort sort);

    @Query("SELECT p FROM Post p " +
            "LEFT JOIN p.postTags pt " +
            "LEFT JOIN pt.tag t " +
            "LEFT JOIN p.member m " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(p.content) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(t.tagName) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(m.username) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Post> findByTitleOrContentOrTagNameOrAuthor(@Param("query") String query, Sort sort);
}