package com.mbc.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "review")
@Getter
@Setter
@ToString(exclude = {"member", "item", "reviewImgs"})
public class Review extends BaseEntity {

    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String memberName; // 등록자명

    private String reviewDetail; // 후기 내용

    private int rating;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩
    @JoinColumn(name = "item_id")
    private Item item;

    @OneToOne(fetch = FetchType.LAZY) // 1:1 관계
    @JoinColumn(name = "order_id")  // Order의 외래 키
    private Order order;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL) // 다대일 관계
    private List<ReviewImg> reviewImgs = new ArrayList<>();
}

