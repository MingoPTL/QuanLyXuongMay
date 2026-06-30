package com.xuongmay.dao;

import com.xuongmay.model.DonHang;
import com.xuongmay.model.HoaDon;
import com.xuongmay.model.PhuongThucThanhToan;
import com.xuongmay.model.TrangThaiHoaDon;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class HoaDonDAO {
    private static final List<HoaDon> list = new ArrayList<>();

    static {
        DonHangDAO dhDao = new DonHangDAO();
        List<DonHang> dhs = dhDao.getAll();
        if (!dhs.isEmpty()) {
            list.add(new HoaDon("HD001", dhs.get(0), LocalDate.now().minusDays(3), dhs.get(0).getTongTien(), PhuongThucThanhToan.TienMat, TrangThaiHoaDon.DaThanhToan));
            if (dhs.size() >= 2) {
                list.add(new HoaDon("HD002", dhs.get(1), LocalDate.now().minusDays(1), dhs.get(1).getTongTien(), PhuongThucThanhToan.ChuyenKhoan, TrangThaiHoaDon.ChuaThanhToan));
            }
        }
    }

    public List<HoaDon> getAll() {
        return new ArrayList<>(list);
    }

    public HoaDon getById(String id) {
        return list.stream().filter(h -> h.getMaHoaDon().equals(id)).findFirst().orElse(null);
    }

    public void add(HoaDon hd) {
        list.add(hd);
    }

    public void update(HoaDon hd) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getMaHoaDon().equals(hd.getMaHoaDon())) {
                list.set(i, hd);
                return;
            }
        }
    }

    public void delete(String id) {
        list.removeIf(h -> h.getMaHoaDon().equals(id));
    }
}
