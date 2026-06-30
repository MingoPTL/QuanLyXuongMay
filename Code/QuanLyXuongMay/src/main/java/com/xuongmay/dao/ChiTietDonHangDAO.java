package com.xuongmay.dao;

import com.xuongmay.model.ChiTietDonHang;
import com.xuongmay.model.DonHang;
import com.xuongmay.model.SanPham;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChiTietDonHangDAO {
    private static final List<ChiTietDonHang> list = new ArrayList<>();

    static {
        DonHangDAO dhDao = new DonHangDAO();
        SanPhamDAO spDao = new SanPhamDAO();
        List<DonHang> dhs = dhDao.getAll();
        List<SanPham> sps = spDao.getAll();
        if (dhs.size() >= 2 && sps.size() >= 2) {
            // DH001 details
            list.add(new ChiTietDonHang(dhs.get(0), sps.get(0), 20, sps.get(0).getGiaThucTe(), 20 * sps.get(0).getGiaThucTe()));
            // DH002 details
            list.add(new ChiTietDonHang(dhs.get(1), sps.get(1), 20, sps.get(1).getGiaThucTe(), 20 * sps.get(1).getGiaThucTe()));
        }
    }

    public List<ChiTietDonHang> getAll() {
        return new ArrayList<>(list);
    }

    public List<ChiTietDonHang> getByDonHangId(String donHangId) {
        return list.stream()
                .filter(ct -> ct.getDonHang() != null && ct.getDonHang().getMaDonHang().equals(donHangId))
                .collect(Collectors.toList());
    }

    public void add(ChiTietDonHang ct) {
        list.add(ct);
    }

    public void deleteByDonHangId(String donHangId) {
        list.removeIf(ct -> ct.getDonHang() != null && ct.getDonHang().getMaDonHang().equals(donHangId));
    }
}
