package com.example.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreatePostRequest {
    private String title;
    private String content;
    private List<String> tags;
    private List<Long> serviceIds;
}