package com.example.dto.request;

import com.example.domain.member.Member;
import com.example.domain.payment.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddPaymentRequest {
    private String orderId;
    private String paymentKey;
    private String category;
    private String item;
    private Integer amount;
    private Date createTime;

    public Payment toEntity(Member member){
        return Payment.builder()
                .member(member)
                .orderId(this.orderId)
                .paymentKey(this.paymentKey)
                .category(this.category)
                .item(this.item)
                .amount(this.amount)
                .createTime(this.createTime)
                .build();
    }
}