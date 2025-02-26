package com.mbc.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class CartOrderDto {

    private Long cartItemId;

    private List<CartOrderDto> cartOrderDtoList;

    // 결제 정보
    String impUid;  // 아임포트 결제 uid
    String merchantUid;  // 주문 번호
}
