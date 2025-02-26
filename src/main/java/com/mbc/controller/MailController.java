package com.mbc.controller;

import com.mbc.dto.MailDTO;
import com.mbc.service.MailService;
import com.mbc.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;
    private final MemberService memberService;

    @PostMapping("/check/email")
    public ResponseEntity<?> checkEmail(@RequestParam("email") String email) {
        if (!memberService.emailExist(email)) {
            return new ResponseEntity<>("일치하는 메일이 없습니다.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("이메일을 사용하는 유저가 존재합니다.", HttpStatus.OK);
    }

    // 이메일과 아이디로 비밀번호 찾기
    @PostMapping("/send/password")
    public ResponseEntity<?> sendPassword(@RequestBody Map<String, String> paramData) {
        String email = paramData.get("email");
        String name = paramData.get("name");

        String result = memberService.updatePasswordToken(email, name);

        // 반환 값에 따라 적절한 메시지 처리
        if ("NOT_FOUND".equals(result)) {
            return new ResponseEntity<>("일치하는 회원정보가 없습니다. 이메일이나 아이디를 확인해주세요.", HttpStatus.BAD_REQUEST);
        }

        if ("TOKEN_LIMIT".equals(result)) {
            return new ResponseEntity<>("비밀번호 찾기는 1시간에 한 번 가능합니다.", HttpStatus.BAD_REQUEST);
        }

        // 임시 비밀번호 생성 및 업데이트
        String tmpPassword = memberService.getTmpPassword();
        memberService.updatePassword(tmpPassword, email, name);
        MailDTO mail = mailService.createMail(tmpPassword, email);

        // 메일 발송
        mailService.sendMail(mail);

        return new ResponseEntity<>("비밀번호 발급이 완료되었습니다.", HttpStatus.OK);
    }


}
