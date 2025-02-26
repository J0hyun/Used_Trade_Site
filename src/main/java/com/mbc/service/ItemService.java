package com.mbc.service;

import com.mbc.dto.ItemFormDto;
import com.mbc.dto.ItemImgDto;
import com.mbc.dto.ItemSearchDto;
import com.mbc.dto.MainItemDto;
import com.mbc.entity.Category;
import com.mbc.entity.Item;
import com.mbc.entity.ItemImg;
import com.mbc.entity.Member;
import com.mbc.repository.CategoryRepository;
import com.mbc.repository.ItemImgRepository;
import com.mbc.repository.ItemRepository;
import com.mbc.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImgService itemImgService;
    private final ItemImgRepository itemImgRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;

    // 상품 등록 메서드
    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        // Member 정보 설정: 판매자 (판매자의 memberId)
        Member member = memberRepository.findById(itemFormDto.getMemberId())
                .orElseThrow(() -> new EntityNotFoundException("판매자가 존재하지 않습니다."));


        // 상품 등록: 판매자와 구매자 정보 포함하여 상품 객체 생성
        Item item = itemFormDto.createItem(member);  // 판매자와 구매자 정보를 전달하여 아이템 생성

        // 상품 저장
        itemRepository.save(item);

        // 이미지 등록
        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);  // 이미지가 속한 상품 설정
            if (i == 0)
                itemImg.setRepimgYn("Y");  // 첫 번째 이미지를 대표 이미지로 설정
            else
                itemImg.setRepimgYn("N");  // 나머지 이미지는 대표 이미지가 아님
            itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));  // 이미지 파일 저장
        }

        // 등록된 상품의 ID 반환
        return item.getId();
    }


    @Transactional(readOnly = true)
    public ItemFormDto getItemDtl(Long itemId) {

        // 상품 이미지 목록을 가져오기
        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
        List<ItemImgDto> itemImgDtoList = new ArrayList<>();
        for (ItemImg itemImg : itemImgList) {
            ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
            itemImgDtoList.add(itemImgDto);
        }

        // 아이템 정보 가져오기
        Item item = itemRepository.findById(itemId)
                .orElseThrow(EntityNotFoundException::new);

        // Item을 ItemFormDto로 변환
        ItemFormDto itemFormDto = ItemFormDto.of(item);

        // 카테고리 경로 설정
        itemFormDto.setCategoryHierarchy(item.getCategoryHierarchy());

        // 이미지 리스트 추가
        itemFormDto.setItemImgDtoList(itemImgDtoList);

        // 카테고리 조부모 ID 설정
        Long grandparentCategoryId = item.getGrandparentCategoryId();
        itemFormDto.setGrandparentCategoryId(grandparentCategoryId);

        return itemFormDto;
    }

    public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList)
            throws Exception{

        //상품 수정
        Item item = itemRepository.findById(itemFormDto.getId())
                .orElseThrow(EntityNotFoundException::new);
        item.updateItem(itemFormDto);

        // 카테고리 설정 (categoryId로 Category 조회)
        Long categoryId = itemFormDto.getCategoryId();
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid category ID: " + categoryId));
        item.setCategory(category);  // 카테고리 설정
        System.out.println("itemFormDto: " + itemFormDto.toString());



        itemImgService.updateItemImgs(item.getId(), itemImgFileList);

        return item.getId();
    }

    @Transactional(readOnly = true)
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto,
                                       Pageable pageable) {
        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto,
                                             Pageable pageable) {
        return itemRepository.getMainItemPage(itemSearchDto, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Item> getUserItems(String userEmail, ItemSearchDto itemSearchDto, Pageable pageable) {
        return itemRepository.findItemsByUserEmail(userEmail, itemSearchDto, pageable);
    }

    public void deleteItem(Long itemId) {
        itemRepository.deleteById(itemId);  // DB에서 아이템 삭제
    }

    // 상품 등록 갯수
    public Long getItemCount() {
        return itemRepository.count();
    }
}
