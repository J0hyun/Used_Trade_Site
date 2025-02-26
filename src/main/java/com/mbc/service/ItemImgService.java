package com.mbc.service;

import com.mbc.entity.ItemImg;
import com.mbc.repository.ItemImgRepository;
import com.mbc.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("C:/shop/item")
    private String itemImgLocation;

    private final ItemImgRepository itemImgRepository;

    private final ItemRepository itemRepository;

    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        //파일 업로드
        if(!StringUtils.isEmpty(oriImgName)){
            imgName = fileService.uploadFile(itemImgLocation, oriImgName,
                    itemImgFile.getBytes());

            // 날짜 경로를 포함한 URL 경로로 imgUrl 설정
            String datePath = new SimpleDateFormat("yyyy/MM/dd").format(new Date());
            imgUrl = "/images/item/" + datePath + "/" + imgName;  // 날짜 경로 포함

            //상품 이미지 정보 저장
            itemImg.updateItemImg(oriImgName, imgName, imgUrl);
            itemImgRepository.save(itemImg);
        }
    }

    public void updateItemImgs(Long itemId, List<MultipartFile> itemImgFileList) throws Exception {
        // 아이템에 속한 모든 기존 이미지 삭제
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        // 기존 이미지 파일 삭제
        for (ItemImg itemImg : itemImgList) {

            fileService.deleteFile(itemImgLocation + "/" + itemImg.getImgName());

            itemImgRepository.delete(itemImg);  // 기존 이미지 정보 삭제
        }

        // 새로운 이미지 파일 저장
        for (int i = 0; i < itemImgFileList.size(); i++) {
            MultipartFile itemImgFile = itemImgFileList.get(i);

            if (!itemImgFile.isEmpty()) {
                ItemImg newItemImg = new ItemImg();

                // 대표 이미지 설정
                if (i == 0) {
                    newItemImg.setRepimgYn("Y");
                } else {
                    newItemImg.setRepimgYn("N");
                }

                // 새 이미지 추가
                saveItemImg(newItemImg, itemImgFile);

                // 아이템 ID 설정 (연관 관계 매핑)
                newItemImg.setItem(itemRepository.findById(itemId)
                        .orElseThrow(() -> new EntityNotFoundException("Item not found with id: " + itemId)));
            }
        }
    }
}
