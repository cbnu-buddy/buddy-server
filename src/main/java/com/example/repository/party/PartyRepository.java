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

    @Query(value = "SELECT p.service_id, COUNT(*) as cnt " +
            "FROM Plan p " +
            "JOIN Party pa ON p.plan_id = pa.plan_id " +
            "WHERE pa.progress_status = true " +
            "GROUP BY p.service_id " +
            "ORDER BY cnt DESC " +
            "LIMIT 5", nativeQuery = true)
    List<Object[]> findTop5HotServices();

    // New query to calculate the sum of current_rec_num for unmatched parties
    @Query("SELECT SUM(p.currentRecNum) FROM Party p WHERE p.progressStatus = false")
    Integer getWaitingMembersCount();
}
