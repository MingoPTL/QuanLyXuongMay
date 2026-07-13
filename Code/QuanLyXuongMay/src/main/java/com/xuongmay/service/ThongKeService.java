package com.xuongmay.service;

import com.xuongmay.dao.ChiTietDonHangDAO;
import com.xuongmay.dao.DonHangDAO;
import com.xuongmay.dao.HoaDonDAO;
import com.xuongmay.dao.SanPhamDAO;
import com.xuongmay.model.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service tổng hợp logic thống kê – tách biệt hoàn toàn khỏi UI.
 */
public class ThongKeService {

    private final HoaDonDAO hdDao           = new HoaDonDAO();
    private final DonHangDAO dhDao          = new DonHangDAO();
    private final ChiTietDonHangDAO ctDao   = new ChiTietDonHangDAO();
    private final SanPhamDAO spDao          = new SanPhamDAO();

    // ─────────────────────────────────────────────────────────────────
    // Inner result records
    // ─────────────────────────────────────────────────────────────────

    public static class SanPhamStat {
        public final String tenSanPham;
        public final int    soLuongRi;
        public final double doanhThu;
        public SanPhamStat(String ten, int sl, double dt) {
            this.tenSanPham = ten; this.soLuongRi = sl; this.doanhThu = dt;
        }
    }

    public static class KhachHangStat {
        public final String tenKhachHang;
        public final int    soDonHang;
        public final double tongTien;
        public KhachHangStat(String ten, int so, double tong) {
            this.tenKhachHang = ten; this.soDonHang = so; this.tongTien = tong;
        }
    }

    public static class CongSuatStat {
        public final String tenSanPham;
        public final int    riDuKien;
        public final int    riThucTe;
        public CongSuatStat(String ten, int dk, int tt) {
            this.tenSanPham = ten; this.riDuKien = dk; this.riThucTe = tt;
        }
    }

    // ─────────────────────────────────────────────────────────────────
    // Core metrics
    // ─────────────────────────────────────────────────────────────────

    /** Tổng doanh thu từ hóa đơn ĐÃ THANH TOÁN trong khoảng thời gian */
    public double getDoanhThu(LocalDate from, LocalDate to) {
        return hdDao.getAll().stream()
                .filter(hd -> hd.getTrangThaiHoaDon() == TrangThaiHoaDon.DaThanhToan)
                .filter(hd -> !hd.getNgayLap().isBefore(from) && !hd.getNgayLap().isAfter(to))
                .mapToDouble(HoaDon::getTongTienHoaDon)
                .sum();
    }

    /** Số đơn hàng trong khoảng thời gian */
    public long getSoDonHang(LocalDate from, LocalDate to) {
        return dhDao.getAll().stream()
                .filter(dh -> !dh.getNgayDat().isBefore(from) && !dh.getNgayDat().isAfter(to))
                .count();
    }

    /** Tổng số ri sản phẩm xuất xưởng (thực tế từ các đơn hàng trong kỳ) */
    public long getTongRiXuatXuong(LocalDate from, LocalDate to) {
        Set<String> dhIds = dhDao.getAll().stream()
                .filter(dh -> !dh.getNgayDat().isBefore(from) && !dh.getNgayDat().isAfter(to))
                .map(DonHang::getMaDonHang)
                .collect(Collectors.toSet());

        return ctDao.getAll().stream()
                .filter(ct -> ct.getDonHang() != null && dhIds.contains(ct.getDonHang().getMaDonHang()))
                .mapToLong(ChiTietDonHang::getSoLuongRi)
                .sum();
    }

    /** Tỷ lệ hóa đơn đã thanh toán trong khoảng thời gian (0.0 – 1.0) */
    public double getTiLeThanhToan(LocalDate from, LocalDate to) {
        List<HoaDon> hdList = hdDao.getAll().stream()
                .filter(hd -> !hd.getNgayLap().isBefore(from) && !hd.getNgayLap().isAfter(to))
                .collect(Collectors.toList());
        if (hdList.isEmpty()) return 0.0;
        long paid = hdList.stream()
                .filter(hd -> hd.getTrangThaiHoaDon() == TrangThaiHoaDon.DaThanhToan)
                .count();
        return (double) paid / hdList.size();
    }

    // ─────────────────────────────────────────────────────────────────
    // Chart data
    // ─────────────────────────────────────────────────────────────────

    /**
     * Doanh thu từng ngày trong khoảng from..to.
     * Trả về LinkedHashMap giữ thứ tự theo ngày.
     */
    public Map<LocalDate, Double> getDoanhThuTheoNgay(LocalDate from, LocalDate to) {
        Map<LocalDate, Double> result = new LinkedHashMap<>();
        // init all days = 0
        for (LocalDate d = from; !d.isAfter(to); d = d.plusDays(1)) {
            result.put(d, 0.0);
        }
        hdDao.getAll().stream()
                .filter(hd -> hd.getTrangThaiHoaDon() == TrangThaiHoaDon.DaThanhToan)
                .filter(hd -> !hd.getNgayLap().isBefore(from) && !hd.getNgayLap().isAfter(to))
                .forEach(hd -> result.merge(hd.getNgayLap(), hd.getTongTienHoaDon(), Double::sum));
        return result;
    }

    /**
     * Top N sản phẩm bán chạy trong kỳ (dựa trên ChiTietDonHang của các đơn trong kỳ).
     */
    public List<SanPhamStat> getTopSanPhamBanChay(LocalDate from, LocalDate to, int limit) {
        // Lấy danh sách đơn trong kỳ
        Set<String> dhIds = dhDao.getAll().stream()
                .filter(dh -> !dh.getNgayDat().isBefore(from) && !dh.getNgayDat().isAfter(to))
                .map(DonHang::getMaDonHang)
                .collect(Collectors.toSet());

        // Gom nhóm theo tên sản phẩm
        Map<String, int[]> grouped = new LinkedHashMap<>(); // key=tenSP, val=[soLuong, doanhThu*100]
        Map<String, Double> dtMap = new LinkedHashMap<>();

        ctDao.getAll().stream()
                .filter(ct -> ct.getDonHang() != null && dhIds.contains(ct.getDonHang().getMaDonHang()))
                .filter(ct -> ct.getSanPham() != null)
                .forEach(ct -> {
                    String ten = ct.getSanPham().getTenSanPham();
                    grouped.merge(ten, new int[]{ct.getSoLuongRi()}, (old, nw) -> {
                        old[0] += nw[0]; return old;
                    });
                    dtMap.merge(ten, ct.getThanhTien(), Double::sum);
                });

        return grouped.entrySet().stream()
                .map(e -> new SanPhamStat(e.getKey(), e.getValue()[0], dtMap.getOrDefault(e.getKey(), 0.0)))
                .sorted((a, b) -> Integer.compare(b.soLuongRi, a.soLuongRi))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Công suất sản xuất — so sánh dự kiến vs thực tế cho mỗi sản phẩm.
     */
    public List<CongSuatStat> getCongSuatSanXuat(int limit) {
        return spDao.getAll().stream()
                .filter(sp -> sp.getTongSoBoDuKien() > 0 || sp.getTongSoBoThucTe() > 0)
                .map(sp -> new CongSuatStat(
                        sp.getTenSanPham(),
                        sp.getTongSoRiDuKien(),
                        sp.getTongSoRiThucTe()))
                .sorted((a, b) -> Integer.compare(b.riDuKien, a.riDuKien))
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Phân loại đơn hàng theo trạng thái trong kỳ.
     */
    public Map<String, Long> getPhanLoaiDonHang(LocalDate from, LocalDate to) {
        Map<String, Long> map = new LinkedHashMap<>();
        List<DonHang> all = dhDao.getAll().stream()
                .filter(dh -> !dh.getNgayDat().isBefore(from) && !dh.getNgayDat().isAfter(to))
                .collect(Collectors.toList());
        map.put("Chưa giao", all.stream().filter(d -> d.getTrangThaiDonHang() == TrangThaiDonHang.ChuaGiao).count());
        map.put("Đã giao",   all.stream().filter(d -> d.getTrangThaiDonHang() == TrangThaiDonHang.DaGiao).count());
        return map;
    }

    /**
     * Top N khách hàng chi tiêu nhiều nhất trong kỳ.
     */
    public List<KhachHangStat> getTopKhachHang(LocalDate from, LocalDate to, int limit) {
        Map<String, double[]> grouped = new LinkedHashMap<>(); // key=tenKH, val=[soDon, tongTien]
        dhDao.getAll().stream()
                .filter(dh -> dh.getKhachHang() != null)
                .filter(dh -> !dh.getNgayDat().isBefore(from) && !dh.getNgayDat().isAfter(to))
                .forEach(dh -> {
                    String ten = dh.getKhachHang().getTenKhachHang();
                    grouped.merge(ten, new double[]{1, dh.getTongTien()}, (old, nw) -> {
                        old[0] += nw[0]; old[1] += nw[1]; return old;
                    });
                });
        return grouped.entrySet().stream()
                .map(e -> new KhachHangStat(e.getKey(), (int) e.getValue()[0], e.getValue()[1]))
                .sorted((a, b) -> Double.compare(b.tongTien, a.tongTien))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // ─────────────────────────────────────────────────────────────────
    // Date range helpers
    // ─────────────────────────────────────────────────────────────────

    public static LocalDate[] rangeOfWeek() {
        LocalDate today = LocalDate.now();
        return new LocalDate[]{today.minusDays(6), today};
    }

    public static LocalDate[] rangeOfMonth() {
        LocalDate today = LocalDate.now();
        return new LocalDate[]{today.withDayOfMonth(1), today};
    }

    public static LocalDate[] rangeOfQuarter() {
        LocalDate today = LocalDate.now();
        int m = today.getMonthValue();
        int startMonth = ((m - 1) / 3) * 3 + 1;
        LocalDate start = today.withMonth(startMonth).withDayOfMonth(1);
        return new LocalDate[]{start, today};
    }

    public static LocalDate[] rangeOfYear() {
        LocalDate today = LocalDate.now();
        return new LocalDate[]{today.withDayOfYear(1), today};
    }
}
