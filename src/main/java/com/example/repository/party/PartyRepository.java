package com.example.repository.party;

import com.example.domain.party.Party;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long> {
    Optional<Party> findByPartyId(Long partyId);
}