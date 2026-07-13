package com.xuongmay.ui.panel;

import com.xuongmay.model.*;
import com.xuongmay.service.SanXuatService;
import com.xuongmay.service.SanPhamService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Node;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class NhanSuPanel extends VBox {
    private final SanXuatService service = new SanXuatService();
    private final SanPhamService spService = new SanPhamService();

    // Tab 1: Staff List
    private TableView<NhanVien> tableNv;
    private TextField txtNvId, txtNvName, txtNvPhone, txtNvNote;
    private ComboBox<ChuyenMon> comboChuyenMon;
    private NhanVien selectedNv;

    // Tab 2: Assignments
    private TableView<PhanCongSanPham> tablePc;
    private TextField txtPcId;
    private ComboBox<SanPham> comboPcSp;
    private ComboBox<NhanVien> comboPcNv;
    private DatePicker pickerPcDate;
    private TextField txtPcQty;
    private CheckBox chkAllQty;
    private PhanCongSanPham selectedPc;
    private TabPane tabPane;
    private Label lblRemainingInfo;
    private Runnable onSanPhamUpdatedCallback;

    public void setOnSanPhamUpdatedCallback(Runnable callback) {
        this.onSanPhamUpdatedCallback = callback;
    }
    public NhanSuPanel() {
        setSpacing(15);
        setPadding(new Insets(15));
        VBox.setVgrow(this, Priority.ALWAYS);

        Label lblTitle = new Label("Quản Lý Nhân Sự & Phân Công");
        lblTitle.getStyleClass().add("tab-title");

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab tabStaff = new Tab("Nhân Viên (Thợ May & Thợ Ủi)", createStaffView());
        Tab tabAssign = new Tab("Phân Công Sản Phẩm", createAssignView());

        tabPane.getTabs().addAll(tabStaff, tabAssign);
        getChildren().addAll(lblTitle, tabPane);

        // Initial load after all panels are initialized
        refreshStaff();
        refreshAssignments();
    }

    private Node createStaffView() {
        HBox root = new HBox(20);
        root.setPadding(new Insets(15));
        HBox.setHgrow(root, Priority.ALWAYS);

        // Table (Left)
        VBox tableBox = new VBox(10);
        HBox.setHgrow(tableBox, Priority.ALWAYS);

        tableNv = new TableView<>();
        VBox.setVgrow(tableNv, Priority.ALWAYS);

        TableColumn<NhanVien, String> colId = new TableColumn<>("Mã NV");
        colId.setCellValueFactory(new PropertyValueFactory<>("maNhanVien"));
        colId.setPrefWidth(90);

        TableColumn<NhanVien, String> colName = new TableColumn<>("Họ Tên");
        colName.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));
        colName.setPrefWidth(200);

        TableColumn<NhanVien, String> colPhone = new TableColumn<>("Số Điện Thoại");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("dienThoai"));
        colPhone.setPrefWidth(120);

        TableColumn<NhanVien, String> colExpertise = new TableColumn<>("Chuyên Môn");
        colExpertise.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getChuyenMon().toString()));
        colExpertise.setPrefWidth(120);

        TableColumn<NhanVien, String> colNote = new TableColumn<>("Ghi Chú");
        colNote.setCellValueFactory(new PropertyValueFactory<>("ghiChu"));
        colNote.setPrefWidth(200);

        tableNv.getColumns().addAll(colId, colName, colPhone, colExpertise, colNote);
        tableBox.getChildren().addAll(new Label("Danh sách nhân sự xưởng"), tableNv);

        // Form (Right)
        VBox formBox = new VBox(12);
        formBox.setPrefWidth(320);
        formBox.setMinWidth(320);
        formBox.getStyleClass().add("card-panel");

        Label lblFormTitle = new Label("Thông Tin Nhân Viên");
        lblFormTitle.getStyleClass().add("sub-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints(90);
        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints(180);
        grid.getColumnConstraints().addAll(col1, col2);

        txtNvId = new TextField();
        txtNvName = new TextField();
        txtNvPhone = new TextField();
        txtNvNote = new TextField();
        comboChuyenMon = new ComboBox<>(FXCollections.observableArrayList(ChuyenMon.values()));
        comboChuyenMon.setValue(ChuyenMon.ThoMay);

        grid.add(new Label("Mã NV:"), 0, 0);
        grid.add(txtNvId, 1, 0);
        grid.add(new Label("Họ tên:"), 0, 1);
        grid.add(txtNvName, 1, 1);
        grid.add(new Label("Điện thoại:"), 0, 2);
        grid.add(txtNvPhone, 1, 2);
        grid.add(new Label("Chuyên môn:"), 0, 3);
        grid.add(comboChuyenMon, 1, 3);
        grid.add(new Label("Ghi chú:"), 0, 4);
        grid.add(txtNvNote, 1, 4);

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
        tableNv.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedNv = newVal;
                txtNvId.setText(newVal.getMaNhanVien());
                txtNvId.setEditable(false);
                txtNvName.setText(newVal.getTenNhanVien());
                txtNvPhone.setText(newVal.getDienThoai());
                comboChuyenMon.setValue(newVal.getChuyenMon());
                txtNvNote.setText(newVal.getGhiChu());
            }
        });

        btnSave.setOnAction(e -> {
            String id = txtNvId.getText().trim();
            String name = txtNvName.getText().trim();
            String phone = txtNvPhone.getText().trim();
            ChuyenMon cm = comboChuyenMon.getValue();
            String note = txtNvNote.getText().trim();

            if (id.isEmpty() || name.isEmpty() || phone.isEmpty() || cm == null) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng điền đầy đủ thông tin nhân viên!");
                return;
            }

            if (selectedNv == null) {
                if (service.getNhanVienById(id) != null) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã nhân viên đã tồn tại!");
                    return;
                }
                NhanVien nv = new NhanVien(id, name, phone, cm, note);
                service.addNhanVien(nv);
            } else {
                selectedNv.setTenNhanVien(name);
                selectedNv.setDienThoai(phone);
                selectedNv.setChuyenMon(cm);
                selectedNv.setGhiChu(note);
                service.updateNhanVien(selectedNv);
            }
            refreshStaff();
            clearStaffForm();
        });

        btnDelete.setOnAction(e -> {
            if (selectedNv != null) {
                service.deleteNhanVien(selectedNv.getMaNhanVien());
                refreshStaff();
                clearStaffForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn nhân viên để xóa!");
            }
        });

        btnClear.setOnAction(e -> clearStaffForm());

        // Context Menu (chuột phải) cho bảng nhân viên
        tableNv.setRowFactory(tv -> {
            TableRow<NhanVien> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("✏️  Chỉnh sửa");
            editItem.setOnAction(event -> {
                if (row.getItem() != null) tableNv.getSelectionModel().select(row.getItem());
            });

            MenuItem deleteItem = new MenuItem("🗑️  Xóa Nhân Viên");
            deleteItem.setOnAction(event -> {
                NhanVien rowData = row.getItem();
                if (rowData != null) {
                    tableNv.getSelectionModel().select(rowData);
                    btnDelete.fire();
                }
            });

            MenuItem copyPhoneItem = new MenuItem("📋  Sao chép số điện thoại");
            copyPhoneItem.setOnAction(event -> {
                NhanVien rowData = row.getItem();
                if (rowData != null && rowData.getDienThoai() != null) {
                    javafx.scene.input.Clipboard cb = javafx.scene.input.Clipboard.getSystemClipboard();
                    javafx.scene.input.ClipboardContent cc = new javafx.scene.input.ClipboardContent();
                    cc.putString(rowData.getDienThoai());
                    cb.setContent(cc);
                }
            });

            contextMenu.getItems().addAll(editItem, deleteItem, new SeparatorMenuItem(), copyPhoneItem);

            row.emptyProperty().addListener((obs, wasEmpty, isEmpty) ->
                row.setContextMenu(isEmpty ? null : contextMenu)
            );
            return row;
        });

        // Initial fill will be called at the end of constructor
        return root;
    }

    private Node createAssignView() {
        HBox root = new HBox(20);
        root.setPadding(new Insets(15));
        HBox.setHgrow(root, Priority.ALWAYS);

        // Table (Left)
        VBox tableBox = new VBox(10);
        HBox.setHgrow(tableBox, Priority.ALWAYS);

        tablePc = new TableView<>();
        VBox.setVgrow(tablePc, Priority.ALWAYS);

        TableColumn<PhanCongSanPham, String> colPcId = new TableColumn<>("Mã PC");
        colPcId.setCellValueFactory(cellData -> {
            String ma = cellData.getValue().getMaPhanCong();
            if (ma != null && ma.startsWith("__UNASSIGNED__")) return new SimpleStringProperty("---");
            return new SimpleStringProperty(ma != null ? ma : "");
        });
        colPcId.setPrefWidth(90);

        TableColumn<PhanCongSanPham, String> colPcSp = new TableColumn<>("Sản Phẩm");
        colPcSp.setCellValueFactory(cellData -> {
            if (cellData.getValue().getSanPham() != null) {
                return new SimpleStringProperty(cellData.getValue().getSanPham().getTenSanPham());
            }
            return new SimpleStringProperty("N/A");
        });
        colPcSp.setPrefWidth(180);

        TableColumn<PhanCongSanPham, String> colPcSpStatus = new TableColumn<>("Khâu hiện tại");
        colPcSpStatus.setCellValueFactory(cellData -> {
            if (cellData.getValue().getSanPham() != null && cellData.getValue().getSanPham().getTrangThaiSanPham() != null) {
                return new SimpleStringProperty(cellData.getValue().getSanPham().getTrangThaiSanPham().toString());
            }
            return new SimpleStringProperty("N/A");
        });
        colPcSpStatus.setPrefWidth(110);

        TableColumn<PhanCongSanPham, String> colPcNv = new TableColumn<>("Nhân Viên Nhận");
        colPcNv.setCellValueFactory(cellData -> {
            String ma = cellData.getValue().getMaPhanCong();
            if (ma != null && ma.startsWith("__UNASSIGNED__")) return new SimpleStringProperty("(Chưa phân công)");
            if (cellData.getValue().getNhanVien() != null) return new SimpleStringProperty(cellData.getValue().getNhanVien().getTenNhanVien());
            return new SimpleStringProperty("N/A");
        });
        colPcNv.setPrefWidth(180);

        TableColumn<PhanCongSanPham, String> colPcDate = new TableColumn<>("Ngày Phân Công");
        colPcDate.setCellValueFactory(cellData -> {
            String ma = cellData.getValue().getMaPhanCong();
            if (ma != null && ma.startsWith("__UNASSIGNED__")) return new SimpleStringProperty("---");
            java.time.LocalDate d = cellData.getValue().getNgayPhanCong();
            return new SimpleStringProperty(d != null ? d.toString() : "---");
        });
        colPcDate.setPrefWidth(120);

        TableColumn<PhanCongSanPham, String> colPcQty = new TableColumn<>("Số Lượng");
        colPcQty.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colPcQty.setPrefWidth(100);

        tablePc.getColumns().addAll(colPcId, colPcSp, colPcSpStatus, colPcNv, colPcDate, colPcQty);
        tableBox.getChildren().addAll(new Label("Danh sách phân công công việc"), tablePc);

        // Form (Right)
        VBox formBox = new VBox(12);
        formBox.setPrefWidth(320);
        formBox.setMinWidth(320);
        formBox.getStyleClass().add("card-panel");

        Label lblFormTitle = new Label("Phân Công Sản Xuất");
        lblFormTitle.getStyleClass().add("sub-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints(90);
        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints(180);
        grid.getColumnConstraints().addAll(col1, col2);

        txtPcId = new TextField();
        comboPcSp = new ComboBox<>();
        comboPcSp.setCellFactory(lv -> new ListCell<SanPham>() {
            @Override
            protected void updateItem(SanPham item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    int remaining = getRemainingQty(item, selectedPc != null ? selectedPc.getMaPhanCong() : null);
                    setText(item.getTenSanPham() + " (Khâu: " + (item.getTrangThaiSanPham() != null ? item.getTrangThaiSanPham().toString() : "Chưa rõ") + " - Còn: " + remaining + "/" + item.getTongSoBoDuKien() + ")");
                }
            }
        });
        comboPcSp.setButtonCell(new ListCell<SanPham>() {
            @Override
            protected void updateItem(SanPham item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    int remaining = getRemainingQty(item, selectedPc != null ? selectedPc.getMaPhanCong() : null);
                    setText(item.getTenSanPham() + " (Khâu: " + (item.getTrangThaiSanPham() != null ? item.getTrangThaiSanPham().toString() : "Chưa rõ") + " - Còn: " + remaining + "/" + item.getTongSoBoDuKien() + ")");
                }
            }
        });
        comboPcNv = new ComboBox<>();
        pickerPcDate = new DatePicker(LocalDate.now());

        HBox qtyBox = new HBox(8);
        qtyBox.setAlignment(Pos.CENTER_LEFT);
        txtPcQty = new TextField("Tất cả");
        txtPcQty.setPrefWidth(90);
        chkAllQty = new CheckBox("Tất cả");
        chkAllQty.setSelected(true);
        txtPcQty.setDisable(true);
        qtyBox.getChildren().addAll(txtPcQty, chkAllQty);

        chkAllQty.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                txtPcQty.setText("Tất cả");
                txtPcQty.setDisable(true);
            } else {
                txtPcQty.setText("");
                txtPcQty.setDisable(false);
                txtPcQty.requestFocus();
            }
        });

        comboPcSp.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateRemainingLabel(newVal);
        });

        lblRemainingInfo = new Label();
        lblRemainingInfo.setStyle("-fx-text-fill: #4338ca; -fx-font-weight: bold; -fx-font-size: 11px;");

        grid.add(new Label("Mã PC:"), 0, 0);
        grid.add(txtPcId, 1, 0);
        grid.add(new Label("Sản phẩm:"), 0, 1);
        grid.add(comboPcSp, 1, 1);
        grid.add(new Label("Nhân viên:"), 0, 2);
        grid.add(comboPcNv, 1, 2);
        grid.add(new Label("Ngày giao:"), 0, 3);
        grid.add(pickerPcDate, 1, 3);
        grid.add(new Label("Số lượng:"), 0, 4);
        grid.add(qtyBox, 1, 4);
        grid.add(new Label("Còn lại:"), 0, 5);
        grid.add(lblRemainingInfo, 1, 5);

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        Button btnSave = new Button("Lưu");
        btnSave.getStyleClass().addAll("btn", "btn-primary");
        Button btnDelete = new Button("Xóa");
        btnDelete.getStyleClass().addAll("btn", "btn-danger");
        Button btnClear = new Button("Làm mới");
        btnClear.getStyleClass().addAll("btn", "btn-secondary");

        btnBox.getChildren().addAll(btnSave, btnDelete, btnClear);
        formBox.getChildren().addAll(lblFormTitle, grid, btnBox);

        root.getChildren().addAll(tableBox, formBox);

        // Events
        tablePc.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String ma = newVal.getMaPhanCong();
                if (ma != null && ma.startsWith("__UNASSIGNED__")) {
                    // Hàng ảo "Chưa phân công" → chuẩn bị form tạo mới
                    selectedPc = null;
                    txtPcId.setText("PC" + (System.currentTimeMillis() % 100000));
                    txtPcId.setEditable(true);
                    comboPcSp.setValue(newVal.getSanPham());
                    comboPcNv.setValue(null);
                    pickerPcDate.setValue(java.time.LocalDate.now());
                    chkAllQty.setSelected(false);
                    txtPcQty.setText(newVal.getSoLuong());
                    txtPcQty.setDisable(false);
                    updateRemainingLabel(newVal.getSanPham());
                } else {
                    // Hàng thật → chỉnh sửa bản ghi hiện tại
                    selectedPc = newVal;
                    txtPcId.setText(newVal.getMaPhanCong());
                    txtPcId.setEditable(false);
                    comboPcSp.setValue(newVal.getSanPham());
                    comboPcNv.setValue(newVal.getNhanVien());
                    pickerPcDate.setValue(newVal.getNgayPhanCong());
                    String qty = newVal.getSoLuong();
                    if (qty == null || qty.isEmpty() || "Tất cả".equals(qty)) {
                        chkAllQty.setSelected(true);
                        txtPcQty.setText("Tất cả");
                        txtPcQty.setDisable(true);
                    } else {
                        chkAllQty.setSelected(false);
                        txtPcQty.setText(qty);
                        txtPcQty.setDisable(false);
                    }
                    updateRemainingLabel(newVal.getSanPham());
                }
            }
        });

        btnSave.setOnAction(e -> {
            String pcId = txtPcId.getText().trim();
            SanPham sp = comboPcSp.getValue();
            NhanVien nv = comboPcNv.getValue();
            LocalDate pDate = pickerPcDate.getValue();
            String qty = chkAllQty.isSelected() ? "Tất cả" : txtPcQty.getText().trim();

            if (pcId.isEmpty() || sp == null || nv == null || pDate == null || qty.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng nhập đầy đủ thông tin phân công!");
                return;
            }

            // Tính remaining: nếu đang sửa một bản ghi (selectedPc != null) thì loại trừ bản ghi đó
            int remaining = getRemainingQty(sp, selectedPc != null ? selectedPc.getMaPhanCong() : null);

            if (chkAllQty.isSelected()) {
                if (remaining <= 0) {
                    showAlert(Alert.AlertType.WARNING, "Hết công suất",
                        "Sản phẩm này đã được phân công hết " + sp.getTongSoBoDuKien() + " bộ!\n"
                        + "Vui lòng chỉnh sửa hoặc xóa phân công hiện có trước.");
                    return;
                }
            } else {
                try {
                    int val = Integer.parseInt(qty);
                    if (val <= 0) {
                        showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Số lượng phải là số nguyên dương lớn hơn 0!");
                        return;
                    }
                    if (val > remaining) {
                        showAlert(Alert.AlertType.WARNING, "Lỗi phân công vượt hạn mức",
                            "Số lượng nhập (" + val + ") vượt quá số bộ còn lại chưa phân công (" + remaining + " bộ).\n"
                            + "Tổng dự kiến: " + sp.getTongSoBoDuKien() + " bộ.");
                        return;
                    }
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Số lượng phải là số nguyên dương hoặc chọn 'Tất cả'!");
                    return;
                }
            }

            if (selectedPc == null) {
                if (service.getAllPhanCong().stream().anyMatch(p -> p.getMaPhanCong().equals(pcId))) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã phân công đã tồn tại!");
                    return;
                }
                PhanCongSanPham pc = new PhanCongSanPham(pcId, sp, nv, pDate, qty);
                service.addPhanCong(pc);
            } else {
                selectedPc.setSanPham(sp);
                selectedPc.setNhanVien(nv);
                selectedPc.setNgayPhanCong(pDate);
                selectedPc.setSoLuong(qty);
                service.updatePhanCong(selectedPc);
            }

            // Tự động cập nhật trạng thái sản phẩm dựa trên chuyên môn thợ được phân công
            if (sp.getTrangThaiSanPham() != TrangThaiSanPham.DaHoanThanh) {
                TrangThaiSanPham newSpStatus = null;
                if (nv.getChuyenMon() == ChuyenMon.ThoUi) {
                    newSpStatus = TrangThaiSanPham.DangUi; // Thợ ủi → Chuyển sang Đang ủi
                } else if (nv.getChuyenMon() == ChuyenMon.ThoMay
                        && sp.getTrangThaiSanPham() == TrangThaiSanPham.DangCat) {
                    newSpStatus = TrangThaiSanPham.DangMay; // Thợ may (từ giai đoạn cắt) → Đang may
                }
                if (newSpStatus != null && sp.getTrangThaiSanPham() != newSpStatus) {
                    sp.setTrangThaiSanPham(newSpStatus);
                    spService.updateSanPham(sp);
                    if (onSanPhamUpdatedCallback != null) {
                        onSanPhamUpdatedCallback.run();
                    }
                }
            }

            refreshAssignments();
            clearAssignForm();
        });

        btnDelete.setOnAction(e -> {
            if (selectedPc != null && (selectedPc.getMaPhanCong() == null || !selectedPc.getMaPhanCong().startsWith("__UNASSIGNED__"))) {
                service.deletePhanCong(selectedPc.getMaPhanCong());
                refreshAssignments();
                clearAssignForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn một phân công thật (không phải hàng chưa phân công) để xóa!");
            }
        });

        btnClear.setOnAction(e -> clearAssignForm());

        // Context Menu (chuột phải) cho bảng phân công
        tablePc.setRowFactory(tv -> {
            TableRow<PhanCongSanPham> row = new TableRow<>() {
                @Override
                protected void updateItem(PhanCongSanPham item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                    } else if (item.getMaPhanCong() != null && item.getMaPhanCong().startsWith("__UNASSIGNED__")) {
                        // Hàng chưa phân công: in nghiêng, màu xám nhạt
                        setStyle("-fx-font-style: italic; -fx-text-fill: #9ca3af;");
                    } else {
                        setStyle("");
                    }
                }
            };
            ContextMenu contextMenu = new ContextMenu();

            MenuItem editItem = new MenuItem("✏️  Chỉnh sửa");
            editItem.setOnAction(event -> {
                if (row.getItem() != null) tablePc.getSelectionModel().select(row.getItem());
            });

            MenuItem deleteItem = new MenuItem("🗑️  Xóa Phân Công");
            deleteItem.setOnAction(event -> {
                PhanCongSanPham rowData = row.getItem();
                if (rowData != null && (rowData.getMaPhanCong() == null || !rowData.getMaPhanCong().startsWith("__UNASSIGNED__"))) {
                    tablePc.getSelectionModel().select(rowData);
                    btnDelete.fire();
                }
            });

            MenuItem assignItem = new MenuItem("➕  Tạo phân công từ đây");
            assignItem.setOnAction(event -> {
                if (row.getItem() != null) tablePc.getSelectionModel().select(row.getItem());
            });

            MenuItem copyIdItem = new MenuItem("📋  Sao chép mã phân công");
            copyIdItem.setOnAction(event -> {
                PhanCongSanPham rowData = row.getItem();
                if (rowData != null && rowData.getMaPhanCong() != null && !rowData.getMaPhanCong().startsWith("__UNASSIGNED__")) {
                    javafx.scene.input.Clipboard cb = javafx.scene.input.Clipboard.getSystemClipboard();
                    javafx.scene.input.ClipboardContent cc = new javafx.scene.input.ClipboardContent();
                    cc.putString(rowData.getMaPhanCong());
                    cb.setContent(cc);
                }
            });

            // Hiển thị menu khác nhau tuỳ loại hàng
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                contextMenu.getItems().clear();
                if (newItem != null && newItem.getMaPhanCong() != null && newItem.getMaPhanCong().startsWith("__UNASSIGNED__")) {
                    contextMenu.getItems().addAll(assignItem);
                } else {
                    contextMenu.getItems().addAll(editItem, deleteItem, new SeparatorMenuItem(), copyIdItem);
                }
            });

            row.emptyProperty().addListener((obs, wasEmpty, isEmpty) ->
                row.setContextMenu(isEmpty ? null : contextMenu)
            );
            return row;
        });

        // Initial fill will be called at the end of constructor
        return root;
    }

    private void refreshStaff() {
        List<NhanVien> list = service.getAllNhanVien();
        tableNv.setItems(FXCollections.observableArrayList(list));
        comboPcNv.setItems(FXCollections.observableArrayList(list));
    }

    private void refreshAssignments() {
        List<PhanCongSanPham> realList = service.getAllPhanCong();
        List<SanPham> allProducts = spService.getAllSanPham();

        // Danh sách hiển thị = phân công thật + hàng ảo "Chưa phân công" cho phần còn lại
        List<PhanCongSanPham> displayList = new ArrayList<>(realList);
        for (SanPham sp : allProducts) {
            int remaining = getRemainingQty(sp, null);
            if (remaining > 0) {
                PhanCongSanPham unassigned = new PhanCongSanPham(
                    "__UNASSIGNED__" + sp.getMaSanPham(), sp, null, null, String.valueOf(remaining));
                displayList.add(unassigned);
            }
        }
        tablePc.setItems(FXCollections.observableArrayList(displayList));
        tablePc.refresh(); // Buộc JavaFX re-render lại tất cả các ô trong bảng

        // Cập nhật combo sản phẩm (luôn hiện tất cả)
        SanPham currentSp = comboPcSp.getValue();
        comboPcSp.setItems(FXCollections.observableArrayList(allProducts));
        if (currentSp != null) {
            for (SanPham sp : allProducts) {
                if (sp.getMaSanPham().equals(currentSp.getMaSanPham())) {
                    comboPcSp.setValue(sp);
                    break;
                }
            }
        }
    }

    private void clearStaffForm() {
        selectedNv = null;
        txtNvId.clear();
        txtNvId.setEditable(true);
        txtNvName.clear();
        txtNvPhone.clear();
        txtNvNote.clear();
        comboChuyenMon.setValue(ChuyenMon.ThoMay);
        tableNv.getSelectionModel().clearSelection();
    }

    private void clearAssignForm() {
        selectedPc = null;
        txtPcId.clear();
        txtPcId.setEditable(true);
        comboPcSp.setValue(null);
        comboPcNv.setValue(null);
        pickerPcDate.setValue(LocalDate.now());
        if (chkAllQty != null) {
            chkAllQty.setSelected(true);
        }
        if (txtPcQty != null) {
            txtPcQty.setText("Tất cả");
            txtPcQty.setDisable(true);
        }
        if (lblRemainingInfo != null) {
            lblRemainingInfo.setText("");
        }
        tablePc.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void selectProductAndAssignTab(SanPham sp) {
        clearAssignForm();
        if (tabPane != null && tabPane.getTabs().size() > 1) {
            tabPane.getSelectionModel().select(1); // Select 2nd tab (Phân Công)
        }
        if (comboPcSp != null) {
            // Need to make sure sp is in combo list before selecting
            refreshAssignments();
            comboPcSp.setValue(sp);
        }
        if (txtPcId != null) {
            txtPcId.setText("PC" + System.currentTimeMillis() % 1000000);
        }
    }

    public int getRemainingQty(SanPham sp, String excludePcId) {
        if (sp == null) return 0;
        int total = sp.getTongSoBoDuKien();
        int assigned = 0;
        for (PhanCongSanPham pc : service.getAllPhanCong()) {
            if (excludePcId != null && pc.getMaPhanCong().equals(excludePcId)) {
                continue;
            }
            if (pc.getSanPham() != null && pc.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) {
                String qtyStr = pc.getSoLuong();
                if ("Tất cả".equalsIgnoreCase(qtyStr)) {
                    assigned = total;
                    break;
                } else {
                    try {
                        assigned += Integer.parseInt(qtyStr);
                    } catch (NumberFormatException e) {
                        // ignore
                    }
                }
            }
        }
        return Math.max(0, total - assigned);
    }

    private void updateRemainingLabel(SanPham sp) {
        if (sp == null) {
            lblRemainingInfo.setText("");
            return;
        }
        String pcId = selectedPc != null ? selectedPc.getMaPhanCong() : null;
        int rem = getRemainingQty(sp, pcId);
        lblRemainingInfo.setText("Tổng dự kiến: " + sp.getTongSoBoDuKien() + " bộ | Còn lại: " + rem + " bộ");
    }
}
