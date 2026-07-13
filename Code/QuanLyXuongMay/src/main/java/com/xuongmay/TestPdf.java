package com.xuongmay;

import com.xuongmay.model.*;
import com.xuongmay.util.InvoicePdfExporter;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TestPdf {
    public static void main(String[] args) {
        try {
            KhachHang kh = new KhachHang();
            kh.setTenKhachHang("Nguyễn Văn An");
            kh.setSdt("0909.000.111");
            kh.setMaSoThue("0314567890");
            kh.setDiaChiNha("12 Lũy Bán Bích, Tân Phú, HCM");

            DonHang dh = new DonHang();
            dh.setMaDonHang("DH001");
            dh.setKhachHang(kh);
            dh.setNgayDat(LocalDate.of(2026, 7, 10));
            dh.setTongTien(1800000.0);
            dh.setTrangThaiDonHang(TrangThaiDonHang.DaGiao);
            dh.setGhiChu("Giao hàng nhanh");

            HoaDon hd = new HoaDon();
            hd.setMaHoaDon("HD001");
            hd.setDonHang(dh);
            hd.setNgayLap(LocalDate.of(2026, 7, 10));
            hd.setTongTienHoaDon(1800000.0);
            hd.setPhuongThucThanhToan(PhuongThucThanhToan.TienMat);
            hd.setTrangThaiHoaDon(TrangThaiHoaDon.DaThanhToan);

            List<ChiTietDonHang> details = new ArrayList<>();

            SanPham sp1 = new SanPham();
            sp1.setTenSanPham("Áo thun Polo Classic");
            ChiTietDonHang c1 = new ChiTietDonHang();
            c1.setDonHang(dh); c1.setSanPham(sp1);
            c1.setSoLuongRi(20); c1.setDonGiaRi(45000); c1.setThanhTien(900000);
            details.add(c1);

            SanPham sp2 = new SanPham();
            sp2.setTenSanPham("Quần Short Basic");
            ChiTietDonHang c2 = new ChiTietDonHang();
            c2.setDonHang(dh); c2.setSanPham(sp2);
            c2.setSoLuongRi(15); c2.setDonGiaRi(60000); c2.setThanhTien(900000);
            details.add(c2);

            File pdfFile = new File("HoaDon_Demo.pdf");
            InvoicePdfExporter.exportToPdf(hd, details, pdfFile);
            System.out.println("✓ Generated: " + pdfFile.getAbsolutePath());
            InvoicePdfExporter.openPdf(pdfFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
