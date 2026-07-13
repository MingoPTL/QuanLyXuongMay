package com.xuongmay.ui.dialog;

import com.xuongmay.model.HoaDon;
import com.xuongmay.model.DonHang;
import com.xuongmay.service.BanHangService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.print.PrinterJob;
import javafx.stage.Window;

public class HoaDonQrDialog extends Dialog<Boolean> {
    private final HoaDon hd;
    private final BanHangService service;

    public HoaDonQrDialog(HoaDon hd, BanHangService service, Window owner) {
        this.hd = hd;
        this.service = service;

        if (owner != null) {
            initOwner(owner);
        }

        setTitle("Thanh Toán Qua Mã QR");
        setHeaderText(null);
        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().setPrefWidth(450);

        // Styling the Dialog Pane directly to fit premium theme
        getDialogPane().setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-border-color: #cbd5e1;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);

        Label lblBankInfo = new Label("Ngân hàng: MB Bank (Ngân hàng Quân Đội)\nChủ TK: DINH VIET QUANG MINH\nSố TK: 0898478626");
        lblBankInfo.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b; -fx-alignment: center; -fx-text-alignment: center;");

        String amountStr = String.format("%.0f", hd.getTongTienHoaDon());
        Label lblAmount = new Label("Số tiền: " + String.format("%,.0f đ", hd.getTongTienHoaDon()));
        lblAmount.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #ef4444;");

        String addInfo = "Thanh toan HD " + hd.getMaHoaDon();
        Label lblInfo = new Label("Nội dung: " + addInfo);
        lblInfo.setStyle("-fx-font-style: italic; -fx-text-fill: #64748b;");

        // QR Code ImageView
        ImageView qrImageView = new ImageView();
        qrImageView.setFitWidth(250);
        qrImageView.setFitHeight(250);
        qrImageView.setPreserveRatio(true);

        String addInfoEncoded = addInfo.replace(" ", "%20");
        String qrUrl = "https://img.vietqr.io/image/MB-0898478626-print.png?amount=" 
                     + amountStr 
                     + "&addInfo=" + addInfoEncoded 
                     + "&accountName=DINH%20VIET%20QUANG%20MINH";
        
        try {
            Image qrImage = new Image(qrUrl, true);
            qrImageView.setImage(qrImage);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Horizontal box for Actions inside Dialog
        HBox dialogActions = new HBox(10);
        dialogActions.setAlignment(Pos.CENTER);

        Button btnConfirmPaid = new Button("Xác Nhận Đã Thanh Toán");
        btnConfirmPaid.setStyle("-fx-background-color: #10b981; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        Button btnPrintQr = new Button("In PDF (Mã QR)");
        btnPrintQr.setStyle("-fx-background-color: #3b82f6; -fx-text-fill: white; -fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;");

        dialogActions.getChildren().addAll(btnConfirmPaid, btnPrintQr);

        content.getChildren().addAll(lblBankInfo, lblAmount, lblInfo, qrImageView, dialogActions);
        getDialogPane().setContent(content);

        // Buttons Setup for standard Cancel / Close
        ButtonType btnTypeCancel = new ButtonType("Đóng", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(btnTypeCancel);
        
        Button btnCancel = (Button) getDialogPane().lookupButton(btnTypeCancel);
        btnCancel.setStyle(
            "-fx-background-color: #e2e8f0; -fx-text-fill: #475569;" +
            "-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;"
        );

        // Action Handlers
        btnConfirmPaid.setOnAction(e -> {
            hd.setTrangThaiHoaDon(com.xuongmay.model.TrangThaiHoaDon.DaThanhToan);
            service.updateHoaDon(hd);
            
            // Đồng bộ trạng thái thanh toán của Đơn Hàng tương ứng
            DonHang dh = hd.getDonHang();
            if (dh != null) {
                service.updateDonHang(dh);
            }
            
            setResult(true);
            close();
        });

        btnPrintQr.setOnAction(e -> {
            dialogActions.setVisible(false);
            dialogActions.setManaged(false);

            PrinterJob job = PrinterJob.createPrinterJob();
            if (job != null) {
                boolean proceed = job.showPrintDialog(getOwner());
                if (proceed) {
                    boolean success = job.printPage(content);
                    if (success) {
                        job.endJob();
                        showAlert(Alert.AlertType.INFORMATION, "In thành công", "Đã in/xuất PDF mã QR thanh toán!");
                    } else {
                        showAlert(Alert.AlertType.ERROR, "Lỗi in", "Có lỗi xảy ra khi in!");
                    }
                }
            } else {
                showAlert(Alert.AlertType.WARNING, "Không tìm thấy máy in", "Không tìm thấy máy in nào trên hệ thống!");
            }

            dialogActions.setVisible(true);
            dialogActions.setManaged(true);
        });

        setResultConverter(btn -> {
            if (btn == btnTypeCancel) {
                return false;
            }
            return null;
        });
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
