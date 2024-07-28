package com.example.repository.subscribe;

import com.example.domain.subscribe.TagSub;
import com.example.domain.subscribe.TagSubId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TagSubRepository extends JpaRepository<TagSub, TagSubId> {
    Optional<TagSub> findByMember_MemberIdAndTag_Id(Long memberId, Long tagId);
}