package com.mbc.repository;

import com.mbc.entity.MemberImg;
import com.mbc.entity.Review;
import com.mbc.entity.ReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewImgRepository extends JpaRepository<ReviewImg, Long> {
    ReviewImg findByReviewId(Long reviewId);

    List<ReviewImg> findByReview(Review review);

    boolean existsByReviewAndImgName(Review review, String oriImgName); // 리뷰와 이미지 이름으로 중복 체크

    boolean existsByReviewAndOriImgName(Review review, String oriImgName);
}
