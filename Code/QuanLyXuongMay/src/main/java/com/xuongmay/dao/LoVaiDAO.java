package com.xuongmay.dao;

import com.xuongmay.model.LoVai;
import com.xuongmay.model.NhaCungCap;
import com.xuongmay.model.TrangThaiLoVai;
import com.xuongmay.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoVaiDAO {

    public List<LoVai> getAll() {
        List<LoVai> list = new ArrayList<>();
        String sql = "SELECT ma_lo, ten_lo, ma_nha_cung_cap, ngay_nhap, so_luong, loai_vai, gia_nhap, ghi_chu, trang_thai, hinh_anh FROM LoVai";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            NhaCungCapDAO nccDao = new NhaCungCapDAO();
            while (rs.next()) list.add(map(rs, nccDao));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public LoVai getById(String id) {
        String sql = "SELECT ma_lo, ten_lo, ma_nha_cung_cap, ngay_nhap, so_luong, loai_vai, gia_nhap, ghi_chu, trang_thai, hinh_anh FROM LoVai WHERE ma_lo=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs, new NhaCungCapDAO());
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void add(LoVai lo) {
        String sql = "INSERT INTO LoVai (ma_lo, ten_lo, ma_nha_cung_cap, ngay_nhap, so_luong, loai_vai, gia_nhap, ghi_chu, trang_thai, hinh_anh) VALUES (?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, lo.getMaLo());
            ps.setString(2, lo.getTenLo());
            ps.setString(3, lo.getNhaCungCap() != null ? lo.getNhaCungCap().getMaNhaCungCap() : null);
            ps.setDate(4, Date.valueOf(lo.getNgayNhap()));
            ps.setInt(5, lo.getSoLuong());
            ps.setString(6, lo.getLoaiVai());
            ps.setDouble(7, lo.getGiaNhap());
            ps.setString(8, lo.getGhiChu());
            ps.setString(9, lo.getTrangThaiLoVai().name());
            ps.setString(10, lo.getHinhAnh());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(LoVai lo) {
        String sql = "UPDATE LoVai SET ten_lo=?, ma_nha_cung_cap=?, ngay_nhap=?, so_luong=?, loai_vai=?, gia_nhap=?, ghi_chu=?, trang_thai=?, hinh_anh=? WHERE ma_lo=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, lo.getTenLo());
            ps.setString(2, lo.getNhaCungCap() != null ? lo.getNhaCungCap().getMaNhaCungCap() : null);
            ps.setDate(3, Date.valueOf(lo.getNgayNhap()));
            ps.setInt(4, lo.getSoLuong());
            ps.setString(5, lo.getLoaiVai());
            ps.setDouble(6, lo.getGiaNhap());
            ps.setString(7, lo.getGhiChu());
            ps.setString(8, lo.getTrangThaiLoVai().name());
            ps.setString(9, lo.getHinhAnh());
            ps.setString(10, lo.getMaLo());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(String id) {
        String sql = "DELETE FROM LoVai WHERE ma_lo=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private LoVai map(ResultSet rs, NhaCungCapDAO nccDao) throws SQLException {
        String maNcc = rs.getString("ma_nha_cung_cap");
        NhaCungCap ncc = (maNcc != null) ? nccDao.getById(maNcc) : null;
        LoVai lo = new LoVai(
            rs.getString("ma_lo"),
            rs.getString("ten_lo"),
            ncc,
            rs.getDate("ngay_nhap").toLocalDate(),
            rs.getInt("so_luong"),
            rs.getString("loai_vai"),
            rs.getDouble("gia_nhap"),
            rs.getString("ghi_chu"),
            TrangThaiLoVai.valueOf(rs.getString("trang_thai"))
        );
        lo.setHinhAnh(rs.getString("hinh_anh"));
        return lo;
    }
}
