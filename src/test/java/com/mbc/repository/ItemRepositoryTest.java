//package com.mbc.repository;
//
//import com.querydsl.core.BooleanBuilder;
//import com.querydsl.jpa.impl.JPAQuery;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import com.mbc.constant.ItemSellStatus;
//import com.mbc.entity.Item;
//import com.mbc.entity.QItem;
//import jakarta.persistence.EntityManager;
//import lombok.extern.log4j.Log4j2;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageRequest;
//import org.springframework.data.domain.Pageable;
//import org.thymeleaf.util.StringUtils;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//@SpringBootTest
//@Log4j2
//class ItemRepositoryTest {
//
//    @Autowired
//    ItemRepository itemRepository;
//
//    @Autowired
//    EntityManager em;
//
//    @Test
//    @DisplayName("Querydsl 조회 테스트1")
//    public void querydslTest(){
//
//        JPAQueryFactory queryFactory = new JPAQueryFactory(em);
//        QItem qItem = QItem.item;
//
//        JPAQuery<Item> query = queryFactory.selectFrom(qItem)
//                .where(qItem.itemSellStatus.eq(ItemSellStatus.SELL))
//                .where(qItem.price.goe(50000))
//                .orderBy(qItem.price.desc());
//
//        List<Item> itemList = query.fetch();
//
//        itemList.forEach(System.out::println);
//
//        int total = itemList.size();
//        log.info("total : " + total);
//    }
//
//    public void createItemList2(){
//        for(int i=1;i<=5;i++){
//            Item item = new Item();
//            item.setItemNm("테스트 상품" + i);
//            item.setPrice(10000 + i);
//            item.setItemDetail("테스트 상품 상세 설명" + i);
//            item.setItemSellStatus(ItemSellStatus.SELL);
//            item.setStockNumber(100);
//            item.setRegTime(LocalDateTime.now());
//            item.setUpdateTime(LocalDateTime.now());
//            itemRepository.save(item);
//        }
//
//        for(int i=6;i<=10;i++){
//            Item item = new Item();
//            item.setItemNm("테스트 상품" + i);
//            item.setPrice(10000 + i);
//            item.setItemDetail("테스트 상품 상세 설명" + i);
//            item.setItemSellStatus(ItemSellStatus.SOLD_OUT);
//            item.setStockNumber(0);
//            item.setRegTime(LocalDateTime.now());
//            item.setUpdateTime(LocalDateTime.now());
//            itemRepository.save(item);
//        }
//    }
//
//    @Test
//    @DisplayName("Querydsl 조회 테스트2")
//    public void querydslTest2(){
//
//        BooleanBuilder booleanBuilder = new BooleanBuilder();
//
//        QItem item = QItem.item;
//
//        String itemDetail = "테스트 상품 상세 설명";
//        int price = 10003;
//        String itemSellStatus = "SELL";
//
//        booleanBuilder.and(item.itemDetail.like("%" + itemDetail + "%"));
//        booleanBuilder.and(item.price.gt(price));
//
//        if(StringUtils.equals(itemSellStatus, ItemSellStatus.SELL)){
//            booleanBuilder.and(item.itemSellStatus.eq(ItemSellStatus.SELL));
//        }
//
//        Pageable pageable = PageRequest.of(0, 5);
//
//        Page<Item> itemPagingResult = itemRepository.findAll(booleanBuilder, pageable);
//
//        log.info("total elements : " + itemPagingResult.getTotalElements());
//
//        itemPagingResult.getContent().forEach(System.out::println);
//    }
//
//    @Test
//    @DisplayName("상품 저장 테스트")
//    public void createItemTest(){
//        Item item = new Item();
//
//        item.setItemNm("item1");
//        item.setPrice(10000);
//        item.setItemDetail("itemDetail1");
//        item.setStockNumber(100);
//        item.setRegTime(LocalDateTime.now());
//        item.setUpdateTime(LocalDateTime.now());
//        item.setItemSellStatus(ItemSellStatus.SELL);
//
//        Item savedItem = itemRepository.save(item);
//
//        System.out.println(savedItem);
//    }
//
//    @Test
//    @DisplayName("레코드 삭제")
//    public void deleteItemTest(){
//        itemRepository.deleteById(2L);
//    }
//
//    @Test
//    @DisplayName("레코드 조회")
//    public void findItemByIdTest(){
//        Optional<Item> item = itemRepository.findById(1L);
//        System.out.println("-----------------");
//        item.ifPresent(System.out::println);
//    }
//
//    @Test
//    @DisplayName("상품 저장 테스트")
//    public void createItemTest2(){
//        for(int i=0; i<10; i++) {
//            Item item = new Item();
//
//            item.setItemNm("item" + i);
//            item.setPrice(10000 * i);
//            item.setItemDetail("itemDetail1" + i);
//            item.setStockNumber(100 * i);
//            item.setRegTime(LocalDateTime.now());
//            item.setUpdateTime(LocalDateTime.now());
//            item.setItemSellStatus(ItemSellStatus.SELL);
//            itemRepository.save(item);
//        }
//    }
//
//    @Test
//    @DisplayName("레코드 개수 조회")
//    public void countItemTest(){
//        long count = itemRepository.count();
//        System.out.println("count: " + count);
//    }
//
//    @Test
//    @DisplayName("전체 레코드 조회")
//    public void selectAllItemTest(){
//        List<Item> items = itemRepository.findAll();
//        items.forEach(System.out::println);
//    }
//
//    @Test
//    @DisplayName("레코드 수정")
//    public void selectByIdTest(){
//        Optional<Item> result = itemRepository.findById(2L);
//        Item item = result.get();
//
//        item.setItemNm("수정된 상품 이름2");
//        item.setPrice(2222);
//
//        itemRepository.save(item); // save() 메서드가 호출되면서 엔티티 상태가 변경 감지(Dirty Checking) 되며, 이때 다시 두 번째 select 쿼리가 발생
//
//        // 변경된 내용 확인
//        Optional<Item> updatedItem = itemRepository.findById(2L);
//        updatedItem.ifPresent(System.out::println);
//    }
//
//    @Test
//    @DisplayName("상품명조회(전체일치)")
//    public void selectByItemNameTest(){
//        String name = "item7";
//        List<Item> itemName = itemRepository.findItemByItemNm(name);
//
//        itemName.forEach(System.out::println);
//    }
//
//    @Test
//    @DisplayName("상품명조회(부분일치)")
//    public void selectByItemNameContainingTest(){
//        String name = "item";
//        List<Item> itemName = itemRepository.findItemByItemNmContaining(name);
//
//        itemName.forEach(System.out::println);
//    }
//
//    @Test
//    @DisplayName("일정가격 이상 조회")
//    public void findByPriceGreaterThanEqualtest(){
//        List<Item> items = itemRepository.findItemByPriceGreaterThanEqual(50000);
//        items.forEach(System.out::println);
//    }
//
//    @Test
//    @DisplayName("일정가격 이하 조회 높은순으로 정렬")
//    public void findByPriceLessThanEqualtest(){
//        List<Item> items = itemRepository.findItemByPriceLessThanEqualOrderByPriceDesc(50000);
//        items.forEach(System.out::println);
//    }
//
//    @Test
//    @DisplayName("판매 상태별 조회")
//    public void selectByItemSellStatusTest() {
//        List<Item> sellItems = itemRepository.findItemByItemSellStatus(ItemSellStatus.SELL);
//        sellItems.forEach(System.out::println);
//
//        List<Item> soldOutItems = itemRepository.findItemByItemSellStatus(ItemSellStatus.SOLD_OUT);
//        soldOutItems.forEach(System.out::println);
//    }
//
//    @Test
//    @DisplayName("등록 시간 순으로 정렬")
//    public void findByOrderByRegTimeDescTest() {
//        List<Item> items = itemRepository.findByOrderByRegTimeDesc();
//        items.forEach(System.out::println);
//    }
//
//    @Test
//    @DisplayName("ItemDetail")
//    public void findByItemDetailTest() {
//        List<Item> items = itemRepository.findByItemDetail("10");
//
//        items.forEach(System.out::println);
//    }
//
//    @Test
//    @DisplayName("ItemDetail")
//    public void findByItemDetailTest2() {
//        List<Item> items = itemRepository.findByItemDetail2("15");
//
//        items.forEach(System.out::println);
//    }
//
//}