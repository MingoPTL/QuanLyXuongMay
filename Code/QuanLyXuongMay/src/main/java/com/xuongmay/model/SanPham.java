package com.xuongmay.model;

public class SanPham {
    private String maSanPham;
    private String tenSanPham;
    private double giaThucTe;
    private String ghiChu;
    private TrangThaiSanPham trangThaiSanPham;
    private LoaiSanPham loaiSanPham;
    private String hinhAnh;

    /**
     * Số bộ trong 1 ri (hằng số nghiệp vụ).
     * 1 ri = SO_BO_MOI_RI bộ (bốc ngẫu nhiên từ các màu).
     * Ví dụ 4 màu: 1 ri = 4 bộ, 1 bộ mỗi màu.
     */
    public static final int SO_BO_MOI_RI = 4;

    // === DỰ KIẾN ===
    private int soMau;           // Số màu sản phẩm
    private int tongSoBoDuKien;  // Tổng số bộ dự kiến (= tổng lượt trải × số màu)
    // tongSoRiDuKien: tính tự động - xem getTongSoRiDuKien()
    // soBoLeDuKien:   tính tự động - xem getSoBoLeDuKien()

    // === THỰC TẾ (chỉ nhập khi DaHoanThanh) ===
    private int tongSoBoThucTe;
    private int tongSoRiThucTe;
    private int soBoLeThucTe;
    private int soRiLeThucTe;

    // === CONSTRUCTORS ===
    public SanPham() {}

    public SanPham(String maSanPham, String tenSanPham, double giaThucTe,
                   int soMau, int tongSoBoDuKien,
                   String ghiChu, TrangThaiSanPham trangThaiSanPham, LoaiSanPham loaiSanPham) {
        this.maSanPham = maSanPham;
        this.tenSanPham = tenSanPham;
        this.giaThucTe = giaThucTe;
        this.soMau = soMau;
        this.tongSoBoDuKien = tongSoBoDuKien;
        this.ghiChu = ghiChu;
        this.trangThaiSanPham = trangThaiSanPham;
        this.loaiSanPham = loaiSanPham;
    }

    // === COMPUTED GETTERS (dự kiến) ===
    /**
     * Tổng số ri dự kiến.
     * Công thức:
     *   1. Tính số bộ mỗi màu = tongSoBoDuKien / soMau  (lấy màu ít bộ nhất làm chuẩn - phần nguyên)
     *   2. Ri ghép được từ 1 màu = số bộ mỗi màu / SO_BO_MOI_RI
     *   3. Tổng ri = ri mỗi màu × soMau
     *
     * Ví dụ: 4 màu, 160 bộ → mỗi màu 40 bộ → 40/4=10 ri/màu → 10×4=40 ri
     *        5 màu, 200 bộ → mỗi màu 40 bộ → 40/4=10 ri/màu → 10×5=50 ri
     */
    public int getTongSoRiDuKien() {
        if (soMau <= 0) return 0;
        int boMoiMau = tongSoBoDuKien / soMau;     // phần nguyên = màu ít bộ nhất
        int riMoiMau = boMoiMau / SO_BO_MOI_RI;   // ri ghép được từ 1 màu
        return riMoiMau * soMau;                    // nhân lại số màu = tổng ri
    }

    /**
     * Số bộ lẻ dự kiến = bộ còn dư sau khi đóng ri.
     * soBoLeDuKien = tongSoBoDuKien - (tổng ri × SO_BO_MOI_RI)
     *
     * Ví dụ: mỗi màu 41 bộ, 5 màu → 205 bộ tổng
     *   ri = (41/4)×5 = 10×5 = 50 ri → bộ đóng ri = 50×4 = 200 → bộ lẻ = 5
     */
    public int getSoBoLeDuKien() {
        return tongSoBoDuKien - getTongSoRiDuKien() * SO_BO_MOI_RI;
    }

    // === GETTERS & SETTERS ===
    public String getMaSanPham() { return maSanPham; }
    public void setMaSanPham(String maSanPham) { this.maSanPham = maSanPham; }

    public String getTenSanPham() { return tenSanPham; }
    public void setTenSanPham(String tenSanPham) { this.tenSanPham = tenSanPham; }

    public double getGiaThucTe() { return giaThucTe; }
    public void setGiaThucTe(double giaThucTe) { this.giaThucTe = giaThucTe; }

    public String getGhiChu() { return ghiChu; }
    public void setGhiChu(String ghiChu) { this.ghiChu = ghiChu; }

    public TrangThaiSanPham getTrangThaiSanPham() { return trangThaiSanPham; }
    public void setTrangThaiSanPham(TrangThaiSanPham trangThaiSanPham) { this.trangThaiSanPham = trangThaiSanPham; }

    public LoaiSanPham getLoaiSanPham() { return loaiSanPham; }
    public void setLoaiSanPham(LoaiSanPham loaiSanPham) { this.loaiSanPham = loaiSanPham; }

    public String getHinhAnh() { return hinhAnh; }
    public void setHinhAnh(String hinhAnh) { this.hinhAnh = hinhAnh; }

    public int getSoMau() { return soMau; }
    public void setSoMau(int soMau) { this.soMau = soMau; }

    public int getTongSoBoDuKien() { return tongSoBoDuKien; }
    public void setTongSoBoDuKien(int tongSoBoDuKien) { this.tongSoBoDuKien = tongSoBoDuKien; }

    public int getTongSoBoThucTe() { return tongSoBoThucTe; }
    public void setTongSoBoThucTe(int tongSoBoThucTe) { this.tongSoBoThucTe = tongSoBoThucTe; }

    public int getTongSoRiThucTe() { return tongSoRiThucTe; }
    public void setTongSoRiThucTe(int tongSoRiThucTe) { this.tongSoRiThucTe = tongSoRiThucTe; }

    public int getSoBoLeThucTe() { return soBoLeThucTe; }
    public void setSoBoLeThucTe(int soBoLeThucTe) { this.soBoLeThucTe = soBoLeThucTe; }

    public int getSoRiLeThucTe() { return soRiLeThucTe; }
    public void setSoRiLeThucTe(int soRiLeThucTe) { this.soRiLeThucTe = soRiLeThucTe; }

    // Legacy compatibility getters
    /** @deprecated dùng getTongSoBoDuKien() */
    public int getTongSoBo() { return tongSoBoDuKien; }
    /** @deprecated dùng getTongSoRiDuKien() */
    public int getTongSoRi() { return getTongSoRiDuKien(); }
    /** @deprecated dùng getSoBoLeDuKien() */
    public int getSoBoLe() { return getSoBoLeDuKien(); }
    /** @deprecated dùng getSoRiLeThucTe() */
    public int getSoRiLe() { return soRiLeThucTe; }

    @Override
    public String toString() {
        return tenSanPham + " - " + String.format("%,.0f đ", giaThucTe);
    }
}
