package com.xuongmay.dao;

import com.xuongmay.model.LoaiSanPham;
import com.xuongmay.model.SanPham;
import com.xuongmay.model.TrangThaiSanPham;
import java.util.ArrayList;
import java.util.List;

public class SanPhamDAO {
    private static final List<SanPham> list = new ArrayList<>();

    static {
        LoaiSanPhamDAO lspDao = new LoaiSanPhamDAO();
        List<LoaiSanPham> lsps = lspDao.getAll();
        if (lsps.size() >= 3) {
            // SP001: 4 màu, 160 bộ (= 40 lượt trải × 4 màu)
            //   → bộ mỗi màu = 40, ri mỗi màu = 40/4 = 10, tổng ri DK = 10×4 = 40 ri, bộ lẻ = 0
            SanPham sp1 = new SanPham("SP001", "Áo thun Polo Classic", 45000, 4, 160, "Dáng suông", TrangThaiSanPham.DaHoanThanh, lsps.get(0));
            sp1.setTongSoBoThucTe(158); sp1.setTongSoRiThucTe(39); sp1.setSoBoLeThucTe(2); sp1.setSoRiLeThucTe(0);
            list.add(sp1);
            // SP002: 3 màu, 90 bộ (= 30 lượt trải × 3 màu)
            //   → bộ mỗi màu = 30, ri mỗi màu = 30/4 = 7, tổng ri DK = 7×3 = 21 ri, bộ lẻ = 90 - 21×4 = 6
            list.add(new SanPham("SP002", "Quần Kaki Công Sở", 75000, 3, 90, "Dáng slimfit", TrangThaiSanPham.DangMay, lsps.get(1)));
            // SP003: 4 màu, 64 bộ (= 16 lượt trải × 4 màu)
            //   → bộ mỗi màu = 16, ri mỗi màu = 16/4 = 4, tổng ri DK = 4×4 = 16 ri, bộ lẻ = 0
            SanPham sp3 = new SanPham("SP003", "Đầm Linen Dạo Phố", 95000, 4, 64, "Dáng chữ A", TrangThaiSanPham.DaHoanThanh, lsps.get(2));
            sp3.setTongSoBoThucTe(61); sp3.setTongSoRiThucTe(15); sp3.setSoBoLeThucTe(1); sp3.setSoRiLeThucTe(0);
            list.add(sp3);
        }
    }

    // Helper class for sub-initialization or simply plain class:
    // Wait, "SlimSanPham" is a typo/copy-paste issue. Let's make sure we write "SanPham" for all.
    // Yes, let's write SanPham.
    public List<SanPham> getAll() {
        return new ArrayList<>(list);
    }

    public SanPham getById(String id) {
        return list.stream().filter(s -> s.getMaSanPham().equals(id)).findFirst().orElse(null);
    }

    public void add(SanPham sp) {
        list.add(sp);
    }

    public void update(SanPham sp) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaSanPham().equals(sp.getMaSanPham())) {
                list.set(i, sp);
                return;
            }
        }
    }

    public void delete(String id) {
        list.removeIf(s -> s.getMaSanPham().equals(id));
    }
}
