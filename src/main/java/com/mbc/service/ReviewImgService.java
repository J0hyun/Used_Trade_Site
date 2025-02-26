package com.mbc.service;

import com.mbc.entity.Review;
import com.mbc.entity.ReviewImg;
import com.mbc.repository.ReviewImgRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ReviewImgService {

    @Value("C:/shop/review")
    private String reviewImgLocation;

    private final ReviewImgRepository reviewImgRepository;

    private final FileService fileService;

    public void saveReviewImgs(List<ReviewImg> reviewImgs, List<MultipartFile> reviewImgFiles, Review review) throws Exception {
        for (int i = 0; i < reviewImgFiles.size(); i++) {
            MultipartFile reviewImgFile = reviewImgFiles.get(i);
            String oriImgName = reviewImgFile.getOriginalFilename();
            String imgName = "";
            String imgUrl = "";

            // 파일 업로드
            if (!StringUtils.isEmpty(oriImgName)) {
                // 파일 이름을 고유하게 처리하여 중복을 방지
                imgName = fileService.uploadFile(reviewImgLocation, oriImgName, reviewImgFile.getBytes());
                // 날짜 경로를 포함한 URL 경로로 imgUrl 설정
                String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                imgUrl = "/images/review/" + datePath + "/" + imgName;  // 날짜 경로 포함

                // ReviewImg 객체 생성 및 업데이트
                ReviewImg reviewImg = new ReviewImg();
                reviewImg.setReview(review);  // Review와 연결
                reviewImg.updateReviewImg(oriImgName, imgName, imgUrl);

                // 중복 체크: 동일한 oriImgName과 review가 이미 DB에 존재하는지 확인
                if (!reviewImgRepository.existsByReviewAndOriImgName(review, oriImgName)) {
                    reviewImgs.add(reviewImg);  // 중복되지 않으면 리스트에 추가
                } else {
                    // 중복된 이미지가 발견되면 처리
                    System.out.println("중복된 이미지가 발견되었습니다: " + oriImgName);
                    // 중복된 이미지를 저장하지 않고 건너뜁니다.
                }
            }
        }

        // 중복되지 않은 이미지들만 DB에 저장
        if (!reviewImgs.isEmpty()) {
            reviewImgRepository.saveAll(reviewImgs);
        }
    }


    public void updateReviewImgs(Review existingReview, List<MultipartFile> reviewImgFiles) throws Exception {
        // 기존 리뷰에 연결된 이미지들 삭제
        List<ReviewImg> existingReviewImgs = reviewImgRepository.findByReview(existingReview);

        for (ReviewImg reviewImg : existingReviewImgs) {
            // 기존 이미지를 삭제하거나 업데이트할 수 있습니다.
            reviewImgRepository.delete(reviewImg);  // 예시로 기존 이미지를 삭제합니다.
        }

        // 새로 업로드된 이미지를 저장
        List<ReviewImg> newReviewImgs = new ArrayList<>();
        for (MultipartFile reviewImgFile : reviewImgFiles) {
            String oriImgName = reviewImgFile.getOriginalFilename();
            String imgName = "";
            String imgUrl = "";

            if (!StringUtils.isEmpty(oriImgName)) {
                // 파일 이름을 고유하게 처리하여 중복을 방지
                imgName = fileService.uploadFile(reviewImgLocation, oriImgName, reviewImgFile.getBytes());

                // 날짜 경로를 포함한 URL 경로로 imgUrl 설정
                String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
                imgUrl = "/images/review/" + datePath + "/" + imgName;  // 날짜 경로 포함

                // 새로운 ReviewImg 객체 생성 및 연결
                ReviewImg reviewImg = new ReviewImg();
                reviewImg.setReview(existingReview);
                reviewImg.updateReviewImg(oriImgName, imgName, imgUrl);

                // 중복 체크 후 리스트에 추가
                if (!reviewImgRepository.existsByReviewAndOriImgName(existingReview, oriImgName)) {
                    newReviewImgs.add(reviewImg);  // 중복되지 않으면 리스트에 추가
                }
            }
        }

        // 새로 추가된 이미지들을 DB에 저장
        if (!newReviewImgs.isEmpty()) {
            reviewImgRepository.saveAll(newReviewImgs);
        }
    }



}
