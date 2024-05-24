package com.example.repository.party;

import com.example.domain.member.Member;
import com.example.domain.party.Party;
import com.example.domain.party.PartyMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {
    boolean existsByPartyAndMember(Party party, Member member);
}
