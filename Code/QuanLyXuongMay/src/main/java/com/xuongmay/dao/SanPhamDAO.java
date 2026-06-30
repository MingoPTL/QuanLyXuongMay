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
            list.add(new SanPham("SP001", "Áo thun Polo Classic", 45000, 150, 30, 0, 0, "Dáng suông", TrangThaiSanPham.DaHoanThanh, lsps.get(0)));
            list.add(new SanPham("SP002", "Quần Kaki Công Sở", 75000, 90, 18, 5, 1, "Dáng slimfit", TrangThaiSanPham.DangMay, lsps.get(1)));
            list.add(new SanPham("SP003", "Đầm Linen Dạo Phố", 95000, 60, 12, 0, 0, "Dáng chữ A", TrangThaiSanPham.DaHoanThanh, lsps.get(2)));
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
