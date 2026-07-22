package com.xuongmay.dao;

import com.xuongmay.model.ChucVu;
import com.xuongmay.model.TaiKhoan;
import com.xuongmay.model.TrangThaiTaiKhoan;
import com.xuongmay.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TaiKhoanDAO {

    public List<TaiKhoan> getAll() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT ma_tai_khoan, ten_dang_nhap, mat_khau, chuc_vu, trang_thai, ngay_tao FROM TaiKhoan";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public TaiKhoan getById(String id) {
        String sql = "SELECT ma_tai_khoan, ten_dang_nhap, mat_khau, chuc_vu, trang_thai, ngay_tao FROM TaiKhoan WHERE ma_tai_khoan=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public TaiKhoan getByTenDangNhap(String tenDangNhap) {
        String sql = "SELECT ma_tai_khoan, ten_dang_nhap, mat_khau, chuc_vu, trang_thai, ngay_tao FROM TaiKhoan WHERE ten_dang_nhap=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, tenDangNhap);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void add(TaiKhoan tk) {
        String sql = "INSERT INTO TaiKhoan (ma_tai_khoan, ten_dang_nhap, mat_khau, chuc_vu, trang_thai, ngay_tao) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, tk.getMaTaiKhoan());
            ps.setString(2, tk.getTenDangNhap());
            ps.setString(3, tk.getMatKhau());
            ps.setString(4, tk.getChucVu().name());
            ps.setString(5, tk.getTrangThaiTaiKhoan().name());
            ps.setDate(6, Date.valueOf(tk.getNgayTao()));
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(TaiKhoan tk) {
        String sql = "UPDATE TaiKhoan SET ten_dang_nhap=?, mat_khau=?, chuc_vu=?, trang_thai=?, ngay_tao=? WHERE ma_tai_khoan=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, tk.getTenDangNhap());
            ps.setString(2, tk.getMatKhau());
            ps.setString(3, tk.getChucVu().name());
            ps.setString(4, tk.getTrangThaiTaiKhoan().name());
            ps.setDate(5, Date.valueOf(tk.getNgayTao()));
            ps.setString(6, tk.getMaTaiKhoan());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(String id) {
        String sql = "DELETE FROM TaiKhoan WHERE ma_tai_khoan=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private TaiKhoan map(ResultSet rs) throws SQLException {
        return new TaiKhoan(
            rs.getString("ma_tai_khoan"),
            rs.getString("ten_dang_nhap"),
            rs.getString("mat_khau"),
            ChucVu.valueOf(rs.getString("chuc_vu")),
            TrangThaiTaiKhoan.valueOf(rs.getString("trang_thai")),
            rs.getDate("ngay_tao").toLocalDate()
        );
    }
}
