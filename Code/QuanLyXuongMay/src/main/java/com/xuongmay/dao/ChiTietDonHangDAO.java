package com.xuongmay.dao;

import com.xuongmay.model.ChiTietDonHang;
import com.xuongmay.model.DonHang;
import com.xuongmay.model.SanPham;
import com.xuongmay.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ChiTietDonHangDAO {

    public List<ChiTietDonHang> getAll() {
        List<ChiTietDonHang> list = new ArrayList<>();
        String sql = "SELECT ma_don_hang, ma_san_pham, so_luong_ri, don_gia_ri, thanh_tien FROM ChiTietDonHang";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            DonHangDAO dhDao = new DonHangDAO();
            SanPhamDAO spDao = new SanPhamDAO();
            while (rs.next()) list.add(map(rs, dhDao, spDao));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<ChiTietDonHang> getByDonHangId(String donHangId) {
        List<ChiTietDonHang> list = new ArrayList<>();
        String sql = "SELECT ma_don_hang, ma_san_pham, so_luong_ri, don_gia_ri, thanh_tien FROM ChiTietDonHang WHERE ma_don_hang=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, donHangId);
            try (ResultSet rs = ps.executeQuery()) {
                DonHangDAO dhDao = new DonHangDAO();
                SanPhamDAO spDao = new SanPhamDAO();
                while (rs.next()) list.add(map(rs, dhDao, spDao));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public void add(ChiTietDonHang ct) {
        String sql = "INSERT INTO ChiTietDonHang (ma_don_hang, ma_san_pham, so_luong_ri, don_gia_ri, thanh_tien) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, ct.getDonHang() != null ? ct.getDonHang().getMaDonHang() : null);
            ps.setString(2, ct.getSanPham() != null ? ct.getSanPham().getMaSanPham() : null);
            ps.setInt(3, ct.getSoLuongRi());
            ps.setDouble(4, ct.getDonGiaRi());
            ps.setDouble(5, ct.getThanhTien());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void deleteByDonHangId(String donHangId) {
        String sql = "DELETE FROM ChiTietDonHang WHERE ma_don_hang=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, donHangId);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private ChiTietDonHang map(ResultSet rs, DonHangDAO dhDao, SanPhamDAO spDao) throws SQLException {
        DonHang dh = dhDao.getById(rs.getString("ma_don_hang"));
        SanPham sp = spDao.getById(rs.getString("ma_san_pham"));
        return new ChiTietDonHang(dh, sp,
            rs.getInt("so_luong_ri"),
            rs.getDouble("don_gia_ri"),
            rs.getDouble("thanh_tien")
        );
    }
}
