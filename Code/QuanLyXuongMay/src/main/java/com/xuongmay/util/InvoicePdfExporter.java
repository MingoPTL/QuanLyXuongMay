package com.xuongmay.util;

import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.Element;
import com.lowagie.text.PageSize;
import com.xuongmay.model.HoaDon;
import com.xuongmay.model.DonHang;
import com.xuongmay.model.ChiTietDonHang;
import com.xuongmay.model.KhachHang;

import java.awt.Desktop;
import java.io.File;
import java.io.FileOutputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.awt.Color;

public class InvoicePdfExporter {

    public static void exportToPdf(HoaDon hd, List<ChiTietDonHang> details, File file) throws Exception {
        Document document = new Document(PageSize.A4, 36, 36, 54, 36);
        PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        // Load Font supporting Vietnamese
        BaseFont bf;
        File f = new File("C:\\Windows\\Fonts\\arial.ttf");
        if (f.exists()) {
            bf = BaseFont.createFont(f.getAbsolutePath(), BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
        } else {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        }

        Font fontCompany = new Font(bf, 16, Font.BOLD, new Color(30, 41, 59));
        Font fontCompanySub = new Font(bf, 10, Font.NORMAL, new Color(71, 85, 105));
        Font fontTitle = new Font(bf, 22, Font.BOLD, new Color(37, 99, 235));
        Font fontHeaderCell = new Font(bf, 11, Font.BOLD, Color.WHITE);
        Font fontBold = new Font(bf, 11, Font.BOLD, new Color(15, 23, 42));
        Font fontNormal = new Font(bf, 11, Font.NORMAL, new Color(51, 65, 85));
        Font fontItalic = new Font(bf, 10, Font.ITALIC, new Color(100, 116, 139));
        Font fontFooter = new Font(bf, 11, Font.BOLD, new Color(15, 23, 42));

        // Company Header Table
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{60, 40});

        PdfPCell compCell = new PdfPCell();
        compCell.setBorder(PdfPCell.NO_BORDER);
        compCell.addElement(new Paragraph("XƯỞNG MAY ANTIGRAVITY", fontCompany));
        compCell.addElement(new Paragraph("Địa chỉ: Đường số 10, KCN Tân Bình, Tân Phú, TP. HCM", fontCompanySub));
        compCell.addElement(new Paragraph("Hotline: 0898.478.626 | Email: contact@antigravity.vn", fontCompanySub));
        headerTable.addCell(compCell);

        PdfPCell invMetaCell = new PdfPCell();
        invMetaCell.setBorder(PdfPCell.NO_BORDER);
        invMetaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph pId = new Paragraph("Mã HĐ: " + hd.getMaHoaDon(), fontBold);
        pId.setAlignment(Element.ALIGN_RIGHT);
        Paragraph pDate = new Paragraph("Ngày lập: " + hd.getNgayLap().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")), fontNormal);
        pDate.setAlignment(Element.ALIGN_RIGHT);
        invMetaCell.addElement(pId);
        invMetaCell.addElement(pDate);
        headerTable.addCell(invMetaCell);

        headerTable.setComplete(true);
        document.add(headerTable);
        document.add(new Paragraph("\n"));

        // Invoice Title
        Paragraph pTitle = new Paragraph("HÓA ĐƠN BÁN HÀNG", fontTitle);
        pTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(pTitle);
        document.add(new Paragraph("\n"));

        // Customer Information Card (styled with table border)
        PdfPTable custTable = new PdfPTable(2);
        custTable.setWidthPercentage(100);
        custTable.setWidths(new float[]{50, 50});

        DonHang dh = hd.getDonHang();
        KhachHang kh = dh != null ? dh.getKhachHang() : null;

        addCustCell(custTable, "Người mua hàng: " + (kh != null ? kh.getTenKhachHang() : "N/A"), fontNormal);
        addCustCell(custTable, "Số điện thoại: " + (kh != null ? kh.getSdt() : "N/A"), fontNormal);
        addCustCell(custTable, "Mã số thuế: " + (kh != null && kh.getMaSoThue() != null ? kh.getMaSoThue() : ""), fontNormal);
        addCustCell(custTable, "Địa chỉ: " + (kh != null && kh.getDiaChiNha() != null ? kh.getDiaChiNha() : ""), fontNormal);
        
        PdfPCell noteCell = new PdfPCell();
        noteCell.setColspan(2);
        noteCell.setBorder(PdfPCell.NO_BORDER);
        noteCell.setPadding(4);
        noteCell.addElement(new Paragraph("Ghi chú đơn hàng: " + (dh != null && dh.getGhiChu() != null ? dh.getGhiChu() : ""), fontItalic));
        custTable.addCell(noteCell);

        custTable.setComplete(true);
        document.add(custTable);
        document.add(new Paragraph("\n"));

        // Product Items Table
        PdfPTable itemsTable = new PdfPTable(5);
        itemsTable.setWidthPercentage(100);
        itemsTable.setWidths(new float[]{8, 42, 15, 15, 20});

        // Header cells
        addHeaderCell(itemsTable, "STT", fontHeaderCell);
        addHeaderCell(itemsTable, "Sản phẩm", fontHeaderCell);
        addHeaderCell(itemsTable, "SL (Ri)", fontHeaderCell);
        addHeaderCell(itemsTable, "Đơn giá (đ)", fontHeaderCell);
        addHeaderCell(itemsTable, "Thành tiền (đ)", fontHeaderCell);

        int stt = 1;
        for (ChiTietDonHang item : details) {
            addBodyCell(itemsTable, String.valueOf(stt++), fontNormal, Element.ALIGN_CENTER);
            addBodyCell(itemsTable, item.getSanPham().getTenSanPham(), fontNormal, Element.ALIGN_LEFT);
            addBodyCell(itemsTable, String.valueOf(item.getSoLuongRi()), fontNormal, Element.ALIGN_CENTER);
            addBodyCell(itemsTable, String.format("%,.0f", item.getDonGiaRi()), fontNormal, Element.ALIGN_RIGHT);
            addBodyCell(itemsTable, String.format("%,.0f", item.getThanhTien()), fontNormal, Element.ALIGN_RIGHT);
        }

        // Total Summary rows
        PdfPCell totalLabelCell = new PdfPCell(new Phrase("Tổng cộng thanh toán:", fontBold));
        totalLabelCell.setColspan(4);
        totalLabelCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalLabelCell.setPadding(8);
        totalLabelCell.setBackgroundColor(new Color(248, 250, 252));
        totalLabelCell.setBorderColor(new Color(226, 230, 235));
        itemsTable.addCell(totalLabelCell);

        PdfPCell totalValCell = new PdfPCell(new Phrase(String.format("%,.0f đ", hd.getTongTienHoaDon()), fontBold));
        totalValCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalValCell.setPadding(8);
        totalValCell.setBackgroundColor(new Color(248, 250, 252));
        totalValCell.setBorderColor(new Color(226, 230, 235));
        itemsTable.addCell(totalValCell);

        itemsTable.setComplete(true);
        document.add(itemsTable);
        document.add(new Paragraph("\n"));

        // Payment Info
        Paragraph pPayMethod = new Paragraph("Phương thức thanh toán: " + hd.getPhuongThucThanhToan().toString(), fontNormal);
        Paragraph pPayStatus = new Paragraph("Trạng thái thanh toán: " + hd.getTrangThaiHoaDon().toString(), fontBold);
        document.add(pPayMethod);
        document.add(pPayStatus);
        document.add(new Paragraph("\n\n"));

        // Signatures
        PdfPTable sigTable = new PdfPTable(2);
        sigTable.setWidthPercentage(100);
        sigTable.setWidths(new float[]{50, 50});

        PdfPCell buyerSig = new PdfPCell();
        buyerSig.setBorder(PdfPCell.NO_BORDER);
        buyerSig.setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph pBuyer = new Paragraph("Người mua hàng", fontFooter);
        pBuyer.setAlignment(Element.ALIGN_CENTER);
        Paragraph pBuyerSub = new Paragraph("(Ký, ghi rõ họ tên)", fontItalic);
        pBuyerSub.setAlignment(Element.ALIGN_CENTER);
        buyerSig.addElement(pBuyer);
        buyerSig.addElement(pBuyerSub);
        sigTable.addCell(buyerSig);

        PdfPCell sellerSig = new PdfPCell();
        sellerSig.setBorder(PdfPCell.NO_BORDER);
        sellerSig.setHorizontalAlignment(Element.ALIGN_CENTER);
        Paragraph pSeller = new Paragraph("Người lập hóa đơn", fontFooter);
        pSeller.setAlignment(Element.ALIGN_CENTER);
        Paragraph pSellerSub = new Paragraph("(Ký, đóng dấu)", fontItalic);
        pSellerSub.setAlignment(Element.ALIGN_CENTER);
        sellerSig.addElement(pSeller);
        sellerSig.addElement(pSellerSub);
        sigTable.addCell(sellerSig);

        sigTable.setComplete(true);
        document.add(sigTable);

        document.close();
    }

    private static void addCustCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBorder(PdfPCell.NO_BORDER);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private static void addHeaderCell(PdfPTable table, String text, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setBackgroundColor(new Color(37, 99, 235)); // Primary Blue
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(8);
        cell.setBorderColor(new Color(29, 78, 216));
        table.addCell(cell);
    }

    private static void addBodyCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(6);
        cell.setBorderColor(new Color(226, 230, 235));
        table.addCell(cell);
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
