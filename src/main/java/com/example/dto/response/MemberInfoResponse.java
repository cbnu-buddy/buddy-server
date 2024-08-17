package com.example.dto.response;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberInfoResponse {
    private Long memberId;
    private String email;
    private String username;
    private Integer point;
    private String Profile_path;
}

