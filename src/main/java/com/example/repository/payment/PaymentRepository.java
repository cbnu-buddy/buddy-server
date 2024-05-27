package com.example.repository.payment;

import com.example.domain.member.Member;
import com.example.domain.payment.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    Optional<Payment> findByPaymentKey(String paymentKey);
    List<Payment> findByMember(Member member);
}