package com.mbc.service;

import com.mbc.dto.OrderDto;
import com.mbc.dto.OrderHistDto;
import com.mbc.dto.OrderItemDto;
import com.mbc.entity.*;
import com.mbc.repository.*;
import com.siot.IamportRestClient.response.IamportResponse;
import com.siot.IamportRestClient.response.Payment;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository;
    private final ItemImgRepository itemImgRepository;
    private final OrderItemRepository orderItemRepository;
    private final PaymentService paymentService;
    private final ReviewRepository reviewRepository;

    public Long order(OrderDto orderDto, String name, String impUid, String merchantUid){
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);
        Member member = memberRepository.findByname(name);

        List<OrderItem> orderItemList = new ArrayList<>();
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
        orderItemList.add(orderItem);

        Order order = Order.createOrder(member, orderItemList, impUid, merchantUid);
        orderRepository.save(order);

        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderHistDto> getOrderList(String name, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(name, pageable);
        Long totalCount = orderRepository.countOrders(name);

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> ordersItems = order.getOrderItems();
            for (OrderItem orderItem : ordersItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn(
                        orderItem.getItem().getId(),"Y");
                // 리뷰 존재 여부 확인
                boolean reviewExists = reviewRepository.existsByItemAndMemberName(
                        orderItem.getItem(), order.getMember().getName());
                OrderItemDto orderItemDto = new OrderItemDto(orderItem, itemImg.getImgUrl(),reviewExists);
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }
        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);
    }

    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String name) {
        Member curMember = memberRepository.findByname(name);
        Order order = orderRepository.findById(orderId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();

        if(!StringUtils.equals(curMember.getName(), savedMember.getName())){
            return false;
        }

        return true;
    }

//    ***DB에서만 주문 취소***
//    public void cancelOrder(Long orderId){
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(EntityNotFoundException::new);
//        order.cancelOrder();
//
//        // order_item 삭제
//        orderItemRepository.deleteAll(order.getOrderItems());
//
//        // order 삭제
//        orderRepository.delete(order);
//    }

    public String cancelOrderWithPayment(Long orderId) throws Exception {
        // 주문 정보 가져오기
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문 정보를 찾을 수 없습니다."));

        // 주문 상태를 취소로 변경하고 DB에서 주문 항목 삭제
        order.cancelOrder();
        orderItemRepository.deleteAll(order.getOrderItems());
        orderRepository.delete(order);

        // 결제 취소 처리
        String impUid = order.getImpUid(); // 결제 고유 ID
        if (impUid == null || impUid.isEmpty()) {
            throw new IllegalArgumentException("결제 정보가 존재하지 않아 취소할 수 없습니다.");
        }

        IamportResponse<Payment> response = paymentService.cancelPayment(impUid);

        // 결제 취소 결과 확인
        if (response == null || response.getResponse() == null) {
            throw new Exception("결제 취소에 실패했습니다. 아임포트 서버와 통신 중 문제가 발생했습니다.");
        }

        Payment payment = response.getResponse();
        if (!"cancelled".equals(payment.getStatus())) {
            throw new Exception("결제 취소에 실패했습니다. 상태: " + payment.getStatus());
        }

        return "주문 및 결제가 성공적으로 취소되었습니다.";
    }

    public Long orders(List<OrderDto> orderDtoList, String name, String impUid, String merchantUid) {

        Member member = memberRepository.findByname(name);
        List<OrderItem> orderItemList = new ArrayList<>();

        for(OrderDto orderDto : orderDtoList){
            Item item = itemRepository.findById(orderDto.getItemId())
                    .orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());
            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList, impUid, merchantUid);
        orderRepository.save(order);

        return order.getId();
    }


}
