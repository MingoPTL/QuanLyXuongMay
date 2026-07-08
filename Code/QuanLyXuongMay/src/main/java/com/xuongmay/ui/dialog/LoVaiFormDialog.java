package com.xuongmay.ui.dialog;

import com.xuongmay.model.CayVai;
import com.xuongmay.model.LoVai;
import com.xuongmay.model.NhaCungCap;
import com.xuongmay.model.TrangThaiLoVai;
import com.xuongmay.service.NguyenLieuService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class LoVaiFormDialog extends Dialog<ButtonType> {
    private final NguyenLieuService service;
    private final LoVai lo;

    private TextField txtId, txtName, txtType, txtPrice, txtNote;
    private ComboBox<NhaCungCap> cbNcc;
    private ComboBox<TrangThaiLoVai> cbStatus;
    private DatePicker dpDate;

    private String selectedImagePath = null;
    private ImageView imgPreview;
    private Label lblImageHint;

    // Track selected CayVai for this lot
    private List<CayVai> selectedCayVais = new ArrayList<>();
    private Label lblCayVaiCount;

    public LoVaiFormDialog(LoVai lo, NguyenLieuService service) {
        this.lo = lo;
        this.service = service;

        // Pre-populate assigned CayVai if editing
        if (lo != null) {
            selectedCayVais = new ArrayList<>(service.getCayVaiByLoVaiId(lo.getMaLo()));
        }

        setTitle(lo == null ? "Thêm Lô Vải Mới" : "Chỉnh Sửa Lô Vải");
        setHeaderText(null);
        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().setPrefWidth(760);

        // === Main layout: Left (image) + Right (form) ===
        HBox mainLayout = new HBox(20);
        mainLayout.setPadding(new Insets(20));

        // ----- Left: Image Upload Panel -----
        VBox imagePanel = new VBox(12);
        imagePanel.setAlignment(Pos.CENTER);
        imagePanel.setPrefWidth(220);
        imagePanel.setMinWidth(220);
        imagePanel.setStyle(
            "-fx-background-color: linear-gradient(to bottom right, #f8fafc, #e2e8f0);" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 16;"
        );

        // Image preview area
        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(180, 180);
        imgBox.setMinSize(180, 180);
        imgBox.setMaxSize(180, 180);
        imgBox.setStyle(
            "-fx-background-color: #e2e8f0;" +
            "-fx-background-radius: 10;" +
            "-fx-border-color: #cbd5e1;" +
            "-fx-border-width: 2;" +
            "-fx-border-style: dashed;" +
            "-fx-border-radius: 10;" +
            "-fx-cursor: hand;"
        );

        imgPreview = new ImageView();
        imgPreview.setFitWidth(176);
        imgPreview.setFitHeight(176);
        imgPreview.setPreserveRatio(true);
        imgPreview.setSmooth(true);

        VBox placeholderBox = new VBox(6);
        placeholderBox.setAlignment(Pos.CENTER);
        Label iconUpload = new Label("🧵");
        iconUpload.setStyle("-fx-font-size: 48px;");
        lblImageHint = new Label("Nhấn để chọn ảnh");
        lblImageHint.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8; -fx-font-weight: bold;");
        placeholderBox.getChildren().addAll(iconUpload, lblImageHint);

        imgBox.getChildren().addAll(placeholderBox, imgPreview);

        // Set image if editing existing
        if (lo != null && lo.getHinhAnh() != null && !lo.getHinhAnh().isEmpty()) {
            File f = new File(lo.getHinhAnh());
            if (f.exists()) {
                imgPreview.setImage(new Image(f.toURI().toString()));
                placeholderBox.setVisible(false);
                selectedImagePath = lo.getHinhAnh();
            }
        }

        Button btnChooseImg = new Button("📂  Chọn ảnh vải");
        btnChooseImg.setStyle(
            "-fx-background-color: #6366f1;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 7 16;" +
            "-fx-background-radius: 7;" +
            "-fx-cursor: hand;"
        );

        Button btnClearImg = new Button("✕  Xóa ảnh");
        btnClearImg.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: #94a3b8;" +
            "-fx-font-size: 11px;" +
            "-fx-cursor: hand;"
        );
        btnClearImg.setVisible(selectedImagePath != null);

        Label lblFormats = new Label("JPG, PNG (tối đa 5MB)");
        lblFormats.setStyle("-fx-font-size: 10px; -fx-text-fill: #cbd5e1;");

        imagePanel.getChildren().addAll(imgBox, btnChooseImg, btnClearImg, lblFormats);

        imgBox.setOnMouseClicked(e -> openFileChooser(imgBox, placeholderBox, btnClearImg));
        btnChooseImg.setOnAction(e -> openFileChooser(imgBox, placeholderBox, btnClearImg));
        btnClearImg.setOnAction(e -> {
            selectedImagePath = null;
            imgPreview.setImage(null);
            placeholderBox.setVisible(true);
            btnClearImg.setVisible(false);
            lblImageHint.setText("Nhấn để chọn ảnh");
        });

        // ----- Right: Form fields -----
        VBox formPanel = new VBox(0);
        HBox.setHgrow(formPanel, Priority.ALWAYS);

        Label lblFormTitle = new Label(lo == null ? "Thông tin lô vải mới" : "Chỉnh sửa thông tin lô vải");
        lblFormTitle.setStyle("-fx-font-size: 15px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");

        Separator sep = new Separator();
        sep.setPadding(new Insets(8, 0, 12, 0));

        GridPane grid = new GridPane();
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setPadding(new Insets(4, 0, 0, 0));

        ColumnConstraints labelCol = new ColumnConstraints(90);
        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setHgrow(Priority.ALWAYS);
        fieldCol.setFillWidth(true);
        grid.getColumnConstraints().addAll(labelCol, fieldCol);

        // Fields
        txtId = styledTextField("Ví dụ: LV005");
        txtName = styledTextField("Tên lô vải");
        cbNcc = new ComboBox<>(FXCollections.observableArrayList(service.getAllNhaCungCap()));
        cbNcc.setMaxWidth(Double.MAX_VALUE);
        cbNcc.setPromptText("Chọn nhà cung cấp");
        styleComboBox(cbNcc);
        dpDate = new DatePicker(LocalDate.now());
        dpDate.setMaxWidth(Double.MAX_VALUE);
        txtType = styledTextField("Ví dụ: Thun Cotton");
        txtPrice = styledTextField("Đơn giá (VNĐ/m)");
        cbStatus = new ComboBox<>(FXCollections.observableArrayList(TrangThaiLoVai.values()));
        cbStatus.setValue(TrangThaiLoVai.ChuaSuDung);
        cbStatus.setMaxWidth(Double.MAX_VALUE);
        styleComboBox(cbStatus);
        txtNote = styledTextField("Ghi chú thêm (không bắt buộc)");

        // Grid rows
        addRow(grid, 0, "Mã lô: *", txtId);
        addRow(grid, 1, "Tên lô: *", txtName);
        addRow(grid, 2, "Nhà CC: *", cbNcc);
        addRow(grid, 3, "Ngày nhập:", dpDate);
        addRow(grid, 4, "Loại vải: *", txtType);
        addRow(grid, 5, "Giá nhập: *", txtPrice);
        addRow(grid, 6, "Trạng thái:", cbStatus);
        addRow(grid, 7, "Ghi chú:", txtNote);

        // --- Cây vải section ---
        Separator sep2 = new Separator();
        sep2.setPadding(new Insets(10, 0, 6, 0));

        HBox cayVaiHeader = new HBox(10);
        cayVaiHeader.setAlignment(Pos.CENTER_LEFT);
        cayVaiHeader.setPadding(new Insets(4, 0, 0, 0));

        lblCayVaiCount = new Label(buildCayVaiCountText());
        lblCayVaiCount.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569; -fx-font-weight: bold;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnPickCayVai = new Button("🧶  Chọn Cây Vải...");
        btnPickCayVai.setStyle(
            "-fx-background-color: #0ea5e9;" +
            "-fx-text-fill: white;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 6 13;" +
            "-fx-background-radius: 7;" +
            "-fx-cursor: hand;"
        );
        btnPickCayVai.setOnAction(e -> {
            CayVaiPickerDialog picker = new CayVaiPickerDialog(lo, service, selectedCayVais);
            picker.initOwner(getDialogPane().getScene().getWindow());
            picker.showAndWait().ifPresent(result -> {
                selectedCayVais = result;
                lblCayVaiCount.setText(buildCayVaiCountText());
            });
        });

        cayVaiHeader.getChildren().addAll(lblCayVaiCount, spacer, btnPickCayVai);

        Label lblRequired = new Label("* Trường bắt buộc");
        lblRequired.setStyle("-fx-font-size: 10px; -fx-text-fill: #94a3b8;");
        lblRequired.setPadding(new Insets(6, 0, 0, 0));

        formPanel.getChildren().addAll(lblFormTitle, sep, grid, sep2, cayVaiHeader, lblRequired);

        // Populate if editing
        if (lo != null) {
            txtId.setText(lo.getMaLo());
            txtId.setEditable(false);
            txtId.setStyle(txtId.getStyle() + "-fx-background-color: #f1f5f9;");
            txtName.setText(lo.getTenLo());
            cbNcc.setValue(lo.getNhaCungCap());
            dpDate.setValue(lo.getNgayNhap());
            txtType.setText(lo.getLoaiVai());
            txtPrice.setText(String.valueOf(lo.getGiaNhap()));
            cbStatus.setValue(lo.getTrangThaiLoVai());
            txtNote.setText(lo.getGhiChu());
        }

        mainLayout.getChildren().addAll(imagePanel, formPanel);
        getDialogPane().setContent(mainLayout);

        // Buttons
        ButtonType btnTypeSave = new ButtonType("Lưu Lô Vải", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnTypeCancel = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(btnTypeSave, btnTypeCancel);

        Button btnSave = (Button) getDialogPane().lookupButton(btnTypeSave);
        btnSave.setStyle(
            "-fx-background-color: #22c55e;" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 18;" +
            "-fx-background-radius: 7;"
        );
        Button btnCancel = (Button) getDialogPane().lookupButton(btnTypeCancel);
        btnCancel.setStyle(
            "-fx-background-color: #e2e8f0;" +
            "-fx-text-fill: #475569;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 8 18;" +
            "-fx-background-radius: 7;"
        );

        setResultConverter(btn -> btn == btnTypeSave ? btnTypeSave : null);
    }

    private String buildCayVaiCountText() {
        if (selectedCayVais.isEmpty()) {
            return "🧶  Cây vải trong lô: (chưa chọn)";
        }
        return "🧶  Cây vải trong lô: " + selectedCayVais.size() + " cây đã chọn";
    }

    private void openFileChooser(StackPane imgBox, VBox placeholder, Button btnClear) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Chọn ảnh lô vải");
        chooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.webp")
        );
        Window window = getDialogPane().getScene().getWindow();
        File file = chooser.showOpenDialog(window);
        if (file != null) {
            if (file.length() > 5 * 1024 * 1024) {
                Alert warn = new Alert(Alert.AlertType.WARNING);
                warn.setTitle("File quá lớn");
                warn.setContentText("Vui lòng chọn file ảnh nhỏ hơn 5MB!");
                warn.showAndWait();
                return;
            }
            selectedImagePath = file.getAbsolutePath();
            imgPreview.setImage(new Image(file.toURI().toString()));
            placeholder.setVisible(false);
            btnClear.setVisible(true);
            lblImageHint.setText("Đã chọn: " + file.getName());
        }
    }

    private TextField styledTextField(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setMaxWidth(Double.MAX_VALUE);
        tf.setStyle(
            "-fx-background-radius: 7;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 7;" +
            "-fx-padding: 7 10;" +
            "-fx-font-size: 12px;"
        );
        return tf;
    }

    private <T> void styleComboBox(ComboBox<T> cb) {
        cb.setStyle(
            "-fx-background-radius: 7;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 7;" +
            "-fx-font-size: 12px;"
        );
    }

    private void addRow(GridPane grid, int row, String labelText, javafx.scene.Node field) {
        Label lbl = new Label(labelText);
        lbl.setStyle("-fx-font-size: 12px; -fx-text-fill: #475569; -fx-font-weight: bold;");
        grid.add(lbl, 0, row);
        grid.add(field, 1, row);
    }

    public boolean handleSave() {
        String id = txtId.getText().trim();
        String name = txtName.getText().trim();
        NhaCungCap ncc = cbNcc.getValue();
        LocalDate importDate = dpDate.getValue();
        String fabType = txtType.getText().trim();
        String priceStr = txtPrice.getText().trim();
        String note = txtNote.getText().trim();
        TrangThaiLoVai status = cbStatus.getValue();

        if (id.isEmpty() || name.isEmpty() || ncc == null || importDate == null || fabType.isEmpty() || priceStr.isEmpty() || status == null) {
            showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng nhập đầy đủ các trường bắt buộc (*)!");
            return false;
        }

        double price;
        try {
            price = Double.parseDouble(priceStr.replace(",", "").replace(" ", ""));
            if (price <= 0) throw new NumberFormatException();
        } catch (NumberFormatException ex) {
            showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Giá nhập phải là số dương!");
            return false;
        }

        LoVai savedLo;
        if (lo == null) {
            if (service.getLoVaiById(id) != null) {
                showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã lô đã tồn tại! Vui lòng chọn mã khác.");
                return false;
            }
            savedLo = new LoVai(id, name, ncc, importDate, selectedCayVais.size(), fabType, price, note, status);
            savedLo.setHinhAnh(selectedImagePath);
            service.addLoVai(savedLo);
        } else {
            lo.setTenLo(name);
            lo.setNhaCungCap(ncc);
            lo.setNgayNhap(importDate);
            lo.setLoaiVai(fabType);
            lo.setGiaNhap(price);
            lo.setGhiChu(note);
            lo.setTrangThaiLoVai(status);
            lo.setSoLuong(selectedCayVais.size());
            lo.setHinhAnh(selectedImagePath);
            service.updateLoVai(lo);
            savedLo = lo;
        }

        // Unassign CayVais that were previously in this lot but are no longer selected
        if (lo != null) {
            List<CayVai> oldCayVais = service.getCayVaiByLoVaiId(lo.getMaLo());
            for (CayVai cv : oldCayVais) {
                boolean stillSelected = false;
                for (CayVai selected : selectedCayVais) {
                    if (selected.getTenCayVai().equals(cv.getTenCayVai())) {
                        stillSelected = true;
                        break;
                    }
                }
                if (!stillSelected) {
                    cv.setLoVai(null);
                    cv.setLuotTraiVai(0);
                    service.updateCayVai(cv);
                }
            }
        }

        // Assign selected CayVai to this lot, and force spread count to 0 if the lot status is not DangSuDung
        for (CayVai cv : selectedCayVais) {
            cv.setLoVai(savedLo);
            if (savedLo.getTrangThaiLoVai() != TrangThaiLoVai.DangSuDung) {
                cv.setLuotTraiVai(0);
            }
            service.updateCayVai(cv);
        }

        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
