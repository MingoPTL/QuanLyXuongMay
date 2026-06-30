package com.xuongmay.model;

public enum TrangThaiDonHang {
    ChuaGiao("Chưa giao"),
    DaGiao("Đã giao");

    private final String description;

    TrangThaiDonHang(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
