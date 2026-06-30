package com.xuongmay.model;

public class NhanVien {
    private String maNhanVien;
    private String tenNhanVien;
    private String dienThoai;
    private ChuyenMon chuyenMon;
    private String ghiChu;

    public NhanVien() {}

    public NhanVien(String maNhanVien, String tenNhanVien, String dienThoai, ChuyenMon chuyenMon, String ghiChu) {
        this.maNhanVien = maNhanVien;
        this.tenNhanVien = tenNhanVien;
        this.dienThoai = dienThoai;
        this.chuyenMon = chuyenMon;
        this.ghiChu = ghiChu;
    }

    public String getMaNhanVien() {
        return maNhanVien;
    }

    public void setMaNhanVien(String maNhanVien) {
        this.maNhanVien = maNhanVien;
    }

    public String getTenNhanVien() {
        return tenNhanVien;
    }

    public void setTenNhanVien(String tenNhanVien) {
        this.tenNhanVien = tenNhanVien;
    }

    public String getDienThoai() {
        return dienThoai;
    }

    public void setDienThoai(String dienThoai) {
        this.dienThoai = dienThoai;
    }

    public ChuyenMon getChuyenMon() {
        return chuyenMon;
    }

    public void setChuyenMon(ChuyenMon chuyenMon) {
        this.chuyenMon = chuyenMon;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return tenNhanVien + " (" + chuyenMon + ")";
    }
}
