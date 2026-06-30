package com.xuongmay.ui.panel;

import com.xuongmay.model.CayVai;
import com.xuongmay.model.LoVai;
import com.xuongmay.model.NhaCungCap;
import com.xuongmay.model.TrangThaiLoVai;
import com.xuongmay.service.NguyenLieuService;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.List;

public class NguyenLieuPanel extends VBox {
    private final NguyenLieuService service = new NguyenLieuService();

    // Supplier UI components
    private TableView<NhaCungCap> supplierTable;
    private TextField txtNccId, txtNccName, txtNccPhone, txtNccAddress;
    private NhaCungCap selectedSupplier;

    // LoVai UI components
    private TableView<LoVai> loTable;
    private TextField txtLoId, txtLoName, txtFabricType, txtLoQty, txtLoPrice, txtLoNote;
    private ComboBox<NhaCungCap> comboSupplier;
    private ComboBox<TrangThaiLoVai> comboStatus;
    private DatePicker pickerImportDate;
    private LoVai selectedLo;

    // CayVai UI components
    private TableView<CayVai> cvTable;
    private TextField txtCvName, txtCvColor, txtCvLength, txtCvPosition, txtCvLayers, txtCvNote;
    private CayVai selectedCv;

    public NguyenLieuPanel() {
        setSpacing(15);
        setPadding(new Insets(15));
        VBox.setVgrow(this, Priority.ALWAYS);

        Label lblTitle = new Label("Quản Lý Nguyên Liệu (Chưa Cắt)");
        lblTitle.getStyleClass().add("tab-title");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab tabLoVai = new Tab("Lô Vải & Cây Vải", createLoVaiView());
        Tab tabNcc = new Tab("Nhà Cung Cấp", createSupplierView());

        tabPane.getTabs().addAll(tabLoVai, tabNcc);
        getChildren().addAll(lblTitle, tabPane);
    }

    // --- SUPPLIER TAB ---
    private Node createSupplierView() {
        HBox root = new HBox(20);
        root.setPadding(new Insets(15));
        HBox.setHgrow(root, Priority.ALWAYS);

        // Table List (Left)
        VBox tableBox = new VBox(10);
        HBox.setHgrow(tableBox, Priority.ALWAYS);
        
        supplierTable = new TableView<>();
        VBox.setVgrow(supplierTable, Priority.ALWAYS);

        TableColumn<NhaCungCap, String> colId = new TableColumn<>("Mã NCC");
        colId.setCellValueFactory(new PropertyValueFactory<>("maNhaCungCap"));
        colId.setPrefWidth(100);

        TableColumn<NhaCungCap, String> colName = new TableColumn<>("Tên Nhà Cung Cấp");
        colName.setCellValueFactory(new PropertyValueFactory<>("tenNhaCungCap"));
        colName.setPrefWidth(220);

        TableColumn<NhaCungCap, String> colPhone = new TableColumn<>("Số Điện Thoại");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("soDienThoai"));
        colPhone.setPrefWidth(120);

        TableColumn<NhaCungCap, String> colAddress = new TableColumn<>("Địa Chỉ");
        colAddress.setCellValueFactory(new PropertyValueFactory<>("diaChiNha"));
        colAddress.setPrefWidth(250);

        supplierTable.getColumns().addAll(colId, colName, colPhone, colAddress);
        tableBox.getChildren().addAll(new Label("Danh sách nhà cung cấp"), supplierTable);

        // Form inputs (Right)
        VBox formBox = new VBox(12);
        formBox.setPrefWidth(320);
        formBox.setMinWidth(320);
        formBox.getStyleClass().add("card-panel");

        Label lblFormTitle = new Label("Thông Tin Nhà Cung Cấp");
        lblFormTitle.getStyleClass().add("sub-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints(90);
        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints(180);
        grid.getColumnConstraints().addAll(col1, col2);

        txtNccId = new TextField();
        txtNccName = new TextField();
        txtNccPhone = new TextField();
        txtNccAddress = new TextField();

        grid.add(new Label("Mã NCC:"), 0, 0);
        grid.add(txtNccId, 1, 0);
        grid.add(new Label("Tên NCC:"), 0, 1);
        grid.add(txtNccName, 1, 1);
        grid.add(new Label("Điện thoại:"), 0, 2);
        grid.add(txtNccPhone, 1, 2);
        grid.add(new Label("Địa chỉ:"), 0, 3);
        grid.add(txtNccAddress, 1, 3);

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
        supplierTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedSupplier = newVal;
                txtNccId.setText(newVal.getMaNhaCungCap());
                txtNccId.setEditable(false);
                txtNccName.setText(newVal.getTenNhaCungCap());
                txtNccPhone.setText(newVal.getSoDienThoai());
                txtNccAddress.setText(newVal.getDiaChiNha());
            }
        });

        btnSave.setOnAction(e -> {
            String id = txtNccId.getText().trim();
            String name = txtNccName.getText().trim();
            String phone = txtNccPhone.getText().trim();
            String addr = txtNccAddress.getText().trim();

            if (id.isEmpty() || name.isEmpty() || phone.isEmpty() || addr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            if (selectedSupplier == null) {
                if (service.getNhaCungCapById(id) != null) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã NCC đã tồn tại!");
                    return;
                }
                NhaCungCap ncc = new NhaCungCap(id, name, phone, addr);
                service.addNhaCungCap(ncc);
            } else {
                selectedSupplier.setTenNhaCungCap(name);
                selectedSupplier.setSoDienThoai(phone);
                selectedSupplier.setDiaChiNha(addr);
                service.updateNhaCungCap(selectedSupplier);
            }
            refreshSuppliers();
            clearSupplierForm();
        });

        btnDelete.setOnAction(e -> {
            if (selectedSupplier != null) {
                service.deleteNhaCungCap(selectedSupplier.getMaNhaCungCap());
                refreshSuppliers();
                clearSupplierForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn một nhà cung cấp để xóa!");
            }
        });

        btnClear.setOnAction(e -> clearSupplierForm());

        refreshSuppliers();
        return root;
    }

    private void refreshSuppliers() {
        ObservableList<NhaCungCap> items = FXCollections.observableArrayList(service.getAllNhaCungCap());
        if (supplierTable != null) {
            supplierTable.setItems(items);
        }
        if (comboSupplier != null) {
            comboSupplier.setItems(items);
        }
    }

    private void clearSupplierForm() {
        selectedSupplier = null;
        txtNccId.clear();
        txtNccId.setEditable(true);
        txtNccName.clear();
        txtNccPhone.clear();
        txtNccAddress.clear();
        supplierTable.getSelectionModel().clearSelection();
    }

    // --- LO VAI & CAY VAI TAB ---
    private Node createLoVaiView() {
        SplitPane splitPane = new SplitPane();
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        // LEFT: LoVai Table and form
        VBox leftPane = new VBox(15);
        leftPane.setPadding(new Insets(10));
        leftPane.setPrefWidth(550);

        loTable = new TableView<>();
        VBox.setVgrow(loTable, Priority.ALWAYS);

        TableColumn<LoVai, String> colLoId = new TableColumn<>("Mã Lô");
        colLoId.setCellValueFactory(new PropertyValueFactory<>("maLo"));
        colLoId.setPrefWidth(80);

        TableColumn<LoVai, String> colLoName = new TableColumn<>("Tên Lô");
        colLoName.setCellValueFactory(new PropertyValueFactory<>("tenLo"));
        colLoName.setPrefWidth(150);

        TableColumn<LoVai, String> colLoNcc = new TableColumn<>("Nhà Cung Cấp");
        colLoNcc.setCellValueFactory(cellData -> {
            NhaCungCap ncc = cellData.getValue().getNhaCungCap();
            return new SimpleStringProperty(ncc != null ? ncc.getTenNhaCungCap() : "N/A");
        });
        colLoNcc.setPrefWidth(130);

        TableColumn<LoVai, String> colLoDate = new TableColumn<>("Ngày Nhập");
        colLoDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNgayNhap().toString()));
        colLoDate.setPrefWidth(100);

        TableColumn<LoVai, Integer> colCvCount = new TableColumn<>("Số Cây Vải");
        colCvCount.setCellValueFactory(cellData -> {
            int size = service.getCayVaiByLoVaiId(cellData.getValue().getMaLo()).size();
            return new SimpleIntegerProperty(size).asObject();
        });
        colCvCount.setPrefWidth(90);

        loTable.getColumns().addAll(colLoId, colLoName, colLoNcc, colLoDate, colCvCount);

        // LoVai Form
        VBox loForm = new VBox(10);
        loForm.setMinWidth(480);
        loForm.getStyleClass().add("card-panel");
        Label lblLoTitle = new Label("Thông Tin Lô Vải");
        lblLoTitle.getStyleClass().add("sub-title");

        GridPane gridLo = new GridPane();
        gridLo.setHgap(10);
        gridLo.setVgap(10);
        
        javafx.scene.layout.ColumnConstraints c0 = new javafx.scene.layout.ColumnConstraints(75);
        javafx.scene.layout.ColumnConstraints c1 = new javafx.scene.layout.ColumnConstraints(145);
        javafx.scene.layout.ColumnConstraints c2 = new javafx.scene.layout.ColumnConstraints(75);
        javafx.scene.layout.ColumnConstraints c3 = new javafx.scene.layout.ColumnConstraints(145);
        gridLo.getColumnConstraints().addAll(c0, c1, c2, c3);

        txtLoId = new TextField();
        txtLoName = new TextField();
        comboSupplier = new ComboBox<>();
        pickerImportDate = new DatePicker(LocalDate.now());
        txtFabricType = new TextField();
        txtLoQty = new TextField();
        txtLoPrice = new TextField();
        txtLoNote = new TextField();
        comboStatus = new ComboBox<>(FXCollections.observableArrayList(TrangThaiLoVai.values()));
        comboStatus.setValue(TrangThaiLoVai.ChuaSuDung);

        gridLo.add(new Label("Mã lô:"), 0, 0);
        gridLo.add(txtLoId, 1, 0);
        gridLo.add(new Label("Tên lô:"), 2, 0);
        gridLo.add(txtLoName, 3, 0);

        gridLo.add(new Label("Nhà CC:"), 0, 1);
        gridLo.add(comboSupplier, 1, 1);
        gridLo.add(new Label("Ngày nhập:"), 2, 1);
        gridLo.add(pickerImportDate, 3, 1);

        gridLo.add(new Label("Loại vải:"), 0, 2);
        gridLo.add(txtFabricType, 1, 2);
        gridLo.add(new Label("Số lượng:"), 2, 2);
        gridLo.add(txtLoQty, 3, 2);

        gridLo.add(new Label("Giá nhập:"), 0, 3);
        gridLo.add(txtLoPrice, 1, 3);
        gridLo.add(new Label("Trạng thái:"), 2, 3);
        gridLo.add(comboStatus, 3, 3);

        gridLo.add(new Label("Ghi chú:"), 0, 4);
        gridLo.add(txtLoNote, 1, 4, 3, 1);

        HBox btnBoxLo = new HBox(10);
        btnBoxLo.setAlignment(Pos.CENTER_RIGHT);
        Button btnSaveLo = new Button("Lưu Lô");
        btnSaveLo.getStyleClass().addAll("btn", "btn-primary");
        Button btnDeleteLo = new Button("Xóa Lô");
        btnDeleteLo.getStyleClass().addAll("btn", "btn-danger");
        Button btnClearLo = new Button("Làm mới");
        btnClearLo.getStyleClass().addAll("btn", "btn-secondary");

        btnBoxLo.getChildren().addAll(btnSaveLo, btnDeleteLo, btnClearLo);
        loForm.getChildren().addAll(lblLoTitle, gridLo, btnBoxLo);

        leftPane.getChildren().addAll(new Label("Danh sách Lô Vải (Chưa Cắt)"), loTable, loForm);

        // RIGHT: CayVai Table and form
        VBox rightPane = new VBox(15);
        rightPane.setPadding(new Insets(10));
        rightPane.setPrefWidth(450);

        cvTable = new TableView<>();
        VBox.setVgrow(cvTable, Priority.ALWAYS);

        TableColumn<CayVai, String> colCvName = new TableColumn<>("Tên Cây");
        colCvName.setCellValueFactory(new PropertyValueFactory<>("tenCayVai"));
        colCvName.setPrefWidth(90);

        TableColumn<CayVai, String> colCvColor = new TableColumn<>("Màu Sắc");
        colCvColor.setCellValueFactory(new PropertyValueFactory<>("mauSac"));
        colCvColor.setPrefWidth(90);

        TableColumn<CayVai, Double> colCvLength = new TableColumn<>("Chiều Dài");
        colCvLength.setCellValueFactory(new PropertyValueFactory<>("chieuDai"));
        colCvLength.setPrefWidth(85);

        TableColumn<CayVai, String> colCvPos = new TableColumn<>("Vị Trí");
        colCvPos.setCellValueFactory(new PropertyValueFactory<>("viTri"));
        colCvPos.setPrefWidth(90);

        cvTable.getColumns().addAll(colCvName, colCvColor, colCvLength, colCvPos);

        // CayVai Form
        VBox cvForm = new VBox(10);
        cvForm.setMinWidth(480);
        cvForm.getStyleClass().add("card-panel");
        Label lblCvTitle = new Label("Thông Tin Cây Vải trong Lô");
        lblCvTitle.getStyleClass().add("sub-title");

        GridPane gridCv = new GridPane();
        gridCv.setHgap(10);
        gridCv.setVgap(10);
        
        javafx.scene.layout.ColumnConstraints cc0 = new javafx.scene.layout.ColumnConstraints(75);
        javafx.scene.layout.ColumnConstraints cc1 = new javafx.scene.layout.ColumnConstraints(145);
        javafx.scene.layout.ColumnConstraints cc2 = new javafx.scene.layout.ColumnConstraints(75);
        javafx.scene.layout.ColumnConstraints cc3 = new javafx.scene.layout.ColumnConstraints(145);
        gridCv.getColumnConstraints().addAll(cc0, cc1, cc2, cc3);

        txtCvName = new TextField();
        txtCvColor = new TextField();
        txtCvLength = new TextField();
        txtCvPosition = new TextField();
        txtCvLayers = new TextField();
        txtCvNote = new TextField();

        gridCv.add(new Label("Tên cây:"), 0, 0);
        gridCv.add(txtCvName, 1, 0);
        gridCv.add(new Label("Màu sắc:"), 2, 0);
        gridCv.add(txtCvColor, 3, 0);

        gridCv.add(new Label("Chiều dài:"), 0, 1);
        gridCv.add(txtCvLength, 1, 1);
        gridCv.add(new Label("Vị trí:"), 2, 1);
        gridCv.add(txtCvPosition, 3, 1);

        gridCv.add(new Label("Lượt trải:"), 0, 2);
        gridCv.add(txtCvLayers, 1, 2);
        gridCv.add(new Label("Ghi chú:"), 2, 2);
        gridCv.add(txtCvNote, 3, 2);

        HBox btnBoxCv = new HBox(10);
        btnBoxCv.setAlignment(Pos.CENTER_RIGHT);
        Button btnSaveCv = new Button("Lưu Cây");
        btnSaveCv.getStyleClass().addAll("btn", "btn-success");
        Button btnDeleteCv = new Button("Xóa Cây");
        btnDeleteCv.getStyleClass().addAll("btn", "btn-danger");
        Button btnClearCv = new Button("Làm mới");
        btnClearCv.getStyleClass().addAll("btn", "btn-secondary");

        btnBoxCv.getChildren().addAll(btnSaveCv, btnDeleteCv, btnClearCv);
        cvForm.getChildren().addAll(lblCvTitle, gridCv, btnBoxCv);

        rightPane.getChildren().addAll(new Label("Chi tiết các Cây Vải thuộc Lô được chọn"), cvTable, cvForm);

        splitPane.getItems().addAll(leftPane, rightPane);

        // Events
        loTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedLo = newVal;
                txtLoId.setText(newVal.getMaLo());
                txtLoId.setEditable(false);
                txtLoName.setText(newVal.getTenLo());
                comboSupplier.setValue(newVal.getNhaCungCap());
                pickerImportDate.setValue(newVal.getNgayNhap());
                txtFabricType.setText(newVal.getLoaiVai());
                txtLoQty.setText(String.valueOf(newVal.getSoLuong()));
                txtLoPrice.setText(String.valueOf(newVal.getGiaNhap()));
                txtLoNote.setText(newVal.getGhiChu());
                comboStatus.setValue(newVal.getTrangThaiLoVai());
                refreshCayVais();
                clearCayVaiForm();
            } else {
                cvTable.setItems(FXCollections.emptyObservableList());
            }
        });

        cvTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedCv = newVal;
                txtCvName.setText(newVal.getTenCayVai());
                txtCvName.setEditable(false);
                txtCvColor.setText(newVal.getMauSac());
                txtCvLength.setText(String.valueOf(newVal.getChieuDai()));
                txtCvPosition.setText(newVal.getViTri());
                txtCvLayers.setText(String.valueOf(newVal.getLuotTraiVai()));
                txtCvNote.setText(newVal.getGhiChu());
            }
        });

        // LoVai Actions
        btnSaveLo.setOnAction(e -> {
            String id = txtLoId.getText().trim();
            String name = txtLoName.getText().trim();
            NhaCungCap ncc = comboSupplier.getValue();
            LocalDate importDate = pickerImportDate.getValue();
            String fabType = txtFabricType.getText().trim();
            String qtyStr = txtLoQty.getText().trim();
            String priceStr = txtLoPrice.getText().trim();
            String note = txtLoNote.getText().trim();
            TrangThaiLoVai status = comboStatus.getValue();

            if (id.isEmpty() || name.isEmpty() || ncc == null || importDate == null || fabType.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty() || status == null) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng nhập đầy đủ thông tin Lô vải!");
                return;
            }

            int qty;
            double price;
            try {
                qty = Integer.parseInt(qtyStr);
                price = Double.parseDouble(priceStr);
                if (qty <= 0 || price <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Số lượng và Giá nhập phải là số dương!");
                return;
            }

            if (selectedLo == null) {
                if (service.getLoVaiById(id) != null) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã lô đã tồn tại!");
                    return;
                }
                LoVai lo = new LoVai(id, name, ncc, importDate, qty, fabType, price, note, status);
                service.addLoVai(lo);
            } else {
                selectedLo.setTenLo(name);
                selectedLo.setNhaCungCap(ncc);
                selectedLo.setNgayNhap(importDate);
                selectedLo.setLoaiVai(fabType);
                selectedLo.setSoLuong(qty);
                selectedLo.setGiaNhap(price);
                selectedLo.setGhiChu(note);
                selectedLo.setTrangThaiLoVai(status);
                service.updateLoVai(selectedLo);
            }
            refreshLoVais();
            clearLoVaiForm();
        });

        btnDeleteLo.setOnAction(e -> {
            if (selectedLo != null) {
                service.deleteLoVai(selectedLo.getMaLo());
                refreshLoVais();
                clearLoVaiForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn một Lô vải để xóa!");
            }
        });

        btnClearLo.setOnAction(e -> clearLoVaiForm());

        // CayVai Actions
        btnSaveCv.setOnAction(e -> {
            if (selectedLo == null) {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn một Lô vải trước khi thao tác Cây vải!");
                return;
            }
            String cvName = txtCvName.getText().trim();
            String color = txtCvColor.getText().trim();
            String lenStr = txtCvLength.getText().trim();
            String pos = txtCvPosition.getText().trim();
            String layersStr = txtCvLayers.getText().trim();
            String note = txtCvNote.getText().trim();

            if (cvName.isEmpty() || color.isEmpty() || lenStr.isEmpty() || pos.isEmpty() || layersStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng nhập đầy đủ thông tin Cây vải!");
                return;
            }

            double len;
            int layers;
            try {
                len = Double.parseDouble(lenStr);
                layers = Integer.parseInt(layersStr);
                if (len <= 0 || layers < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Chiều dài phải > 0 và Lượt trải phải >= 0!");
                return;
            }

            if (selectedCv == null) {
                if (service.getAllCayVai().stream().anyMatch(c -> c.getTenCayVai().equals(cvName))) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên/Mã cây vải đã tồn tại!");
                    return;
                }
                CayVai cv = new CayVai(cvName, color, selectedLo, len, pos, note, layers);
                service.addCayVai(cv);
            } else {
                selectedCv.setMauSac(color);
                selectedCv.setChieuDai(len);
                selectedCv.setViTri(pos);
                selectedCv.setLuotTraiVai(layers);
                selectedCv.setGhiChu(note);
                service.updateCayVai(selectedCv);
            }
            refreshCayVais();
            clearCayVaiForm();
        });

        btnDeleteCv.setOnAction(e -> {
            if (selectedCv != null) {
                service.deleteCayVai(selectedCv.getTenCayVai());
                refreshCayVais();
                clearCayVaiForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn một Cây vải để xóa!");
            }
        });

        btnClearCv.setOnAction(e -> clearCayVaiForm());

        refreshLoVais();
        refreshSuppliers(); // Initial fill for combo
        return splitPane;
    }

    private void refreshLoVais() {
        loTable.setItems(FXCollections.observableArrayList(service.getLoVaiChuaCat()));
    }

    private void refreshCayVais() {
        if (selectedLo != null) {
            cvTable.setItems(FXCollections.observableArrayList(service.getCayVaiByLoVaiId(selectedLo.getMaLo())));
        }
    }

    private void clearLoVaiForm() {
        selectedLo = null;
        txtLoId.clear();
        txtLoId.setEditable(true);
        txtLoName.clear();
        comboSupplier.setValue(null);
        pickerImportDate.setValue(LocalDate.now());
        txtFabricType.clear();
        txtLoQty.clear();
        txtLoPrice.clear();
        txtLoNote.clear();
        comboStatus.setValue(TrangThaiLoVai.ChuaSuDung);
        loTable.getSelectionModel().clearSelection();
        cvTable.setItems(FXCollections.emptyObservableList());
    }

    private void clearCayVaiForm() {
        selectedCv = null;
        txtCvName.clear();
        txtCvName.setEditable(true);
        txtCvColor.clear();
        txtCvLength.clear();
        txtCvPosition.clear();
        txtCvLayers.clear();
        txtCvNote.clear();
        cvTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
