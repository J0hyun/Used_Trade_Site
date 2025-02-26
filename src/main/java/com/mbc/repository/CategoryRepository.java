package com.mbc.repository;

import com.mbc.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    // 상위 카테고리가 없는 카테고리 (부모가 없는 카테고리) 리스트를 찾는 쿼리
    List<Category> findByParentIsNull();

    // 특정 부모 카테고리의 하위 카테고리 리스트를 찾는 쿼리
    List<Category> findByParent_Id(Long parentCategoryId);

}
