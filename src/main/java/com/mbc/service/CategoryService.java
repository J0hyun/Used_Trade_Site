package com.mbc.service;

import com.mbc.entity.Category;
import com.mbc.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // 부모 카테고리 없는 카테고리 목록
    public List<Category> getParentCategories() {
        return categoryRepository.findByParentIsNull(); // 최상위 부모 카테고리만 가져옴
    }

    // 특정 카테고리의 하위 카테고리 목록
    public List<Category> getSubCategories(Long parentId) {
        return categoryRepository.findByParent_Id(parentId); // 부모 ID로 하위 카테고리 목록을 가져옴
    }

}
