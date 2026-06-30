package com.xuongmay.model;

public enum TrangThaiHoaDon {
    ChuaThanhToan("Chưa thanh toán"),
    DaThanhToan("Đã thanh toán");

    private final String description;

    TrangThaiHoaDon(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
