package com.example.repository.party;

import com.example.domain.party.Party;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartyRepository extends JpaRepository<Party, Long> {
    // 추가적인 커스텀 쿼리가 필요하다면 여기에 메서드를 추가할 수 있습니다.
}