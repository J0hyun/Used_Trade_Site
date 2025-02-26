package com.mbc.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MailDTO {
    private Long id; // 기본 키
    private String from; // 발신자 이메일
    private String to; // 수신자 이메일
    private String title; // 이메일 제목
    private String message; // 이메일 내용
    private LocalDateTime sentAt; // 이메일 발송 시간

    public MailDTO(String from, String to, String title, String message) {
        this.from = from;
        this.to = to;
        this.title = title;
        this.message = message;
    }
}
