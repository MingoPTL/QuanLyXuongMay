package com.xuongmay.model;

public enum ChuyenMon {
    ThoMay("Thợ may"),
    ThoUi("Thợ ủi");

    private final String description;

    ChuyenMon(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }
}
