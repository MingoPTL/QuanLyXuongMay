package com.xuongmay.model;

import java.time.LocalDate;

public class LoVai {
    private String maLo;
    private String tenLo;
    private NhaCungCap nhaCungCap;
    private LocalDate ngayNhap;
    private int soLuong;
    private String loaiVai;
    private double giaNhap;
    private String ghiChu;
    private TrangThaiLoVai trangThaiLoVai;
    private String hinhAnh; // image file path

    public LoVai() {}

    public LoVai(String maLo, String tenLo, NhaCungCap nhaCungCap, LocalDate ngayNhap, int soLuong, String loaiVai, double giaNhap, String ghiChu, TrangThaiLoVai trangThaiLoVai) {
        this.maLo = maLo;
        this.tenLo = tenLo;
        this.nhaCungCap = nhaCungCap;
        this.ngayNhap = ngayNhap;
        this.soLuong = soLuong;
        this.loaiVai = loaiVai;
        this.giaNhap = giaNhap;
        this.ghiChu = ghiChu;
        this.trangThaiLoVai = trangThaiLoVai;
    }

    public String getMaLo() {
        return maLo;
    }

    public void setMaLo(String maLo) {
        this.maLo = maLo;
    }

    public String getTenLo() {
        return tenLo;
    }

    public void setTenLo(String tenLo) {
        this.tenLo = tenLo;
    }

    public NhaCungCap getNhaCungCap() {
        return nhaCungCap;
    }

    public void setNhaCungCap(NhaCungCap nhaCungCap) {
        this.nhaCungCap = nhaCungCap;
    }

    public LocalDate getNgayNhap() {
        return ngayNhap;
    }

    public void setNgayNhap(LocalDate ngayNhap) {
        this.ngayNhap = ngayNhap;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getLoaiVai() {
        return loaiVai;
    }

    public void setLoaiVai(String loaiVai) {
        this.loaiVai = loaiVai;
    }

    public double getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(double giaNhap) {
        this.giaNhap = giaNhap;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public TrangThaiLoVai getTrangThaiLoVai() {
        return trangThaiLoVai;
    }

    public void setTrangThaiLoVai(TrangThaiLoVai trangThaiLoVai) {
        this.trangThaiLoVai = trangThaiLoVai;
    }

    public String getHinhAnh() {
        return hinhAnh;
    }

    public void setHinhAnh(String hinhAnh) {
        this.hinhAnh = hinhAnh;
    }

    @Override
    public String toString() {
        return tenLo;
    }
}
