package com.example.domain.community;

import com.example.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "reply_likes")
@Getter
@Setter
@Entity
@NoArgsConstructor
public class ReplyLike {

    @EmbeddedId
    private ReplyLikeId id;

    @ManyToOne
    @MapsId("memberId")
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @MapsId("replyId")
    @JoinColumn(name = "reply_id")
    private Reply reply;

    public ReplyLike(Member member, Reply reply) {
        this.id = new ReplyLikeId(member.getMemberId(), reply.getId());
        this.member = member;
        this.reply = reply;
    }
}