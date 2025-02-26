package com.mbc.repository;

import com.mbc.constant.ItemSellStatus;
import com.mbc.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>,
        ItemRepositoryCustom{
    List<Item>  findItemByItemNm(String itemNm);
    List<Item>  findItemByItemNmContaining(String itemNm);

    List<Item> findItemByPriceGreaterThanEqual(int price);

    List<Item> findItemByPriceLessThanEqualOrderByPriceDesc(int price);

    List<Item> findItemByItemSellStatus(ItemSellStatus itemSellStatus);

    List<Item> findByOrderByRegTimeDesc();

    @Query("select i from Item i where i.itemDetail like %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);

    @Query(value = "select * from item where item_detail like %:itemDetail% order by price desc", nativeQuery = true)
    List<Item> findByItemDetail2(@Param("itemDetail") String itemDetail);

    @Query("SELECT i FROM Item i WHERE i.category.id = :categoryId")
    List<Item> findItemsByCategoryId(@Param("categoryId") Long categoryId);


    List<Item> findByItemNm(String itemName);
}
