//package com.mbc.entity;
//
//import com.mbc.constant.ItemSellStatus;
//import com.mbc.repository.ItemRepository;
//import com.mbc.repository.MemberRepository;
//import com.mbc.repository.OrderItemRepository;
//import com.mbc.repository.OrderRepository;
//import jakarta.persistence.EntityManager;
//import jakarta.persistence.EntityNotFoundException;
//import jakarta.persistence.PersistenceContext;
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
//import static org.junit.jupiter.api.Assertions.assertEquals;
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
//    private ItemRepository itemRepository;
//
//    @PersistenceContext
//    EntityManager em;
//
//    public Item createItem(){
//        Item item = new Item();
//
//        item.setItemNm("테스트 상품");
//        item.setPrice(10000);
//        item.setItemDetail("상세설명");
//        item.setItemSellStatus(ItemSellStatus.SELL);
//        item.setStockNumber(100);
//        item.setRegTime(LocalDateTime.now());
//        item.setUpdateTime(LocalDateTime.now());
//        return item;
//    }
//
//    @Test
//    @DisplayName("영속성 전이 테스트")
//    public void cascadeTest(){
//        Order order = new Order();
//
//        for(int i=0; i<3; i++){
//            Item item = this.createItem();
//            itemRepository.save(item);
//
//            OrderItem orderItem = new OrderItem();
//            orderItem.setItem(item);
//            orderItem.setCount(10);
//            orderItem.setOrderPrice(1000);
//            orderItem.setOrder(order);
//            order.getOrderItems().add(orderItem);  // 아직 영속성 컨텍스트에 저장되지 않은 orderItem 엔티티를 order 엔티티에 담아줌
//        }
//
//        orderRepository.saveAndFlush(order);  // order 엔티티를 저장하면서 강제로 flush를 호촐하여 영속성 컨텍스트에 있는 객체들을 DB에 반영
//        em.clear();  // 영속성 컨텍스트의 상태 초기화
//
//        Order savedOrder = orderRepository.findById(order.getId())  // DB에서 order 엔티티 조회(콘솔창에 select 쿼리문 실행)
//                .orElseThrow(EntityNotFoundException::new);
//        assertEquals(3, savedOrder.getOrderItems().size());
//    }
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    public Order createOrder(){
//        Order order = new Order();
//
//        for(int i=0; i<3; i++){
//            Item item = this.createItem();
//            itemRepository.save(item);
//
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
//    public void orphanRemovalTest(){
//        Order order = this.createOrder();
//        order.getOrderItems().remove(0);  // order 엔티티에서 관리하고 있는 orderItems 리스트의 0번째 요소 제거
//        em.flush();
//    }
//
//    @Autowired
//    OrderItemRepository orderItemRepository;
//
//    @Test
//    @DisplayName("지연 로딩 테스트")
//    @Commit
//    public void lazyLoadingTest(){
//        Order order = this.createOrder();
//        Long orderItemId = order.getOrderItems().get(0).getId();
//        em.flush();
//        em.clear();
//
//        OrderItem orderItem = orderItemRepository.findById(orderItemId)
//                .orElseThrow(EntityNotFoundException::new);
//        System.out.println("Order class : " +
//                orderItem.getOrder().getClass());
//        System.out.println("======================");
//        orderItem.getOrder().getOrderDate();
//        System.out.println("======================");
//    }
//}