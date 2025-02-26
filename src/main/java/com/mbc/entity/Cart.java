package com.mbc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="cart")
@Getter
@Setter
@ToString
public class Cart {

    @Id
    @Column(name="cart_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // @OneToOne(fetch = FetchType.LAZY) 지연 로딩. 필요할 때까지 데이터베이스에서 가져오지 않습니다.
    @OneToOne(fetch = FetchType.LAZY) // 즉시 로딩. Cart 엔티티를 조회할 때, Member 엔티티도 같이 조회
    @JoinColumn(name = "member_id")
    private Member member;

    public static Cart createCart(Member member) {
        Cart cart = new Cart();
        cart.setMember(member);
        return cart;
    }
}
