//package com.mbc.service;
//
//import com.mbc.constant.ItemSellStatus;
//import com.mbc.dto.CartItemDto;
//import com.mbc.entity.CartItem;
//import com.mbc.entity.Item;
//import com.mbc.entity.Member;
//import com.mbc.repository.CartItemRepository;
//import com.mbc.repository.ItemRepository;
//import com.mbc.repository.MemberRepository;
//import jakarta.persistence.EntityNotFoundException;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@Transactional
//class CartServiceTest {
//
//    @Autowired
//    ItemRepository itemRepository;
//
//    @Autowired
//    MemberRepository memberRepository;
//
//    @Autowired
//    CartService cartService;
//
//    @Autowired
//    CartItemRepository cartItemRepository;
//
//    public Item saveItem(){
//        Item item = new Item();
//        item.setItemNm("테스트 상품");
//        item.setPrice(10000);
//        item.setItemDetail("테스트 상품 상세 설명");
//        item.setItemSellStatus(ItemSellStatus.SELL);
//        item.setStockNumber(100);
//        return itemRepository.save(item);
//    }
//
//    public Member saveMember(){
//        Member member = new Member();
//        member.setEmail("test@gmail.com");
//        return memberRepository.save(member);
//    }
//
//    @Test
//    @DisplayName("장바구니 담기 테스트")
//    public void addCart(){
//        Item item = saveItem();
//        Member member = saveMember();
//
//        CartItemDto cartItemDto = new CartItemDto();
//        cartItemDto.setCount(5);
//        cartItemDto.setItemId(item.getId());
//
//        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());
//
//        CartItem cartItem = cartItemRepository.findById(cartItemId)
//                .orElseThrow(EntityNotFoundException::new);
//
////        assertEquals(item.getId(), cartItem.getItem().getId());
////        assertEquals(cartItemDto.getCount(), cartItem.getCount());
//    }
//
//}