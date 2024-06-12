package com.example.repository.party;

import com.example.domain.member.Member;
import com.example.domain.party.Party;
import com.example.domain.party.PartyMember;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PartyMemberRepository extends JpaRepository<PartyMember, Long> {
    boolean existsByPartyAndMember(Party party, Member member);
    List<PartyMember> findByMember(Member member);
    void deleteByPartyAndMember(Party party, Member member);

    List<PartyMember> findByParty(Party party);

    @Transactional
    void deleteByParty(Party party);
}
