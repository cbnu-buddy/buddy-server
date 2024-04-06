package com.example.domain.member;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * 회원 정보를 담고 있는 엔티티
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;                                // PK

    private String email;
    private String pwd;
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

    public void setPoint(Integer point) {
        this.point = point;
    }

}
