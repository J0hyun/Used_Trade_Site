//package com.mbc.service;
//
//import com.mbc.constant.ItemSellStatus;
//import com.mbc.dto.ItemFormDto;
//import com.mbc.entity.Item;
//import com.mbc.entity.ItemImg;
//import com.mbc.repository.ItemImgRepository;
//import com.mbc.repository.ItemRepository;
//import jakarta.persistence.EntityNotFoundException;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.test.context.support.WithMockUser;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.ArrayList;
//import java.util.List;
//
//@SpringBootTest
//@Log4j2
//@Transactional
//class ItemServiceTest {
//
//    @Autowired
//    ItemService itemService;
//
//    @Autowired
//    ItemRepository itemRepository;
//
//    @Autowired
//    ItemImgRepository itemImgRepository;
//
//    List<MultipartFile> createMultipartFiles() throws Exception {
//
//        List<MultipartFile> multipartFilelist = new ArrayList<>();
//
//        for(int i=0;i<5;i++){
//            String path = "C:/shop/item/";
//            String imageName = "image" + i + ".jpg";
//            MockMultipartFile multipartFile =
//                    new MockMultipartFile(path, imageName,
//                            "image/jpg", new byte[]{1,2,3,4});
//            multipartFilelist.add(multipartFile);
//        }
//
//        return multipartFilelist;
//    }
//
//    @Test
//    @DisplayName("상품 등록 테스트")
//    @WithMockUser(username = "admin", roles = "ADMIN")
//    void saveItem() throws Exception {
//        ItemFormDto itemFormDto = new ItemFormDto();
//        itemFormDto.setItemNm("테스트상품");
//        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
//        itemFormDto.setItemDetail("테스트 상품 입니다.");
//        itemFormDto.setPrice(1000);
//        itemFormDto.setStockNumber(100);
//
//        List<MultipartFile> multipartFilelist = createMultipartFiles();
//        Long itemId = itemService.saveItem(itemFormDto, multipartFilelist);
//
//        List<ItemImg> itemImgList =
//                itemImgRepository.findByItemIdOrderByIdAsc(itemId);
//        Item item = itemRepository.findById(itemId)
//                .orElseThrow(EntityNotFoundException::new);
//
////        assertEquals(itemFormDto.getItemNm(), item.getItemNm());
////        assertEquals(itemFormDto.getItemSellStatus(), item.getItemSellStatus());
////        assertEquals(itemFormDto.getItemDetail(), item.getItemDetail());
////        assertEquals(itemFormDto.getPrice(), item.getPrice());
////        assertEquals(itemFormDto.getStockNumber(), item.getStockNumber());
////        assertEquals(multipartFilelist.get(0).getOriginalFilename(),
////                itemImgList.get(0).getOriImgName());
//    }
//
//}