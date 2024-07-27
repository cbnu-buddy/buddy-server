package com.example.repository.subscribe;

import com.example.domain.subscribe.TagSub;
import com.example.domain.subscribe.TagSubId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagSubRepository extends JpaRepository<TagSub, TagSubId> {
}
