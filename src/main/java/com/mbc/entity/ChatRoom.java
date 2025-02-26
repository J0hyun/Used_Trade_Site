package com.mbc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name="chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;  // roomId를 Long으로 변경

    private String name;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item; // Item과의 관계 설정

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member; // Member와의 관계 설정

    // 채팅방 생성 메소드
    public static ChatRoom create(String name, Item item, Member member) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setName(name);
        chatRoom.setItem(item);
        chatRoom.setMember(member);  // member 설정
        return chatRoom;
    }
}
