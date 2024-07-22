package com.example.domain.member;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 회원 정보를 담고 있는 엔티티
 */
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long memberId;                                // PK
    @NotBlank
    private String userId;
    @NotBlank
    private String email;
    @NotBlank
    private String pwd;
    @NotBlank
    private String username;

    private Integer point;

    private String profileImagePathUrl;


    @Enumerated(EnumType.STRING)
    private MemberAuthority memberAuthority;


    //== 비지니스 로직 ==//
    /*
    비밀번호 암호화
     */
    public Member encodePassword(PasswordEncoder passwordEncoder){
        this.pwd = passwordEncoder.encode(this.pwd);
        return this;
    }

    public String encodePassword(PasswordEncoder passwordEncoder, String pwd){
        return passwordEncoder.encode(pwd);
    }

    /*
    이메일 변경
     */
    public void changeEmail(String email){
        this.email = email;
    }


    /*
    비밀번호 변경
     */
    public void changePwd(PasswordEncoder passwordEncoder, String pwd){
        this.pwd = encodePassword(passwordEncoder, pwd);
    }

    /*
    이름 변경
     */
    public void changeUsername(String username){
        this.username = username;
    }

}
