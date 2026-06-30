package com.xuongmay.model;

import java.time.LocalDate;

public class TaiKhoan {
    private String maTaiKhoan;
    private String tenDangNhap;
    private String matKhau;
    private ChucVu chucVu;
    private TrangThaiTaiKhoan trangThaiTaiKhoan;
    private LocalDate ngayTao;

    public TaiKhoan() {}

    public TaiKhoan(String maTaiKhoan, String tenDangNhap, String matKhau, ChucVu chucVu, TrangThaiTaiKhoan trangThaiTaiKhoan, LocalDate ngayTao) {
        this.maTaiKhoan = maTaiKhoan;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.chucVu = chucVu;
        this.trangThaiTaiKhoan = trangThaiTaiKhoan;
        this.ngayTao = ngayTao;
    }

    public String getMaTaiKhoan() {
        return maTaiKhoan;
    }

    public void setMaTaiKhoan(String maTaiKhoan) {
        this.maTaiKhoan = maTaiKhoan;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public ChucVu getChucVu() {
        return chucVu;
    }

    public void setChucVu(ChucVu chucVu) {
        this.chucVu = chucVu;
    }

    public TrangThaiTaiKhoan getTrangThaiTaiKhoan() {
        return trangThaiTaiKhoan;
    }

    public void setTrangThaiTaiKhoan(TrangThaiTaiKhoan trangThaiTaiKhoan) {
        this.trangThaiTaiKhoan = trangThaiTaiKhoan;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
        this.ngayTao = ngayTao;
    }
}
