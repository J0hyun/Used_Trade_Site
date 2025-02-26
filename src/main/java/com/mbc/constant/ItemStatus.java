package com.mbc.constant;

public enum ItemStatus {
    NEW("새 상품 (미사용)"),
    UNUSED("사용감 없음"),
    LIGHT_USE("사용감 적음"),
    HEAVY_USE("사용감 많음"),
    DAMAGED("고장/파손 상품");

    private final String description;

    ItemStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
