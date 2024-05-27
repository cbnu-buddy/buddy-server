package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
public class PaymentInfoResponse {
    private String category;
    private String item;
    private Integer amount;
    private Date createTime;
}
