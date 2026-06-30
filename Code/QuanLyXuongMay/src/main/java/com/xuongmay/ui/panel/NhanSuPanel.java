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
    private PhanCongSanPham selectedPc;

    public NhanSuPanel() {
        setSpacing(15);
        setPadding(new Insets(15));
        VBox.setVgrow(this, Priority.ALWAYS);

        Label lblTitle = new Label("Quản Lý Nhân Sự & Phân Công");
        lblTitle.getStyleClass().add("tab-title");

        TabPane tabPane = new TabPane();
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
        colPcId.setCellValueFactory(new PropertyValueFactory<>("maPhanCong"));
        colPcId.setPrefWidth(90);

        TableColumn<PhanCongSanPham, String> colPcSp = new TableColumn<>("Sản Phẩm");
        colPcSp.setCellValueFactory(cellData -> {
            if (cellData.getValue().getSanPham() != null) {
                return new SimpleStringProperty(cellData.getValue().getSanPham().getTenSanPham());
            }
            return new SimpleStringProperty("N/A");
        });
        colPcSp.setPrefWidth(180);

        TableColumn<PhanCongSanPham, String> colPcNv = new TableColumn<>("Nhân Viên Nhận");
        colPcNv.setCellValueFactory(cellData -> {
            if (cellData.getValue().getNhanVien() != null) {
                return new SimpleStringProperty(cellData.getValue().getNhanVien().getTenNhanVien());
            }
            return new SimpleStringProperty("N/A");
        });
        colPcNv.setPrefWidth(180);

        TableColumn<PhanCongSanPham, String> colPcDate = new TableColumn<>("Ngày Phân Công");
        colPcDate.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNgayPhanCong().toString()));
        colPcDate.setPrefWidth(120);

        tablePc.getColumns().addAll(colPcId, colPcSp, colPcNv, colPcDate);
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
        comboPcNv = new ComboBox<>();
        pickerPcDate = new DatePicker(LocalDate.now());

        grid.add(new Label("Mã PC:"), 0, 0);
        grid.add(txtPcId, 1, 0);
        grid.add(new Label("Sản phẩm:"), 0, 1);
        grid.add(comboPcSp, 1, 1);
        grid.add(new Label("Nhân viên:"), 0, 2);
        grid.add(comboPcNv, 1, 2);
        grid.add(new Label("Ngày giao:"), 0, 3);
        grid.add(pickerPcDate, 1, 3);

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
                selectedPc = newVal;
                txtPcId.setText(newVal.getMaPhanCong());
                txtPcId.setEditable(false);
                comboPcSp.setValue(newVal.getSanPham());
                comboPcNv.setValue(newVal.getNhanVien());
                pickerPcDate.setValue(newVal.getNgayPhanCong());
            }
        });

        btnSave.setOnAction(e -> {
            String pcId = txtPcId.getText().trim();
            SanPham sp = comboPcSp.getValue();
            NhanVien nv = comboPcNv.getValue();
            LocalDate pDate = pickerPcDate.getValue();

            if (pcId.isEmpty() || sp == null || nv == null || pDate == null) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng nhập đầy đủ thông tin phân công!");
                return;
            }

            if (selectedPc == null) {
                if (service.getAllPhanCong().stream().anyMatch(p -> p.getMaPhanCong().equals(pcId))) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã phân công đã tồn tại!");
                    return;
                }
                PhanCongSanPham pc = new PhanCongSanPham(pcId, sp, nv, pDate);
                service.addPhanCong(pc);
            } else {
                selectedPc.setSanPham(sp);
                selectedPc.setNhanVien(nv);
                selectedPc.setNgayPhanCong(pDate);
                service.updatePhanCong(selectedPc);
            }
            refreshAssignments();
            clearAssignForm();
        });

        btnDelete.setOnAction(e -> {
            if (selectedPc != null) {
                service.deletePhanCong(selectedPc.getMaPhanCong());
                refreshAssignments();
                clearAssignForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn một phân công để xóa!");
            }
        });

        btnClear.setOnAction(e -> clearAssignForm());

        // Initial fill will be called at the end of constructor
        return root;
    }

    private void refreshStaff() {
        List<NhanVien> list = service.getAllNhanVien();
        tableNv.setItems(FXCollections.observableArrayList(list));
        comboPcNv.setItems(FXCollections.observableArrayList(list));
    }

    private void refreshAssignments() {
        tablePc.setItems(FXCollections.observableArrayList(service.getAllPhanCong()));
        comboPcSp.setItems(FXCollections.observableArrayList(spService.getAllSanPham()));
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
        tablePc.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
