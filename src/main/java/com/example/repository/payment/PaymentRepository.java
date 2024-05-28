package com.example.repository.payment;

import com.example.domain.member.Member;
import com.example.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentKey(String paymentKey);
    @Query("SELECT p FROM Payment p WHERE p.member = :member ORDER BY p.createTime DESC")
    List<Payment> findByMemberOrderByCreateTimeDesc(Member member);
}