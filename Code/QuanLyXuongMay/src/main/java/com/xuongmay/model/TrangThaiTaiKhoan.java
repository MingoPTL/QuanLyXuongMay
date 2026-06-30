package com.xuongmay.model;

public enum TrangThaiTaiKhoan {
    HoatDong("Hoạt động"),
    NgungHoatDong("Ngừng hoạt động");

    private final String description;

    TrangThaiTaiKhoan(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
