package com.mbc.service;

import com.mbc.entity.MemberImg;
import com.mbc.repository.ItemRepository;
import com.mbc.repository.MemberImgRepository;
import com.mbc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class MemberImgService {

    @Value("C:/shop/member")
    private String memberImgLocation;

    private final MemberImgRepository memberImgRepository;

    private final FileService fileService;

    public void saveMemberImg(MemberImg memberImg, MultipartFile profileImgFile) throws Exception {
        String oriImgName = profileImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {
            imgName = fileService.uploadFile(memberImgLocation, oriImgName, profileImgFile.getBytes());
            // 날짜 경로를 포함한 URL 경로로 imgUrl 설정
            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            imgUrl = "/images/member/" + datePath + "/" + imgName;  // 날짜 경로 포함

            // 이미지 정보 업데이트 및 저장
            memberImg.updateMemberImg(oriImgName, imgName, imgUrl);
            log.info("이미지경로: " + imgUrl);
            memberImgRepository.save(memberImg);
        }
    }

}
