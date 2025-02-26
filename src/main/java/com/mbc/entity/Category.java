package com.mbc.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "category")
@Getter
@Setter
public class Category {

    @Id
    @Column(name = "category_id")
    private Long id;  // 카테고리 고유 ID

    private String name;  // 카테고리 이름

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    @JsonBackReference  // 부모 카테고리에서 자식 카테고리를 직렬화하지 않도록 설정
    private Category parent;  // 상위 카테고리 (자기 참조 관계)

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // 자식 카테고리에서 부모 카테고리를 직렬화하도록 설정
    private List<Category> children;  // 하위 카테고리 목록

    public Long getGrandparentId() {
        return (parent != null && parent.getParent() != null) ? parent.getParent().getId() : null;
    }
}
