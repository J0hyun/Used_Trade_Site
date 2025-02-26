package com.mbc.repository;

import com.mbc.dto.ItemSearchDto;
import com.mbc.dto.MainItemDto;
import com.mbc.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);

    Page<Item> findItemsByUserEmail(String userEmail, ItemSearchDto itemSearchDto, Pageable pageable);

}
