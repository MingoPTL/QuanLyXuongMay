package com.xuongmay.model;

public enum ChucVu {
    ADMIN("Admin"),
    QUANLY("Quản lý");

    private final String description;

    ChucVu(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
