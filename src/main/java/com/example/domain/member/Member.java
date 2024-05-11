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

}
