package com.xuongmay.ui.dialog;

import com.xuongmay.model.SanPham;
import com.xuongmay.model.TrangThaiSanPham;
import com.xuongmay.service.SanPhamService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.File;

public class SanPhamDetailsDialog extends Dialog<Void> {
    public SanPhamDetailsDialog(SanPham sp, SanPhamService service) {
        setTitle("Thông Tin Chi Tiết Sản Phẩm");
        setHeaderText(null);
        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().setPrefWidth(620);

        HBox root = new HBox(20);
        root.setPadding(new Insets(16, 16, 12, 16));

        // ===== LEFT: Image =====
        VBox leftPane = new VBox(12);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPrefWidth(180);
        leftPane.setMinWidth(180);
        leftPane.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #f8fafc, #e2e8f0);" +
            "-fx-background-radius: 12; -fx-padding: 16;"
        );

        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(148, 148);
        imgBox.setMinSize(148, 148);
        imgBox.setMaxSize(148, 148);
        imgBox.setStyle(
            "-fx-background-color: white; -fx-background-radius: 10;" +
            "-fx-border-color: #e2e8f0; -fx-border-width: 1; -fx-border-radius: 10;"
        );

        boolean hasImage = false;
        if (sp.getHinhAnh() != null && !sp.getHinhAnh().isEmpty()) {
            File imgFile = new File(sp.getHinhAnh());
            if (imgFile.exists()) {
                ImageView iv = new ImageView(new Image(imgFile.toURI().toString()));
                iv.setFitWidth(144); iv.setFitHeight(144);
                iv.setPreserveRatio(true); iv.setSmooth(true);
                imgBox.getChildren().add(iv);
                hasImage = true;
            }
        }
        if (!hasImage) {
            VBox ph = new VBox(6);
            ph.setAlignment(Pos.CENTER);
            Label icon = new Label("👕");
            icon.setStyle("-fx-font-size: 52px;");
            Label ltype = new Label(sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoai() : "");
            ltype.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
            ph.getChildren().addAll(icon, ltype);
            imgBox.getChildren().add(ph);
        }

        Label statusBadge = new Label();
        String badgeClass = "badge-orange";
        if (sp.getTrangThaiSanPham() != null) {
            statusBadge.setText(sp.getTrangThaiSanPham().toString());
            if (sp.getTrangThaiSanPham() == TrangThaiSanPham.DaHoanThanh) badgeClass = "badge-green";
        } else {
            statusBadge.setText("N/A");
        }
        statusBadge.getStyleClass().addAll("fabric-card-badge", badgeClass);
        leftPane.getChildren().addAll(imgBox, statusBadge);

        // ===== RIGHT: Info =====
        VBox rightPane = new VBox(12);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        rightPane.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label(sp.getTenSanPham());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        titleLabel.setWrapText(true);

        Label subLabel = new Label("Mã sản phẩm: " + sp.getMaSanPham() + "   •   " +
            (sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoai() : "N/A"));
        subLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        Separator sep1 = new Separator();

        // --- Basic info ---
        GridPane basicGrid = buildGrid();
        addDetailRow(basicGrid, 0, "Loại sản phẩm", sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoai() : "N/A", false);
        addDetailRow(basicGrid, 1, "Giá bán", String.format("%,.0f VNĐ", sp.getGiaThucTe()), false);
        addDetailRow(basicGrid, 2, "Khâu sản xuất", sp.getTrangThaiSanPham() != null ? sp.getTrangThaiSanPham().toString() : "N/A", false);
        addDetailRow(basicGrid, 3, "Ghi chú", sp.getGhiChu() != null && !sp.getGhiChu().isEmpty() ? sp.getGhiChu() : "(Không có)", false);

        // --- Dự kiến section ---
        Label lblDK = sectionLabel("📊 Số lượng dự kiến");
        GridPane dkGrid = buildGrid();
        addDetailRow(dkGrid, 0, "Số màu", sp.getSoMau() + " màu", false);
        addDetailRow(dkGrid, 1, "Tổng bộ DK", sp.getTongSoBoDuKien() + " bộ", false);
        addDetailRow(dkGrid, 2, "Tổng ri DK", sp.getTongSoRiDuKien() + " ri", false);
        addDetailRow(dkGrid, 3, "Bộ lẻ DK",
            sp.getSoBoLeDuKien() > 0 ? sp.getSoBoLeDuKien() + " bộ lẻ" : "Không có", sp.getSoBoLeDuKien() > 0);

        // --- Thực tế section ---
        Label lblTT = sectionLabel("✅ Thực tế sau sản xuất");
        GridPane ttGrid = buildGrid();
        boolean done = sp.getTrangThaiSanPham() == TrangThaiSanPham.DaHoanThanh;
        if (!done) {
            Label lblNotYet = new Label("Chưa có dữ liệu — Sản phẩm chưa hoàn thành");
            lblNotYet.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic; -fx-font-size: 12px;");
            VBox ttSection = buildInfoSection(lblDK, dkGrid, new Separator(), lblTT, lblNotYet);
            rightPane.getChildren().addAll(titleLabel, subLabel, sep1, basicGrid, ttSection);
        } else {
            addDetailRow(ttGrid, 0, "Tổng bộ TT", sp.getTongSoBoThucTe() > 0 ? sp.getTongSoBoThucTe() + " bộ" : "Chưa nhập", sp.getTongSoBoThucTe() == 0);
            addDetailRow(ttGrid, 1, "Tổng ri TT", sp.getTongSoRiThucTe() > 0 ? sp.getTongSoRiThucTe() + " ri" : "Chưa nhập", sp.getTongSoRiThucTe() == 0);
            addDetailRow(ttGrid, 2, "Bộ lẻ TT", sp.getSoBoLeThucTe() > 0 ? sp.getSoBoLeThucTe() + " bộ lẻ" : "Không có", false);
            addDetailRow(ttGrid, 3, "Ri lẻ TT", sp.getSoRiLeThucTe() > 0 ? sp.getSoRiLeThucTe() + " ri lẻ" : "Không có", false);
            VBox ttSection = buildInfoSection(lblDK, dkGrid, new Separator(), lblTT, ttGrid);
            rightPane.getChildren().addAll(titleLabel, subLabel, sep1, basicGrid, ttSection);
        }

        root.getChildren().addAll(leftPane, rightPane);
        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        
        getDialogPane().setContent(scroll);
        getDialogPane().setPrefHeight(600);
        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Button btnClose = (Button) getDialogPane().lookupButton(ButtonType.CLOSE);
        btnClose.setText("Đóng");
        btnClose.setStyle(
            "-fx-background-color: #e2e8f0; -fx-text-fill: #475569;" +
            "-fx-font-weight: bold; -fx-padding: 7 16; -fx-background-radius: 7;"
        );
    }

    private VBox buildInfoSection(javafx.scene.Node... nodes) {
        VBox box = new VBox(8);
        box.setStyle(
            "-fx-background-color: #f8fafc; -fx-background-radius: 10;" +
            "-fx-padding: 12; -fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1; -fx-border-radius: 10;"
        );
        box.getChildren().addAll(nodes);
        return box;
    }

    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        return l;
    }

    private GridPane buildGrid() {
        GridPane g = new GridPane();
        g.setHgap(14); g.setVgap(7);
        ColumnConstraints lc = new ColumnConstraints(120);
        ColumnConstraints vc = new ColumnConstraints();
        vc.setHgrow(Priority.ALWAYS);
        g.getColumnConstraints().addAll(lc, vc);
        return g;
    }

    private void addDetailRow(GridPane g, int row, String labelText, String value, boolean warn) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748b; -fx-font-size: 12px;");
        Label val = new Label(value);
        val.setStyle("-fx-font-size: 12px; -fx-text-fill: " + (warn ? "#ef4444" : "#1e293b") + ";");
        val.setWrapText(true);
        g.add(lbl, 0, row);
        g.add(val, 1, row);
    }
}
