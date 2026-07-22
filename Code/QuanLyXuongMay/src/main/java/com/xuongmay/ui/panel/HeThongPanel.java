package com.xuongmay.ui.panel;

import com.xuongmay.model.LoaiSanPham;
import com.xuongmay.model.TaiKhoan;
import com.xuongmay.model.ChucVu;
import com.xuongmay.model.TrangThaiTaiKhoan;
import com.xuongmay.service.SanPhamService;
import com.xuongmay.dao.TaiKhoanDAO;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.List;

public class HeThongPanel extends VBox {
    private final SanPhamService spService = new SanPhamService();
    private final TaiKhoanDAO tkDao = new TaiKhoanDAO();

    // Tab 1: Product Types (Category pricing)
    private TableView<LoaiSanPham> tableLsp;
    private TextField txtLspId, txtLspName, txtLspDesc, txtLspBasePrice, txtLspNote;
    private LoaiSanPham selectedLsp;

    // Tab 2: User Accounts
    private TableView<TaiKhoan> tableTk;
    private TextField txtTkId, txtTkUsername, txtTkPassword;
    private ComboBox<ChucVu> comboTkRole;
    private ComboBox<TrangThaiTaiKhoan> comboTkStatus;
    private DatePicker pickerTkDate;
    private TaiKhoan selectedTk;

    public HeThongPanel() {
        setSpacing(15);
        setPadding(new Insets(15));
        VBox.setVgrow(this, Priority.ALWAYS);

        Label lblTitle = new Label("Cấu Hình Hệ Thống & Danh Mục");
        lblTitle.getStyleClass().add("tab-title");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab tabLsp = new Tab("Giá Loại Sản Phẩm", createLspView());
        Tab tabUser = new Tab("Tài Khoản Hệ Thống", createAccountView());

        tabPane.getTabs().addAll(tabLsp, tabUser);
        getChildren().addAll(lblTitle, tabPane);
    }

    private Node createLspView() {
        HBox root = new HBox(20);
        root.setPadding(new Insets(15));
        HBox.setHgrow(root, Priority.ALWAYS);

        // Table (Left)
        VBox tableBox = new VBox(10);
        HBox.setHgrow(tableBox, Priority.ALWAYS);

        tableLsp = new TableView<>();
        VBox.setVgrow(tableLsp, Priority.ALWAYS);

        TableColumn<LoaiSanPham, String> colId = new TableColumn<>("Mã Loại");
        colId.setCellValueFactory(new PropertyValueFactory<>("maLoai"));
        colId.setPrefWidth(90);

        TableColumn<LoaiSanPham, String> colName = new TableColumn<>("Tên Loại SP");
        colName.setCellValueFactory(new PropertyValueFactory<>("tenLoai"));
        colName.setPrefWidth(160);

        TableColumn<LoaiSanPham, String> colDesc = new TableColumn<>("Mô Tả");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        colDesc.setPrefWidth(220);

        TableColumn<LoaiSanPham, Double> colBasePrice = new TableColumn<>("Giá Gốc (Hệ Thống)");
        colBasePrice.setCellValueFactory(new PropertyValueFactory<>("giaGoc"));
        colBasePrice.setPrefWidth(140);

        tableLsp.getColumns().addAll(colId, colName, colDesc, colBasePrice);
        tableBox.getChildren().addAll(new Label("Danh mục Loại sản phẩm & Giá gốc cơ bản"), tableLsp);

        // Form (Right)
        VBox formBox = new VBox(12);
        formBox.setPrefWidth(320);
        formBox.setMinWidth(320);
        formBox.getStyleClass().add("card-panel");

        Label lblFormTitle = new Label("Cập Nhật Loại Sản Phẩm");
        lblFormTitle.getStyleClass().add("sub-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints(90);
        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints(180);
        grid.getColumnConstraints().addAll(col1, col2);

        txtLspId = new TextField();
        txtLspName = new TextField();
        txtLspDesc = new TextField();
        txtLspBasePrice = new TextField();
        txtLspNote = new TextField();

        grid.add(new Label("Mã loại:"), 0, 0);
        grid.add(txtLspId, 1, 0);
        grid.add(new Label("Tên loại:"), 0, 1);
        grid.add(txtLspName, 1, 1);
        grid.add(new Label("Mô tả:"), 0, 2);
        grid.add(txtLspDesc, 1, 2);
        grid.add(new Label("Giá gốc:"), 0, 3);
        grid.add(txtLspBasePrice, 1, 3);
        grid.add(new Label("Ghi chú:"), 0, 4);
        grid.add(txtLspNote, 1, 4);

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        Button btnSave = new Button("Lưu");
        btnSave.getStyleClass().addAll("btn", "btn-primary");
        Button btnClear = new Button("Làm mới");
        btnClear.getStyleClass().addAll("btn", "btn-secondary");

        btnBox.getChildren().addAll(btnSave, btnClear);
        formBox.getChildren().addAll(lblFormTitle, grid, btnBox);

        root.getChildren().addAll(tableBox, formBox);

        // Events
        tableLsp.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedLsp = newVal;
                txtLspId.setText(newVal.getMaLoai());
                txtLspId.setEditable(false);
                txtLspName.setText(newVal.getTenLoai());
                txtLspDesc.setText(newVal.getMoTa());
                txtLspBasePrice.setText(String.valueOf(newVal.getGiaGoc()));
                txtLspNote.setText(newVal.getGhiChu());
            }
        });

        btnSave.setOnAction(e -> {
            String id = txtLspId.getText().trim();
            String name = txtLspName.getText().trim();
            String desc = txtLspDesc.getText().trim();
            String basePriceStr = txtLspBasePrice.getText().trim();
            String note = txtLspNote.getText().trim();

            if (id.isEmpty() || name.isEmpty() || basePriceStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng nhập đầy đủ thông tin loại sản phẩm!");
                return;
            }

            double basePrice;
            try {
                basePrice = Double.parseDouble(basePriceStr);
                if (basePrice < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Giá gốc phải là số dương!");
                return;
            }

            if (selectedLsp == null) {
                if (spService.getAllLoaiSanPham().stream().anyMatch(l -> l.getMaLoai().equals(id))) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã loại sản phẩm đã tồn tại!");
                    return;
                }
                LoaiSanPham lsp = new LoaiSanPham(id, name, desc, basePrice, note);
                spService.addLoaiSanPham(lsp);
            } else {
                selectedLsp.setTenLoai(name);
                selectedLsp.setMoTa(desc);
                selectedLsp.setGiaGoc(basePrice);
                selectedLsp.setGhiChu(note);
                spService.updateLoaiSanPham(selectedLsp);
            }
            refreshLsp();
            clearLspForm();
        });

        btnClear.setOnAction(e -> clearLspForm());

        refreshLsp();
        return root;
    }

    private Node createAccountView() {
        HBox root = new HBox(20);
        root.setPadding(new Insets(15));
        HBox.setHgrow(root, Priority.ALWAYS);

        // Table (Left)
        VBox tableBox = new VBox(10);
        HBox.setHgrow(tableBox, Priority.ALWAYS);

        tableTk = new TableView<>();
        VBox.setVgrow(tableTk, Priority.ALWAYS);

        TableColumn<TaiKhoan, String> colId = new TableColumn<>("Mã TK");
        colId.setCellValueFactory(new PropertyValueFactory<>("maTaiKhoan"));
        colId.setPrefWidth(90);

        TableColumn<TaiKhoan, String> colUser = new TableColumn<>("Tên Đăng Nhập");
        colUser.setCellValueFactory(new PropertyValueFactory<>("tenDangNhap"));
        colUser.setPrefWidth(130);

        TableColumn<TaiKhoan, String> colRole = new TableColumn<>("Chức Vụ");
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChucVu().toString()));
        colRole.setPrefWidth(120);

        TableColumn<TaiKhoan, String> colStatus = new TableColumn<>("Trạng Thái");
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrangThaiTaiKhoan().toString()));
        colStatus.setPrefWidth(130);

        tableTk.getColumns().addAll(colId, colUser, colRole, colStatus);
        tableBox.getChildren().addAll(new Label("Danh sách tài khoản nhân viên quản trị"), tableTk);

        // Form (Right)
        VBox formBox = new VBox(12);
        formBox.setPrefWidth(320);
        formBox.setMinWidth(320);
        formBox.getStyleClass().add("card-panel");

        Label lblFormTitle = new Label("Quản Lý Tài Khoản");
        lblFormTitle.getStyleClass().add("sub-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints(90);
        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints(180);
        grid.getColumnConstraints().addAll(col1, col2);

        txtTkId = new TextField();
        txtTkUsername = new TextField();
        txtTkPassword = new TextField();
        comboTkRole = new ComboBox<>(FXCollections.observableArrayList(ChucVu.values()));
        comboTkStatus = new ComboBox<>(FXCollections.observableArrayList(TrangThaiTaiKhoan.values()));
        pickerTkDate = new DatePicker(LocalDate.now());

        grid.add(new Label("Mã TK:"), 0, 0);
        grid.add(txtTkId, 1, 0);
        grid.add(new Label("Username:"), 0, 1);
        grid.add(txtTkUsername, 1, 1);
        grid.add(new Label("Password:"), 0, 2);
        grid.add(txtTkPassword, 1, 2);
        grid.add(new Label("Chức vụ:"), 0, 3);
        grid.add(comboTkRole, 1, 3);
        grid.add(new Label("Trạng thái:"), 0, 4);
        grid.add(comboTkStatus, 1, 4);
        grid.add(new Label("Ngày tạo:"), 0, 5);
        grid.add(pickerTkDate, 1, 5);

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        Button btnSave = new Button("Lưu");
        btnSave.getStyleClass().addAll("btn", "btn-success");
        Button btnDelete = new Button("Xóa");
        btnDelete.getStyleClass().addAll("btn", "btn-danger");
        Button btnClear = new Button("Làm mới");
        btnClear.getStyleClass().addAll("btn", "btn-secondary");

        btnBox.getChildren().addAll(btnSave, btnDelete, btnClear);
        formBox.getChildren().addAll(lblFormTitle, grid, btnBox);

        root.getChildren().addAll(tableBox, formBox);

        // Events
        tableTk.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedTk = newVal;
                txtTkId.setText(newVal.getMaTaiKhoan());
                txtTkId.setEditable(false);
                txtTkUsername.setText(newVal.getTenDangNhap());
                txtTkPassword.setText(newVal.getMatKhau());
                comboTkRole.setValue(newVal.getChucVu());
                comboTkStatus.setValue(newVal.getTrangThaiTaiKhoan());
                pickerTkDate.setValue(newVal.getNgayTao());
            }
        });

        btnSave.setOnAction(e -> {
            String id = txtTkId.getText().trim();
            String user = txtTkUsername.getText().trim();
            String pass = txtTkPassword.getText().trim();
            ChucVu cv = comboTkRole.getValue();
            TrangThaiTaiKhoan status = comboTkStatus.getValue();
            LocalDate cDate = pickerTkDate.getValue();

            if (id.isEmpty() || user.isEmpty() || pass.isEmpty() || cv == null || status == null || cDate == null) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng nhập đầy đủ thông tin tài khoản!");
                return;
            }

            if (selectedTk == null) {
                if (tkDao.getById(id) != null) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã tài khoản đã tồn tại!");
                    return;
                }
                TaiKhoan tk = new TaiKhoan(id, user, pass, cv, status, cDate);
                tkDao.add(tk);
            } else {
                selectedTk.setTenDangNhap(user);
                selectedTk.setMatKhau(pass);
                selectedTk.setChucVu(cv);
                selectedTk.setTrangThaiTaiKhoan(status);
                selectedTk.setNgayTao(cDate);
                tkDao.update(selectedTk);
            }
            refreshAccounts();
            clearAccountForm();
        });

        btnDelete.setOnAction(e -> {
            if (selectedTk != null) {
                tkDao.delete(selectedTk.getMaTaiKhoan());
                refreshAccounts();
                clearAccountForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn tài khoản để xóa!");
            }
        });

        btnClear.setOnAction(e -> clearAccountForm());

        refreshAccounts();
        return root;
    }

    private void refreshLsp() {
        tableLsp.setItems(FXCollections.observableArrayList(spService.getAllLoaiSanPham()));
    }

    private void refreshAccounts() {
        tableTk.setItems(FXCollections.observableArrayList(tkDao.getAll()));
    }

    private void clearLspForm() {
        selectedLsp = null;
        txtLspId.clear();
        txtLspId.setEditable(true);
        txtLspName.clear();
        txtLspDesc.clear();
        txtLspBasePrice.clear();
        txtLspNote.clear();
        tableLsp.getSelectionModel().clearSelection();
    }

    private void clearAccountForm() {
        selectedTk = null;
        txtTkId.clear();
        txtTkId.setEditable(true);
        txtTkUsername.clear();
        txtTkPassword.clear();
        comboTkRole.setValue(null);
        comboTkStatus.setValue(null);
        pickerTkDate.setValue(LocalDate.now());
        tableTk.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
