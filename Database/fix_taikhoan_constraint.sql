-- Chạy script này trong SSMS để fix CHECK constraint cho TaiKhoan
-- (enum Java dùng 'NgungHoatDong', không phải 'BiKhoa')

USE QuanLyXuongMay;
GO

ALTER TABLE TaiKhoan
    DROP CONSTRAINT CHK_TaiKhoan_TrangThai;
GO

ALTER TABLE TaiKhoan
    ADD CONSTRAINT CHK_TaiKhoan_TrangThai
    CHECK (trang_thai IN ('HoatDong', 'NgungHoatDong'));
GO

-- Thêm tài khoản test (sử dụng được ngay trên giao diện đăng nhập)
INSERT INTO TaiKhoan (ma_tai_khoan, ten_dang_nhap, mat_khau, chuc_vu, trang_thai, ngay_tao)
VALUES 
(N'TK001', N'admin', N'admin123', N'ADMIN', N'HoatDong', CAST(GETDATE() AS DATE)),
(N'TK002', N'quanly1', N'ql123', N'QUANLY', N'HoatDong', CAST(GETDATE() AS DATE));
GO

