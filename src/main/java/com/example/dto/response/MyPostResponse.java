package com.example.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class MyPostResponse {
    private Long postId;
    private String title;
    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modifiedAt;
    private List<String> postImagePathUrls;
    private Author author;
    private List<String> tags;
    private int views;
    private int commentCount;
    private int likeCount;

    @Getter
    @Builder
    public static class Author {
        private Long memberId;
        private String username;
        private String profileImagePathUrl;
    }
}