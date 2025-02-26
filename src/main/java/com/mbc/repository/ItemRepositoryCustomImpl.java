package com.mbc.repository;

import com.mbc.entity.QItem;
import com.mbc.entity.QItemImg;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.mbc.constant.ItemSellStatus;
import com.mbc.dto.ItemSearchDto;
import com.mbc.dto.MainItemDto;
import com.mbc.dto.QMainItemDto;
import com.mbc.entity.Item;
import jakarta.persistence.EntityManager;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;


@Log4j2
public class ItemRepositoryCustomImpl implements ItemRepositoryCustom {

    private  JPAQueryFactory jpaQueryFactory;

    public ItemRepositoryCustomImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus itemSellStatus) {
        return itemSellStatus == null ? null : QItem.item.itemSellStatus.eq(itemSellStatus);
    }

    private BooleanExpression regDtsAfter(String searchDateType){

        LocalDateTime dateTime = LocalDateTime.now();  //현재시간

        if(StringUtils.equals("all", searchDateType) || searchDateType == null){
            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);
        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);
        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);
        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }
        return QItem.item.regTime.after(dateTime);
    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery){

        if(StringUtils.equals("itemNm", searchBy)){
            return QItem.item.itemNm.like("%" +searchQuery+ "%");
        }else if(StringUtils.equals("createdBy", searchBy)){
            return QItem.item.createdBy.like("%" +searchQuery+ "%");
        }
        return null;
    }

    private BooleanExpression itemNmLike(String searchQuery){
        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemNm.like("%" +searchQuery+ "%");
    }


    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        List<Item> content = jpaQueryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType())
                        , searchSellStatusEq(itemSearchDto.getSearchSellStatus())
                        , searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                ).fetchOne();

        log.info("total : " + total);

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> content = jpaQueryFactory.select(
                        new QMainItemDto(
                                item.id,
                                item.itemNm,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price,
                                item.itemSellStatus
                        )
                ).from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        itemNmLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        /*
        select count(*)
        from item_img img
        join  item  i
        on img.item_id = i.item_id
        where
        img.repimg_yn = 'Y'
        and
        i.item_nm like '%스프링%';
         */

        Long total = jpaQueryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        itemNmLike(itemSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public Page<Item> findItemsByUserEmail(String userEmail, ItemSearchDto itemSearchDto, Pageable pageable) {
        QItem item = QItem.item;

        // 조건 추가: 로그인한 사용자가 작성한 상품만 필터링
        List<Item> content = jpaQueryFactory
                .selectFrom(item)
                .where(
                        item.createdBy.eq(userEmail), // 유저 이메일로 필터링
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                )
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = jpaQueryFactory
                .select(Wildcard.count)
                .from(item)
                .where(
                        item.createdBy.eq(userEmail), // 유저 이메일로 필터링
                        regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery())
                )
                .fetchOne();

        log.info("Total items for user [{}]: {}", userEmail, total);

        return new PageImpl<>(content, pageable, total);
    }
}