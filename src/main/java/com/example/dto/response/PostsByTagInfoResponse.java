package com.example.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class PostsByTagInfoResponse {
    private Long postId;
    private String title;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime modifiedAt;
    private AuthorDto author;
    private List<TagsDto> tags;
    private int views;
    private List<ServiceDto> services;
    private List<CommentDto> comments;
    private int likeCount;

    @Getter
    @Setter
    @Builder
    public static class AuthorDto {
        private Long memberId;
        private String username;
        private String profileImagePathUrl;
    }

    @Getter
    @Setter
    @Builder
    public static class TagsDto {
        private Long tagId;
        private String tag;
    }

    @Getter
    @Setter
    @Builder
    public static class ServiceDto {
        private Long serviceId;
        private List<Long> planIds;
        private String name;
        private String url;
    }

    @Getter
    @Setter
    @Builder
    public static class CommentDto {
        private Long commentId;
        private String commentContent;
        private int likeCount;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
        private LocalDateTime createdAt;
        private AuthorDto writer;
        private List<ReplyDto> replies;

        @Getter
        @Setter
        @Builder
        public static class ReplyDto {
            private Long replyId;
            private String replyContent;
            private int likeCount;
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
            private LocalDateTime createdAt;
            private AuthorDto writer;
        }
    }
}
