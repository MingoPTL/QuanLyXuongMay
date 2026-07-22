package com.xuongmay.dao;

import com.xuongmay.model.CayVai;
import com.xuongmay.model.LoVai;
import com.xuongmay.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class CayVaiDAO {

    public List<CayVai> getAll() {
        List<CayVai> list = new ArrayList<>();
        String sql = "SELECT id_cay_vai, ten_cay_vai, mau_sac, ma_lo, chieu_dai, vi_tri, ghi_chu, luot_trai_vai FROM CayVai";
        try (Statement st = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            LoVaiDAO loDao = new LoVaiDAO();
            while (rs.next()) list.add(map(rs, loDao));
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public List<CayVai> getByLoVaiId(String loVaiId) {
        List<CayVai> list = new ArrayList<>();
        String sql = "SELECT id_cay_vai, ten_cay_vai, mau_sac, ma_lo, chieu_dai, vi_tri, ghi_chu, luot_trai_vai FROM CayVai WHERE ma_lo=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, loVaiId);
            try (ResultSet rs = ps.executeQuery()) {
                LoVaiDAO loDao = new LoVaiDAO();
                while (rs.next()) list.add(map(rs, loDao));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return list;
    }

    public CayVai getById(String tenCayVai) {
        // Lookup by name (legacy interface)
        return getAll().stream()
            .filter(cv -> cv.getTenCayVai().equals(tenCayVai))
            .findFirst().orElse(null);
    }

    public void add(CayVai cv) {
        String sql = "INSERT INTO CayVai (ten_cay_vai, mau_sac, ma_lo, chieu_dai, vi_tri, ghi_chu, luot_trai_vai) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cv.getTenCayVai());
            ps.setString(2, cv.getMauSac());
            ps.setString(3, cv.getLoVai() != null ? cv.getLoVai().getMaLo() : null);
            ps.setDouble(4, cv.getChieuDai());
            ps.setString(5, cv.getViTri());
            ps.setString(6, cv.getGhiChu());
            ps.setInt(7, cv.getLuotTraiVai());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) cv.setIdCayVai(keys.getLong(1));
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void update(CayVai cv) {
        if (cv.getIdCayVai() == null) return;
        String sql = "UPDATE CayVai SET ten_cay_vai=?, mau_sac=?, ma_lo=?, chieu_dai=?, vi_tri=?, ghi_chu=?, luot_trai_vai=? WHERE id_cay_vai=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, cv.getTenCayVai());
            ps.setString(2, cv.getMauSac());
            ps.setString(3, cv.getLoVai() != null ? cv.getLoVai().getMaLo() : null);
            ps.setDouble(4, cv.getChieuDai());
            ps.setString(5, cv.getViTri());
            ps.setString(6, cv.getGhiChu());
            ps.setInt(7, cv.getLuotTraiVai());
            ps.setLong(8, cv.getIdCayVai());
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    public void delete(String tenCayVai) {
        String sql = "DELETE FROM CayVai WHERE ten_cay_vai=?";
        try (PreparedStatement ps = DatabaseConnection.getConnection().prepareStatement(sql)) {
            ps.setString(1, tenCayVai);
            ps.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    private CayVai map(ResultSet rs, LoVaiDAO loDao) throws SQLException {
        String maLo = rs.getString("ma_lo");
        LoVai loVai = (maLo != null) ? loDao.getById(maLo) : null;
        CayVai cv = new CayVai(
            rs.getString("ten_cay_vai"),
            rs.getString("mau_sac"),
            loVai,
            rs.getDouble("chieu_dai"),
            rs.getString("vi_tri"),
            rs.getString("ghi_chu"),
            rs.getInt("luot_trai_vai")
        );
        cv.setIdCayVai(rs.getLong("id_cay_vai"));
        return cv;
    }
}
