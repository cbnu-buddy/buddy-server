package com.example.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TagSubInfoResponse {
    private Long tagId;
    private String tag;
    private Boolean isReceiveNotification;
    private Long postCount;
}
