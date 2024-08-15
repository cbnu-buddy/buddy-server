package com.example.repository.community;

import com.example.domain.community.Post;
import com.example.domain.community.PostTag;
import com.example.domain.community.Tag;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.awt.print.Pageable;
import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findByPost(Post post);

    // 특정 태그와 관련된 게시글 수 계산
    @Query("SELECT COUNT(pt) FROM PostTag pt WHERE pt.tag.id = :tagId")
    Long countPostsByTagId(@Param("tagId") Long tagId);

    //태그 등록 수가 가장 높은 상위 태그 목록 조회
    @Query("SELECT pt.tag, COUNT(pt.post) FROM PostTag pt GROUP BY pt.tag ORDER BY COUNT(pt.post) DESC")
    List<Object[]> findTopTagsWithPostCount();

    //특정 검색어를 포함하는 태그와 해당 태그에 대한 게시물 개수 조회
    @Query("SELECT pt.tag, COUNT(pt.post) FROM PostTag pt WHERE pt.tag.tagName LIKE %:query% GROUP BY pt.tag ORDER BY COUNT(pt.post) DESC")
    List<Object[]> findRelatedTagsWithPostCount(@Param("query") String query);
}