package com.mbc.controller;

import com.mbc.dto.ChatMessageDTO;
import com.mbc.entity.ChatMessage;
import com.mbc.entity.ChatRoom;
import com.mbc.repository.ChatMessageRepository;
import com.mbc.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {

    private final SimpMessagingTemplate template;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 채팅방 메시지 처리
    @MessageMapping(value = "/chat/enter")
    public void enter(ChatMessageDTO messageDTO) {
        Long roomId = messageDTO.getRoomId();
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid roomId"));

        // 입장 시, 기본 메시지를 빈 문자열로 설정하거나 다른 메시지로 설정
        messageDTO.setMessage("");  // 또는 "님이 채팅방에 참여하였습니다."와 같은 메시지로 설정 가능합니다.
        saveMessage(messageDTO, chatRoom);
        /*template.convertAndSend("/sub/chat/room/" + roomId, messageDTO);*/

        // 입장 시 채팅방의 모든 메시지 반환
        List<ChatMessage> chatMessages = chatMessageRepository.findByRoomId(roomId);

        // 이전 메시지를 WebSocket을 통해 전달
        for (ChatMessage chatMessage : chatMessages) {
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setRoomId(chatMessage.getRoomId());
            dto.setWriter(chatMessage.getWriter());
            dto.setMessage(chatMessage.getMessage());
            template.convertAndSend("/sub/chat/room/" + roomId, dto);
        }
    }

    // 일반 메시지 처리
    @MessageMapping(value = "/chat/message")
    public void message(ChatMessageDTO messageDTO) {
        Long roomId = messageDTO.getRoomId();
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid roomId"));

        saveMessage(messageDTO, chatRoom);
        template.convertAndSend("/sub/chat/room/" + roomId, messageDTO);
    }

    // 메시지 저장
    private void saveMessage(ChatMessageDTO messageDTO, ChatRoom chatRoom) {
        // 메시지가 비어 있거나 null인 경우, 저장하지 않음
        if (messageDTO.getMessage() == null || messageDTO.getMessage().trim().isEmpty()) {
            return;  // 메시지를 저장하지 않고 리턴
        }

        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setRoomId(messageDTO.getRoomId());
        chatMessage.setWriter(messageDTO.getWriter());
        chatMessage.setMessage(messageDTO.getMessage());
        chatMessage.setChatRoom(chatRoom);

        chatMessageRepository.save(chatMessage);
    }
}
