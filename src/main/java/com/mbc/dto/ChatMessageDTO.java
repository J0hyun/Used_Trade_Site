package com.mbc.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChatMessageDTO {
    private Long roomId;
    private String writer;
    private String message;
}
