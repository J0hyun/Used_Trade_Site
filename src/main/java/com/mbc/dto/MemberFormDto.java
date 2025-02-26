package com.mbc.dto;

import com.mbc.entity.Member;
import com.mbc.entity.MemberImg;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MemberFormDto {

    private Long memberId;

    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    private String name;

    @NotEmpty(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "이메일 형식으로 입력해주세요.")
    private String email;

    @NotEmpty(message = "비밀번호는 필수 입력 값입니다.")
    @Length(min=4, max=16, message = "비밀번호는 4자 이상, 16자 이하로 입력해주세요")
    private String password;

    @NotEmpty(message = "주소는 필수 입력 값입니다.")
    private String address;

    @NotEmpty(message = "전화번호는 필수 입력 값입니다.")
    private String phone;

    private MultipartFile profileImg;

    private String viewPofile;

    private LocalDateTime regTime;

    private String phoneVerificationCode; // 핸드폰 인증 번호

    // Member 객체를 기반으로 DTO 초기화
    public MemberFormDto(Member member, MemberImg memberImg) {
        this.name = member.getName();
        this.email = member.getEmail();
        this.password = member.getPassword();
        this.address = member.getAddress();
        this.phone = member.getPhone();
        if (memberImg != null) {
            this.viewPofile = memberImg.getImgUrl(); // 이미지를 표시할 URL 저장
        }
    }
}
