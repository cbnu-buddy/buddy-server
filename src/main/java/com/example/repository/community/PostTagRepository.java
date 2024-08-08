package com.example.repository.community;

import com.example.domain.community.Post;
import com.example.domain.community.PostTag;
import com.example.domain.community.Tag;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
    List<PostTag> findByPost(Post post);
//    List<Tag> findTop10ByOrderByTagCountDesc();4

    //태그 등록 수가 가장 높은 상위 태그 목록 조회
    @Query("SELECT pt.tag, COUNT(pt.post) FROM PostTag pt GROUP BY pt.tag ORDER BY COUNT(pt.post) DE" +
            "SC")
    List<Object[]> findTopTagsWithPostCount();

    //특정 검색어를 포함하는 태그와 해당 태그에 대한 게시물 개수 조회
    @Query(value = "SELECT t.tag_id, t.tag_name, COUNT(pt.tag_id) " +
            "FROM post_tags pt " +
            "JOIN tag t ON pt.tag_id = t.tag_id " +
            "WHERE t.tag_name LIKE %:query% COLLATE utf8mb4_unicode_ci " +
            "GROUP BY t.tag_id, t.tag_name " +
            "ORDER BY COUNT(pt.tag_id) DESC", nativeQuery = true)
    List<Object[]> findRelatedTagsWithPostCount(@Param("query") String query);


}