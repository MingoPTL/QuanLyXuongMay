package com.xuongmay.model;

import java.time.LocalDate;

public class PhanCongSanPham {
    private String maPhanCong;
    private SanPham sanPham;
    private NhanVien nhanVien;
    private LocalDate ngayPhanCong;

    public PhanCongSanPham() {}

    public PhanCongSanPham(String maPhanCong, SanPham sanPham, NhanVien nhanVien, LocalDate ngayPhanCong) {
        this.maPhanCong = maPhanCong;
        this.sanPham = sanPham;
        this.nhanVien = nhanVien;
        this.ngayPhanCong = ngayPhanCong;
    }

    public String getMaPhanCong() {
        return maPhanCong;
    }

    public void setMaPhanCong(String maPhanCong) {
        this.maPhanCong = maPhanCong;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public LocalDate getNgayPhanCong() {
        return ngayPhanCong;
    }

    public void setNgayPhanCong(LocalDate ngayPhanCong) {
        this.ngayPhanCong = ngayPhanCong;
    }
}
