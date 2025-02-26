package com.mbc.entity;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.Objects;

@Entity
@Table(name = "review_img")
@Getter
@Setter
public class ReviewImg extends BaseEntity {

    @Id
    @Column(name = "review_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public void updateReviewImg(String oriImgName, String imgName, String imgUrl) {
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
    // equals()와 hashCode()를 imgName과 review를 기준으로 정의
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReviewImg reviewImg = (ReviewImg) o;
        return imgName != null && imgName.equals(reviewImg.imgName) && review != null && review.equals(reviewImg.review);
    }

    @Override
    public int hashCode() {
        return Objects.hash(imgName, review);
    }
}
