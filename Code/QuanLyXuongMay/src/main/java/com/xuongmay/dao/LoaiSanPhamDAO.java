package com.xuongmay.dao;

import com.xuongmay.model.LoaiSanPham;
import com.xuongmay.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoaiSanPhamDAO {

    public List<LoaiSanPham> getAll() {
        List<LoaiSanPham> list = new ArrayList<>();
        String sql = "SELECT ma_loai, ten_loai, mo_ta, gia_goc, ghi_chu FROM LoaiSanPham";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public LoaiSanPham getById(String id) {
        String sql = "SELECT ma_loai, ten_loai, mo_ta, gia_goc, ghi_chu FROM LoaiSanPham WHERE ma_loai=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void add(LoaiSanPham lsp) {
        String sql = "INSERT INTO LoaiSanPham (ma_loai, ten_loai, mo_ta, gia_goc, ghi_chu) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, lsp.getMaLoai());
            ps.setString(2, lsp.getTenLoai());
            ps.setString(3, lsp.getMoTa());
            ps.setDouble(4, lsp.getGiaGoc());
            ps.setString(5, lsp.getGhiChu());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(LoaiSanPham lsp) {
        String sql = "UPDATE LoaiSanPham SET ten_loai=?, mo_ta=?, gia_goc=?, ghi_chu=? WHERE ma_loai=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, lsp.getTenLoai());
            ps.setString(2, lsp.getMoTa());
            ps.setDouble(3, lsp.getGiaGoc());
            ps.setString(4, lsp.getGhiChu());
            ps.setString(5, lsp.getMaLoai());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(String id) {
        String sql = "DELETE FROM LoaiSanPham WHERE ma_loai=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private LoaiSanPham map(ResultSet rs) throws SQLException {
        return new LoaiSanPham(
            rs.getString("ma_loai"),
            rs.getString("ten_loai"),
            rs.getString("mo_ta"),
            rs.getDouble("gia_goc"),
            rs.getString("ghi_chu")
        );
    }
}
