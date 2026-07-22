package com.xuongmay.dao;

import com.xuongmay.model.DonHang;
import com.xuongmay.model.KhachHang;
import com.xuongmay.model.TrangThaiDonHang;
import com.xuongmay.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DonHangDAO {

    public List<DonHang> getAll() {
        List<DonHang> list = new ArrayList<>();
        String sql = "SELECT ma_don_hang, ma_khach_hang, ngay_dat, tong_tien, trang_thai, ghi_chu FROM DonHang";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            KhachHangDAO khDao = new KhachHangDAO();
            while (rs.next()) list.add(map(rs, khDao));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public DonHang getById(String id) {
        String sql = "SELECT ma_don_hang, ma_khach_hang, ngay_dat, tong_tien, trang_thai, ghi_chu FROM DonHang WHERE ma_don_hang=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs, new KhachHangDAO());
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void add(DonHang dh) {
        String sql = "INSERT INTO DonHang (ma_don_hang, ma_khach_hang, ngay_dat, tong_tien, trang_thai, ghi_chu) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, dh.getMaDonHang());
            ps.setString(2, dh.getKhachHang() != null ? dh.getKhachHang().getMaKhachHang() : null);
            ps.setDate(3, Date.valueOf(dh.getNgayDat()));
            ps.setDouble(4, dh.getTongTien());
            ps.setString(5, dh.getTrangThaiDonHang().name());
            ps.setString(6, dh.getGhiChu());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(DonHang dh) {
        String sql = "UPDATE DonHang SET ma_khach_hang=?, ngay_dat=?, tong_tien=?, trang_thai=?, ghi_chu=? WHERE ma_don_hang=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, dh.getKhachHang() != null ? dh.getKhachHang().getMaKhachHang() : null);
            ps.setDate(2, Date.valueOf(dh.getNgayDat()));
            ps.setDouble(3, dh.getTongTien());
            ps.setString(4, dh.getTrangThaiDonHang().name());
            ps.setString(5, dh.getGhiChu());
            ps.setString(6, dh.getMaDonHang());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(String id) {
        String sql = "DELETE FROM DonHang WHERE ma_don_hang=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private DonHang map(ResultSet rs, KhachHangDAO khDao) throws SQLException {
        String maKh = rs.getString("ma_khach_hang");
        KhachHang kh = (maKh != null) ? khDao.getById(maKh) : null;
        return new DonHang(
            rs.getString("ma_don_hang"),
            kh,
            rs.getDate("ngay_dat").toLocalDate(),
            rs.getDouble("tong_tien"),
            TrangThaiDonHang.valueOf(rs.getString("trang_thai")),
            rs.getString("ghi_chu")
        );
    }
}
