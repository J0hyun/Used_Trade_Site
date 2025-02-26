package com.mbc.entity;

import com.mbc.constant.MemberStatus;
import com.mbc.constant.Role;
import com.mbc.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
//@Table(name="member")
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Member extends BaseEntity {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;
    private String address;
    private String phone;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private MemberStatus status; // 회원 상태 추가 (ACTIVE, DELETED)

    private String provider;
    private String providerId;

    // 비밀번호 재설정 토큰
    private String passwordResetToken;

    // 토큰 유효기간
    private LocalDateTime tokenExpiration;

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){

        Member member = new Member();

        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        String password = passwordEncoder.encode(memberFormDto.getPassword());
        member.setPassword(password);
        member.setAddress(memberFormDto.getAddress());
        member.setPhone(memberFormDto.getPhone());
        member.setRole(Role.USER);
        member.setStatus(MemberStatus.ACTIVE);

        return member;
    }

}
