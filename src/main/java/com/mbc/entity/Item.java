package com.mbc.entity;

import com.mbc.constant.ItemSellStatus;
import com.mbc.constant.ItemStatus;
import com.mbc.dto.ItemFormDto;
import exception.OutOfStockException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "item")
@Getter
@Setter
@ToString
public class Item extends BaseEntity {

    @Id
    @Column(name = "item_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String itemNm; //상품명

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stockNumber; //재고수량

    @Lob
    @Column(nullable = false)
    private String itemDetail; //상품 상세 설명

    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus; //상품 판매 상태

    @Enumerated(EnumType.STRING)
    private ItemStatus itemStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    // 추가된 필드들
    @Column(nullable = false)
    private String shipping; // 배송비 포함 여부 ("free" 또는 "separate")

    @Column(nullable = false)
    private Integer shippingPrice;

    @Column(nullable = false)
    private String tradeAvailable; // 직거래 가능 여부 ("possible" 또는 "impossible")

    @Column(nullable = true)
    private String tradeLocation; // 직거래 위치 정보

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id") // 로그인한 사용자와 연관된 필드
    private Member member; // 아이템을 등록한 사용자



    // OneToMany 관계 추가 (아이템에 대한 리뷰)
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>(); // 아이템에 대한 리뷰 리스트


    // OneToMany 관계 추가
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemImg> itemImgs = new ArrayList<>(); // 아이템 이미지 리스트

    public void updateItem(ItemFormDto itemFormDto) {
        this.itemNm = itemFormDto.getItemNm();
        this.price = itemFormDto.getPrice();
        this.stockNumber = itemFormDto.getStockNumber();
        this.itemDetail = itemFormDto.getItemDetail();
        this.itemSellStatus = itemFormDto.getItemSellStatus();
        this.itemStatus = itemFormDto.getItemStatus(); // 추가된 상품 상태 반영
        this.shipping = itemFormDto.getShipping(); // 배송비 포함 여부 반영
        this.shippingPrice = itemFormDto.getShippingPrice(); // 배송비 반영
        this.tradeAvailable = itemFormDto.getTradeAvailable(); // 직거래 가능 여부 반영
        this.tradeLocation = itemFormDto.getTradeLocation(); // 직거래 위치 반영
    }

//    private static ModelMapper modelMapper = new ModelMapper();
//
//    public void updateItem(ItemFormDto itemFormDto) {
//        modelMapper.map(itemFormDto, this);  // ModelMapper를 사용하여 DTO를 엔티티로 매핑
//    }

    //updateItem 메서드는 인스턴스 상태를 수정하는 작업이기 때문에 static으로 선언하지 않고,
    //해당 객체에서 직접 호출하는 인스턴스 메서드로 두는 것

    public void removeStock(int stockNumber){
        int restStock = this.stockNumber - stockNumber;
        if(restStock < 0){
            throw new OutOfStockException("상품의 재고가 부족 합니다.(현재 재고 수량: " +
                    this.stockNumber + ")");
        }
        this.stockNumber = restStock;

        // 재고가 0이면 상태를 SOLD_OUT으로 변경
        if (this.stockNumber == 0) {
            this.itemSellStatus = ItemSellStatus.SOLD_OUT;
        }
    }

    public void addStock(int stockNumber){
        this.stockNumber += stockNumber;
        this.itemSellStatus = ItemSellStatus.SELL;
    }

    // 카테고리 계층을 반환하는 메서드
    public String getCategoryHierarchy() {
        StringBuilder categoryHierarchy = new StringBuilder();

        Category currentCategory = this.category;
        while (currentCategory != null) {
            if (categoryHierarchy.length() > 0) {
                categoryHierarchy.insert(0, " > ");  // 구분자 " > " 삽입
            }
            categoryHierarchy.insert(0, currentCategory.getName());  // 카테고리 이름 삽입
            currentCategory = currentCategory.getParent();  // 부모 카테고리로 이동
        }

        return categoryHierarchy.toString();  // 카테고리 경로 반환
    }

    public Long getGrandparentCategoryId() {
        return category != null ? category.getGrandparentId() : null;
    }
}