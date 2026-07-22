package com.xuongmay.dao;

import com.xuongmay.model.LoaiSanPham;
import com.xuongmay.model.SanPham;
import com.xuongmay.model.TrangThaiSanPham;
import com.xuongmay.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {

    public List<SanPham> getAll() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT ma_san_pham, ten_san_pham, gia_thuc_te, ghi_chu, trang_thai, ma_loai, hinh_anh, " +
                     "so_mau, tong_so_bo_du_kien, tong_so_ri_du_kien, so_bo_le_du_kien, " +
                     "tong_so_bo_thuc_te, tong_so_ri_thuc_te, so_bo_le_thuc_te, so_ri_le_thuc_te FROM SanPham";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            LoaiSanPhamDAO lspDao = new LoaiSanPhamDAO();
            while (rs.next()) list.add(map(rs, lspDao));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public SanPham getById(String id) {
        String sql = "SELECT ma_san_pham, ten_san_pham, gia_thuc_te, ghi_chu, trang_thai, ma_loai, hinh_anh, " +
                     "so_mau, tong_so_bo_du_kien, tong_so_ri_du_kien, so_bo_le_du_kien, " +
                     "tong_so_bo_thuc_te, tong_so_ri_thuc_te, so_bo_le_thuc_te, so_ri_le_thuc_te FROM SanPham WHERE ma_san_pham=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs, new LoaiSanPhamDAO());
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    public void add(SanPham sp) {
        String sql = "INSERT INTO SanPham (ma_san_pham, ten_san_pham, gia_thuc_te, ghi_chu, trang_thai, ma_loai, hinh_anh, " +
                     "so_mau, tong_so_bo_du_kien, tong_so_ri_du_kien, so_bo_le_du_kien, " +
                     "tong_so_bo_thuc_te, tong_so_ri_thuc_te, so_bo_le_thuc_te, so_ri_le_thuc_te) " +
                     "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            setParams(ps, sp);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(SanPham sp) {
        String sql = "UPDATE SanPham SET ten_san_pham=?, gia_thuc_te=?, ghi_chu=?, trang_thai=?, ma_loai=?, hinh_anh=?, " +
                     "so_mau=?, tong_so_bo_du_kien=?, tong_so_ri_du_kien=?, so_bo_le_du_kien=?, " +
                     "tong_so_bo_thuc_te=?, tong_so_ri_thuc_te=?, so_bo_le_thuc_te=?, so_ri_le_thuc_te=? WHERE ma_san_pham=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, sp.getTenSanPham());
            ps.setDouble(2, sp.getGiaThucTe());
            ps.setString(3, sp.getGhiChu());
            ps.setString(4, sp.getTrangThaiSanPham().name());
            ps.setString(5, sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getMaLoai() : null);
            ps.setString(6, sp.getHinhAnh());
            ps.setInt(7,  sp.getSoMau());
            ps.setInt(8,  sp.getTongSoBoDuKien());
            ps.setInt(9,  sp.getTongSoRiDuKien());
            ps.setInt(10, sp.getSoBoLeDuKien());
            ps.setInt(11, sp.getTongSoBoThucTe());
            ps.setInt(12, sp.getTongSoRiThucTe());
            ps.setInt(13, sp.getSoBoLeThucTe());
            ps.setInt(14, sp.getSoRiLeThucTe());
            ps.setString(15, sp.getMaSanPham());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(String id) {
        String sql = "DELETE FROM SanPham WHERE ma_san_pham=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private void setParams(PreparedStatement ps, SanPham sp) throws SQLException {
        ps.setString(1, sp.getMaSanPham());
        ps.setString(2, sp.getTenSanPham());
        ps.setDouble(3, sp.getGiaThucTe());
        ps.setString(4, sp.getGhiChu());
        ps.setString(5, sp.getTrangThaiSanPham().name());
        ps.setString(6, sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getMaLoai() : null);
        ps.setString(7, sp.getHinhAnh());
        ps.setInt(8,  sp.getSoMau());
        ps.setInt(9,  sp.getTongSoBoDuKien());
        ps.setInt(10, sp.getTongSoRiDuKien());
        ps.setInt(11, sp.getSoBoLeDuKien());
        ps.setInt(12, sp.getTongSoBoThucTe());
        ps.setInt(13, sp.getTongSoRiThucTe());
        ps.setInt(14, sp.getSoBoLeThucTe());
        ps.setInt(15, sp.getSoRiLeThucTe());
    }

    private SanPham map(ResultSet rs, LoaiSanPhamDAO lspDao) throws SQLException {
        String maLoai = rs.getString("ma_loai");
        LoaiSanPham loai = (maLoai != null) ? lspDao.getById(maLoai) : null;
        SanPham sp = new SanPham(
            rs.getString("ma_san_pham"),
            rs.getString("ten_san_pham"),
            rs.getDouble("gia_thuc_te"),
            rs.getInt("so_mau"),
            rs.getInt("tong_so_bo_du_kien"),
            rs.getString("ghi_chu"),
            TrangThaiSanPham.valueOf(rs.getString("trang_thai")),
            loai
        );
        sp.setHinhAnh(rs.getString("hinh_anh"));
        sp.setTongSoBoThucTe(rs.getInt("tong_so_bo_thuc_te"));
        sp.setTongSoRiThucTe(rs.getInt("tong_so_ri_thuc_te"));
        sp.setSoBoLeThucTe(rs.getInt("so_bo_le_thuc_te"));
        sp.setSoRiLeThucTe(rs.getInt("so_ri_le_thuc_te"));
        return sp;
    }
}
