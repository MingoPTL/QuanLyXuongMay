package com.xuongmay.dao;

import com.xuongmay.model.DonHang;
import com.xuongmay.model.HoaDon;
import com.xuongmay.model.PhuongThucThanhToan;
import com.xuongmay.model.TrangThaiHoaDon;
import com.xuongmay.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {

    public List<HoaDon> getAll() {
        List<HoaDon> list = new ArrayList<>();
        String sql = "SELECT ma_hoa_don, ma_don_hang, ngay_lap, tong_tien_hoa_don, phuong_thuc_thanh_toan, trang_thai FROM HoaDon";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            DonHangDAO dhDao = new DonHangDAO();
            while (rs.next()) list.add(map(rs, dhDao));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public HoaDon getById(String id) {
        String sql = "SELECT ma_hoa_don, ma_don_hang, ngay_lap, tong_tien_hoa_don, phuong_thuc_thanh_toan, trang_thai FROM HoaDon WHERE ma_hoa_don=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs, new DonHangDAO());
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void add(HoaDon hd) {
        String sql = "INSERT INTO HoaDon (ma_hoa_don, ma_don_hang, ngay_lap, tong_tien_hoa_don, phuong_thuc_thanh_toan, trang_thai) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, hd.getMaHoaDon());
            ps.setString(2, hd.getDonHang() != null ? hd.getDonHang().getMaDonHang() : null);
            ps.setDate(3, Date.valueOf(hd.getNgayLap()));
            ps.setDouble(4, hd.getTongTienHoaDon());
            ps.setString(5, hd.getPhuongThucThanhToan().name());
            ps.setString(6, hd.getTrangThaiHoaDon().name());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(HoaDon hd) {
        String sql = "UPDATE HoaDon SET ma_don_hang=?, ngay_lap=?, tong_tien_hoa_don=?, phuong_thuc_thanh_toan=?, trang_thai=? WHERE ma_hoa_don=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, hd.getDonHang() != null ? hd.getDonHang().getMaDonHang() : null);
            ps.setDate(2, Date.valueOf(hd.getNgayLap()));
            ps.setDouble(3, hd.getTongTienHoaDon());
            ps.setString(4, hd.getPhuongThucThanhToan().name());
            ps.setString(5, hd.getTrangThaiHoaDon().name());
            ps.setString(6, hd.getMaHoaDon());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(String id) {
        String sql = "DELETE FROM HoaDon WHERE ma_hoa_don=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private HoaDon map(ResultSet rs, DonHangDAO dhDao) throws SQLException {
        String maDh = rs.getString("ma_don_hang");
        DonHang dh = (maDh != null) ? dhDao.getById(maDh) : null;
        return new HoaDon(
            rs.getString("ma_hoa_don"),
            dh,
            rs.getDate("ngay_lap").toLocalDate(),
            rs.getDouble("tong_tien_hoa_don"),
            PhuongThucThanhToan.valueOf(rs.getString("phuong_thuc_thanh_toan")),
            TrangThaiHoaDon.valueOf(rs.getString("trang_thai"))
        );
    }
}
