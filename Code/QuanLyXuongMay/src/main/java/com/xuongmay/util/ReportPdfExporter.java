package com.xuongmay.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.xuongmay.service.ThongKeService.*;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ReportPdfExporter {

    // ── Color Palette ──────────────────────────────────────────────
    private static final Color COL_PRIMARY      = new Color(30,  64, 175);  // Deep Blue
    private static final Color COL_PRIMARY_DARK = new Color(15,  35, 110);  // Darker Blue
    private static final Color COL_HEADER_BG    = new Color(30,  64, 175);  // Table header bg
    private static final Color COL_ROW_ALT      = new Color(241, 245, 254); // Alternating row
    private static final Color COL_SECTION_BG   = new Color(248, 250, 252); // Section card bg
    private static final Color COL_BORDER       = new Color(203, 213, 225); // Border color
    private static final Color COL_TEXT_DARK    = new Color(15,  23,  42);  // Dark text
    private static final Color COL_TEXT_MED     = new Color(71,  85, 105);  // Medium text
    private static final Color COL_TEXT_LIGHT   = new Color(148, 163, 184); // Light text
    private static final Color COL_WHITE        = Color.WHITE;

    public static void exportReport(String periodName, LocalDate from, LocalDate to,
                                     double revenue, long orderCount, long riCount, double payRate,
                                     List<SanPhamStat> topProducts, List<KhachHangStat> topCustomers,
                                     Map<String, Long> orderStatusMap, File file) throws Exception {
        // A4 with margins
        Document doc = new Document(PageSize.A4, 40, 40, 30, 30);
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        // ── Load Fonts ──────────────────────────────────────────────
        BaseFont bf = loadBaseFont();
        Font fTitle         = new Font(bf, 20, Font.BOLD,   COL_PRIMARY);
        Font fSubtitle      = new Font(bf, 10, Font.NORMAL, COL_TEXT_MED);
        Font fHeader        = new Font(bf, 12, Font.BOLD,   COL_PRIMARY_DARK);
        Font fTableHeader   = new Font(bf, 9,  Font.BOLD,   COL_WHITE);
        Font fTableBody     = new Font(bf, 9,  Font.NORMAL, COL_TEXT_DARK);
        Font fFooter        = new Font(bf, 8,  Font.ITALIC, COL_TEXT_LIGHT);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // 1. Header Banner — full-width dark blue background
        PdfPTable banner = new PdfPTable(1);
        banner.setWidthPercentage(100);
        banner.setSpacingAfter(20);

        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COL_PRIMARY_DARK);
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(16);

        Paragraph pCmp = new Paragraph("XƯỞNG MAY ANTIGRAVITY", new Font(bf, 15, Font.BOLD, COL_WHITE));
        pCmp.setSpacingAfter(4);
        cell.addElement(pCmp);
        cell.addElement(new Paragraph("BÁO CÁO THỐNG KÊ HOẠT ĐỘNG SẢN XUẤT & KINH DOANH", new Font(bf, 11, Font.BOLD, new Color(196, 213, 255))));
        banner.addCell(cell);
        doc.add(banner);

        // Title and period
        Paragraph titlePara = new Paragraph("BÁO CÁO THEO KỲ: " + periodName.toUpperCase(), fTitle);
        titlePara.setAlignment(Element.ALIGN_CENTER);
        titlePara.setSpacingAfter(6);
        doc.add(titlePara);

        Paragraph timePara = new Paragraph("Khoảng thời gian: từ " + from.format(dtf) + " đến " + to.format(dtf), fSubtitle);
        timePara.setAlignment(Element.ALIGN_CENTER);
        timePara.setSpacingAfter(20);
        doc.add(timePara);

        // 2. Key Metrics Card
        Paragraph kpiHeader = new Paragraph("▌  CHỈ SỐ THỰC HIỆN CHÍNH (KPI)", fHeader);
        kpiHeader.setSpacingAfter(8);
        doc.add(kpiHeader);

        PdfPTable kpiTable = new PdfPTable(4);
        kpiTable.setWidthPercentage(100);
        kpiTable.setWidths(new float[]{25, 25, 25, 25});
        kpiTable.setSpacingAfter(20);

        addKpiCell(kpiTable, "Tổng Doanh Thu", formatMoney(revenue), bf);
        addKpiCell(kpiTable, "Tổng Số Đơn Hàng", String.valueOf(orderCount), bf);
        addKpiCell(kpiTable, "Tổng Số Ri Xuất", riCount + " ri", bf);
        addKpiCell(kpiTable, "Tỷ Lệ Thanh Toán", String.format("%.0f%%", payRate * 100), bf);

        doc.add(kpiTable);

        // 3. Top Products Table
        Paragraph prodHeader = new Paragraph("▌  TOP 5 SẢN PHẨM BÁN CHẠY", fHeader);
        prodHeader.setSpacingAfter(8);
        doc.add(prodHeader);

        PdfPTable prodTable = new PdfPTable(4);
        prodTable.setWidthPercentage(100);
        prodTable.setWidths(new float[]{10, 45, 20, 25});
        prodTable.setSpacingAfter(20);

        String[] headers = {"STT", "Tên Sản Phẩm", "Số Lượng (Ri)", "Doanh Thu"};
        for (String h : headers) {
            PdfPCell hCell = new PdfPCell(new Phrase(h, fTableHeader));
            hCell.setBackgroundColor(COL_HEADER_BG);
            hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hCell.setPadding(6);
            prodTable.addCell(hCell);
        }

        int stt = 1;
        for (SanPhamStat p : topProducts) {
            Color rowBg = (stt % 2 == 0) ? COL_ROW_ALT : COL_WHITE;
            prodTable.addCell(styledCell(String.valueOf(stt), fTableBody, Element.ALIGN_CENTER, rowBg, COL_BORDER));
            prodTable.addCell(styledCell(p.tenSanPham, fTableBody, Element.ALIGN_LEFT, rowBg, COL_BORDER));
            prodTable.addCell(styledCell(String.valueOf(p.soLuongRi), fTableBody, Element.ALIGN_CENTER, rowBg, COL_BORDER));
            prodTable.addCell(styledCell(formatMoney(p.doanhThu), fTableBody, Element.ALIGN_RIGHT, rowBg, COL_BORDER));
            stt++;
        }
        if (topProducts.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("Chưa có dữ liệu sản phẩm", fTableBody));
            empty.setColspan(4);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            empty.setPadding(8);
            prodTable.addCell(empty);
        }
        doc.add(prodTable);

        // 4. Top Customers Table
        Paragraph custHeader = new Paragraph("▌  TOP 5 KHÁCH HÀNG MUA NHIỀU NHẤT", fHeader);
        custHeader.setSpacingAfter(8);
        doc.add(custHeader);

        PdfPTable custTable = new PdfPTable(4);
        custTable.setWidthPercentage(100);
        custTable.setWidths(new float[]{10, 45, 20, 25});
        custTable.setSpacingAfter(20);

        String[] headersCust = {"STT", "Tên Khách Hàng", "Số Đơn Hàng", "Tổng Chi Tiêu"};
        for (String h : headersCust) {
            PdfPCell hCell = new PdfPCell(new Phrase(h, fTableHeader));
            hCell.setBackgroundColor(COL_HEADER_BG);
            hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hCell.setPadding(6);
            custTable.addCell(hCell);
        }

        stt = 1;
        for (KhachHangStat k : topCustomers) {
            Color rowBg = (stt % 2 == 0) ? COL_ROW_ALT : COL_WHITE;
            custTable.addCell(styledCell(String.valueOf(stt), fTableBody, Element.ALIGN_CENTER, rowBg, COL_BORDER));
            custTable.addCell(styledCell(k.tenKhachHang, fTableBody, Element.ALIGN_LEFT, rowBg, COL_BORDER));
            custTable.addCell(styledCell(String.valueOf(k.soDonHang), fTableBody, Element.ALIGN_CENTER, rowBg, COL_BORDER));
            custTable.addCell(styledCell(formatMoney(k.tongTien), fTableBody, Element.ALIGN_RIGHT, rowBg, COL_BORDER));
            stt++;
        }
        if (topCustomers.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("Chưa có dữ liệu khách hàng", fTableBody));
            empty.setColspan(4);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            empty.setPadding(8);
            custTable.addCell(empty);
        }
        doc.add(custTable);

        // 5. Order Status Summary
        Paragraph statusHeader = new Paragraph("▌  TỔNG HỢP TRẠNG THÁI ĐƠN HÀNG", fHeader);
        statusHeader.setSpacingAfter(8);
        doc.add(statusHeader);

        PdfPTable statusTable = new PdfPTable(3);
        statusTable.setWidthPercentage(60);
        statusTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        statusTable.setWidths(new float[]{40, 30, 30});
        statusTable.setSpacingAfter(25);

        String[] headersStatus = {"Trạng Thái", "Số Lượng Đơn", "Tỷ Lệ %"};
        for (String h : headersStatus) {
            PdfPCell hCell = new PdfPCell(new Phrase(h, fTableHeader));
            hCell.setBackgroundColor(COL_HEADER_BG);
            hCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            hCell.setPadding(6);
            statusTable.addCell(hCell);
        }

        long totalOrders = orderStatusMap.values().stream().mapToLong(Long::longValue).sum();
        stt = 1;
        for (Map.Entry<String, Long> entry : orderStatusMap.entrySet()) {
            Color rowBg = (stt % 2 == 0) ? COL_ROW_ALT : COL_WHITE;
            double pct = totalOrders > 0 ? (double) entry.getValue() / totalOrders * 100 : 0;
            statusTable.addCell(styledCell(entry.getKey(), fTableBody, Element.ALIGN_LEFT, rowBg, COL_BORDER));
            statusTable.addCell(styledCell(String.valueOf(entry.getValue()), fTableBody, Element.ALIGN_CENTER, rowBg, COL_BORDER));
            statusTable.addCell(styledCell(String.format("%.1f%%", pct), fTableBody, Element.ALIGN_RIGHT, rowBg, COL_BORDER));
            stt++;
        }
        doc.add(statusTable);

        // Divider
        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(100);
        divider.setSpacingAfter(15);
        PdfPCell divCell = new PdfPCell(new Phrase(" "));
        divCell.setBorderWidthTop(1.0f);
        divCell.setBorderWidthBottom(0);
        divCell.setBorderWidthLeft(0);
        divCell.setBorderWidthRight(0);
        divCell.setBorderColorTop(COL_BORDER);
        divCell.setPadding(0);
        divider.addCell(divCell);
        doc.add(divider);

        // Footer info
        Paragraph footPara = new Paragraph("Báo cáo được tạo tự động bởi Hệ Thống Quản Lý Xưởng May Antigravity ngày " + LocalDate.now().format(dtf), fFooter);
        footPara.setAlignment(Element.ALIGN_CENTER);
        doc.add(footPara);

        doc.close();
    }

    private static void addKpiCell(PdfPTable table, String label, String value, BaseFont bf) {
        PdfPCell cell = new PdfPCell();
        cell.setBackgroundColor(COL_SECTION_BG);
        cell.setBorderColor(COL_BORDER);
        cell.setPadding(8);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph pLbl = new Paragraph(label.toUpperCase(), new Font(bf, 7, Font.BOLD, COL_TEXT_MED));
        pLbl.setAlignment(Element.ALIGN_CENTER);
        pLbl.setSpacingAfter(4);

        Paragraph pVal = new Paragraph(value, new Font(bf, 11, Font.BOLD, COL_PRIMARY));
        pVal.setAlignment(Element.ALIGN_CENTER);

        cell.addElement(pLbl);
        cell.addElement(pVal);
        table.addCell(cell);
    }

    private static PdfPCell styledCell(String text, Font font, int hAlign, Color bg, Color border) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(hAlign);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(bg);
        cell.setBorderColor(border);
        cell.setPadding(6);
        return cell;
    }

    private static String formatMoney(double amount) {
        if (amount >= 1_000_000_000) return String.format("%.1fB đ", amount / 1_000_000_000.0);
        if (amount >= 1_000_000)     return String.format("%.1fM đ", amount / 1_000_000.0);
        if (amount >= 1_000)         return String.format("%.0fK đ", amount / 1_000.0);
        return String.format("%,.0f đ", amount);
    }

    private static BaseFont loadBaseFont() throws Exception {
        String[] fontPaths = {
            "C:\\Windows\\Fonts\\arial.ttf",
            "C:\\Windows\\Fonts\\segoeui.ttf",
            "C:\\Windows\\Fonts\\calibri.ttf"
        };
        for (String path : fontPaths) {
            File f = new File(path);
            if (f.exists()) {
                return BaseFont.createFont(f.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
            }
        }
        return BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
    }

    public static void openPdf(File file) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
