package com.xuongmay.model;

public class ChiTietDonHang {
    private DonHang donHang;
    private SanPham sanPham;
    private int soLuongRi;
    private double donGiaRi;
    private double thanhTien;

    public ChiTietDonHang() {}

    public ChiTietDonHang(DonHang donHang, SanPham sanPham, int soLuongRi, double donGiaRi, double thanhTien) {
        this.donHang = donHang;
        this.sanPham = sanPham;
        this.soLuongRi = soLuongRi;
        this.donGiaRi = donGiaRi;
        this.thanhTien = thanhTien;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }

    public SanPham getSanPham() {
        return sanPham;
    }

    public void setSanPham(SanPham sanPham) {
        this.sanPham = sanPham;
    }

    public int getSoLuongRi() {
        return soLuongRi;
    }

    public void setSoLuongRi(int soLuongRi) {
        this.soLuongRi = soLuongRi;
    }

    public double getDonGiaRi() {
        return donGiaRi;
    }

    public void setDonGiaRi(double donGiaRi) {
        this.donGiaRi = donGiaRi;
    }

    public double getThanhTien() {
        return thanhTien;
    }

    public void setThanhTien(double thanhTien) {
        this.thanhTien = thanhTien;
    }
}
