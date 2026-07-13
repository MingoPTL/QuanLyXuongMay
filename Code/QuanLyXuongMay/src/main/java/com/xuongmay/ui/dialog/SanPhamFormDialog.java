package com.xuongmay.ui.dialog;

import com.xuongmay.model.SanPham;
import com.xuongmay.model.LoaiSanPham;
import com.xuongmay.model.TrangThaiSanPham;
import com.xuongmay.service.SanPhamService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;

public class SanPhamFormDialog extends Dialog<ButtonType> {
    private final SanPhamService service;
    private final SanPham sp;
    private final boolean isEdit;

    // Basic info
    private TextField txtId, txtName, txtPrice, txtNote;
    private ComboBox<LoaiSanPham> cbType;
    private ComboBox<TrangThaiSanPham> cbStatus;

    // Dự kiến
    private TextField txtSoMau, txtTongBoDuKien;
    private TextField txtRiDuKien, txtBoLeDuKien; // read-only, auto-calculated

    // Thực tế (chỉ enable khi DaHoanThanh)
    private TextField txtTongBoThucTe, txtTongRiThucTe, txtBoLeThucTe, txtRiLeThucTe;

    private String selectedImagePath = null;
    private ImageView imgPreview;
    private Label lblImageHint;

    public SanPhamFormDialog(SanPham sp, SanPhamService service) {
        this.sp = sp;
        this.service = service;
        this.isEdit = sp != null;

        setTitle(isEdit ? "Chỉnh Sửa Sản Phẩm" : "Thêm Sản Phẩm Mới");
        setHeaderText(null);
        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().setPrefWidth(820);
        getDialogPane().setPrefHeight(600);

        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(20));

        // ===== LEFT: Image panel =====
        VBox imagePanel = buildImagePanel();

        // ===== RIGHT: Form sections =====
        VBox formPanel = new VBox(16);
        HBox.setHgrow(formPanel, Priority.ALWAYS);

        Label lblFormTitle = new Label(isEdit ? "Chỉnh sửa thông tin sản phẩm" : "Thông tin sản phẩm mới");
        lblFormTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        // --- Section 1: Thông tin cơ bản ---
        VBox basicSection = buildSection("📋 Thông tin cơ bản", buildBasicGrid());

        // --- Section 2: Số lượng Dự kiến ---
        VBox expectedSection = buildSection("📊 Số lượng dự kiến", buildExpectedGrid());

        // --- Section 3: Thực tế (chỉ khi Hoàn thành) ---
        boolean isCompleted = isEdit && sp.getTrangThaiSanPham() == TrangThaiSanPham.DaHoanThanh;
        VBox actualSection = buildSection("✅ Thực tế sau sản xuất (chỉ nhập khi Hoàn thành)", buildActualGrid(isCompleted));
        actualSection.setOpacity(isCompleted ? 1.0 : 0.45);

        Label lblRequired = new Label("* Trường bắt buộc");
        lblRequired.setStyle("-fx-font-size: 10px; -fx-text-fill: #94a3b8;");

        formPanel.getChildren().addAll(lblFormTitle, basicSection, expectedSection, actualSection, lblRequired);

        ScrollPane scrollForm = new ScrollPane(formPanel);
        scrollForm.setFitToWidth(true);
        scrollForm.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollForm.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        HBox.setHgrow(scrollForm, Priority.ALWAYS);

        mainLayout.getChildren().addAll(imagePanel, scrollForm);
        getDialogPane().setContent(mainLayout);

        // Populate if editing
        if (isEdit) populateForm();

        // Setup auto-calculation listeners
        setupAutoCalc();

        // Buttons
        ButtonType btnTypeSave = new ButtonType("Lưu Sản Phẩm", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnTypeCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(btnTypeSave, btnTypeCancel);

        Button btnSave = (Button) getDialogPane().lookupButton(btnTypeSave);
        btnSave.setStyle(
            "-fx-background-color: #22c55e; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 7;"
        );
        Button btnCancel = (Button) getDialogPane().lookupButton(btnTypeCancel);
        btnCancel.setStyle(
            "-fx-background-color: #e2e8f0; -fx-text-fill: #475569;" +
            "-fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 7;"
        );

        setResultConverter(btn -> btn == btnTypeSave ? btnTypeSave : null);
    }

    // ===== IMAGE PANEL =====
    private VBox buildImagePanel() {
        VBox panel = new VBox(12);
        panel.setAlignment(Pos.CENTER);
        panel.setPrefWidth(200);
        panel.setMinWidth(200);
        panel.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #f8fafc, #e2e8f0);" +
            "-fx-background-radius: 12; -fx-padding: 16;"
        );

        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(168, 168);
        imgBox.setMinSize(168, 168);
        imgBox.setMaxSize(168, 168);
        imgBox.setStyle(
            "-fx-background-color: #e2e8f0; -fx-background-radius: 10;" +
            "-fx-border-color: #cbd5e1; -fx-border-width: 2;" +
            "-fx-border-style: dashed; -fx-border-radius: 10; -fx-cursor: hand;"
        );

        imgPreview = new ImageView();
        imgPreview.setFitWidth(164); imgPreview.setFitHeight(164);
        imgPreview.setPreserveRatio(true); imgPreview.setSmooth(true);

        VBox placeholderBox = new VBox(6);
        placeholderBox.setAlignment(Pos.CENTER);
        Label iconUpload = new Label("👕");
        iconUpload.setStyle("-fx-font-size: 42px;");
        lblImageHint = new Label("Nhấn để chọn ảnh");
        lblImageHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8; -fx-font-weight: bold;");
        placeholderBox.getChildren().addAll(iconUpload, lblImageHint);

        imgBox.getChildren().addAll(placeholderBox, imgPreview);

        if (sp != null && sp.getHinhAnh() != null && !sp.getHinhAnh().isEmpty()) {
            File f = new File(sp.getHinhAnh());
            if (f.exists()) {
                imgPreview.setImage(new Image(f.toURI().toString()));
                placeholderBox.setVisible(false);
                selectedImagePath = sp.getHinhAnh();
            }
        }

        Button btnChooseImg = new Button("📂  Chọn ảnh SP");
        btnChooseImg.setStyle(
            "-fx-background-color: #6366f1; -fx-text-fill: white;" +
            "-fx-font-size: 12px; -fx-font-weight: bold;" +
            "-fx-padding: 7 14; -fx-background-radius: 7; -fx-cursor: hand;"
        );
        Button btnClearImg = new Button("✕  Xóa ảnh");
        btnClearImg.setStyle(
            "-fx-background-color: transparent; -fx-text-fill: #94a3b8;" +
            "-fx-font-size: 11px; -fx-cursor: hand;"
        );
        btnClearImg.setVisible(selectedImagePath != null);

        imgBox.setOnMouseClicked(e -> openFileChooser(imgBox, placeholderBox, btnClearImg));
        btnChooseImg.setOnAction(e -> openFileChooser(imgBox, placeholderBox, btnClearImg));
        btnClearImg.setOnAction(e -> {
            selectedImagePath = null;
            imgPreview.setImage(null);
            placeholderBox.setVisible(true);
            btnClearImg.setVisible(false);
            lblImageHint.setText("Nhấn để chọn ảnh");
        });

        panel.getChildren().addAll(imgBox, btnChooseImg, btnClearImg);
        return panel;
    }

    // ===== BASIC GRID =====
    private GridPane buildBasicGrid() {
        GridPane grid = sectionGrid();
        txtId    = field("Ví dụ: SP004");
        txtName  = field("Tên sản phẩm");
        cbType   = new ComboBox<>(FXCollections.observableArrayList(service.getAllLoaiSanPham()));
        cbType.setMaxWidth(Double.MAX_VALUE); cbType.setPromptText("Chọn loại sản phẩm"); styleCombo(cbType);
        txtPrice = field("Giá bán thực tế (VNĐ)");
        cbStatus = new ComboBox<>(FXCollections.observableArrayList(TrangThaiSanPham.values()));
        cbStatus.setValue(TrangThaiSanPham.DangCat); cbStatus.setMaxWidth(Double.MAX_VALUE); styleCombo(cbStatus);
        txtNote  = field("Ghi chú thêm");

        addRow(grid, 0, "Mã sản phẩm: *", txtId);
        addRow(grid, 1, "Tên sản phẩm: *", txtName);
        addRow(grid, 2, "Loại SP: *", cbType);
        addRow(grid, 3, "Giá bán: *", txtPrice);
        addRow(grid, 4, "Khâu SX: *", cbStatus);
        addRow(grid, 5, "Ghi chú:", txtNote);
        return grid;
    }

    // ===== EXPECTED QUANTITY GRID =====
    private GridPane buildExpectedGrid() {
        GridPane grid = sectionGrid();

        txtSoMau         = field("Số màu sản phẩm (ví dụ: 4)");
        txtTongBoDuKien  = field("Tổng bộ dự kiến (lượt trải × số màu)");
        txtRiDuKien      = readOnlyField("Tự động = (bộ mỗi màu ÷ 4) × số màu");
        txtBoLeDuKien    = readOnlyField("Tự động = tổng bộ - tổng ri × 4");

        addRow(grid, 0, "Số màu: *", txtSoMau);
        addRow(grid, 1, "Tổng bộ DK: *", txtTongBoDuKien);
        addRow(grid, 2, "Tổng ri DK:", txtRiDuKien);
        addRow(grid, 3, "Bộ lẻ DK:", txtBoLeDuKien);
        return grid;
    }

    // ===== ACTUAL QUANTITY GRID =====
    private GridPane buildActualGrid(boolean enabled) {
        GridPane grid = sectionGrid();

        txtTongBoThucTe  = field("Tổng bộ sau khi kiểm đếm");
        txtTongRiThucTe  = field("Tổng ri thực tế");
        txtBoLeThucTe    = field("Số bộ lẻ thực tế");
        txtRiLeThucTe    = field("Số ri lẻ thực tế");

        txtTongBoThucTe.setDisable(!enabled);
        txtTongRiThucTe.setDisable(!enabled);
        txtBoLeThucTe.setDisable(!enabled);
        txtRiLeThucTe.setDisable(!enabled);

        if (!enabled) {
            String hint = "Chỉ nhập khi Đã hoàn thành";
            txtTongBoThucTe.setPromptText(hint);
            txtTongRiThucTe.setPromptText(hint);
            txtBoLeThucTe.setPromptText(hint);
            txtRiLeThucTe.setPromptText(hint);
        }

        addRow(grid, 0, "Tổng bộ TT:", txtTongBoThucTe);
        addRow(grid, 1, "Tổng ri TT:", txtTongRiThucTe);
        addRow(grid, 2, "Bộ lẻ TT:", txtBoLeThucTe);
        addRow(grid, 3, "Ri lẻ TT:", txtRiLeThucTe);
        return grid;
    }

    // ===== SECTION WRAPPER =====
    private VBox buildSection(String title, GridPane grid) {
        VBox section = new VBox(10);
        section.setStyle(
            "-fx-background-color: #f8fafc; -fx-background-radius: 10;" +
            "-fx-padding: 12; -fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1; -fx-border-radius: 10;"
        );
        Label lbl = new Label(title);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold; -fx-text-fill: #475569;");
        section.getChildren().addAll(lbl, grid);
        return section;
    }

    // ===== AUTO CALC =====
    private void setupAutoCalc() {
        Runnable calc = () -> {
            try {
                int tongBo = Integer.parseInt(txtTongBoDuKien.getText().trim());
                int mau    = Integer.parseInt(txtSoMau.getText().trim());
                if (mau > 0 && tongBo >= 0) {
                    // Công thức: ri mỗi màu = (bộ mỗi màu) / SO_BO_MOI_RI
                    //             tổng ri   = ri mỗi màu × soMau
                    int boMoiMau = tongBo / mau;
                    int riMoiMau = boMoiMau / com.xuongmay.model.SanPham.SO_BO_MOI_RI;
                    int tongRi   = riMoiMau * mau;
                    int boLe     = tongBo - tongRi * com.xuongmay.model.SanPham.SO_BO_MOI_RI;
                    txtRiDuKien.setText(String.valueOf(tongRi));
                    txtBoLeDuKien.setText(String.valueOf(boLe));
                } else {
                    txtRiDuKien.setText("—");
                    txtBoLeDuKien.setText("—");
                }
            } catch (NumberFormatException e) {
                txtRiDuKien.setText("—");
                txtBoLeDuKien.setText("—");
            }
        };
        txtTongBoDuKien.textProperty().addListener((obs, o, n) -> calc.run());
        txtSoMau.textProperty().addListener((obs, o, n) -> calc.run());
    }

    // ===== POPULATE IF EDITING =====
    private void populateForm() {
        txtId.setText(sp.getMaSanPham());
        txtId.setEditable(false);
        txtId.setStyle(txtId.getStyle() + "-fx-background-color: #f1f5f9;");
        txtName.setText(sp.getTenSanPham());
        cbType.setValue(sp.getLoaiSanPham());
        txtPrice.setText(String.valueOf(sp.getGiaThucTe()));
        cbStatus.setValue(sp.getTrangThaiSanPham());
        if (sp.getTrangThaiSanPham() == TrangThaiSanPham.DaHoanThanh) cbStatus.setDisable(true);
        txtNote.setText(sp.getGhiChu() != null ? sp.getGhiChu() : "");

        txtSoMau.setText(String.valueOf(sp.getSoMau()));
        txtTongBoDuKien.setText(String.valueOf(sp.getTongSoBoDuKien()));
        // ri and le dk auto-calculated by listener above

        if (sp.getTrangThaiSanPham() == TrangThaiSanPham.DaHoanThanh) {
            txtTongBoThucTe.setText(sp.getTongSoBoThucTe() > 0 ? String.valueOf(sp.getTongSoBoThucTe()) : "");
            txtTongRiThucTe.setText(sp.getTongSoRiThucTe() > 0 ? String.valueOf(sp.getTongSoRiThucTe()) : "");
            txtBoLeThucTe.setText(sp.getSoBoLeThucTe() > 0 ? String.valueOf(sp.getSoBoLeThucTe()) : "");
            txtRiLeThucTe.setText(sp.getSoRiLeThucTe() > 0 ? String.valueOf(sp.getSoRiLeThucTe()) : "");
        }
    }

    // ===== OPEN FILE CHOOSER =====
    private void openFileChooser(StackPane imgBox, VBox placeholder, Button btnClear) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn ảnh sản phẩm");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );
        Window window = getDialogPane().getScene().getWindow();
        File file = chooser.showOpenDialog(window);
        if (file != null) {
            if (file.length() > 5 * 1024 * 1024) {
                new Alert(Alert.AlertType.WARNING, "Vui lòng chọn file ảnh nhỏ hơn 5MB!").showAndWait();
                return;
            }
            selectedImagePath = file.getAbsolutePath();
            imgPreview.setImage(new Image(file.toURI().toString()));
            placeholder.setVisible(false);
            btnClear.setVisible(true);
            lblImageHint.setText("Đã chọn: " + file.getName());
        }
    }

    // ===== SAVE =====
    public boolean handleSave() {
        String id    = txtId.getText().trim();
        String name  = txtName.getText().trim();
        LoaiSanPham lsp = cbType.getValue();
        String priceStr = txtPrice.getText().trim();
        String boStr    = txtTongBoDuKien.getText().trim();
        String mauStr   = txtSoMau.getText().trim();
        TrangThaiSanPham status = cbStatus.getValue();
        String note = txtNote.getText().trim();

        if (id.isEmpty() || name.isEmpty() || lsp == null || priceStr.isEmpty() || boStr.isEmpty() || mauStr.isEmpty() || status == null) {
            alert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng nhập đầy đủ các trường bắt buộc (*)!");
            return false;
        }

        double price; int bo, mau;
        try {
            price = Double.parseDouble(priceStr.replace(",", "").replace(" ", ""));
            bo    = Integer.parseInt(boStr);
            mau   = Integer.parseInt(mauStr);
            if (price < 0 || bo < 0 || mau <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            alert(Alert.AlertType.ERROR, "Lỗi định dạng", "Giá bán, tổng bộ phải là số không âm. Số màu phải >= 1!");
            return false;
        }

        // Optional actual fields
        int boTT = 0, riTT = 0, boleTT = 0, rileTT = 0;
        if (status == TrangThaiSanPham.DaHoanThanh) {
            try {
                if (!txtTongBoThucTe.getText().trim().isEmpty()) boTT  = Integer.parseInt(txtTongBoThucTe.getText().trim());
                if (!txtTongRiThucTe.getText().trim().isEmpty()) riTT  = Integer.parseInt(txtTongRiThucTe.getText().trim());
                if (!txtBoLeThucTe.getText().trim().isEmpty())   boleTT = Integer.parseInt(txtBoLeThucTe.getText().trim());
                if (!txtRiLeThucTe.getText().trim().isEmpty())   rileTT = Integer.parseInt(txtRiLeThucTe.getText().trim());
            } catch (NumberFormatException ex) {
                alert(Alert.AlertType.ERROR, "Lỗi định dạng", "Số liệu thực tế phải là số nguyên!");
                return false;
            }
        }

        if (sp == null) {
            if (service.getSanPhamById(id) != null) {
                alert(Alert.AlertType.ERROR, "Lỗi", "Mã sản phẩm đã tồn tại!");
                return false;
            }
            SanPham newSp = new SanPham(id, name, price, mau, bo, note, status, lsp);
            newSp.setHinhAnh(selectedImagePath);
            newSp.setTongSoBoThucTe(boTT); newSp.setTongSoRiThucTe(riTT);
            newSp.setSoBoLeThucTe(boleTT); newSp.setSoRiLeThucTe(rileTT);
            service.addSanPham(newSp);
        } else {
            sp.setTenSanPham(name);
            sp.setLoaiSanPham(lsp);
            sp.setGiaThucTe(price);
            sp.setSoMau(mau);
            sp.setTongSoBoDuKien(bo);
            sp.setTrangThaiSanPham(status);
            sp.setGhiChu(note);
            sp.setHinhAnh(selectedImagePath);
            sp.setTongSoBoThucTe(boTT); sp.setTongSoRiThucTe(riTT);
            sp.setSoBoLeThucTe(boleTT); sp.setSoRiLeThucTe(rileTT);
            service.updateSanPham(sp);
        }
        return true;
    }

    // ===== HELPERS =====
    private GridPane sectionGrid() {
        GridPane g = new GridPane();
        g.setHgap(12); g.setVgap(8);
        ColumnConstraints lc = new ColumnConstraints(110);
        ColumnConstraints fc = new ColumnConstraints();
        fc.setHgrow(Priority.ALWAYS); fc.setFillWidth(true);
        g.getColumnConstraints().addAll(lc, fc);
        return g;
    }

    private TextField field(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMaxWidth(Double.MAX_VALUE);
        tf.setStyle("-fx-background-radius: 7; -fx-border-color: #e2e8f0; -fx-border-radius: 7; -fx-padding: 6 10; -fx-font-size: 12px;");
        return tf;
    }

    private TextField readOnlyField(String prompt) {
        TextField tf = field(prompt);
        tf.setEditable(false);
        tf.setStyle(tf.getStyle() + "-fx-background-color: #f1f5f9; -fx-text-fill: #64748b;");
        return tf;
    }

    private <T> void styleCombo(ComboBox<T> cb) {
        cb.setStyle("-fx-background-radius: 7; -fx-border-color: #e2e8f0; -fx-border-radius: 7; -fx-font-size: 12px;");
    }

    private void addRow(GridPane g, int row, String labelText, javafx.scene.Node field) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569; -fx-font-weight: bold;");
        g.add(lbl, 0, row);
        g.add(field, 1, row);
    }

    private void alert(Alert.AlertType type, String title, String content) {
        Alert a = new Alert(type);
        a.setTitle(title); a.setHeaderText(null); a.setContentText(content); a.showAndWait();
    }
}
