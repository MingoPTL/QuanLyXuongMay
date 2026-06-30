package com.xuongmay.service;

import com.xuongmay.dao.KhachHangDAO;
import com.xuongmay.dao.DonHangDAO;
import com.xuongmay.dao.ChiTietDonHangDAO;
import com.xuongmay.dao.SanPhamDAO;
import com.xuongmay.dao.HoaDonDAO;
import com.xuongmay.model.*;
import java.time.LocalDate;
import java.util.List;

public class BanHangService {
    private final KhachHangDAO khDao = new KhachHangDAO();
    private final DonHangDAO dhDao = new DonHangDAO();
    private final ChiTietDonHangDAO ctDao = new ChiTietDonHangDAO();
    private final SanPhamDAO spDao = new SanPhamDAO();
    private final HoaDonDAO hdDao = new HoaDonDAO();

    // Customer operations
    public List<KhachHang> getAllKhachHang() {
        return khDao.getAll();
    }

    public KhachHang getKhachHangById(String id) {
        return khDao.getById(id);
    }

    public void addKhachHang(KhachHang kh) {
        khDao.add(kh);
    }

    public void updateKhachHang(KhachHang kh) {
        khDao.update(kh);
    }

    public void deleteKhachHang(String id) {
        khDao.delete(id);
    }

    // Sales Order operations
    public List<DonHang> getAllDonHang() {
        return dhDao.getAll();
    }

    public List<ChiTietDonHang> getChiTietByDonHangId(String donHangId) {
        return ctDao.getByDonHangId(donHangId);
    }

    /**
     * Create a sales order, insert details, and update product stock.
     */
    public void addDonHang(DonHang dh, List<ChiTietDonHang> details, PhuongThucThanhToan pt, TrangThaiHoaDon tthd) {
        dhDao.add(dh);
        double total = 0;
        for (ChiTietDonHang ct : details) {
            ct.setDonHang(dh);
            ctDao.add(ct);
            total += ct.getThanhTien();

            // Decrement inventory stock
            if (ct.getSanPham() != null) {
                SanPham sp = spDao.getById(ct.getSanPham().getMaSanPham());
                if (sp != null) {
                    sp.setTongSoBo(sp.getTongSoBo() - ct.getSoLuongRi());
                    spDao.update(sp);
                }
            }
        }
        dh.setTongTien(total);
        dhDao.update(dh);

        // Automatically create Invoice (HoaDon)
        String maHD = "HD" + dh.getMaDonHang().substring(2);
        HoaDon hd = new HoaDon(maHD, dh, LocalDate.now(), total, pt, tthd);
        hdDao.add(hd);
    }

    /**
     * Delete a sales order, remove details, refund product stock, and delete associated invoice.
     */
    public void deleteDonHang(String donHangId) {
        List<ChiTietDonHang> details = ctDao.getByDonHangId(donHangId);
        for (ChiTietDonHang ct : details) {
            // Refund stock
            if (ct.getSanPham() != null) {
                SanPham sp = spDao.getById(ct.getSanPham().getMaSanPham());
                if (sp != null) {
                    sp.setTongSoBo(sp.getTongSoBo() + ct.getSoLuongRi());
                    spDao.update(sp);
                }
            }
        }
        ctDao.deleteByDonHangId(donHangId);
        dhDao.delete(donHangId);

        // Delete associated invoice
        String maHD = "HD" + donHangId.substring(2);
        hdDao.delete(maHD);
    }

    // Invoice operations
    public List<HoaDon> getAllHoaDon() {
        return hdDao.getAll();
    }

    public void addHoaDon(HoaDon hd) {
        hdDao.add(hd);
    }

    public void updateHoaDon(HoaDon hd) {
        hdDao.update(hd);
    }

    public void deleteHoaDon(String id) {
        hdDao.delete(id);
    }
}
