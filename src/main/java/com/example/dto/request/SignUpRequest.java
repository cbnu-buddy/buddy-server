package com.example.dto.request;

import com.example.domain.member.Member;
import com.example.domain.member.MemberAuthority;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    private String userId;
    private String email;
    private String pwd;
    private String username;

    public Member toEntity(){
        return Member.builder()
                .userId(this.userId)
                .email(this.email)
                .pwd(this.pwd)
                .username(this.username)
                .point(0)
                .memberAuthority(MemberAuthority.ROLE_USER)
                .build();
    }
}
