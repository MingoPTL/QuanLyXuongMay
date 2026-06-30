package com.xuongmay.model;

public class LoaiSanPham {
    private String maLoai;
    private String tenLoai;
    private String moTa;
    private double giaGoc;
    private String ghiChu;

    public LoaiSanPham() {}

    public LoaiSanPham(String maLoai, String tenLoai, String moTa, double giaGoc, String ghiChu) {
        this.maLoai = maLoai;
        this.tenLoai = tenLoai;
        this.moTa = moTa;
        this.giaGoc = giaGoc;
        this.ghiChu = ghiChu;
    }

    public String getMaLoai() {
        return maLoai;
    }

    public void setMaLoai(String maLoai) {
        this.maLoai = maLoai;
    }

    public String getTenLoai() {
        return tenLoai;
    }

    public void setTenLoai(String tenLoai) {
        this.tenLoai = tenLoai;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public double getGiaGoc() {
        return giaGoc;
    }

    public void setGiaGoc(double giaGoc) {
        this.giaGoc = giaGoc;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return tenLoai;
    }
}
