package com.mbc.controller;


import com.mbc.dto.ChatRoomDTO;
import com.mbc.entity.ChatRoom;
import com.mbc.entity.Item;
import com.mbc.entity.Member;
import com.mbc.repository.ChatMessageRepository;
import com.mbc.repository.ChatRoomRepository;
import com.mbc.repository.ItemRepository;
import com.mbc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping(value = "/chat")
@Log4j2
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatMessageRepository chatMessageRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;


    @GetMapping("/item/{itemId}/{memberId}")
    public String getItemChat(@PathVariable Long itemId, @PathVariable Long memberId, Model model) {
        // itemId와 memberId를 기반으로 채팅방을 찾는 로직
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findByItemIdAndMemberId(itemId, memberId);

        ChatRoom chatRoom;

        if (chatRoomOptional.isEmpty()) {
            // 채팅방이 존재하지 않으면 새로 생성
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid itemId"));

            // memberId로 member를 찾습니다.
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid memberId"));

            // member를 포함하여 채팅방을 생성
            chatRoom = ChatRoom.create("Chat Room for Item " + itemId, item, member);
            chatRoomRepository.save(chatRoom);  // 생성된 채팅방 저장
        } else {
            chatRoom = chatRoomOptional.get();
        }

        // 채팅방과 메시지 데이터를 모델에 추가
        model.addAttribute("room", chatRoom);
        model.addAttribute("messages", chatMessageRepository.findByRoomId(chatRoom.getRoomId())); // 이전 메시지 추가

        // 모든 채팅방 목록을 모델에 추가
        model.addAttribute("rooms", chatRoomRepository.findAll());  // 모든 채팅방 목록

        return "chat/room";
    }

    // 채팅방 생성
    @PostMapping("/create")
    public String createChatRoom(@RequestParam Long itemId, @RequestParam Long memberId, @RequestParam String name, RedirectAttributes redirectAttributes) {
        // itemId로 item을 찾고, 채팅방 생성
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid itemId"));

        // memberId로 member를 찾습니다.
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid memberId"));

        // member와 item을 포함하여 채팅방을 생성
        ChatRoom chatRoom = ChatRoom.create(name, item, member);

        // 채팅방 저장
        chatRoomRepository.save(chatRoom);

        // 생성된 채팅방으로 리디렉션
        redirectAttributes.addAttribute("roomId", chatRoom.getRoomId());
        return "redirect:/chat/item/{itemId}";
    }

    @GetMapping(value = "/rooms")
    public ModelAndView rooms() {
        log.info("# All Chat Rooms");

        // 채팅방 목록을 가져옵니다
        ModelAndView mv = new ModelAndView("chat/rooms");
        mv.addObject("list", chatRoomRepository.findAll()); // 모든 채팅방 목록

        return mv;
    }

    @GetMapping("/room")
    public String getRoom(@RequestParam Long roomId, Model model) {
        log.info("# get Chat Room, roomID : " + roomId);

        // roomId로 채팅방을 조회합니다
        ChatRoom chatRoom = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid roomId"));

        // 모델에 채팅방 데이터를 추가
        model.addAttribute("room", chatRoom);

        // 해당 채팅방에 관련된 메시지를 모델에 추가 (옵션)
        model.addAttribute("messages", chatMessageRepository.findByRoomId(roomId)); // 이전 메시지 추가

        return "chat/room"; // 채팅방 뷰로 이동
    }

}
