package com.xuongmay.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.xuongmay.model.*;

import java.awt.Color;
import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class InvoicePdfExporter {

    // ── Color Palette ──────────────────────────────────────────────
    private static final Color COL_PRIMARY      = new Color(30,  64, 175);  // Deep Blue
    private static final Color COL_PRIMARY_DARK = new Color(15,  35, 110);  // Darker Blue
    private static final Color COL_ACCENT       = new Color(239, 68,  68);  // Red accent
    private static final Color COL_HEADER_BG    = new Color(30,  64, 175);  // Table header bg
    private static final Color COL_ROW_ALT      = new Color(241, 245, 254); // Alternating row
    private static final Color COL_TOTAL_BG     = new Color(30,  64, 175);  // Total row bg
    private static final Color COL_SECTION_BG   = new Color(248, 250, 252); // Section card bg
    private static final Color COL_BORDER       = new Color(203, 213, 225); // Border color
    private static final Color COL_TEXT_DARK    = new Color(15,  23,  42);  // Dark text
    private static final Color COL_TEXT_MED     = new Color(71,  85, 105);  // Medium text
    private static final Color COL_TEXT_LIGHT   = new Color(148, 163, 184); // Light text
    private static final Color COL_WHITE        = Color.WHITE;

    public static void exportToPdf(HoaDon hd, List<ChiTietDonHang> details, File file) throws Exception {
        // A4 with 40pt margins left/right, 30pt top/bottom
        Document doc = new Document(PageSize.A4, 40, 40, 30, 30);
        PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(file));
        doc.open();

        // ── Load Fonts ──────────────────────────────────────────────
        BaseFont bf = loadBaseFont();
        Font fCompanyName   = new Font(bf, 18, Font.BOLD,   COL_WHITE);
        Font fCompanySub    = new Font(bf, 9,  Font.NORMAL, new Color(196, 213, 255));
        Font fInvoiceTitle  = new Font(bf, 24, Font.BOLD,   COL_PRIMARY);
        Font fSectionLabel  = new Font(bf, 9,  Font.BOLD,   COL_PRIMARY);
        Font fLabel         = new Font(bf, 10, Font.BOLD,   COL_TEXT_DARK);
        Font fValue         = new Font(bf, 10, Font.NORMAL, COL_TEXT_DARK);
        Font fValueMed      = new Font(bf, 10, Font.NORMAL, COL_TEXT_MED);
        Font fNote          = new Font(bf, 9,  Font.ITALIC, COL_TEXT_LIGHT);
        Font fTableHeader   = new Font(bf, 10, Font.BOLD,   COL_WHITE);
        Font fTableBody     = new Font(bf, 10, Font.NORMAL, COL_TEXT_DARK);
        Font fTotal         = new Font(bf, 12, Font.BOLD,   COL_WHITE);
        Font fTotalLabel    = new Font(bf, 10, Font.BOLD,   COL_WHITE);
        Font fPayMethod     = new Font(bf, 10, Font.NORMAL, COL_TEXT_MED);
        Font fPayStatus     = new Font(bf, 11, Font.BOLD,   COL_ACCENT);
        Font fSigLabel      = new Font(bf, 10, Font.BOLD,   COL_TEXT_DARK);
        Font fSigSub        = new Font(bf, 9,  Font.ITALIC, COL_TEXT_MED);

        DonHang dh = hd.getDonHang();
        KhachHang kh = dh != null ? dh.getKhachHang() : null;
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        // ═══════════════════════════════════════════════════════════
        //  1. HEADER BANNER — full-width dark blue background
        // ═══════════════════════════════════════════════════════════
        PdfPTable banner = new PdfPTable(2);
        banner.setWidthPercentage(100);
        banner.setWidths(new float[]{62, 38});
        banner.setSpacingAfter(16);

        // Left: Company info
        PdfPCell cmpCell = new PdfPCell();
        cmpCell.setBackgroundColor(COL_PRIMARY_DARK);
        cmpCell.setBorder(Rectangle.NO_BORDER);
        cmpCell.setPadding(16);

        Paragraph pCmpName = new Paragraph("XƯỞNG MAY", fCompanyName);
        pCmpName.setSpacingAfter(4);
        cmpCell.addElement(pCmpName);
        cmpCell.addElement(new Paragraph("Địa chỉ: Đường số 10, KCN Tân Bình, Tân Phú, TP. HCM", fCompanySub));
        cmpCell.addElement(new Paragraph("Hotline: 0898.478.626  |  Email: contact@antigravity.vn", fCompanySub));
        banner.addCell(cmpCell);

        // Right: Invoice meta
        PdfPCell metaCell = new PdfPCell();
        metaCell.setBackgroundColor(COL_PRIMARY);
        metaCell.setBorder(Rectangle.NO_BORDER);
        metaCell.setPadding(16);
        metaCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Font fMetaKey = new Font(bf, 9,  Font.NORMAL, new Color(196, 213, 255));
        Font fMetaVal = new Font(bf, 12, Font.BOLD,   COL_WHITE);
        Font fMetaValSm = new Font(bf, 10, Font.NORMAL, COL_WHITE);

        Paragraph pInvNo = new Paragraph();
        pInvNo.add(new Chunk("Mã hóa đơn\n", fMetaKey));
        pInvNo.add(new Chunk(hd.getMaHoaDon(), fMetaVal));
        pInvNo.setAlignment(Element.ALIGN_RIGHT);
        pInvNo.setSpacingAfter(6);
        metaCell.addElement(pInvNo);

        Paragraph pInvDate = new Paragraph();
        pInvDate.add(new Chunk("Ngày lập: ", fMetaKey));
        pInvDate.add(new Chunk(hd.getNgayLap().format(dtf), fMetaValSm));
        pInvDate.setAlignment(Element.ALIGN_RIGHT);
        metaCell.addElement(pInvDate);

        banner.addCell(metaCell);
        doc.add(banner);

        // ═══════════════════════════════════════════════════════════
        //  2. INVOICE TITLE
        // ═══════════════════════════════════════════════════════════
        Paragraph title = new Paragraph("HÓA ĐƠN BÁN HÀNG", fInvoiceTitle);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(14);
        doc.add(title);

        // ═══════════════════════════════════════════════════════════
        //  3. CUSTOMER INFORMATION CARD
        // ═══════════════════════════════════════════════════════════
        // Section label
        Paragraph custLabel = new Paragraph("▌  THÔNG TIN KHÁCH HÀNG", fSectionLabel);
        custLabel.setSpacingAfter(6);
        doc.add(custLabel);

        PdfPTable custTable = new PdfPTable(4);
        custTable.setWidthPercentage(100);
        custTable.setWidths(new float[]{22, 28, 20, 30});
        custTable.setSpacingAfter(14);

        // Row 1: Name | Phone
        addInfoRow(custTable, "Người mua hàng:", kh != null ? kh.getTenKhachHang() : "—",
                              "Số điện thoại:", kh != null ? kh.getSdt() : "—",
                              fLabel, fValue, COL_SECTION_BG, COL_BORDER);
        // Row 2: Tax | Address
        addInfoRow(custTable, "Mã số thuế:", kh != null && kh.getMaSoThue() != null ? kh.getMaSoThue() : "—",
                              "Địa chỉ:", kh != null && kh.getDiaChiNha() != null ? kh.getDiaChiNha() : "—",
                              fLabel, fValue, COL_WHITE, COL_BORDER);

        // Row 3: Note (full width)
        String ghiChu = dh != null && dh.getGhiChu() != null && !dh.getGhiChu().isBlank() ? dh.getGhiChu() : "—";
        PdfPCell noteLabel = styledCell("Ghi chú:", fLabel, Element.ALIGN_LEFT, 1, COL_SECTION_BG, COL_BORDER, 6);
        PdfPCell noteVal   = new PdfPCell(new Phrase(ghiChu, fNote));
        noteVal.setColspan(3);
        noteVal.setBackgroundColor(COL_SECTION_BG);
        noteVal.setBorderColor(COL_BORDER);
        noteVal.setPadding(7);
        custTable.addCell(noteLabel);
        custTable.addCell(noteVal);

        doc.add(custTable);

        // ═══════════════════════════════════════════════════════════
        //  4. PRODUCT TABLE
        // ═══════════════════════════════════════════════════════════
        Paragraph prodLabel = new Paragraph("▌  CHI TIẾT SẢN PHẨM", fSectionLabel);
        prodLabel.setSpacingAfter(6);
        doc.add(prodLabel);

        PdfPTable prodTable = new PdfPTable(5);
        prodTable.setWidthPercentage(100);
        prodTable.setWidths(new float[]{7, 43, 13, 18, 19});
        prodTable.setSpacingAfter(12);

        // Header row
        String[] headers = {"STT", "Sản phẩm", "SL (Ri)", "Đơn giá (đ)", "Thành tiền (đ)"};
        int[] hAlign = {Element.ALIGN_CENTER, Element.ALIGN_CENTER, Element.ALIGN_CENTER, Element.ALIGN_CENTER, Element.ALIGN_CENTER};
        for (int i = 0; i < headers.length; i++) {
            PdfPCell hCell = new PdfPCell(new Phrase(headers[i], fTableHeader));
            hCell.setBackgroundColor(COL_HEADER_BG);
            hCell.setHorizontalAlignment(hAlign[i]);
            hCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            hCell.setPaddingTop(9);
            hCell.setPaddingBottom(9);
            hCell.setPaddingLeft(6);
            hCell.setPaddingRight(6);
            hCell.setBorderColor(COL_PRIMARY_DARK);
            prodTable.addCell(hCell);
        }

        // Body rows
        int stt = 1;
        for (ChiTietDonHang item : details) {
            Color rowBg = (stt % 2 == 0) ? COL_ROW_ALT : COL_WHITE;
            addProdRow(prodTable, stt++, item, fTableBody, rowBg, COL_BORDER);
        }

        // Empty filler if no items
        if (details.isEmpty()) {
            PdfPCell empty = new PdfPCell(new Phrase("(Không có sản phẩm)", fValueMed));
            empty.setColspan(5);
            empty.setHorizontalAlignment(Element.ALIGN_CENTER);
            empty.setPadding(12);
            empty.setBorderColor(COL_BORDER);
            prodTable.addCell(empty);
        }

        // ── Total row ─────────────────────────────────────────────
        PdfPCell totalSpan = new PdfPCell(new Phrase("TỔNG CỘNG THANH TOÁN", fTotalLabel));
        totalSpan.setColspan(4);
        totalSpan.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalSpan.setBackgroundColor(COL_TOTAL_BG);
        totalSpan.setBorderColor(COL_PRIMARY_DARK);
        totalSpan.setPaddingTop(10);
        totalSpan.setPaddingBottom(10);
        totalSpan.setPaddingRight(10);
        totalSpan.setPaddingLeft(6);
        prodTable.addCell(totalSpan);

        PdfPCell totalVal = new PdfPCell(new Phrase(String.format("%,.0f đ", hd.getTongTienHoaDon()), fTotal));
        totalVal.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalVal.setBackgroundColor(COL_TOTAL_BG);
        totalVal.setBorderColor(COL_PRIMARY_DARK);
        totalVal.setPaddingTop(10);
        totalVal.setPaddingBottom(10);
        totalVal.setPaddingRight(8);
        prodTable.addCell(totalVal);

        doc.add(prodTable);

        // ═══════════════════════════════════════════════════════════
        //  5. PAYMENT INFO
        // ═══════════════════════════════════════════════════════════
        PdfPTable payTable = new PdfPTable(2);
        payTable.setWidthPercentage(60);
        payTable.setHorizontalAlignment(Element.ALIGN_LEFT);
        payTable.setWidths(new float[]{40, 60});
        payTable.setSpacingAfter(20);

        addPayRow(payTable, "Phương thức T.Toán:", hd.getPhuongThucThanhToan().toString(), fLabel, fPayMethod, COL_SECTION_BG, COL_BORDER);

        String statusText = hd.getTrangThaiHoaDon().toString();
        PdfPCell sLbl = styledCell("Trạng thái:", fLabel, Element.ALIGN_LEFT, 1, COL_WHITE, COL_BORDER, 7);
        PdfPCell sVal = new PdfPCell(new Phrase(statusText, fPayStatus));
        sVal.setBackgroundColor(COL_WHITE);
        sVal.setBorderColor(COL_BORDER);
        sVal.setPadding(7);
        payTable.addCell(sLbl);
        payTable.addCell(sVal);

        doc.add(payTable);

        // ═══════════════════════════════════════════════════════════
        //  6. DIVIDER LINE
        // ═══════════════════════════════════════════════════════════
        PdfPTable divider = new PdfPTable(1);
        divider.setWidthPercentage(100);
        divider.setSpacingAfter(18);
        PdfPCell divCell = new PdfPCell(new Phrase(" "));
        divCell.setBorderWidthTop(1.5f);
        divCell.setBorderWidthBottom(0);
        divCell.setBorderWidthLeft(0);
        divCell.setBorderWidthRight(0);
        divCell.setBorderColorTop(COL_BORDER);
        divCell.setPadding(0);
        divider.addCell(divCell);
        doc.add(divider);

        // ═══════════════════════════════════════════════════════════
        //  7. SIGNATURES
        // ═══════════════════════════════════════════════════════════
        PdfPTable sigTable = new PdfPTable(2);
        sigTable.setWidthPercentage(100);
        sigTable.setWidths(new float[]{50, 50});

        // Buyer
        PdfPCell buyerCell = new PdfPCell();
        buyerCell.setBorder(Rectangle.NO_BORDER);
        buyerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph pBuyTitle = new Paragraph("Người mua hàng", fSigLabel);
        pBuyTitle.setAlignment(Element.ALIGN_CENTER);
        Paragraph pBuySub = new Paragraph("(Ký, ghi rõ họ tên)", fSigSub);
        pBuySub.setAlignment(Element.ALIGN_CENTER);
        Paragraph pBuySpace = new Paragraph("\n\n\n", fSigSub); // space for signing
        buyerCell.addElement(pBuyTitle);
        buyerCell.addElement(pBuySub);
        buyerCell.addElement(pBuySpace);
        sigTable.addCell(buyerCell);

        // Seller
        PdfPCell sellerCell = new PdfPCell();
        sellerCell.setBorder(Rectangle.NO_BORDER);
        sellerCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph pSelTitle = new Paragraph("Người lập hóa đơn", fSigLabel);
        pSelTitle.setAlignment(Element.ALIGN_CENTER);
        Paragraph pSelSub = new Paragraph("(Ký, đóng dấu)", fSigSub);
        pSelSub.setAlignment(Element.ALIGN_CENTER);
        sellerCell.addElement(pSelTitle);
        sellerCell.addElement(pSelSub);
        sellerCell.addElement(new Paragraph("\n\n\n", fSigSub));
        sigTable.addCell(sellerCell);

        doc.add(sigTable);

        // ═══════════════════════════════════════════════════════════
        //  8. FOOTER STRIP
        // ═══════════════════════════════════════════════════════════
        PdfPTable footer = new PdfPTable(1);
        footer.setWidthPercentage(100);
        PdfPCell footCell = new PdfPCell(new Phrase("Cảm ơn quý khách đã tin tưởng và sử dụng dịch vụ của XƯỞNG MAY!",
                new Font(bf, 9, Font.ITALIC, new Color(196, 213, 255))));
        footCell.setBackgroundColor(COL_PRIMARY_DARK);
        footCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        footCell.setBorder(Rectangle.NO_BORDER);
        footCell.setPaddingTop(10);
        footCell.setPaddingBottom(10);
        footer.addCell(footCell);
        doc.add(footer);

        doc.close();
    }

    // ── Helper: load font ──────────────────────────────────────────
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

    // ── Helper: info row (4 cols) ──────────────────────────────────
    private static void addInfoRow(PdfPTable table,
                                   String lbl1, String val1,
                                   String lbl2, String val2,
                                   Font fLabel, Font fValue,
                                   Color bg, Color border) {
        table.addCell(styledCell(lbl1, fLabel, Element.ALIGN_LEFT,  1, bg, border, 7));
        table.addCell(styledCell(val1, fValue, Element.ALIGN_LEFT,  1, bg, border, 7));
        table.addCell(styledCell(lbl2, fLabel, Element.ALIGN_LEFT,  1, bg, border, 7));
        table.addCell(styledCell(val2, fValue, Element.ALIGN_LEFT,  1, bg, border, 7));
    }

    // ── Helper: payment row (2 cols) ──────────────────────────────
    private static void addPayRow(PdfPTable table, String lbl, String val,
                                  Font fLabel, Font fValue, Color bg, Color border) {
        table.addCell(styledCell(lbl, fLabel, Element.ALIGN_LEFT, 1, bg, border, 7));
        table.addCell(styledCell(val, fValue, Element.ALIGN_LEFT, 1, bg, border, 7));
    }

    // ── Helper: styled cell ────────────────────────────────────────
    private static PdfPCell styledCell(String text, Font font, int hAlign,
                                       int colspan, Color bg, Color border, float padding) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setColspan(colspan);
        cell.setHorizontalAlignment(hAlign);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setBackgroundColor(bg);
        cell.setBorderColor(border);
        cell.setPadding(padding);
        return cell;
    }

    // ── Helper: product row ────────────────────────────────────────
    private static void addProdRow(PdfPTable table, int stt, ChiTietDonHang item,
                                   Font font, Color bg, Color border) {
        int[] align = {Element.ALIGN_CENTER, Element.ALIGN_LEFT, Element.ALIGN_CENTER,
                       Element.ALIGN_RIGHT, Element.ALIGN_RIGHT};
        String[] vals = {
            String.valueOf(stt),
            item.getSanPham() != null ? item.getSanPham().getTenSanPham() : "—",
            String.valueOf(item.getSoLuongRi()),
            String.format("%,.0f", item.getDonGiaRi()),
            String.format("%,.0f", item.getThanhTien())
        };
        for (int i = 0; i < vals.length; i++) {
            PdfPCell cell = new PdfPCell(new Phrase(vals[i], font));
            cell.setHorizontalAlignment(align[i]);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setBackgroundColor(bg);
            cell.setBorderColor(border);
            cell.setPaddingTop(8);
            cell.setPaddingBottom(8);
            cell.setPaddingLeft(6);
            cell.setPaddingRight(6);
            table.addCell(cell);
        }
    }

    // ── Open PDF file with default viewer ─────────────────────────
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
