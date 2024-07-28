package com.example.domain.subscribe;

import com.example.domain.community.Tag;
import com.example.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tag_subscriptions")
@NoArgsConstructor
@Getter
@Setter
public class TagSub {

    @EmbeddedId
    private TagSubId id;

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @MapsId("tagId")
    @JoinColumn(name = "tag_id")
    private Tag tag;

    @Column(name = "sub_notify", columnDefinition = "BOOLEAN DEFAULT 1")
    private Boolean subNotify = true;

    public TagSub(Member member, Tag tag) {
        this.id = new TagSubId(member.getMemberId(), tag.getId());
        this.member = member;
        this.tag = tag;
    }
}