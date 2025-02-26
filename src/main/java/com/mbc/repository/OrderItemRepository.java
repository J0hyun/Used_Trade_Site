package com.mbc.repository;

import com.mbc.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    // 회원명으로 주문 항목을 찾는 쿼리 추가
    List<OrderItem> findByOrder_MemberName(String memberName); // OrderItem에서 Order를 참조하고, Order에서 MemberName을 참조
}
