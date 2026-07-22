package com.xuongmay.dao;

import com.xuongmay.model.NhanVien;
import com.xuongmay.model.PhanCongSanPham;
import com.xuongmay.model.SanPham;
import com.xuongmay.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PhanCongSanPhamDAO {

    public List<PhanCongSanPham> getAll() {
        List<PhanCongSanPham> list = new ArrayList<>();
        String sql = "SELECT ma_phan_cong, ma_san_pham, ma_nhan_vien, ngay_phan_cong, so_luong FROM PhanCongSanPham";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            SanPhamDAO spDao = new SanPhamDAO();
            NhanVienDAO nvDao = new NhanVienDAO();
            while (rs.next()) list.add(map(rs, spDao, nvDao));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public PhanCongSanPham getById(String id) {
        String sql = "SELECT ma_phan_cong, ma_san_pham, ma_nhan_vien, ngay_phan_cong, so_luong FROM PhanCongSanPham WHERE ma_phan_cong=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs, new SanPhamDAO(), new NhanVienDAO());
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void add(PhanCongSanPham pc) {
        String sql = "INSERT INTO PhanCongSanPham (ma_phan_cong, ma_san_pham, ma_nhan_vien, ngay_phan_cong, so_luong) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, pc.getMaPhanCong());
            ps.setString(2, pc.getSanPham() != null ? pc.getSanPham().getMaSanPham() : null);
            ps.setString(3, pc.getNhanVien() != null ? pc.getNhanVien().getMaNhanVien() : null);
            ps.setDate(4, Date.valueOf(pc.getNgayPhanCong()));
            ps.setString(5, pc.getSoLuong());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(PhanCongSanPham pc) {
        String sql = "UPDATE PhanCongSanPham SET ma_san_pham=?, ma_nhan_vien=?, ngay_phan_cong=?, so_luong=? WHERE ma_phan_cong=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, pc.getSanPham() != null ? pc.getSanPham().getMaSanPham() : null);
            ps.setString(2, pc.getNhanVien() != null ? pc.getNhanVien().getMaNhanVien() : null);
            ps.setDate(3, Date.valueOf(pc.getNgayPhanCong()));
            ps.setString(4, pc.getSoLuong());
            ps.setString(5, pc.getMaPhanCong());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(String id) {
        String sql = "DELETE FROM PhanCongSanPham WHERE ma_phan_cong=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private PhanCongSanPham map(ResultSet rs, SanPhamDAO spDao, NhanVienDAO nvDao) throws SQLException {
        SanPham sp = spDao.getById(rs.getString("ma_san_pham"));
        NhanVien nv = nvDao.getById(rs.getString("ma_nhan_vien"));
        return new PhanCongSanPham(
            rs.getString("ma_phan_cong"),
            sp, nv,
            rs.getDate("ngay_phan_cong").toLocalDate(),
            rs.getString("so_luong")
        );
    }
}
