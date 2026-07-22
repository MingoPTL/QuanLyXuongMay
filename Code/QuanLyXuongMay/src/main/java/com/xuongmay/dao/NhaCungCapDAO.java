package com.xuongmay.dao;

import com.xuongmay.model.NhaCungCap;
import com.xuongmay.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhaCungCapDAO {

    public List<NhaCungCap> getAll() {
        List<NhaCungCap> list = new ArrayList<>();
        String sql = "SELECT ma_nha_cung_cap, ten_nha_cung_cap, so_dien_thoai, dia_chi_nha FROM NhaCungCap";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public NhaCungCap getById(String id) {
        String sql = "SELECT ma_nha_cung_cap, ten_nha_cung_cap, so_dien_thoai, dia_chi_nha FROM NhaCungCap WHERE ma_nha_cung_cap = ?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void add(NhaCungCap ncc) {
        String sql = "INSERT INTO NhaCungCap (ma_nha_cung_cap, ten_nha_cung_cap, so_dien_thoai, dia_chi_nha) VALUES (?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ncc.getMaNhaCungCap());
            ps.setString(2, ncc.getTenNhaCungCap());
            ps.setString(3, ncc.getSoDienThoai());
            ps.setString(4, ncc.getDiaChiNha());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(NhaCungCap ncc) {
        String sql = "UPDATE NhaCungCap SET ten_nha_cung_cap=?, so_dien_thoai=?, dia_chi_nha=? WHERE ma_nha_cung_cap=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ncc.getTenNhaCungCap());
            ps.setString(2, ncc.getSoDienThoai());
            ps.setString(3, ncc.getDiaChiNha());
            ps.setString(4, ncc.getMaNhaCungCap());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(String id) {
        String sql = "DELETE FROM NhaCungCap WHERE ma_nha_cung_cap=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private NhaCungCap map(ResultSet rs) throws SQLException {
        return new NhaCungCap(
            rs.getString("ma_nha_cung_cap"),
            rs.getString("ten_nha_cung_cap"),
            rs.getString("so_dien_thoai"),
            rs.getString("dia_chi_nha")
        );
    }
}
