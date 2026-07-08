package com.xuongmay.ui.dialog;

import com.xuongmay.model.LoVai;
import com.xuongmay.service.NguyenLieuService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import java.io.File;

public class LoVaiDetailsDialog extends Dialog<Void> {
    public LoVaiDetailsDialog(LoVai lo, NguyenLieuService service) {
        setTitle("Thông Tin Chi Tiết Lô Vải");
        setHeaderText(null);
        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().setPrefWidth(560);

        HBox root = new HBox(20);
        root.setPadding(new Insets(16, 16, 12, 16));

        // Left Pane: Fabric image or placeholder
        VBox leftPane = new VBox(12);
        leftPane.setAlignment(Pos.CENTER);
        leftPane.setPrefWidth(180);
        leftPane.setMinWidth(180);
        leftPane.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #f8fafc, #e2e8f0);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 16;"
        );

        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(148, 148);
        imgBox.setMinSize(148, 148);
        imgBox.setMaxSize(148, 148);
        imgBox.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );

        // Try to load real image first
        boolean hasImage = false;
        if (lo.getHinhAnh() != null && !lo.getHinhAnh().isEmpty()) {
            File imgFile = new File(lo.getHinhAnh());
            if (imgFile.exists()) {
                ImageView imageView = new ImageView(new Image(imgFile.toURI().toString()));
                imageView.setFitWidth(144);
                imageView.setFitHeight(144);
                imageView.setPreserveRatio(true);
                imageView.setSmooth(true);
                imgBox.getChildren().add(imageView);
                hasImage = true;
            }
        }

        if (!hasImage) {
            VBox placeholderBox = new VBox(6);
            placeholderBox.setAlignment(Pos.CENTER);
            Label iconLabel = new Label("🧵");
            iconLabel.setStyle("-fx-font-size: 52px;");
            Label lblType = new Label(lo.getLoaiVai() != null ? lo.getLoaiVai() : "");
            lblType.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
            placeholderBox.getChildren().addAll(iconLabel, lblType);
            imgBox.getChildren().add(placeholderBox);
        }

        // Status badge
        Label statusBadge = new Label();
        String badgeClass;
        switch (lo.getTrangThaiLoVai()) {
            case ChuaSuDung:
                statusBadge.setText("Chưa sử dụng");
                badgeClass = "badge-orange";
                break;
            case DangSuDung:
                statusBadge.setText("Đang sử dụng");
                badgeClass = "badge-green";
                break;
            default:
                statusBadge.setText("Ra sản phẩm");
                badgeClass = "badge-green";
                break;
        }
        statusBadge.getStyleClass().addAll("fabric-card-badge", badgeClass);

        leftPane.getChildren().addAll(imgBox, statusBadge);

        // Right Pane: Info grid
        VBox rightPane = new VBox(10);
        HBox.setHgrow(rightPane, Priority.ALWAYS);
        rightPane.setAlignment(Pos.TOP_LEFT);

        Label titleLabel = new Label(lo.getTenLo());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        titleLabel.setWrapText(true);

        Label subLabel = new Label("Mã lô: " + lo.getMaLo() + "   •   " + lo.getLoaiVai());
        subLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        Separator separator = new Separator();

        // Info rows
        GridPane grid = new GridPane();
        grid.setHgap(14);
        grid.setVgap(9);

        int cvSize = service.getCayVaiByLoVaiId(lo.getMaLo()).size();
        String[][] rows = {
            {"Nhà cung cấp", lo.getNhaCungCap() != null ? lo.getNhaCungCap().getTenNhaCungCap() : "N/A"},
            {"Ngày nhập",    lo.getNgayNhap().toString()},
            {"Số cây hiện có", cvSize + " cây"},
            {"Giá nhập",    String.format("%,.0f VNĐ", lo.getGiaNhap())},
            {"Ghi chú",     lo.getGhiChu() != null && !lo.getGhiChu().isEmpty() ? lo.getGhiChu() : "(Không có)"}
        };

        for (int i = 0; i < rows.length; i++) {
            Label lbl = new Label(rows[i][0]);
            lbl.setStyle("-fx-font-weight: bold; -fx-text-fill: #64748b; -fx-font-size: 12px;");
            Label val = new Label(rows[i][1]);
            val.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 12px;");
            val.setWrapText(true);
            grid.add(lbl, 0, i);
            grid.add(val, 1, i);
        }

        rightPane.getChildren().addAll(titleLabel, subLabel, separator, grid);

        root.getChildren().addAll(leftPane, rightPane);
        getDialogPane().setContent(root);

        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Button btnClose = (Button) getDialogPane().lookupButton(ButtonType.CLOSE);
        btnClose.setText("Đóng");
        btnClose.setStyle(
            "-fx-background-color: #e2e8f0;" +
            "-fx-text-fill: #475569;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 7 16;" +
            "-fx-background-radius: 7;"
        );
    }
}
