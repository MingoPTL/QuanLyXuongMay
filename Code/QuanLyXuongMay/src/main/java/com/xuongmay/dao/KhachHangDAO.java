package com.xuongmay.dao;

import com.xuongmay.model.KhachHang;
import com.xuongmay.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KhachHangDAO {

    public List<KhachHang> getAll() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT ma_khach_hang, ten_khach_hang, sdt, dia_chi_nha, ma_so_thue, ghi_chu FROM KhachHang";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public KhachHang getById(String id) {
        String sql = "SELECT ma_khach_hang, ten_khach_hang, sdt, dia_chi_nha, ma_so_thue, ghi_chu FROM KhachHang WHERE ma_khach_hang=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void add(KhachHang kh) {
        String sql = "INSERT INTO KhachHang (ma_khach_hang, ten_khach_hang, sdt, dia_chi_nha, ma_so_thue, ghi_chu) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, kh.getMaKhachHang());
            ps.setString(2, kh.getTenKhachHang());
            ps.setString(3, kh.getSdt());
            ps.setString(4, kh.getDiaChiNha());
            ps.setString(5, kh.getMaSoThue());
            ps.setString(6, kh.getGhiChu());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(KhachHang kh) {
        String sql = "UPDATE KhachHang SET ten_khach_hang=?, sdt=?, dia_chi_nha=?, ma_so_thue=?, ghi_chu=? WHERE ma_khach_hang=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, kh.getTenKhachHang());
            ps.setString(2, kh.getSdt());
            ps.setString(3, kh.getDiaChiNha());
            ps.setString(4, kh.getMaSoThue());
            ps.setString(5, kh.getGhiChu());
            ps.setString(6, kh.getMaKhachHang());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(String id) {
        String sql = "DELETE FROM KhachHang WHERE ma_khach_hang=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private KhachHang map(ResultSet rs) throws SQLException {
        return new KhachHang(
            rs.getString("ma_khach_hang"),
            rs.getString("ten_khach_hang"),
            rs.getString("sdt"),
            rs.getString("dia_chi_nha"),
            rs.getString("ma_so_thue"),
            rs.getString("ghi_chu")
        );
    }
}
