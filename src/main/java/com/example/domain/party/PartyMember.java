package com.example.domain.party;

import com.example.domain.member.Member;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartyMember {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "party_member_id")  // PK
    private Long partyMemberId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "party_id")  // FK
    private Party party;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")  // FK
    private Member member;
}



