package com.example.dto.response;

import com.example.domain.point.Point;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class PointInfoResponse {
    private Integer point;
    private Integer totalPoint;
    private String createTime;
    private String category;
    private String item;
}
