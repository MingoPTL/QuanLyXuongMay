package com.xuongmay.dao;

import com.xuongmay.model.ChuyenMon;
import com.xuongmay.model.NhanVien;
import com.xuongmay.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NhanVienDAO {

    public List<NhanVien> getAll() {
        List<NhanVien> list = new ArrayList<>();
        String sql = "SELECT ma_nhan_vien, ten_nhan_vien, dien_thoai, chuyen_mon, ghi_chu FROM NhanVien";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public NhanVien getById(String id) {
        String sql = "SELECT ma_nhan_vien, ten_nhan_vien, dien_thoai, chuyen_mon, ghi_chu FROM NhanVien WHERE ma_nhan_vien=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void add(NhanVien nv) {
        String sql = "INSERT INTO NhanVien (ma_nhan_vien, ten_nhan_vien, dien_thoai, chuyen_mon, ghi_chu) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, nv.getMaNhanVien());
            ps.setString(2, nv.getTenNhanVien());
            ps.setString(3, nv.getDienThoai());
            ps.setString(4, nv.getChuyenMon().name()); // lưu tên enum: ThoMay / ThoUi
            ps.setString(5, nv.getGhiChu());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(NhanVien nv) {
        String sql = "UPDATE NhanVien SET ten_nhan_vien=?, dien_thoai=?, chuyen_mon=?, ghi_chu=? WHERE ma_nhan_vien=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, nv.getTenNhanVien());
            ps.setString(2, nv.getDienThoai());
            ps.setString(3, nv.getChuyenMon().name());
            ps.setString(4, nv.getGhiChu());
            ps.setString(5, nv.getMaNhanVien());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(String id) {
        String sql = "DELETE FROM NhanVien WHERE ma_nhan_vien=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private NhanVien map(ResultSet rs) throws SQLException {
        return new NhanVien(
            rs.getString("ma_nhan_vien"),
            rs.getString("ten_nhan_vien"),
            rs.getString("dien_thoai"),
            ChuyenMon.valueOf(rs.getString("chuyen_mon")),
            rs.getString("ghi_chu")
        );
    }
}
