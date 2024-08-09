package com.example.dto.response;

import com.example.domain.community.Photo;
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
    private String createdAt;
//    private String modifiedAt;
    private List<PhotoDto> postImagePathUrls;
    private AuthorDto author;
    private List<TagsDto> tags;
    private int views;
    private List<ServiceDto> services;
    private List<CommentDto> comments;
    private int likeCount;


    @Getter
    @Setter
    @Builder
    public static class PhotoDto {
        private String photoPath;
    }

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
        private String comment;
        private int likeCount;
        private String createdAt;
        private AuthorDto writer;
        private List<ReplyDto> replies;

        @Getter
        @Setter
        @Builder
        public static class ReplyDto {
            private String reply;
            private int likeCount;
            private String createdAt;
            private AuthorDto writer;
        }
    }
}
