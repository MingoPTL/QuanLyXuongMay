-- ============================================================
--  QUẢN LÝ XƯỞNG MAY — Database Creation Script
--  Engine  : SQL Server (T-SQL) — dùng với SSMS
--  Charset : NVARCHAR (hỗ trợ Unicode / tiếng Việt)
--  Mapped from Java models / DAOs
-- ============================================================

-- Tạo database (bỏ qua nếu đã tạo tay trong SSMS)
IF NOT EXISTS (
    SELECT name FROM sys.databases WHERE name = N'QuanLyXuongMay'
)
BEGIN
    CREATE DATABASE QuanLyXuongMay
        COLLATE Vietnamese_CI_AS;
END
GO

USE QuanLyXuongMay;
GO

-- ============================================================
-- 1. NHÀ CUNG CẤP  (NhaCungCap)
-- ============================================================
IF OBJECT_ID('NhaCungCap', 'U') IS NULL
CREATE TABLE NhaCungCap (
    ma_nha_cung_cap  NVARCHAR(20)  NOT NULL,
    ten_nha_cung_cap NVARCHAR(100) NOT NULL,
    so_dien_thoai    NVARCHAR(20)  NULL,
    dia_chi_nha      NVARCHAR(200) NULL,
    CONSTRAINT PK_NhaCungCap PRIMARY KEY (ma_nha_cung_cap)
);
GO

-- ============================================================
-- 2. LOẠI SẢN PHẨM  (LoaiSanPham)
-- ============================================================
IF OBJECT_ID('LoaiSanPham', 'U') IS NULL
CREATE TABLE LoaiSanPham (
    ma_loai  NVARCHAR(20)   NOT NULL,
    ten_loai NVARCHAR(100)  NOT NULL,
    mo_ta    NVARCHAR(MAX)  NULL,
    gia_goc  DECIMAL(15, 2) NOT NULL DEFAULT 0,
    ghi_chu  NVARCHAR(MAX)  NULL,
    CONSTRAINT PK_LoaiSanPham PRIMARY KEY (ma_loai)
);
GO

-- ============================================================
-- 3. LÔ VẢI  (LoVai)
--    TrangThaiLoVai: ChuaSuDung | DangSuDung | RaSanPham
-- ============================================================
IF OBJECT_ID('LoVai', 'U') IS NULL
CREATE TABLE LoVai (
    ma_lo           NVARCHAR(20)   NOT NULL,
    ten_lo          NVARCHAR(100)  NOT NULL,
    ma_nha_cung_cap NVARCHAR(20)   NULL,
    ngay_nhap       DATE           NOT NULL,
    so_luong        INT            NOT NULL DEFAULT 0,
    loai_vai        NVARCHAR(100)  NULL,
    gia_nhap        DECIMAL(15, 2) NOT NULL DEFAULT 0,
    ghi_chu         NVARCHAR(MAX)  NULL,
    trang_thai      NVARCHAR(20)   NOT NULL DEFAULT 'ChuaSuDung',
    hinh_anh        NVARCHAR(500)  NULL,
    CONSTRAINT PK_LoVai PRIMARY KEY (ma_lo),
    CONSTRAINT CHK_LoVai_TrangThai CHECK (
        trang_thai IN ('ChuaSuDung', 'DangSuDung', 'RaSanPham')
    ),
    CONSTRAINT FK_LoVai_NhaCungCap FOREIGN KEY (ma_nha_cung_cap)
        REFERENCES NhaCungCap(ma_nha_cung_cap)
        ON UPDATE CASCADE ON DELETE SET NULL
);
GO

-- ============================================================
-- 4. CÂY VẢI  (CayVai)
--    Không có PK riêng trong Java → dùng IDENTITY
-- ============================================================
IF OBJECT_ID('CayVai', 'U') IS NULL
CREATE TABLE CayVai (
    id_cay_vai    BIGINT        NOT NULL IDENTITY(1,1),
    ten_cay_vai   NVARCHAR(100) NOT NULL,
    mau_sac       NVARCHAR(50)  NULL,
    ma_lo         NVARCHAR(20)  NULL,
    chieu_dai     FLOAT         NOT NULL DEFAULT 0,
    vi_tri        NVARCHAR(100) NULL,
    ghi_chu       NVARCHAR(MAX) NULL,
    luot_trai_vai INT           NOT NULL DEFAULT 0,
    CONSTRAINT PK_CayVai PRIMARY KEY (id_cay_vai),
    CONSTRAINT FK_CayVai_LoVai FOREIGN KEY (ma_lo)
        REFERENCES LoVai(ma_lo)
        ON UPDATE CASCADE ON DELETE SET NULL
);
GO

-- ============================================================
-- 5. SẢN PHẨM  (SanPham)
--    TrangThaiSanPham: DangCat | DangMay | DangUi | DaHoanThanh
--    tong_so_ri_du_kien, so_bo_le_du_kien: ứng dụng tự cập nhật
-- ============================================================
IF OBJECT_ID('SanPham', 'U') IS NULL
CREATE TABLE SanPham (
    ma_san_pham          NVARCHAR(20)   NOT NULL,
    ten_san_pham         NVARCHAR(200)  NOT NULL,
    gia_thuc_te          DECIMAL(15, 2) NOT NULL DEFAULT 0,
    ghi_chu              NVARCHAR(MAX)  NULL,
    trang_thai           NVARCHAR(20)   NOT NULL DEFAULT 'DangCat',
    ma_loai              NVARCHAR(20)   NULL,
    hinh_anh             NVARCHAR(500)  NULL,
    -- Dự kiến
    so_mau               INT            NOT NULL DEFAULT 1,
    tong_so_bo_du_kien   INT            NOT NULL DEFAULT 0,
    tong_so_ri_du_kien   INT            NOT NULL DEFAULT 0, -- computed bởi app: (tong_so_bo/so_mau/4)*so_mau
    so_bo_le_du_kien     INT            NOT NULL DEFAULT 0, -- computed bởi app: tong_so_bo - ri*4
    -- Thực tế (nhập khi DaHoanThanh)
    tong_so_bo_thuc_te   INT            NOT NULL DEFAULT 0,
    tong_so_ri_thuc_te   INT            NOT NULL DEFAULT 0,
    so_bo_le_thuc_te     INT            NOT NULL DEFAULT 0,
    so_ri_le_thuc_te     INT            NOT NULL DEFAULT 0,
    CONSTRAINT PK_SanPham PRIMARY KEY (ma_san_pham),
    CONSTRAINT CHK_SanPham_TrangThai CHECK (
        trang_thai IN ('DangCat', 'DangMay', 'DangUi', 'DaHoanThanh')
    ),
    CONSTRAINT FK_SanPham_LoaiSanPham FOREIGN KEY (ma_loai)
        REFERENCES LoaiSanPham(ma_loai)
        ON UPDATE CASCADE ON DELETE SET NULL
);
GO

-- ============================================================
-- 6. NHÂN VIÊN  (NhanVien)
--    ChuyenMon: ThoMay | ThoUi
-- ============================================================
IF OBJECT_ID('NhanVien', 'U') IS NULL
CREATE TABLE NhanVien (
    ma_nhan_vien  NVARCHAR(20)  NOT NULL,
    ten_nhan_vien NVARCHAR(100) NOT NULL,
    dien_thoai    NVARCHAR(20)  NULL,
    chuyen_mon    NVARCHAR(10)  NOT NULL,
    ghi_chu       NVARCHAR(MAX) NULL,
    CONSTRAINT PK_NhanVien PRIMARY KEY (ma_nhan_vien),
    CONSTRAINT CHK_NhanVien_ChuyenMon CHECK (
        chuyen_mon IN ('ThoMay', 'ThoUi')
    )
);
GO

-- ============================================================
-- 7. PHÂN CÔNG SẢN PHẨM  (PhanCongSanPham)
--    so_luong là NVARCHAR vì có thể là "Tất cả" hoặc số nguyên
-- ============================================================
IF OBJECT_ID('PhanCongSanPham', 'U') IS NULL
CREATE TABLE PhanCongSanPham (
    ma_phan_cong   NVARCHAR(20) NOT NULL,
    ma_san_pham    NVARCHAR(20) NOT NULL,
    ma_nhan_vien   NVARCHAR(20) NOT NULL,
    ngay_phan_cong DATE         NOT NULL,
    so_luong       NVARCHAR(20) NOT NULL DEFAULT N'Tất cả',
    -- "Tất cả" hoặc số nguyên dạng chuỗi (business rule hiện tại)
    CONSTRAINT PK_PhanCongSanPham PRIMARY KEY (ma_phan_cong),
    CONSTRAINT FK_PhanCong_SanPham FOREIGN KEY (ma_san_pham)
        REFERENCES SanPham(ma_san_pham)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_PhanCong_NhanVien FOREIGN KEY (ma_nhan_vien)
        REFERENCES NhanVien(ma_nhan_vien)
        ON UPDATE CASCADE ON DELETE CASCADE
);
GO

-- ============================================================
-- 8. KHÁCH HÀNG  (KhachHang)
-- ============================================================
IF OBJECT_ID('KhachHang', 'U') IS NULL
CREATE TABLE KhachHang (
    ma_khach_hang  NVARCHAR(20)  NOT NULL,
    ten_khach_hang NVARCHAR(100) NOT NULL,
    sdt            NVARCHAR(20)  NULL,
    dia_chi_nha    NVARCHAR(200) NULL,
    ma_so_thue     NVARCHAR(30)  NULL,
    ghi_chu        NVARCHAR(MAX) NULL,
    CONSTRAINT PK_KhachHang PRIMARY KEY (ma_khach_hang)
);
GO

-- ============================================================
-- 9. ĐƠN HÀNG  (DonHang)
--    TrangThaiDonHang: ChuaGiao | DaGiao
-- ============================================================
IF OBJECT_ID('DonHang', 'U') IS NULL
CREATE TABLE DonHang (
    ma_don_hang   NVARCHAR(20)   NOT NULL,
    ma_khach_hang NVARCHAR(20)   NULL,
    ngay_dat      DATE           NOT NULL,
    tong_tien     DECIMAL(15, 2) NOT NULL DEFAULT 0,
    trang_thai    NVARCHAR(15)   NOT NULL DEFAULT 'ChuaGiao',
    ghi_chu       NVARCHAR(MAX)  NULL,
    CONSTRAINT PK_DonHang PRIMARY KEY (ma_don_hang),
    CONSTRAINT CHK_DonHang_TrangThai CHECK (
        trang_thai IN ('ChuaGiao', 'DaGiao')
    ),
    CONSTRAINT FK_DonHang_KhachHang FOREIGN KEY (ma_khach_hang)
        REFERENCES KhachHang(ma_khach_hang)
        ON UPDATE CASCADE ON DELETE SET NULL
);
GO

-- ============================================================
-- 10. CHI TIẾT ĐƠN HÀNG  (ChiTietDonHang)
--     PK composite: (ma_don_hang, ma_san_pham)
-- ============================================================
IF OBJECT_ID('ChiTietDonHang', 'U') IS NULL
CREATE TABLE ChiTietDonHang (
    ma_don_hang NVARCHAR(20)   NOT NULL,
    ma_san_pham NVARCHAR(20)   NOT NULL,
    so_luong_ri INT            NOT NULL DEFAULT 1,
    don_gia_ri  DECIMAL(15, 2) NOT NULL DEFAULT 0,
    thanh_tien  DECIMAL(15, 2) NOT NULL DEFAULT 0,
    CONSTRAINT PK_ChiTietDonHang PRIMARY KEY (ma_don_hang, ma_san_pham),
    CONSTRAINT FK_CTDH_DonHang FOREIGN KEY (ma_don_hang)
        REFERENCES DonHang(ma_don_hang)
        ON UPDATE CASCADE ON DELETE CASCADE,
    CONSTRAINT FK_CTDH_SanPham FOREIGN KEY (ma_san_pham)
        REFERENCES SanPham(ma_san_pham)
        ON UPDATE NO ACTION ON DELETE NO ACTION
);
GO

-- ============================================================
-- 11. HÓA ĐƠN  (HoaDon)
--     PhuongThucThanhToan: TienMat | ChuyenKhoan
--     TrangThaiHoaDon   : ChuaThanhToan | DaThanhToan
--     Quan hệ 1:1 với DonHang
-- ============================================================
IF OBJECT_ID('HoaDon', 'U') IS NULL
CREATE TABLE HoaDon (
    ma_hoa_don             NVARCHAR(20)   NOT NULL,
    ma_don_hang            NVARCHAR(20)   NULL,
    ngay_lap               DATE           NOT NULL,
    tong_tien_hoa_don      DECIMAL(15, 2) NOT NULL DEFAULT 0,
    phuong_thuc_thanh_toan NVARCHAR(15)   NOT NULL DEFAULT 'TienMat',
    trang_thai             NVARCHAR(20)   NOT NULL DEFAULT 'ChuaThanhToan',
    CONSTRAINT PK_HoaDon PRIMARY KEY (ma_hoa_don),
    CONSTRAINT UQ_HoaDon_DonHang UNIQUE (ma_don_hang),
    CONSTRAINT CHK_HoaDon_PhuongThuc CHECK (
        phuong_thuc_thanh_toan IN ('TienMat', 'ChuyenKhoan')
    ),
    CONSTRAINT CHK_HoaDon_TrangThai CHECK (
        trang_thai IN ('ChuaThanhToan', 'DaThanhToan')
    ),
    CONSTRAINT FK_HoaDon_DonHang FOREIGN KEY (ma_don_hang)
        REFERENCES DonHang(ma_don_hang)
        ON UPDATE NO ACTION ON DELETE SET NULL
);
GO

-- ============================================================
-- 12. TÀI KHOẢN  (TaiKhoan)
--     ChucVu          : ADMIN | QUANLY
--     TrangThaiTaiKhoan: HoatDong | BiKhoa
-- ============================================================
IF OBJECT_ID('TaiKhoan', 'U') IS NULL
CREATE TABLE TaiKhoan (
    ma_tai_khoan  NVARCHAR(20)  NOT NULL,
    ten_dang_nhap NVARCHAR(50)  NOT NULL,
    mat_khau      NVARCHAR(255) NOT NULL,
    chuc_vu       NVARCHAR(10)  NOT NULL DEFAULT 'QUANLY',
    trang_thai    NVARCHAR(10)  NOT NULL DEFAULT 'HoatDong',
    ngay_tao      DATE          NOT NULL,
    CONSTRAINT PK_TaiKhoan PRIMARY KEY (ma_tai_khoan),
    CONSTRAINT UQ_TaiKhoan_TenDangNhap UNIQUE (ten_dang_nhap),
    CONSTRAINT CHK_TaiKhoan_ChucVu CHECK (
        chuc_vu IN ('ADMIN', 'QUANLY')
    ),
    CONSTRAINT CHK_TaiKhoan_TrangThai CHECK (
        trang_thai IN ('HoatDong', 'BiKhoa')
    )
);
GO

-- ============================================================
-- INDEXES bổ sung (tối ưu truy vấn hay dùng)
-- ============================================================
CREATE INDEX IDX_HoaDon_TrangThai    ON HoaDon(trang_thai);
CREATE INDEX IDX_HoaDon_NgayLap      ON HoaDon(ngay_lap);
CREATE INDEX IDX_DonHang_NgayDat     ON DonHang(ngay_dat);
CREATE INDEX IDX_DonHang_TrangThai   ON DonHang(trang_thai);
CREATE INDEX IDX_SanPham_TrangThai   ON SanPham(trang_thai);
CREATE INDEX IDX_PhanCong_SanPham    ON PhanCongSanPham(ma_san_pham);
CREATE INDEX IDX_PhanCong_NhanVien   ON PhanCongSanPham(ma_nhan_vien);
CREATE INDEX IDX_CayVai_LoVai        ON CayVai(ma_lo);
GO

-- ============================================================
-- END OF SCRIPT
-- ============================================================
