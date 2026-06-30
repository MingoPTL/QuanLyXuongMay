package com.xuongmay.model;

import java.time.LocalDate;

public class DonHang {
    private String maDonHang;
    private KhachHang khachHang;
    private LocalDate ngayDat;
    private double tongTien;
    private TrangThaiDonHang trangThaiDonHang;
    private String ghiChu;

    public DonHang() {}

    public DonHang(String maDonHang, KhachHang khachHang, LocalDate ngayDat, double tongTien, TrangThaiDonHang trangThaiDonHang, String ghiChu) {
        this.maDonHang = maDonHang;
        this.khachHang = khachHang;
        this.ngayDat = ngayDat;
        this.tongTien = tongTien;
        this.trangThaiDonHang = trangThaiDonHang;
        this.ghiChu = ghiChu;
    }

    public String getMaDonHang() {
        return maDonHang;
    }

    public void setMaDonHang(String maDonHang) {
        this.maDonHang = maDonHang;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public LocalDate getNgayDat() {
        return ngayDat;
    }

    public void setNgayDat(LocalDate ngayDat) {
        this.ngayDat = ngayDat;
    }

    public double getTongTien() {
        return tongTien;
    }

    public void setTongTien(double tongTien) {
        this.tongTien = tongTien;
    }

    public TrangThaiDonHang getTrangThaiDonHang() {
        return trangThaiDonHang;
    }

    public void setTrangThaiDonHang(TrangThaiDonHang trangThaiDonHang) {
        this.trangThaiDonHang = trangThaiDonHang;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return maDonHang + " - " + (khachHang != null ? khachHang.getTenKhachHang() : "N/A");
    }
}
