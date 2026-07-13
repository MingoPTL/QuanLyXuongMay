package com.xuongmay.ui.dialog;

import com.xuongmay.model.SanPham;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Window;

public class SanPhamActualQtyDialog extends Dialog<Boolean> {
    private final SanPham sp;
    
    private TextField txtTongBoThucTe;
    private TextField txtTongRiThucTe;
    private TextField txtBoLeThucTe;
    private TextField txtRiLeThucTe;
    
    private boolean isAutoCalculating = false;

    public SanPhamActualQtyDialog(SanPham sp, Window owner) {
        this.sp = sp;
        
        if (owner != null) {
            initOwner(owner);
        }
        
        setTitle("Nhập Số Liệu Thực Tế");
        setHeaderText(null);
        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().setPrefWidth(480);

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

        // Header info
        Label lblTitle = new Label("Hoàn thành sản xuất: " + sp.getTenSanPham());
        lblTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #0f172a;");
        
        Label lblCode = new Label("Mã sản phẩm: " + sp.getMaSanPham());
        lblCode.setStyle("-fx-font-size: 12px; -fx-text-fill: #64748b;");

        // Expected statistics card for comparison
        VBox expectedCard = new VBox(6);
        expectedCard.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;" +
            "-fx-padding: 12;"
        );
        Label lblExpTitle = new Label("📊 Số liệu dự kiến ban đầu:");
        lblExpTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #475569;");
        
        Label lblExpDetails = new Label(
            "• Số màu: " + sp.getSoMau() + "\n" +
            "• Tổng bộ dự kiến: " + sp.getTongSoBoDuKien() + " bộ\n" +
            "• Tổng ri dự kiến: " + sp.getTongSoRiDuKien() + " ri (Bộ lẻ: " + sp.getSoBoLeDuKien() + " bộ)"
        );
        lblExpDetails.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569; -fx-line-spacing: 4px;");
        expectedCard.getChildren().addAll(lblExpTitle, lblExpDetails);

        // Input Grid
        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setAlignment(Pos.CENTER);

        txtTongBoThucTe = createStyledField("Nhập tổng bộ thực tế...");
        txtTongRiThucTe = createStyledField("Nhập tổng ri thực tế...");
        txtBoLeThucTe = createStyledField("Nhập số bộ lẻ thực tế...");
        txtRiLeThucTe = createStyledField("Nhập số ri lẻ thực tế...");

        // Pre-populate with default values
        int defaultBo = sp.getTongSoBoThucTe() > 0 ? sp.getTongSoBoThucTe() : sp.getTongSoBoDuKien();
        int defaultRi = sp.getTongSoRiThucTe() > 0 ? sp.getTongSoRiThucTe() : sp.getTongSoRiDuKien();
        int defaultBoLe = sp.getSoBoLeThucTe() > 0 ? sp.getSoBoLeThucTe() : sp.getSoBoLeDuKien();
        int defaultRiLe = sp.getSoRiLeThucTe();

        txtTongBoThucTe.setText(String.valueOf(defaultBo));
        txtTongRiThucTe.setText(String.valueOf(defaultRi));
        txtBoLeThucTe.setText(String.valueOf(defaultBoLe));
        txtRiLeThucTe.setText(String.valueOf(defaultRiLe));

        // Auto calculate suggestions based on actual bộ typed
        txtTongBoThucTe.textProperty().addListener((obs, oldVal, newVal) -> {
            if (isAutoCalculating) return;
            isAutoCalculating = true;
            try {
                String valStr = newVal.trim();
                if (!valStr.isEmpty()) {
                    int bo = Integer.parseInt(valStr);
                    if (bo >= 0 && sp.getSoMau() > 0) {
                        int ri = bo / sp.getSoMau();
                        int le = bo % sp.getSoMau();
                        txtTongRiThucTe.setText(String.valueOf(ri));
                        txtBoLeThucTe.setText(String.valueOf(le));
                    }
                }
            } catch (NumberFormatException e) {
                // Ignore parsing errors during typing
            } finally {
                isAutoCalculating = false;
            }
        });

        addGridRow(grid, 0, "Tổng bộ thực tế: *", txtTongBoThucTe);
        addGridRow(grid, 1, "Tổng ri thực tế: *", txtTongRiThucTe);
        addGridRow(grid, 2, "Số bộ lẻ thực tế:", txtBoLeThucTe);
        addGridRow(grid, 3, "Số ri lẻ thực tế:", txtRiLeThucTe);

        content.getChildren().addAll(lblTitle, lblCode, expectedCard, grid);
        getDialogPane().setContent(content);

        // Buttons Setup
        ButtonType btnTypeSave = new ButtonType("Xác Nhận Hoàn Thành", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnTypeCancel = new ButtonType("Hủy Bỏ", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(btnTypeSave, btnTypeCancel);

        Button btnSave = (Button) getDialogPane().lookupButton(btnTypeSave);
        btnSave.setStyle(
            "-fx-background-color: #22c55e; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;"
        );
        Button btnCancel = (Button) getDialogPane().lookupButton(btnTypeCancel);
        btnCancel.setStyle(
            "-fx-background-color: #e2e8f0; -fx-text-fill: #475569;" +
            "-fx-font-weight: bold; -fx-padding: 8 16; -fx-background-radius: 6; -fx-cursor: hand;"
        );

        setResultConverter(btn -> {
            if (btn == btnTypeSave) {
                if (validateAndSave()) {
                    return true;
                }
                return null; // prevent closing if validation fails
            }
            return false;
        });
    }

    private TextField createStyledField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setPrefWidth(240);
        tf.setStyle(
            "-fx-background-color: #ffffff;" +
            "-fx-border-color: #cbd5e1;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 6;" +
            "-fx-background-radius: 6;" +
            "-fx-padding: 6 10;"
        );
        return tf;
    }

    private void addGridRow(GridPane grid, int row, String labelText, Control inputControl) {
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #334155; -fx-font-size: 12px;");
        grid.add(label, 0, row);
        grid.add(inputControl, 1, row);
    }

    private boolean validateAndSave() {
        try {
            int boTT = Integer.parseInt(txtTongBoThucTe.getText().trim());
            int riTT = Integer.parseInt(txtTongRiThucTe.getText().trim());
            
            String boLeStr = txtBoLeThucTe.getText().trim();
            int boLeTT = boLeStr.isEmpty() ? 0 : Integer.parseInt(boLeStr);

            String riLeStr = txtRiLeThucTe.getText().trim();
            int riLeTT = riLeStr.isEmpty() ? 0 : Integer.parseInt(riLeStr);

            if (boTT < 0 || riTT < 0 || boLeTT < 0 || riLeTT < 0) {
                showError("Lỗi nhập liệu", "Các số lượng thực tế không được âm!");
                return false;
            }

            // Save values to SanPham object
            sp.setTongSoBoThucTe(boTT);
            sp.setTongSoRiThucTe(riTT);
            sp.setSoBoLeThucTe(boLeTT);
            sp.setSoRiLeThucTe(riLeTT);
            
            return true;
        } catch (NumberFormatException e) {
            showError("Lỗi định dạng", "Vui lòng nhập các giá trị là số nguyên hợp lệ!");
            return false;
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
