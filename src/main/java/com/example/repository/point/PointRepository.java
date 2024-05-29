package com.example.repository.point;

import com.example.domain.member.Member;
import com.example.domain.point.Point;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PointRepository extends JpaRepository<Point, Long> {
    List<Point> findByMemberOrderByCreateTimeDesc(Member member);
}
