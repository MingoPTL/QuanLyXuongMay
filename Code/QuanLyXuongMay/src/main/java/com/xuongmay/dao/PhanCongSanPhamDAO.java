package com.xuongmay.dao;

import com.xuongmay.model.NhanVien;
import com.xuongmay.model.PhanCongSanPham;
import com.xuongmay.model.SanPham;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PhanCongSanPhamDAO {
    private static final List<PhanCongSanPham> list = new ArrayList<>();

    static {
        SanPhamDAO spDao = new SanPhamDAO();
        NhanVienDAO nvDao = new NhanVienDAO();
        List<SanPham> sps = spDao.getAll();
        List<NhanVien> nvs = nvDao.getAll();
        if (sps.size() >= 2 && nvs.size() >= 5) {
            list.add(new PhanCongSanPham("PC001", sps.get(0), nvs.get(0), LocalDate.now().minusDays(5)));
            list.add(new PhanCongSanPham("PC002", sps.get(1), nvs.get(1), LocalDate.now().minusDays(4)));
            list.add(new PhanCongSanPham("PC003", sps.get(0), nvs.get(3), LocalDate.now().minusDays(3)));
        }
    }

    public List<PhanCongSanPham> getAll() {
        return new ArrayList<>(list);
    }

    public PhanCongSanPham getById(String id) {
        return list.stream().filter(p -> p.getMaPhanCong().equals(id)).findFirst().orElse(null);
    }

    public void add(PhanCongSanPham pc) {
        list.add(pc);
    }

    public void update(PhanCongSanPham pc) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaPhanCong().equals(pc.getMaPhanCong())) {
                list.set(i, pc);
                return;
            }
        }
    }

    public void delete(String id) {
        list.removeIf(p -> p.getMaPhanCong().equals(id));
    }
}
