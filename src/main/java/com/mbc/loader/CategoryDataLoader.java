package com.mbc.loader;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbc.entity.Category;
import com.mbc.repository.CategoryRepository;
import org.springframework.context.event.EventListener;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class CategoryDataLoader {

    private final CategoryRepository categoryRepository;

    public CategoryDataLoader(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void loadCategoryData() throws Exception {
        // JSON 파일 경로
        File jsonFile = new File("src/main/resources/categories.json");

        // JSON 파일 읽기
        ObjectMapper objectMapper = new ObjectMapper();
        List<Category> categories = objectMapper.readValue(jsonFile, objectMapper.getTypeFactory().constructCollectionType(List.class, Category.class));

        // 카테고리 저장을 위한 임시 맵 (id -> Category)
        Map<Long, Category> categoryMap = new HashMap<>();

        // 부모-자식 관계 설정
        for (Category category : categories) {
            // 부모 카테고리가 이미 존재하는지 확인하고, 존재하면 부모를 설정
            if (category.getParent() != null) {
                Long parentId = category.getParent().getId();
                Category parentCategory = categoryMap.get(parentId);
                category.setParent(parentCategory);
            }

            // 카테고리 id로 존재하는지 확인
            Category existingCategory = categoryRepository.findById(category.getId()).orElse(null);

            if (existingCategory == null) {
                // 카테고리가 존재하지 않으면 저장
                Category savedCategory = categoryRepository.save(category);
                categoryMap.put(savedCategory.getId(), savedCategory);
                System.out.println("카테고리 '" + category.getName() + "'가 저장되었습니다.");
            } else {
                // 이미 존재하는 카테고리라면, 그 카테고리를 사용
                category.setId(existingCategory.getId());
                categoryMap.put(existingCategory.getId(), existingCategory);
                System.out.println("카테고리 '" + category.getName() + "'는 이미 존재합니다.");
            }
        }
    }
}