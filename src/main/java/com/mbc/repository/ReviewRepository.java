package com.mbc.repository;

import com.mbc.entity.Item;
import com.mbc.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Review findByItemAndMemberName(Item item, String memberName);

    List<Review> findByMemberName(String memberName);

    // 판매자가 판매한 상품에 대한 리뷰를 조회하는 메서드 추가
    List<Review> findByItem_CreatedBy(String memberName); // 'item'의 'createdBy' 필드에 해당하는 판매자 이메일로 조회

    Review findByItemIdAndMemberName(Long itemId, String memberName);

    boolean existsByItemAndMemberName(Item item, String memberName);

}
