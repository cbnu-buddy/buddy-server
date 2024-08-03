package com.example.domain.community;

import com.example.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "post_likes")
@Getter
@Setter
@NoArgsConstructor
public class PostLike {
    @EmbeddedId
    private PostLikeId id;

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    public PostLike(Member member, Post post) {
        this.member = member;
        this.post = post;
        this.id = new PostLikeId(member.getMemberId(), post.getId());
    }
}