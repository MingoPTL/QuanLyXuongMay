package com.xuongmay.model;

public enum PhuongThucThanhToan {
    TienMat("Tiền mặt"),
    ChuyenKhoan("Chuyển khoản");

    private final String description;

    PhuongThucThanhToan(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
