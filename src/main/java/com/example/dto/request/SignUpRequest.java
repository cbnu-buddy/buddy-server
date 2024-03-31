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

    private String email;
    private String pwd;
    private String username;
    private String tel;

    public Member toEntity(){
        return Member.builder()
                .email(this.email)
                .pwd(this.pwd)
                .username(this.username)
                .tel(this.tel)
                .memberAuthority(MemberAuthority.ROLE_USER)     // member 권한의 기본은 ROLE_USER
                .build();
    }
}
