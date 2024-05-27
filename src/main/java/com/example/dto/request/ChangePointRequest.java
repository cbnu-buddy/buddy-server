package com.example.dto.request;

import com.example.domain.member.Member;
import com.example.domain.payment.Payment;
import com.example.domain.point.Point;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ChangePointRequest {
    private Integer point;
    private String category;
    private String item;
    private Date createTime;

    public Point toEntity(Member member, Integer totalPoint){
        return Point.builder()
                .member(member)
                .point(this.point)
                .totalPoint(totalPoint)
                .category(this.category)
                .item(this.item)
                .createTime(this.createTime)
                .build();
    }
}