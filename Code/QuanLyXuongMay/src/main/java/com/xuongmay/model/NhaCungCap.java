package com.xuongmay.model;

public class NhaCungCap {
    private String maNhaCungCap;
    private String tenNhaCungCap;
    private String soDienThoai;
    private String diaChiNha;

    public NhaCungCap() {}

    public NhaCungCap(String maNhaCungCap, String tenNhaCungCap, String soDienThoai, String diaChiNha) {
        this.maNhaCungCap = maNhaCungCap;
        this.tenNhaCungCap = tenNhaCungCap;
        this.soDienThoai = soDienThoai;
        this.diaChiNha = diaChiNha;
    }

    public String getMaNhaCungCap() {
        return maNhaCungCap;
    }

    public void setMaNhaCungCap(String maNhaCungCap) {
        this.maNhaCungCap = maNhaCungCap;
    }

    public String getTenNhaCungCap() {
        return tenNhaCungCap;
    }

    public void setTenNhaCungCap(String tenNhaCungCap) {
        this.tenNhaCungCap = tenNhaCungCap;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getDiaChiNha() {
        return diaChiNha;
    }

    public void setDiaChiNha(String diaChiNha) {
        this.diaChiNha = diaChiNha;
    }

    @Override
    public String toString() {
        return tenNhaCungCap;
    }
}
