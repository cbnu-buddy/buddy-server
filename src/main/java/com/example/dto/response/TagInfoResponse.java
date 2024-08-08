package com.example.dto.response;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class TagInfoResponse {
    private Long tagId;
    private String tagName;
    private Long postCount;
}
