-- SQL Script to set up QuanLyXuongMay Database
-- OS: Windows, DBMS: SQL Server

CREATE DATABASE QuanLyXuongMay;
GO

USE QuanLyXuongMay;
GO

-- 1. NhaCungCap Table
CREATE TABLE NhaCungCap (
    MaNhaCungCap VARCHAR(50) PRIMARY KEY,
    TenNhaCungCap NVARCHAR(100) NOT NULL,
    SoDienThoai VARCHAR(15) NOT NULL,
    DiaChi NVARCHAR(255) NULL
);
GO

-- 2. LoVai Table
CREATE TABLE LoVai (
    MaLo VARCHAR(50) PRIMARY KEY,
    TenLo NVARCHAR(100) NOT NULL,
    MaNhaCungCap VARCHAR(50) FOREIGN KEY REFERENCES NhaCungCap(MaNhaCungCap) ON DELETE SET NULL,
    NgayNhap DATE NOT NULL,
    SoLuong INT NOT NULL CHECK (SoLuong >= 0),
    LoaiVai NVARCHAR(100) NOT NULL,
    GiaNhap DECIMAL(18,2) NOT NULL CHECK (GiaNhap >= 0),
    GhiChu NVARCHAR(255) NULL,
    TrangThaiLoVai VARCHAR(30) NOT NULL CHECK (TrangThaiLoVai IN ('ChuaSuDung', 'DangSuDung', 'RaSanPham'))
);
GO

-- 3. CayVai Table
CREATE TABLE CayVai (
    TenCayVai VARCHAR(50) PRIMARY KEY,
    MauSac NVARCHAR(50) NOT NULL,
    MaLo VARCHAR(50) FOREIGN KEY REFERENCES LoVai(MaLo) ON DELETE CASCADE,
    ChieuDai DECIMAL(18,2) NOT NULL CHECK (ChieuDai >= 0),
    ViTri NVARCHAR(50) NULL,
    GhiChu NVARCHAR(255) NULL,
    LuotTraiVai INT NOT NULL DEFAULT 0 CHECK (LuotTraiVai >= 0)
);
GO

-- 4. KhachHang Table
CREATE TABLE KhachHang (
    MaKhachHang VARCHAR(50) PRIMARY KEY,
    TenKhachHang NVARCHAR(100) NOT NULL,
    SDT VARCHAR(15) NOT NULL,
    DiaChiNha NVARCHAR(255) NOT NULL,
    MaSoThue VARCHAR(30) NULL,
    GhiChu NVARCHAR(255) NULL
);
GO

-- 5. LoaiSanPham Table
CREATE TABLE LoaiSanPham (
    MaLoai VARCHAR(50) PRIMARY KEY,
    TenLoai NVARCHAR(100) NOT NULL,
    MoTa NVARCHAR(255) NULL,
    GiaGoc DECIMAL(18,2) NOT NULL CHECK (GiaGoc >= 0),
    GhiChu NVARCHAR(255) NULL
);
GO

-- 6. SanPham Table
CREATE TABLE SanPham (
    MaSanPham VARCHAR(50) PRIMARY KEY,
    TenSanPham NVARCHAR(100) NOT NULL,
    GiaThucTe DECIMAL(18,2) NOT NULL CHECK (GiaThucTe >= 0),
    TongSoBo INT NOT NULL DEFAULT 0 CHECK (TongSoBo >= 0),
    TongSoRi INT NOT NULL DEFAULT 0 CHECK (TongSoRi >= 0),
    SoBoLe INT NOT NULL DEFAULT 0 CHECK (SoBoLe >= 0),
    SoRiLe INT NOT NULL DEFAULT 0 CHECK (SoRiLe >= 0),
    GhiChu NVARCHAR(255) NULL,
    TrangThaiSanPham VARCHAR(30) NOT NULL CHECK (TrangThaiSanPham IN ('DangCat', 'DangMay', 'DangUi', 'DaHoanThanh')),
    MaLoai VARCHAR(50) FOREIGN KEY REFERENCES LoaiSanPham(MaLoai) ON DELETE SET NULL
);
GO

-- 7. NhanVien Table
CREATE TABLE NhanVien (
    MaNhanVien VARCHAR(50) PRIMARY KEY,
    TenNhanVien NVARCHAR(100) NOT NULL,
    DienThoai VARCHAR(15) NOT NULL,
    ChuyenMon VARCHAR(30) NOT NULL CHECK (ChuyenMon IN ('ThoMay', 'ThoUi')),
    GhiChu NVARCHAR(255) NULL
);
GO

-- 8. PhanCongSanPham Table
CREATE TABLE PhanCongSanPham (
    MaPhanCong VARCHAR(50) PRIMARY KEY,
    MaSanPham VARCHAR(50) FOREIGN KEY REFERENCES SanPham(MaSanPham) ON DELETE CASCADE,
    MaNhanVien VARCHAR(50) FOREIGN KEY REFERENCES NhanVien(MaNhanVien) ON DELETE CASCADE,
    NgayPhanCong DATE NOT NULL
);
GO

-- 9. DonHang Table
CREATE TABLE DonHang (
    MaDonHang VARCHAR(50) PRIMARY KEY,
    MaKhachHang VARCHAR(50) FOREIGN KEY REFERENCES KhachHang(MaKhachHang) ON DELETE SET NULL,
    NgayDat DATE NOT NULL,
    TongTien DECIMAL(18,2) NOT NULL DEFAULT 0 CHECK (TongTien >= 0),
    TrangThaiDonHang VARCHAR(30) NOT NULL CHECK (TrangThaiDonHang IN ('ChuaGiao', 'DaGiao')),
    GhiChu NVARCHAR(255) NULL
);
GO

-- 10. ChiTietDonHang Table
CREATE TABLE ChiTietDonHang (
    MaDonHang VARCHAR(50) FOREIGN KEY REFERENCES DonHang(MaDonHang) ON DELETE CASCADE,
    MaSanPham VARCHAR(50) FOREIGN KEY REFERENCES SanPham(MaSanPham) ON DELETE CASCADE,
    SoLuongRi INT NOT NULL CHECK (SoLuongRi > 0),
    DonGiaRi DECIMAL(18,2) NOT NULL CHECK (DonGiaRi >= 0),
    ThanhTien DECIMAL(18,2) NOT NULL CHECK (ThanhTien >= 0),
    PRIMARY KEY (MaDonHang, MaSanPham)
);
GO

-- 11. TaiKhoan Table
CREATE TABLE TaiKhoan (
    MaTaiKhoan VARCHAR(50) PRIMARY KEY,
    TenDangNhap VARCHAR(50) NOT NULL UNIQUE,
    MatKhau VARCHAR(50) NOT NULL,
    ChucVu VARCHAR(30) NOT NULL CHECK (ChucVu IN ('Admin', 'Quản lý')),
    TrangThaiTaiKhoan VARCHAR(30) NOT NULL CHECK (TrangThaiTaiKhoan IN ('HoatDong', 'NgungHoatDong')),
    NgayTao DATE NOT NULL
);
GO

-- 12. HoaDon Table
CREATE TABLE HoaDon (
    MaHoaDon VARCHAR(50) PRIMARY KEY,
    MaDonHang VARCHAR(50) FOREIGN KEY REFERENCES DonHang(MaDonHang) ON DELETE CASCADE,
    NgayLap DATE NOT NULL,
    TongTienHoaDon DECIMAL(18,2) NOT NULL CHECK (TongTienHoaDon >= 0),
    PhuongThucThanhToan VARCHAR(30) NOT NULL CHECK (PhuongThucThanhToan IN ('TienMat', 'ChuyenKhoan')),
    TrangThaiHoaDon VARCHAR(30) NOT NULL CHECK (TrangThaiHoaDon IN ('ChuaThanhToan', 'DaThanhToan'))
);
GO

-- ==========================================
-- INSERT SAMPLE DATA (MATCHING MOCK DAOs)
-- ==========================================

-- NhaCungCap
INSERT INTO NhaCungCap (MaNhaCungCap, TenNhaCungCap, SoDienThoai, DiaChi) VALUES 
('NCC001', N'Dệt may Thành Công', '0901234567', N'12 Lũy Bán Bích, Tân Phú, HCM'),
('NCC002', N'Vải sỉ Tân Bình', '0987654321', N'Lý Thường Kiệt, Tân Bình, HCM'),
('NCC003', N'Phong Phú Fabric', '0918273645', N'KCN Tân Tạo, Bình Tân, HCM');

-- LoVai
INSERT INTO LoVai (MaLo, TenLo, MaNhaCungCap, NgayNhap, SoLuong, LoaiVai, GiaNhap, GhiChu, TrangThaiLoVai) VALUES
('LV001', N'Lô Kaki Thun Hàn Quốc', 'NCC001', DATEADD(day, -10, GETDATE()), 200, 'Kaki', 50000, N'Vải nhập khẩu', 'DangSuDung'),
('LV002', N'Lô Thun Cotton 4 Chiều', 'NCC002', DATEADD(day, -5, GETDATE()), 350, 'Thun Cotton', 35000, N'Mềm mịn co giãn', 'ChuaSuDung'),
('LV003', N'Lô Linen Hoa Tiết', 'NCC003', DATEADD(day, -2, GETDATE()), 150, 'Linen', 60000, N'Vải linen mát mẻ', 'ChuaSuDung'),
('LV004', N'Lô Jean Thun Dày', 'NCC001', DATEADD(day, -12, GETDATE()), 400, 'Jean', 70000, N'Jean chất lượng cao', 'RaSanPham');

-- CayVai
INSERT INTO CayVai (TenCayVai, MauSac, MaLo, ChieuDai, ViTri, GhiChu, LuotTraiVai) VALUES
('CV001', N'Đen', 'LV001', 100.5, N'Kệ A1', N'Kaki trơn màu đen', 2),
('CV002', N'Xanh đen', 'LV001', 120.0, N'Kệ A1', N'Kaki trơn màu xanh đen', 1),
('CV003', N'Xám', 'LV001', 95.2, N'Kệ A2', N'Kaki trơn màu xám', 3),
('CV004', N'Trắng', 'LV002', 150.0, N'Kệ B1', N'Cotton 4 chiều màu trắng', 0),
('CV005', N'Đỏ đô', 'LV002', 140.5, N'Kệ B2', N'Cotton 4 chiều màu đỏ đô', 0),
('CV006', N'Vàng', 'LV002', 130.0, N'Kệ B2', N'Cotton 4 chiều màu vàng', 0),
('CV007', N'Xanh lá', 'LV002', 145.8, N'Kệ B3', N'Cotton 4 chiều màu xanh lá', 0),
('CV008', N'Be', 'LV003', 80.0, N'Kệ C1', N'Linen hoa nhí màu be', 0),
('CV009', N'Xanh dương', 'LV003', 85.5, N'Kệ C2', N'Linen kẻ sọc xanh dương', 0),
('CV010', N'Xanh Jean', 'LV004', 200.0, N'Kệ D1', N'Jean xanh co giãn', 5);

-- KhachHang
INSERT INTO KhachHang (MaKhachHang, TenKhachHang, SDT, DiaChiNha, MaSoThue, GhiChu) VALUES
('KH001', N'Nguyễn Văn An', '0909000111', N'12 Lũy Bán Bích, Tân Phú, HCM', '0314567890', N'Khách sỉ lâu năm'),
('KH002', N'Trần Thị Bình', '0909000222', N'456 CMT8, Quận 3, HCM', '0314567891', N'Thanh toán đúng hạn'),
('KH003', N'Lê Hoàng Long', '0909000333', N'789 Quang Trung, Gò Vấp, HCM', '0314567892', N'Khách hàng VIP');

-- LoaiSanPham
INSERT INTO LoaiSanPham (MaLoai, TenLoai, MoTa, GiaGoc, GhiChu) VALUES
('LSP001', N'Áo thun Polo', N'Áo thun Polo cổ bẻ', 35000, N'Vải Cotton 4 chiều'),
('LSP002', N'Quần Kaki Nam', N'Quần dài kaki dáng ôm', 55000, N'Vải Kaki Thun'),
('LSP003', N'Đầm Linen Nữ', N'Đầm chữ A họa tiết hoa nhí', 70000, N'Vải Linen');

-- SanPham
INSERT INTO SanPham (MaSanPham, TenSanPham, GiaThucTe, TongSoBo, TongSoRi, SoBoLe, SoRiLe, GhiChu, TrangThaiSanPham, MaLoai) VALUES
('SP001', N'Áo thun Polo Classic', 45000, 150, 30, 0, 0, N'Dáng suông', 'DaHoanThanh', 'LSP001'),
('SP002', N'Quần Kaki Công Sở', 75000, 90, 18, 5, 1, N'Dáng slimfit', 'DangMay', 'LSP002'),
('SP003', N'Đầm Linen Dạo Phố', 95000, 60, 12, 0, 0, N'Dáng chữ A', 'DaHoanThanh', 'LSP003');

-- NhanVien
INSERT INTO NhanVien (MaNhanVien, TenNhanVien, DienThoai, ChuyenMon, GhiChu) VALUES
('NV001', N'Nguyễn Thị Hoa', '0909111222', 'ThoMay', N'Kinh nghiệm 5 năm'),
('NV002', N'Trần Văn Hùng', '0909333444', 'ThoMay', N'May nhanh, tay nghề cao'),
('NV003', N'Lê Thị Mai', '0909555666', 'ThoMay', N'Chuyên may viền'),
('NV004', N'Phạm Văn Bình', '0909777888', 'ThoUi', N'Ủi phẳng, cẩn thận'),
('NV005', N'Nguyễn Thị Lan', '0909999000', 'ThoUi', N'Ủi nhanh, đúng tiến độ');

-- PhanCongSanPham
INSERT INTO PhanCongSanPham (MaPhanCong, MaSanPham, MaNhanVien, NgayPhanCong) VALUES
('PC001', 'SP001', 'NV001', DATEADD(day, -5, GETDATE())),
('PC002', 'SP002', 'NV002', DATEADD(day, -4, GETDATE())),
('PC003', 'SP001', 'NV004', DATEADD(day, -3, GETDATE()));

-- DonHang
INSERT INTO DonHang (MaDonHang, MaKhachHang, NgayDat, TongTien, TrangThaiDonHang, GhiChu) VALUES
('DH001', 'KH001', DATEADD(day, -3, GETDATE()), 900000.0, 'DaGiao', N'Giao hàng nhanh'),
('DH002', 'KH002', DATEADD(day, -1, GETDATE()), 1500000.0, 'ChuaGiao', N'Giao buổi chiều');

-- ChiTietDonHang
INSERT INTO ChiTietDonHang (MaDonHang, MaSanPham, SoLuongRi, DonGiaRi, ThanhTien) VALUES
('DH001', 'SP001', 20, 45000, 900000.0),
('DH002', 'SP002', 20, 75000, 1500000.0);

-- TaiKhoan
INSERT INTO TaiKhoan (MaTaiKhoan, TenDangNhap, MatKhau, ChucVu, TrangThaiTaiKhoan, NgayTao) VALUES
('TK001', 'admin', 'admin123', 'Admin', 'HoatDong', DATEADD(month, -6, GETDATE())),
('TK002', 'quanly1', 'ql123', N'Quản lý', 'HoatDong', DATEADD(month, -3, GETDATE())),
('TK003', 'quanly2', 'ql123', N'Quản lý', 'NgungHoatDong', DATEADD(month, -1, GETDATE()));

-- HoaDon
INSERT INTO HoaDon (MaHoaDon, MaDonHang, NgayLap, TongTienHoaDon, PhuongThucThanhToan, TrangThaiHoaDon) VALUES
('HD001', 'DH001', DATEADD(day, -3, GETDATE()), 900000.0, 'TienMat', 'DaThanhToan'),
('HD002', 'DH002', DATEADD(day, -1, GETDATE()), 1500000.0, 'ChuyenKhoan', 'ChuaThanhToan');
GO
