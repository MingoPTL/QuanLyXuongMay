package com.xuongmay.dao;

import com.xuongmay.model.DonHang;
import com.xuongmay.model.KhachHang;
import com.xuongmay.model.TrangThaiDonHang;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DonHangDAO {
    private static final List<DonHang> list = new ArrayList<>();

    static {
        KhachHangDAO khDao = new KhachHangDAO();
        List<KhachHang> khs = khDao.getAll();
        if (!khs.isEmpty()) {
            list.add(new DonHang("DH001", khs.get(0), LocalDate.now().minusDays(3), 900000.0, TrangThaiDonHang.DaGiao, "Giao hàng nhanh"));
            list.add(new DonHang("DH002", khs.get(1), LocalDate.now().minusDays(1), 1500000.0, TrangThaiDonHang.ChuaGiao, "Giao buổi chiều"));
        }
    }

    public List<DonHang> getAll() {
        return new ArrayList<>(list);
    }

    public DonHang getById(String id) {
        return list.stream().filter(d -> d.getMaDonHang().equals(id)).findFirst().orElse(null);
    }

    public void add(DonHang dh) {
        list.add(dh);
    }

    public void update(DonHang dh) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaDonHang().equals(dh.getMaDonHang())) {
                list.set(i, dh);
                return;
            }
        }
    }

    public void delete(String id) {
        list.removeIf(d -> d.getMaDonHang().equals(id));
    }
}
