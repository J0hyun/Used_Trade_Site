//package com.mbc.service;
//
//import com.mbc.constant.ItemSellStatus;
//import com.mbc.constant.OrderStatus;
//import com.mbc.dto.OrderDto;
//import com.mbc.entity.Item;
//import com.mbc.entity.Member;
//import com.mbc.entity.Order;
//import com.mbc.entity.OrderItem;
//import com.mbc.repository.ItemRepository;
//import com.mbc.repository.MemberRepository;
//import com.mbc.repository.OrderRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//
//@SpringBootTest
//@Transactional
//class OrderServiceTest {
//
//    @Autowired
//    private OrderService orderService;
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    ItemRepository itemRepository;
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    public Item saveItem(){
//        Item item = new Item();
//        item.setItemNm("테스트 상품");
//        item.setPrice(10000);
//        item.setItemDetail("테스트 상품 상세 설명");
//        item.setItemSellStatus(ItemSellStatus.SELL);
//        item.setStockNumber(100);
//        return itemRepository.save(item);
//    }
//
//    public Member saveMember(){
//        Member member = new Member();
//        member.setEmail("test5@test5");
//        return memberRepository.save(member);
//    }
//
////    @Test
////    @DisplayName("주문 테스트")
////    public void order(){
////        Item item = saveItem();
////        Member member = saveMember();
////
////        OrderDto orderDto = new OrderDto();
////        orderDto.setCount(10);
////        orderDto.setItemId(item.getId());
////
////        Long orderId = orderService.order(orderDto, member.getEmail());
////
////        Order order = orderRepository.findById(orderId)
////                .orElseThrow(EntityNotFoundException::new);
////
////        List<OrderItem> orderItems = order.getOrderItems();
////
////        int totalPrice = orderDto.getCount()*item.getPrice();
////
////        assertEquals(totalPrice, order.getTotalPrice());
////    }
//
////    @Test
////    @DisplayName("주문 취소 테스트")
////    public void cancelOrder(){
////        Item item = saveItem();
////        Member member = saveMember();
////
////        OrderDto orderDto = new OrderDto();
////        orderDto.setCount(10);
////        orderDto.setItemId(item.getId());
////        Long orderId = orderService.order(orderDto, member.getEmail());
////
////        Order order = orderRepository.findById(orderId)
////                .orElseThrow(EntityNotFoundException::new);
////        orderService.cancelOrder(orderId);
////
////        assertEquals(OrderStatus.CANCLE, order.getOrderStatus());
////        assertEquals(100, item.getStockNumber());
////    }
//
//}