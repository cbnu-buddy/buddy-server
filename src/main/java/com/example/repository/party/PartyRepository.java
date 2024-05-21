package com.example.repository.party;

import com.example.domain.party.Party;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PartyRepository extends JpaRepository<Party, Long> {
    Optional<Party> findByPartyId(Long partyId);

    @Query("SELECT p FROM Party p WHERE p.plan.service.id = :serviceId AND p.progressStatus = false")
    List<Party> findUnmatchedPartiesByServiceId(@Param("serviceId") Long serviceId);

}