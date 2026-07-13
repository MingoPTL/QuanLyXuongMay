package com.xuongmay.ui.dialog;

import com.xuongmay.model.SanPham;
import com.xuongmay.model.TrangThaiSanPham;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class SanPhamProgressDialog extends Dialog<Void> {
    public SanPhamProgressDialog(SanPham sp) {
        setTitle("Tiến Độ Sản Xuất - " + sp.getTenSanPham());
        setHeaderText(null);
        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().setPrefWidth(540);

        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.TOP_CENTER);

        // Header info
        VBox header = new VBox(6);
        header.setAlignment(Pos.TOP_LEFT);
        Label lblTitle = new Label("Theo Dõi Tiến Độ");
        lblTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Label lblSubtitle = new Label("Sản phẩm: " + sp.getTenSanPham() + " (" + sp.getMaSanPham() + ")");
        lblSubtitle.setStyle("-fx-font-size: 13px; -fx-text-fill: #64748b; -fx-font-weight: bold;");

        Label lblQty = new Label("Quy mô: " + sp.getTongSoBo() + " bộ / " + sp.getTongSoRi() + " ri");
        lblQty.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        header.getChildren().addAll(lblTitle, lblSubtitle, lblQty);

        Separator sep = new Separator();

        // Workflow Progress Row
        HBox progressRow = new HBox(0);
        progressRow.setAlignment(Pos.CENTER);
        progressRow.setPadding(new Insets(15, 10, 15, 10));

        TrangThaiSanPham current = sp.getTrangThaiSanPham();
        int activeIdx = getStatusIndex(current);

        // Define steps
        String[] stepNames = {"Cắt vải", "May ráp", "Là / Ủi", "Hoàn thành"};
        
        for (int i = 0; i < stepNames.length; i++) {
            // Step node
            VBox stepNode = new VBox(8);
            stepNode.setAlignment(Pos.CENTER);

            StackPane circle = new StackPane();
            circle.setPrefSize(32, 32);
            circle.setMinSize(32, 32);

            Label lblIndicator = new Label();
            lblIndicator.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

            if (i < activeIdx) {
                // Completed
                circle.setStyle(
                    "-fx-background-color: #22c55e;" +
                    "-fx-background-radius: 16;" +
                    "-fx-effect: dropshadow(gaussian, rgba(34,197,94,0.3), 6, 0, 0, 2);"
                );
                lblIndicator.setText("✓");
                lblIndicator.setStyle(lblIndicator.getStyle() + " -fx-text-fill: white; -fx-font-size: 14px;");
            } else if (i == activeIdx) {
                // Current
                circle.setStyle(
                    "-fx-background-color: #6366f1;" +
                    "-fx-background-radius: 16;" +
                    "-fx-border-color: #a5b4fc;" +
                    "-fx-border-width: 3;" +
                    "-fx-border-radius: 16;" +
                    "-fx-effect: dropshadow(gaussian, rgba(99,102,241,0.4), 8, 0, 0, 3);"
                );
                lblIndicator.setText(String.valueOf(i + 1));
                lblIndicator.setStyle(lblIndicator.getStyle() + " -fx-text-fill: white;");
            } else {
                // Pending
                circle.setStyle(
                    "-fx-background-color: #f1f5f9;" +
                    "-fx-background-radius: 16;" +
                    "-fx-border-color: #cbd5e1;" +
                    "-fx-border-width: 1;" +
                    "-fx-border-radius: 16;"
                );
                lblIndicator.setText(String.valueOf(i + 1));
                lblIndicator.setStyle(lblIndicator.getStyle() + " -fx-text-fill: #94a3b8;");
            }

            circle.getChildren().add(lblIndicator);

            Label lblStepName = new Label(stepNames[i]);
            if (i == activeIdx) {
                lblStepName.setStyle("-fx-font-weight: bold; -fx-text-fill: #6366f1; -fx-font-size: 12px;");
            } else if (i < activeIdx) {
                lblStepName.setStyle("-fx-font-weight: bold; -fx-text-fill: #22c55e; -fx-font-size: 12px;");
            } else {
                lblStepName.setStyle("-fx-text-fill: #64748b; -fx-font-size: 12px;");
            }

            stepNode.getChildren().addAll(circle, lblStepName);
            progressRow.getChildren().add(stepNode);

            // Connector line (except last step)
            if (i < stepNames.length - 1) {
                Region line = new Region();
                line.setPrefHeight(4);
                HBox.setHgrow(line, Priority.ALWAYS);
                line.setMinWidth(40);

                if (i < activeIdx) {
                    line.setStyle("-fx-background-color: #22c55e;"); // Completed line
                } else {
                    line.setStyle("-fx-background-color: #cbd5e1;"); // Gray line
                }

                // Center the line vertically with respect to the 32px circles
                VBox lineWrapper = new VBox(line);
                lineWrapper.setAlignment(Pos.CENTER);
                lineWrapper.setPadding(new Insets(0, 0, 16, 0)); // Offset to match circle center
                HBox.setHgrow(lineWrapper, Priority.ALWAYS);

                progressRow.getChildren().add(lineWrapper);
            }
        }

        // Summary details box
        VBox detailsBox = new VBox(10);
        detailsBox.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-background-radius: 10;" +
            "-fx-padding: 16;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 10;"
        );
        detailsBox.setAlignment(Pos.CENTER_LEFT);

        Label lblStatusTitle = new Label("Trạng thái hiện tại:");
        lblStatusTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #475569;");

        Label lblStatusName = new Label();
        String desc = "";
        if (current != null) {
            lblStatusName.setText(current.toString().toUpperCase());
            switch (current) {
                case DangCat:
                    lblStatusName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #3b82f6;");
                    desc = "Sản phẩm hiện đang ở tổ cắt để cắt phôi vải theo dưỡng mẫu thiết kế.";
                    break;
                case DangMay:
                    lblStatusName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #a855f7;");
                    desc = "Sản phẩm đã cắt xong và đang trong quá trình ráp/may tại các tổ máy.";
                    break;
                case DangUi:
                    lblStatusName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #06b6d4;");
                    desc = "Sản phẩm đã may hoàn chỉnh, đang được chuyển qua khâu là/ủi phẳng và gắn nhãn mác.";
                    break;
                case DaHoanThanh:
                    lblStatusName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #22c55e;");
                    desc = "Sản phẩm đã được kiểm tra chất lượng (QC) đạt chuẩn và đã nhập kho thành phẩm thành công.";
                    break;
            }
        } else {
            lblStatusName.setText("N/A");
            lblStatusName.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #94a3b8;");
            desc = "Không xác định được trạng thái hiện tại của sản phẩm.";
        }

        Label lblStatusDesc = new Label(desc);
        lblStatusDesc.setStyle("-fx-text-fill: #1e293b; -fx-font-size: 12px;");
        lblStatusDesc.setWrapText(true);

        detailsBox.getChildren().addAll(lblStatusTitle, lblStatusName, lblStatusDesc);

        root.getChildren().addAll(header, sep, progressRow, detailsBox);
        getDialogPane().setContent(root);

        getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        Button btnClose = (Button) getDialogPane().lookupButton(ButtonType.CLOSE);
        btnClose.setText("Đóng");
        btnClose.setStyle(
            "-fx-background-color: #cbd5e1;" +
            "-fx-text-fill: #1e293b;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 7 16;" +
            "-fx-background-radius: 7;"
        );
    }

    private int getStatusIndex(TrangThaiSanPham current) {
        if (current == null) return 0;
        switch (current) {
            case DangCat: return 0;
            case DangMay: return 1;
            case DangUi: return 2;
            case DaHoanThanh: return 3;
            default: return 0;
        }
    }
}
