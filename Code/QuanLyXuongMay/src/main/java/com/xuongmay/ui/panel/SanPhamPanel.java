package com.xuongmay.ui.panel;

import com.xuongmay.model.*;
import com.xuongmay.service.SanPhamService;
import com.xuongmay.service.BanHangService;
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
import java.util.ArrayList;
import java.util.List;

public class SanPhamPanel extends VBox {
    private final SanPhamService spService = new SanPhamService();
    private final BanHangService bhService = new BanHangService();

    // Tab 1: Products
    private TableView<SanPham> tableSp;
    private TextField txtSpId, txtSpName, txtSpPrice, txtSpQty, txtSpRi, txtSpBoLe, txtSpRiLe, txtSpNote;
    private ComboBox<TrangThaiSanPham> comboSpStatus;
    private ComboBox<LoaiSanPham> comboSpType;
    private SanPham selectedSp;

    // Tab 2: Orders
    private TableView<DonHang> tableDh;
    private TableView<ChiTietDonHang> tableCtdh;
    private ComboBox<KhachHang> comboCustomer;
    private ComboBox<TrangThaiDonHang> comboDhStatus;
    private DatePicker pickerOrderDate;
    private TextField txtDhId, txtDhNote;
    
    // Add items to order
    private ComboBox<SanPham> comboAddSp;
    private TextField txtAddQty;
    private ObservableList<ChiTietDonHang> tempDetailsList = FXCollections.observableArrayList();
    private Label lblTotalAmount;
    private ComboBox<PhuongThucThanhToan> comboPayMethod;
    private ComboBox<TrangThaiHoaDon> comboInvoiceStatus;
    private DonHang selectedDh;

    public SanPhamPanel() {
        setSpacing(15);
        setPadding(new Insets(15));
        VBox.setVgrow(this, Priority.ALWAYS);

        Label lblTitle = new Label("Quản Lý Sản Phẩm & Đơn Hàng");
        lblTitle.getStyleClass().add("tab-title");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab tabProduct = new Tab("Sản Phẩm", createProductView());
        Tab tabOrder = new Tab("Đơn Bán Hàng", createOrderView());

        tabPane.getTabs().addAll(tabProduct, tabOrder);
        getChildren().addAll(lblTitle, tabPane);

        // Initial load after all components are initialized
        refreshProducts();
        refreshOrders();
    }

    private Node createProductView() {
        HBox root = new HBox(20);
        root.setPadding(new Insets(15));
        HBox.setHgrow(root, Priority.ALWAYS);

        // Product list (Left)
        VBox tableBox = new VBox(10);
        HBox.setHgrow(tableBox, Priority.ALWAYS);

        tableSp = new TableView<>();
        VBox.setVgrow(tableSp, Priority.ALWAYS);

        TableColumn<SanPham, String> colId = new TableColumn<>("Mã SP");
        colId.setCellValueFactory(new PropertyValueFactory<>("maSanPham"));
        colId.setPrefWidth(80);

        TableColumn<SanPham, String> colName = new TableColumn<>("Tên Sản Phẩm");
        colName.setCellValueFactory(new PropertyValueFactory<>("tenSanPham"));
        colName.setPrefWidth(180);

        TableColumn<SanPham, String> colType = new TableColumn<>("Loại");
        colType.setCellValueFactory(cellData -> {
            if (cellData.getValue().getLoaiSanPham() != null) {
                return new SimpleStringProperty(cellData.getValue().getLoaiSanPham().getTenLoai());
            }
            return new SimpleStringProperty("N/A");
        });
        colType.setPrefWidth(120);

        TableColumn<SanPham, Double> colPrice = new TableColumn<>("Giá Bán");
        colPrice.setCellValueFactory(new PropertyValueFactory<>("giaThucTe"));
        colPrice.setPrefWidth(110);

        TableColumn<SanPham, Integer> colQty = new TableColumn<>("Tổng Số Bộ");
        colQty.setCellValueFactory(new PropertyValueFactory<>("tongSoBo"));
        colQty.setPrefWidth(100);

        TableColumn<SanPham, String> colStatus = new TableColumn<>("Khâu Sản Xuất");
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrangThaiSanPham().toString()));
        colStatus.setPrefWidth(120);

        tableSp.getColumns().addAll(colId, colName, colType, colPrice, colQty, colStatus);
        tableBox.getChildren().addAll(new Label("Danh sách sản phẩm"), tableSp);

        // Form (Right)
        VBox formBox = new VBox(10);
        formBox.setPrefWidth(320);
        formBox.setMinWidth(320);
        formBox.getStyleClass().add("card-panel");

        Label lblFormTitle = new Label("Thông Tin Sản Phẩm");
        lblFormTitle.getStyleClass().add("sub-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints(90);
        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints(180);
        grid.getColumnConstraints().addAll(col1, col2);

        txtSpId = new TextField();
        txtSpName = new TextField();
        txtSpPrice = new TextField();
        txtSpQty = new TextField();
        txtSpRi = new TextField();
        txtSpBoLe = new TextField();
        txtSpRiLe = new TextField();
        txtSpNote = new TextField();
        comboSpStatus = new ComboBox<>(FXCollections.observableArrayList(TrangThaiSanPham.values()));
        comboSpType = new ComboBox<>();

        grid.add(new Label("Mã SP:"), 0, 0);
        grid.add(txtSpId, 1, 0);
        grid.add(new Label("Tên SP:"), 0, 1);
        grid.add(txtSpName, 1, 1);

        grid.add(new Label("Loại SP:"), 0, 2);
        grid.add(comboSpType, 1, 2);
        grid.add(new Label("Giá bán:"), 0, 3);
        grid.add(txtSpPrice, 1, 3);

        grid.add(new Label("Tổng bộ:"), 0, 4);
        grid.add(txtSpQty, 1, 4);
        grid.add(new Label("Tổng ri:"), 0, 5);
        grid.add(txtSpRi, 1, 5);

        grid.add(new Label("Bộ lẻ:"), 0, 6);
        grid.add(txtSpBoLe, 1, 6);
        grid.add(new Label("Ri lẻ:"), 0, 7);
        grid.add(txtSpRiLe, 1, 7);

        grid.add(new Label("Khâu SX:"), 0, 8);
        grid.add(comboSpStatus, 1, 8);
        grid.add(new Label("Ghi chú:"), 0, 9);
        grid.add(txtSpNote, 1, 9);

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

        // Product Events
        tableSp.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedSp = newVal;
                txtSpId.setText(newVal.getMaSanPham());
                txtSpId.setEditable(false);
                txtSpName.setText(newVal.getTenSanPham());
                txtSpPrice.setText(String.valueOf(newVal.getGiaThucTe()));
                txtSpQty.setText(String.valueOf(newVal.getTongSoBo()));
                txtSpRi.setText(String.valueOf(newVal.getTongSoRi()));
                txtSpBoLe.setText(String.valueOf(newVal.getSoBoLe()));
                txtSpRiLe.setText(String.valueOf(newVal.getSoRiLe()));
                txtSpNote.setText(newVal.getGhiChu());
                comboSpStatus.setValue(newVal.getTrangThaiSanPham());
                comboSpType.setValue(newVal.getLoaiSanPham());
            }
        });

        btnSave.setOnAction(e -> {
            String id = txtSpId.getText().trim();
            String name = txtSpName.getText().trim();
            String priceStr = txtSpPrice.getText().trim();
            String qtyStr = txtSpQty.getText().trim();
            String riStr = txtSpRi.getText().trim();
            String boLeStr = txtSpBoLe.getText().trim();
            String riLeStr = txtSpRiLe.getText().trim();
            String note = txtSpNote.getText().trim();
            TrangThaiSanPham status = comboSpStatus.getValue();
            LoaiSanPham lsp = comboSpType.getValue();

            if (id.isEmpty() || name.isEmpty() || priceStr.isEmpty() || qtyStr.isEmpty() || status == null || lsp == null) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng nhập đầy đủ thông tin sản phẩm!");
                return;
            }

            double price;
            int qty, ri, boLe, riLe;
            try {
                price = Double.parseDouble(priceStr);
                qty = Integer.parseInt(qtyStr);
                ri = riStr.isEmpty() ? 0 : Integer.parseInt(riStr);
                boLe = boLeStr.isEmpty() ? 0 : Integer.parseInt(boLeStr);
                riLe = riLeStr.isEmpty() ? 0 : Integer.parseInt(riLeStr);
                if (price < 0 || qty < 0 || ri < 0 || boLe < 0 || riLe < 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Các trường số phải là số dương!");
                return;
            }

            if (selectedSp == null) {
                if (spService.getSanPhamById(id) != null) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã sản phẩm đã tồn tại!");
                    return;
                }
                SanPham sp = new SanPham(id, name, price, qty, ri, boLe, riLe, note, status, lsp);
                spService.addSanPham(sp);
            } else {
                selectedSp.setTenSanPham(name);
                selectedSp.setGiaThucTe(price);
                selectedSp.setTongSoBo(qty);
                selectedSp.setTongSoRi(ri);
                selectedSp.setSoBoLe(boLe);
                selectedSp.setSoRiLe(riLe);
                selectedSp.setGhiChu(note);
                selectedSp.setTrangThaiSanPham(status);
                selectedSp.setLoaiSanPham(lsp);
                spService.updateSanPham(selectedSp);
            }
            refreshProducts();
            clearProductForm();
        });

        btnDelete.setOnAction(e -> {
            if (selectedSp != null) {
                spService.deleteSanPham(selectedSp.getMaSanPham());
                refreshProducts();
                clearProductForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn sản phẩm để xóa!");
            }
        });

        btnClear.setOnAction(e -> clearProductForm());

        // Initial fill will be called at the end of the constructor
        return root;
    }

    private Node createOrderView() {
        SplitPane split = new SplitPane();
        VBox.setVgrow(split, Priority.ALWAYS);

        // LEFT: Orders table
        VBox left = new VBox(10);
        left.setPadding(new Insets(10));
        left.setPrefWidth(500);

        tableDh = new TableView<>();
        VBox.setVgrow(tableDh, Priority.ALWAYS);

        TableColumn<DonHang, String> colDhId = new TableColumn<>("Mã Đơn");
        colDhId.setCellValueFactory(new PropertyValueFactory<>("maDonHang"));
        colDhId.setPrefWidth(80);

        TableColumn<DonHang, String> colDhKh = new TableColumn<>("Khách Hàng");
        colDhKh.setCellValueFactory(cellData -> {
            if (cellData.getValue().getKhachHang() != null) {
                return new SimpleStringProperty(cellData.getValue().getKhachHang().getTenKhachHang());
            }
            return new SimpleStringProperty("N/A");
        });
        colDhKh.setPrefWidth(160);

        TableColumn<DonHang, Double> colDhTotal = new TableColumn<>("Tổng Tiền");
        colDhTotal.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        colDhTotal.setPrefWidth(120);

        TableColumn<DonHang, String> colDhStatus = new TableColumn<>("Trạng Thái");
        colDhStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrangThaiDonHang().toString()));
        colDhStatus.setPrefWidth(110);

        tableDh.getColumns().addAll(colDhId, colDhKh, colDhTotal, colDhStatus);

        HBox bottomBtn = new HBox(10);
        Button btnDeleteDh = new Button("Hủy Đơn Hàng");
        btnDeleteDh.getStyleClass().addAll("btn", "btn-danger");
        bottomBtn.getChildren().addAll(btnDeleteDh);

        left.getChildren().addAll(new Label("Danh sách đơn hàng (Bán hàng đã & chưa xong)"), tableDh, bottomBtn);

        // RIGHT: Order Details Form & Cart
        VBox right = new VBox(15);
        right.setPadding(new Insets(10));
        right.setPrefWidth(550);

        VBox orderForm = new VBox(10);
        orderForm.getStyleClass().add("card-panel");
        Label lblFormTitle = new Label("Tạo Đơn Hàng Mới");
        lblFormTitle.getStyleClass().add("sub-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        txtDhId = new TextField();
        comboCustomer = new ComboBox<>();
        pickerOrderDate = new DatePicker(LocalDate.now());
        comboDhStatus = new ComboBox<>(FXCollections.observableArrayList(TrangThaiDonHang.values()));
        comboDhStatus.setValue(TrangThaiDonHang.ChuaGiao);
        txtDhNote = new TextField();
        comboPayMethod = new ComboBox<>(FXCollections.observableArrayList(PhuongThucThanhToan.values()));
        comboPayMethod.setValue(PhuongThucThanhToan.TienMat);
        comboInvoiceStatus = new ComboBox<>(FXCollections.observableArrayList(TrangThaiHoaDon.values()));
        comboInvoiceStatus.setValue(TrangThaiHoaDon.DaThanhToan);

        grid.add(new Label("Mã đơn:"), 0, 0);
        grid.add(txtDhId, 1, 0);
        grid.add(new Label("Khách hàng:"), 2, 0);
        grid.add(comboCustomer, 3, 0);

        grid.add(new Label("Ngày đặt:"), 0, 1);
        grid.add(pickerOrderDate, 1, 1);
        grid.add(new Label("Trạng thái đơn:"), 2, 1);
        grid.add(comboDhStatus, 3, 1);

        grid.add(new Label("P.Thức T.Toán:"), 0, 2);
        grid.add(comboPayMethod, 1, 2);
        grid.add(new Label("Trạng thái HĐ:"), 2, 2);
        grid.add(comboInvoiceStatus, 3, 2);

        grid.add(new Label("Ghi chú đơn:"), 0, 3);
        grid.add(txtDhNote, 1, 3, 3, 1);

        // Cart details section
        Separator sep = new Separator();
        HBox addBox = new HBox(10);
        addBox.setAlignment(Pos.CENTER_LEFT);
        comboAddSp = new ComboBox<>();
        txtAddQty = new TextField();
        txtAddQty.setPromptText("SL Ri");
        txtAddQty.setPrefWidth(80);
        Button btnAddCart = new Button("Thêm SP");
        btnAddCart.getStyleClass().addAll("btn", "btn-primary");
        addBox.getChildren().addAll(new Label("Sản phẩm:"), comboAddSp, new Label("SL Ri:"), txtAddQty, btnAddCart);

        tableCtdh = new TableView<>();
        tableCtdh.setPrefHeight(150);
        
        TableColumn<ChiTietDonHang, String> colCtSp = new TableColumn<>("Sản Phẩm");
        colCtSp.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSanPham().getTenSanPham()));
        colCtSp.setPrefWidth(180);

        TableColumn<ChiTietDonHang, Integer> colCtQty = new TableColumn<>("SL Ri");
        colCtQty.setCellValueFactory(new PropertyValueFactory<>("soLuongRi"));
        colCtQty.setPrefWidth(80);

        TableColumn<ChiTietDonHang, Double> colCtPrice = new TableColumn<>("Đơn Giá Ri");
        colCtPrice.setCellValueFactory(new PropertyValueFactory<>("donGiaRi"));
        colCtPrice.setPrefWidth(110);

        TableColumn<ChiTietDonHang, Double> colCtTotal = new TableColumn<>("Thành Tiền");
        colCtTotal.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));
        colCtTotal.setPrefWidth(120);

        tableCtdh.getColumns().addAll(colCtSp, colCtQty, colCtPrice, colCtTotal);
        tableCtdh.setItems(tempDetailsList);

        HBox summaryBox = new HBox(15);
        summaryBox.setAlignment(Pos.CENTER_RIGHT);
        lblTotalAmount = new Label("Tổng tiền: 0 đ");
        lblTotalAmount.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #10b981;");
        Button btnSaveOrder = new Button("Lưu Đơn & Xuất Hóa Đơn");
        btnSaveOrder.getStyleClass().addAll("btn", "btn-success");
        summaryBox.getChildren().addAll(lblTotalAmount, btnSaveOrder);

        orderForm.getChildren().addAll(lblFormTitle, grid, sep, addBox, tableCtdh, summaryBox);
        right.getChildren().add(orderForm);

        split.getItems().addAll(left, right);

        // Actions & Events for Orders
        tableDh.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedDh = newVal;
                // View details only
                tempDetailsList.setAll(bhService.getChiTietByDonHangId(newVal.getMaDonHang()));
                txtDhId.setText(newVal.getMaDonHang());
                txtDhId.setEditable(false);
                comboCustomer.setValue(newVal.getKhachHang());
                pickerOrderDate.setValue(newVal.getNgayDat());
                comboDhStatus.setValue(newVal.getTrangThaiDonHang());
                txtDhNote.setText(newVal.getGhiChu());
                lblTotalAmount.setText(String.format("Tổng tiền: %,.0f đ", newVal.getTongTien()));
            }
        });

        btnAddCart.setOnAction(e -> {
            SanPham sp = comboAddSp.getValue();
            String qtyStr = txtAddQty.getText().trim();
            if (sp == null || qtyStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng chọn sản phẩm và nhập số lượng!");
                return;
            }
            int qty;
            try {
                qty = Integer.parseInt(qtyStr);
                if (qty <= 0) throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Số lượng phải là số nguyên dương!");
                return;
            }

            // Check if sp already in cart
            boolean exists = false;
            for (ChiTietDonHang ct : tempDetailsList) {
                if (ct.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) {
                    ct.setSoLuongRi(ct.getSoLuongRi() + qty);
                    ct.setThanhTien(ct.getSoLuongRi() * ct.getDonGiaRi());
                    exists = true;
                    break;
                }
            }
            if (!exists) {
                double pricePerRi = sp.getGiaThucTe() * 5; // 1 Ri = 5 bo typically, or just use sp.getGiaThucTe() as price
                // Let's use simple logic: pricePerRi = sp.getGiaThucTe() * 4 (or let's say ri is just the unit here)
                double unitPrice = sp.getGiaThucTe();
                ChiTietDonHang ct = new ChiTietDonHang(null, sp, qty, unitPrice, qty * unitPrice);
                tempDetailsList.add(ct);
            }
            tableCtdh.refresh();
            updateCartTotal();
            txtAddQty.clear();
        });

        btnSaveOrder.setOnAction(e -> {
            String dhId = txtDhId.getText().trim();
            KhachHang kh = comboCustomer.getValue();
            LocalDate oDate = pickerOrderDate.getValue();
            TrangThaiDonHang dhStatus = comboDhStatus.getValue();
            String note = txtDhNote.getText().trim();
            PhuongThucThanhToan pt = comboPayMethod.getValue();
            TrangThaiHoaDon tthd = comboInvoiceStatus.getValue();

            if (dhId.isEmpty() || kh == null || oDate == null || dhStatus == null || tempDetailsList.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng điền đủ thông tin đơn hàng và giỏ hàng!");
                return;
            }

            if (selectedDh == null) {
                if (bhService.getAllDonHang().stream().anyMatch(d -> d.getMaDonHang().equals(dhId))) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã đơn hàng đã tồn tại!");
                    return;
                }
                DonHang dh = new DonHang(dhId, kh, oDate, 0.0, dhStatus, note);
                bhService.addDonHang(dh, new ArrayList<>(tempDetailsList), pt, tthd);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Không hỗ trợ sửa trực tiếp đơn hàng để bảo toàn hóa đơn. Hãy hủy đơn và tạo lại.");
                return;
            }
            refreshOrders();
            clearOrderForm();
        });

        btnDeleteDh.setOnAction(e -> {
            if (selectedDh != null) {
                bhService.deleteDonHang(selectedDh.getMaDonHang());
                refreshOrders();
                clearOrderForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn một đơn hàng để hủy!");
            }
        });

        // Initial fill will be called at the end of the constructor
        return split;
    }

    private void updateCartTotal() {
        double total = tempDetailsList.stream().mapToDouble(ChiTietDonHang::getThanhTien).sum();
        lblTotalAmount.setText(String.format("Tổng tiền: %,.0f đ", total));
    }

    private void refreshProducts() {
        List<SanPham> spList = spService.getAllSanPham();
        tableSp.setItems(FXCollections.observableArrayList(spList));
        comboAddSp.setItems(FXCollections.observableArrayList(spList));
        
        List<LoaiSanPham> lspList = spService.getAllLoaiSanPham();
        comboSpType.setItems(FXCollections.observableArrayList(lspList));
    }

    private void refreshOrders() {
        tableDh.setItems(FXCollections.observableArrayList(bhService.getAllDonHang()));
        comboCustomer.setItems(FXCollections.observableArrayList(bhService.getAllKhachHang()));
    }

    private void clearProductForm() {
        selectedSp = null;
        txtSpId.clear();
        txtSpId.setEditable(true);
        txtSpName.clear();
        txtSpPrice.clear();
        txtSpQty.clear();
        txtSpRi.clear();
        txtSpBoLe.clear();
        txtSpRiLe.clear();
        txtSpNote.clear();
        comboSpStatus.setValue(null);
        comboSpType.setValue(null);
        tableSp.getSelectionModel().clearSelection();
    }

    private void clearOrderForm() {
        selectedDh = null;
        txtDhId.clear();
        txtDhId.setEditable(true);
        comboCustomer.setValue(null);
        pickerOrderDate.setValue(LocalDate.now());
        comboDhStatus.setValue(TrangThaiDonHang.ChuaGiao);
        txtDhNote.clear();
        comboPayMethod.setValue(PhuongThucThanhToan.TienMat);
        comboInvoiceStatus.setValue(TrangThaiHoaDon.DaThanhToan);
        tempDetailsList.clear();
        updateCartTotal();
        tableDh.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
