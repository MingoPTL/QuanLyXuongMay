package com.xuongmay.model;

public class SanPham {
    private String maSanPham;
    private String tenSanPham;
    private double giaThucTe;
    private int tongSoBo;
    private int tongSoRi;
    private int soBoLe;
    private int soRiLe;
    private String ghiChu;
    private TrangThaiSanPham trangThaiSanPham;
    private LoaiSanPham loaiSanPham;

    public SanPham() {}

    public SanPham(String maSanPham, String tenSanPham, double giaThucTe, int tongSoBo, int tongSoRi, int soBoLe, int soRiLe, String ghiChu, TrangThaiSanPham trangThaiSanPham, LoaiSanPham loaiSanPham) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.giaThucTe = giaThucTe;
        this.tongSoBo = tongSoBo;
        this.tongSoRi = tongSoRi;
        this.soBoLe = soBoLe;
        this.soRiLe = soRiLe;
        this.ghiChu = ghiChu;
        this.trangThaiSanPham = trangThaiSanPham;
        this.loaiSanPham = loaiSanPham;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public void setMaSanPham(String maSanPham) {
        this.maSanPham = maSanPham;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public double getGiaThucTe() {
        return giaThucTe;
    }

    public void setGiaThucTe(double giaThucTe) {
        this.giaThucTe = giaThucTe;
    }

    public int getTongSoBo() {
        return tongSoBo;
    }

    public void setTongSoBo(int tongSoBo) {
        this.tongSoBo = tongSoBo;
    }

    public int getTongSoRi() {
        return tongSoRi;
    }

    public void setTongSoRi(int tongSoRi) {
        this.tongSoRi = tongSoRi;
    }

    public int getSoBoLe() {
        return soBoLe;
    }

    public void setSoBoLe(int soBoLe) {
        this.soBoLe = soBoLe;
    }

    public int getSoRiLe() {
        return soRiLe;
    }

    public void setSoRiLe(int soRiLe) {
        this.soRiLe = soRiLe;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public TrangThaiSanPham getTrangThaiSanPham() {
        return trangThaiSanPham;
    }

    public void setTrangThaiSanPham(TrangThaiSanPham trangThaiSanPham) {
        this.trangThaiSanPham = trangThaiSanPham;
    }

    public LoaiSanPham getLoaiSanPham() {
        return loaiSanPham;
    }

    public void setLoaiSanPham(LoaiSanPham loaiSanPham) {
        this.loaiSanPham = loaiSanPham;
    }

    @Override
    public String toString() {
        return tenSanPham + " - " + String.format("%,.0f đ", giaThucTe);
    }
}
