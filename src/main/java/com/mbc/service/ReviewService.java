package com.mbc.service;


import com.mbc.dto.ReviewFormDto;
import com.mbc.entity.Item;
import com.mbc.entity.Order;
import com.mbc.entity.Review;
import com.mbc.entity.ReviewImg;
import com.mbc.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.junit.runner.manipulation.Orderer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Log4j2
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final ItemRepository itemRepository;
    private final ReviewImgService reviewImgService;
    private final OrderRepository orderRepository;

    // 리뷰 조회 시 이미지 포함하기
    public List<Review> getReviewsWithImagesByItemOwner(String userEmail) {
        // 판매자가 판매한 상품에 대한 모든 리뷰를 가져옵니다.
        List<Review> reviews = reviewRepository.findByItem_CreatedBy(userEmail);

        // 각 리뷰에 관련된 이미지들을 조회하여 세팅
        for (Review review : reviews) {
            List<ReviewImg> reviewImgs = reviewImgRepository.findByReview(review);
            review.setReviewImgs(reviewImgs); // 리뷰에 해당 이미지를 세팅
        }

        return reviews;
    }

    // 리뷰 저장
    public Review saveReview(ReviewFormDto reviewFormDto) {
        log.info("리뷰서비스시작");
        // 현재 로그인한 사용자 정보 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication: "+ authentication.getName());

        String loggedInUserName = authentication.getName(); // 로그인한 사용자의 이름을 가져옵니다.
        log.info("loggedInUserName: " + loggedInUserName);
        log.info("reviewFormDtoItemId: " + reviewFormDto.getItemId());
        Item item = itemRepository.findById(reviewFormDto.getItemId())
                .orElseThrow(() -> new RuntimeException("아이템을 찾을 수 없습니다.")); // 아이템이 존재하지 않으면 예외를 던짐;

        log.info("getOrderId: " + reviewFormDto.getOrderId());
        // 주문 정보 가져오기
        Order order = orderRepository.findById(reviewFormDto.getOrderId())
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        log.info("아이템: " + item);

        Review existingReviews = reviewRepository.findByItemAndMemberName(item, loggedInUserName);

        if (existingReviews != null) {
            return existingReviews; // 이미 리뷰가 있다면 해당 리뷰 반환
        }

        // 새로운 리뷰 객체 생성
        Review review = new Review();
        review.setMemberName(loggedInUserName);  // 로그인한 사용자명 설정
        review.setReviewDetail(reviewFormDto.getReviewDetail()); // 후기 내용 설정
        review.setItem(item); // 상품 설정
        review.setRating(reviewFormDto.getRating()); // 별점 설정
        review.setOrder(order);

        // 리뷰 저장
        review = reviewRepository.save(review); // 리뷰를 먼저 저장하여 review_id가 생성되도록 함

        // 리뷰 이미지 저장 처리
        try {
            if (reviewFormDto.getReviewImgs() != null && !reviewFormDto.getReviewImgs().isEmpty()) {
                // 여러 이미지를 처리하기 위해 List<MultipartFile> 사용
                List<MultipartFile> reviewImgFiles = reviewFormDto.getReviewImgs();

                // ReviewImg 객체 리스트 준비
                List<ReviewImg> reviewImgs = new ArrayList<>();
                reviewImgService.saveReviewImgs(reviewImgs, reviewImgFiles, review);  // 이미지 저장
            }
        } catch (Exception e) {
            throw new RuntimeException("이미지 업로드 실패: " + e.getMessage());
        }

        return review; // 리뷰 반환
    }



    // 리뷰 등록 폼을 표시할 때 자동으로 로그인한 사용자명 설정
    public ReviewFormDto getReviewFormDto() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String loggedInUserName = authentication.getName();  // 로그인한 사용자의 이름을 가져옵니다.

        ReviewFormDto reviewFormDto = new ReviewFormDto();
        reviewFormDto.setMemberName(loggedInUserName);  // 로그인한 사용자명 자동 설정
        return reviewFormDto;
    }

    // 모든 리뷰 조회
    public List<Review> getAllReviews() {
        return reviewRepository.findAll(); // 모든 리뷰를 반환
    }

    public List<Review> getReviewsByMemberName(String memberName) {
        log.info("Fetching reviews for member: {}", memberName);
        return reviewRepository.findByMemberName(memberName);  // memberName으로 리뷰 조회
    }


    // 판매자가 판매한 상품에 대한 리뷰를 조회하는 메서드
    public List<Review> getReviewsByItemOwner(String userEmail) {
        return reviewRepository.findByItem_CreatedBy(userEmail);  // 올바른 메서드를 호출합니다.
    }

    // 특정 상품에 대해 해당 사용자가 이미 작성한 리뷰가 있는지 확인하는 메서드
    public Review getReviewsByItemAndMemberName(Long itemId, String memberName) {

        log.info("서비스 진입확인용");

        log.info(itemId + memberName);

        Review review = reviewRepository.findByItemIdAndMemberName(itemId, memberName);

        log.info("review: " + review);

        return review;  // 리뷰 조회
    }



    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("리뷰를 찾을 수 없습니다."));
    }

    public ReviewFormDto convertToReviewFormDto(Review review) {
        ReviewFormDto reviewFormDto = new ReviewFormDto();
        reviewFormDto.setId(review.getId());
        reviewFormDto.setMemberName(review.getMemberName());
        reviewFormDto.setReviewDetail(review.getReviewDetail());
        reviewFormDto.setItemName(review.getItem().getItemNm());
        reviewFormDto.setItemId(review.getItem().getId());
        reviewFormDto.setRating(review.getRating());
        return reviewFormDto;
    }

    public Review updateReview(Long reviewId, ReviewFormDto reviewFormDto, List<MultipartFile> reviewImgs) throws Exception {
        // 기존 리뷰 조회
        Review existingReview = getReviewById(reviewId);

        // 리뷰 내용 수정
        existingReview.setRating(reviewFormDto.getRating());
        existingReview.setReviewDetail(reviewFormDto.getReviewDetail());

        // 이미지 수정 처리
        if (!reviewImgs.isEmpty()) {
            reviewImgService.updateReviewImgs(existingReview, reviewImgs);
        }

        // 수정된 리뷰 저장
        return reviewRepository.save(existingReview);
    }



}