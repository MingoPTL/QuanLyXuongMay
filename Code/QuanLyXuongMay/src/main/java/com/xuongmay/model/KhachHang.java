package com.xuongmay.model;

public class KhachHang {
    private String maKhachHang;
    private String tenKhachHang;
    private String sdt;
    private String diaChiNha;
    private String maSoThue;
    private String ghiChu;

    public KhachHang() {}

    public KhachHang(String maKhachHang, String tenKhachHang, String sdt, String diaChiNha, String maSoThue, String ghiChu) {
        this.maKhachHang = maKhachHang;
        this.tenKhachHang = tenKhachHang;
        this.sdt = sdt;
        this.diaChiNha = diaChiNha;
        this.maSoThue = maSoThue;
        this.ghiChu = ghiChu;
    }

    public String getMaKhachHang() {
        return maKhachHang;
    }

    public void setMaKhachHang(String maKhachHang) {
        this.maKhachHang = maKhachHang;
    }

    public String getTenKhachHang() {
        return tenKhachHang;
    }

    public void setTenKhachHang(String tenKhachHang) {
        this.tenKhachHang = tenKhachHang;
    }

    public String getSdt() {
        return sdt;
    }

    public void setSdt(String sdt) {
        this.sdt = sdt;
    }

    public String getDiaChiNha() {
        return diaChiNha;
    }

    public void setDiaChiNha(String diaChiNha) {
        this.diaChiNha = diaChiNha;
    }

    public String getMaSoThue() {
        return maSoThue;
    }

    public void setMaSoThue(String maSoThue) {
        this.maSoThue = maSoThue;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    @Override
    public String toString() {
        return tenKhachHang + " - " + sdt;
    }
}
