package com.xuongmay.ui.dialog;

import com.xuongmay.model.LoVai;
import com.xuongmay.model.TrangThaiLoVai;
import com.xuongmay.model.CayVai;
import com.xuongmay.service.NguyenLieuService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.List;

public class QuyTrinhLoVaiDialog extends Dialog<Void> {
    public QuyTrinhLoVaiDialog(LoVai lo, NguyenLieuService service) {
        setTitle("Quy Trình Sản Xuất Của Lô Vải");
        setHeaderText(null);
        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().setPrefWidth(520);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));

        // Header Title
        Label titleLabel = new Label("Quy Trình Lô Vải: " + lo.getTenLo());
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label subLabel = new Label("Mã lô: " + lo.getMaLo() + "   •   " + lo.getLoaiVai());
        subLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        VBox headerBox = new VBox(4);
        headerBox.getChildren().addAll(titleLabel, subLabel);
        
        Separator sep = new Separator();

        // Steps container
        VBox stepsBox = new VBox(0); // 0 spacing since we'll draw connecting lines

        int cvSize = service.getCayVaiByLoVaiId(lo.getMaLo()).size();
        TrangThaiLoVai status = lo.getTrangThaiLoVai();

        // Step 1: Nhập vải (Always Completed)
        stepsBox.getChildren().add(createStepRow(
            "1", "Nhập kho lô vải", "Đã hoàn thành",
            "Lô vải được nhập kho ngày " + lo.getNgayNhap() + " từ nhà cung cấp " + 
            (lo.getNhaCungCap() != null ? lo.getNhaCungCap().getTenNhaCungCap() : "N/A") + ".",
            true, false, false
        ));

        // Step 2: Trải vải & Chuẩn bị
        boolean step2Done = (status == TrangThaiLoVai.DangSuDung || status == TrangThaiLoVai.RaSanPham);
        String step2Status;
        String step2Desc;
        if (status == TrangThaiLoVai.ChuaSuDung) {
            step2Status = "Chưa thực hiện";
            step2Desc = "Lô vải đang chờ để được đưa vào sử dụng.";
        } else if (status == TrangThaiLoVai.DangSuDung) {
            step2Status = "Đang thực hiện";
            step2Desc = "Đang tiến hành trải vải cho sản xuất. Số cây vải trong lô: " + cvSize + " cây.";
        } else {
            step2Status = "Đã hoàn thành";
            step2Desc = "Đã hoàn thành trải toàn bộ cây vải để đưa vào cắt may.";
        }
        stepsBox.getChildren().add(createStepRow(
            "2", "Trải vải & Chuẩn bị", step2Status, step2Desc,
            step2Done, (status == TrangThaiLoVai.DangSuDung), false
        ));

        // Step 3: Cắt & May
        boolean step3Done = (status == TrangThaiLoVai.RaSanPham);
        boolean step3Active = (status == TrangThaiLoVai.DangSuDung);
        String step3Status;
        String step3Desc;
        if (status == TrangThaiLoVai.ChuaSuDung) {
            step3Status = "Chưa thực hiện";
            step3Desc = "Chờ hoàn thành bước trải vải.";
        } else if (status == TrangThaiLoVai.DangSuDung) {
            step3Status = "Đang thực hiện";
            step3Desc = "Các bộ phận cắt và may đang xử lý bán thành phẩm.";
        } else {
            step3Status = "Đã hoàn thành";
            step3Desc = "Đã hoàn thành cắt và may toàn bộ vải của lô.";
        }
        stepsBox.getChildren().add(createStepRow(
            "3", "Cắt & Sản xuất", step3Status, step3Desc,
            step3Done, step3Active, false
        ));

        // Step 4: Ra thành phẩm
        boolean step4Done = (status == TrangThaiLoVai.RaSanPham);
        String step4Status = step4Done ? "Đã hoàn thành" : "Chưa thực hiện";
        String step4Desc = step4Done 
            ? "Đã hoàn tất sản xuất. Toàn bộ sản phẩm đã được nhập kho thành phẩm." 
            : "Chờ hoàn thành công đoạn may.";
        stepsBox.getChildren().add(createStepRow(
            "4", "Ra thành phẩm", step4Status, step4Desc,
            step4Done, false, true
        ));

        root.getChildren().addAll(headerBox, sep, stepsBox);
        getDialogPane().setContent(root);

        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Button btnClose = (Button) getDialogPane().lookupButton(ButtonType.CLOSE);
        btnClose.setText("Đóng");
        btnClose.setStyle(
            "-fx-background-color: #e2e8f0;" +
            "-fx-text-fill: #475569;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 18;" +
            "-fx-background-radius: 7;"
        );
    }

    private HBox createStepRow(String stepNum, String title, String statusStr, String desc,
                               boolean isDone, boolean isActive, boolean isLast) {
        HBox row = new HBox(15);
        row.setAlignment(Pos.TOP_LEFT);

        // Indicator column (circle + connecting line)
        VBox indicatorBox = new VBox(0);
        indicatorBox.setAlignment(Pos.TOP_CENTER);
        indicatorBox.setPrefWidth(30);

        Label circle = new Label(isDone ? "✓" : stepNum);
        circle.setAlignment(Pos.CENTER);
        circle.setPrefSize(28, 28);
        circle.setMinSize(28, 28);
        circle.setMaxSize(28, 28);

        // Styling circle based on status
        if (isDone) {
            circle.setStyle(
                "-fx-background-color: #22c55e; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: 13px; -fx-background-radius: 14;"
            );
        } else if (isActive) {
            circle.setStyle(
                "-fx-background-color: #3b82f6; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 14;"
            );
        } else {
            circle.setStyle(
                "-fx-background-color: #cbd5e1; -fx-text-fill: #64748b; " +
                "-fx-font-weight: bold; -fx-font-size: 12px; -fx-background-radius: 14;"
            );
        }

        indicatorBox.getChildren().add(circle);

        if (!isLast) {
            Region line = new Region();
            line.setPrefWidth(2);
            line.setPrefHeight(45);
            if (isDone) {
                line.setStyle("-fx-background-color: #22c55e;");
            } else if (isActive) {
                line.setStyle("-fx-background-color: #3b82f6;");
            } else {
                line.setStyle("-fx-background-color: #cbd5e1;");
            }
            indicatorBox.getChildren().add(line);
        }

        // Details column
        VBox textBox = new VBox(3);
        textBox.setPadding(new Insets(2, 0, 10, 0));
        HBox.setHgrow(textBox, Priority.ALWAYS);

        HBox titleBox = new HBox(8);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: #1e293b;");

        Label badge = new Label(statusStr);
        if (isDone) {
            badge.setStyle("-fx-background-color: #dcfce7; -fx-text-fill: #15803d; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 4;");
        } else if (isActive) {
            badge.setStyle("-fx-background-color: #dbeafe; -fx-text-fill: #1d4ed8; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 4;");
        } else {
            badge.setStyle("-fx-background-color: #f1f5f9; -fx-text-fill: #64748b; -fx-font-size: 10px; -fx-font-weight: bold; -fx-padding: 2 6; -fx-background-radius: 4;");
        }

        titleBox.getChildren().addAll(titleLbl, badge);

        Label descLbl = new Label(desc);
        descLbl.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11.5px;");
        descLbl.setWrapText(true);

        textBox.getChildren().addAll(titleBox, descLbl);

        row.getChildren().addAll(indicatorBox, textBox);
        return row;
    }
}
