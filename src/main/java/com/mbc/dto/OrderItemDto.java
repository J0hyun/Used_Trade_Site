package com.mbc.dto;

import com.mbc.entity.Item;
import com.mbc.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderItemDto {

    private String itemNm;

    private int count;

    private int orderPrice;

    private String imgUrl;

    private Item item;  // Item 객체를 추가합니다.

    private boolean reviewExists; // 리뷰 존재 여부 추가

    public OrderItemDto(OrderItem orderItem, String imgUrl, boolean reviewExists) {
        this.item = orderItem.getItem(); // OrderItem에서 item을 가져와 저장
        this.itemNm = orderItem.getItem().getItemNm();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
        this.reviewExists = reviewExists; // 리뷰 여부 설정
    }
}
