//package com.mbc.repository;
//
//import com.mbc.constant.ItemSellStatus;
//import com.mbc.entity.Item;
//import com.mbc.entity.Member;
//import com.mbc.entity.Order;
//import com.mbc.entity.OrderItem;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.annotation.Commit;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.LocalDateTime;
//
//@SpringBootTest
//@Log4j2
//@Transactional
//class OrderTest {
//
//    @Autowired
//    private OrderRepository orderRepository;
//
//    @Autowired
//    ItemRepository itemRepository;
//
//    @Autowired
//    EntityManager em;
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Autowired
//    OrderItemRepository orderItemRepository;
//
//    @Test
//    @DisplayName("지연 로딩 테스트")
//    @Commit
//    public void lazyloadingTest(){
//        Order order = this.createOrder();
//        Long orderItemID = order.getOrderItems().get(0).getId();
//        em.flush();
//        em.clear();
//
//        OrderItem orderItem = orderItemRepository.findById(orderItemID)
//                .orElseThrow(() -> new EntityNotFoundException());
//        log.info("---Order class---" + orderItem.getOrder().getClass());
//        orderItem.getOrder().getOrderDate();
//    }
//
//    private Order createOrder() {
//        Order order = new Order();
//
//        for (int i = 0; i < 3; i++) {
//            Item item = createItem();
//            itemRepository.save(item);
//            OrderItem orderItem = new OrderItem();
//            orderItem.setItem(item);
//            orderItem.setCount(10);
//            orderItem.setOrderPrice(1000);
//            orderItem.setOrder(order);
//            order.getOrderItems().add(orderItem);
//        }
//
//        Member member = new Member();
//        memberRepository.save(member);
//
//        order.setMember(member);
//        orderRepository.save(order);
//        return order;
//    }
//
//    @Test
//    @DisplayName("고아객체 제거 테스트")
//    public void orphanRemovalTest() {
//        Order order = this.createOrder();
//        order.getOrderItems().remove(0);
//        em.flush();
//    }
//
//    public Item createItem(){
//        Item item = new Item();
//        item.setItemNm("item1");
//        item.setPrice(10000);
//        item.setItemDetail("itemDetail1");
//        item.setItemSellStatus(ItemSellStatus.SELL);
//        item.setStockNumber(100);
//        item.setRegTime(LocalDateTime.now());
//        item.setUpdateTime(LocalDateTime.now());
//        return item;
//    }
//
//    @Test
//    @DisplayName("영속성 전이 테스트")
//    public void cascadeTest() {
//
//        Order order = new Order();
//
//        for (int i = 0; i < 3; i++) {
//            Item item = this.createItem();
//            itemRepository.save(item);
//            OrderItem orderItem = new OrderItem();
//            orderItem.setItem(item);
//            orderItem.setCount(10);
//            orderItem.setOrderPrice(1000);
//            orderItem.setOrder(order);
//            order.getOrderItems().add(orderItem);
//        }
//
//        orderRepository.saveAndFlush(order);
//        em.clear();
//
//        Order savedOrder = orderRepository.findById(order.getId())
//                .orElseThrow(() -> new EntityNotFoundException());
////        assertEquals(3, savedOrder.getOrderItems().size());
//    }
//
//}