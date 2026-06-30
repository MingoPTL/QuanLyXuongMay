package com.xuongmay.model;

public enum TrangThaiSanPham {
    DangCat("Đang cắt"),
    DangMay("Đang may"),
    DangUi("Đang ủi"),
    DaHoanThanh("Đã hoàn thành");

    private final String description;

    TrangThaiSanPham(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
