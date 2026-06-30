package com.xuongmay.model;

import java.time.LocalDate;

public class HoaDon {
    private String maHoaDon;
    private DonHang donHang;
    private LocalDate ngayLap;
    private double tongTienHoaDon;
    private PhuongThucThanhToan phuongThucThanhToan;
    private TrangThaiHoaDon trangThaiHoaDon;

    public HoaDon() {}

    public HoaDon(String maHoaDon, DonHang donHang, LocalDate ngayLap, double tongTienHoaDon, PhuongThucThanhToan phuongThucThanhToan, TrangThaiHoaDon trangThaiHoaDon) {
        this.maHoaDon = maHoaDon;
        this.donHang = donHang;
        this.ngayLap = ngayLap;
        this.tongTienHoaDon = tongTienHoaDon;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.trangThaiHoaDon = trangThaiHoaDon;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public DonHang getDonHang() {
        return donHang;
    }

    public void setDonHang(DonHang donHang) {
        this.donHang = donHang;
    }

    public LocalDate getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDate ngayLap) {
        this.ngayLap = ngayLap;
    }

    public double getTongTienHoaDon() {
        return tongTienHoaDon;
    }

    public void setTongTienHoaDon(double tongTienHoaDon) {
        this.tongTienHoaDon = tongTienHoaDon;
    }

    public PhuongThucThanhToan getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(PhuongThucThanhToan phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public TrangThaiHoaDon getTrangThaiHoaDon() {
        return trangThaiHoaDon;
    }

    public void setTrangThaiHoaDon(TrangThaiHoaDon trangThaiHoaDon) {
        this.trangThaiHoaDon = trangThaiHoaDon;
    }
}
