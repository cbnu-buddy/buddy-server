package com.example.domain.community;

import com.example.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "comment_likes")
@Getter
@Setter
@Entity
@NoArgsConstructor
public class CommentLike {

    @EmbeddedId
    private CommentLikeId id;

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @MapsId("commentId")
    @JoinColumn(name = "comment_id")
    private Comment comment;

    public CommentLike(Member member, Comment comment) {
        this.id = new CommentLikeId(member.getMemberId(), comment.getId());
        this.member = member;
        this.comment = comment;
    }
}