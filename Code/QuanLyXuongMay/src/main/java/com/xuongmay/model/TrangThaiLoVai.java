package com.xuongmay.model;

public enum TrangThaiLoVai {
    ChuaSuDung("Chưa sử dụng"),
    DangSuDung("Đang sử dụng"),
    RaSanPham("Ra sản phẩm");

    private final String description;

    TrangThaiLoVai(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
