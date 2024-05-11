package com.example.domain.party;

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
    @Column(name = "party_member_id")                   // PK
    private Long partyMemberId;

    @Column(name = "party_id")                          // FK 설정해야함
    private Long partyId;
    @Column(name = "member_id")
    private Long member_id;

}
