package com.mbc.repository;

import com.mbc.entity.ChatRoom;
import com.mbc.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // itemId와 memberId로 채팅방을 찾는 메소드 추가
    Optional<ChatRoom> findByItemIdAndMemberId(Long itemId, Long memberId);
}
