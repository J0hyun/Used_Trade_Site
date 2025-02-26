package com.mbc.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name="member_img")
@Getter
@Setter
public class MemberImg extends BaseTimeEntity {

    @Id
    @Column(name="member_img_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imgName;

    private String oriImgName;

    private String imgUrl;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Member member;

    // 기본 생성자
    public MemberImg() {}

    // Member를 인자로 받는 생성자 추가
    public MemberImg(Member member) {
        this.member = member;
    }

    // 이미지를 업데이트할 때 사용할 메서드
    public void updateMemberImg(String oriImgName, String imgName, String imgUrl){
        this.oriImgName = oriImgName;
        this.imgName = imgName;
        this.imgUrl = imgUrl;
    }
}
