package com.xuongmay.ui.panel;

import com.xuongmay.model.KhachHang;
import com.xuongmay.service.BanHangService;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import java.util.List;

public class KhachHangPanel extends VBox {
    private final BanHangService service = new BanHangService();
    private TableView<KhachHang> tableKh;
    private TextField txtKhId, txtKhName, txtKhPhone, txtKhAddress, txtKhTaxCode, txtKhNote;
    private KhachHang selectedKh;
    private java.util.function.Consumer<KhachHang> onCreateOrderCallback;

    public void setOnCreateOrderCallback(java.util.function.Consumer<KhachHang> callback) {
        this.onCreateOrderCallback = callback;
    }

    public KhachHangPanel() {
        setSpacing(15);
        setPadding(new Insets(15));
        VBox.setVgrow(this, Priority.ALWAYS);

        Label lblTitle = new Label("Quản Lý Khách Hàng");
        lblTitle.getStyleClass().add("tab-title");

        HBox root = new HBox(20);
        HBox.setHgrow(root, Priority.ALWAYS);
        VBox.setVgrow(root, Priority.ALWAYS);

        // Table (Left)
        VBox tableBox = new VBox(10);
        HBox.setHgrow(tableBox, Priority.ALWAYS);

        tableKh = new TableView<>();
        VBox.setVgrow(tableKh, Priority.ALWAYS);

        TableColumn<KhachHang, String> colId = new TableColumn<>("Mã Khách");
        colId.setCellValueFactory(new PropertyValueFactory<>("maKhachHang"));
        colId.setPrefWidth(90);

        TableColumn<KhachHang, String> colName = new TableColumn<>("Họ Tên");
        colName.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));
        colName.setPrefWidth(180);

        TableColumn<KhachHang, String> colPhone = new TableColumn<>("Số Điện Thoại");
        colPhone.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        colPhone.setPrefWidth(120);

        TableColumn<KhachHang, String> colAddress = new TableColumn<>("Địa Chỉ");
        colAddress.setCellValueFactory(new PropertyValueFactory<>("diaChiNha"));
        colAddress.setPrefWidth(220);

        TableColumn<KhachHang, String> colTax = new TableColumn<>("Mã Số Thuế");
        colTax.setCellValueFactory(new PropertyValueFactory<>("maSoThue"));
        colTax.setPrefWidth(120);

        tableKh.getColumns().addAll(colId, colName, colPhone, colAddress, colTax);
        tableBox.getChildren().addAll(new Label("Danh sách đối tác khách hàng"), tableKh);

        // Form (Right)
        VBox formBox = new VBox(12);
        formBox.setPrefWidth(320);
        formBox.setMinWidth(320);
        formBox.getStyleClass().add("card-panel");

        Label lblFormTitle = new Label("Thông Tin Khách Hàng");
        lblFormTitle.getStyleClass().add("sub-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints(90);
        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints(180);
        grid.getColumnConstraints().addAll(col1, col2);

        txtKhId = new TextField();
        txtKhName = new TextField();
        txtKhPhone = new TextField();
        txtKhAddress = new TextField();
        txtKhTaxCode = new TextField();
        txtKhNote = new TextField();

        grid.add(new Label("Mã khách:"), 0, 0);
        grid.add(txtKhId, 1, 0);
        grid.add(new Label("Họ tên:"), 0, 1);
        grid.add(txtKhName, 1, 1);
        grid.add(new Label("Điện thoại:"), 0, 2);
        grid.add(txtKhPhone, 1, 2);
        grid.add(new Label("Địa chỉ:"), 0, 3);
        grid.add(txtKhAddress, 1, 3);
        grid.add(new Label("Mã số thuế:"), 0, 4);
        grid.add(txtKhTaxCode, 1, 4);
        grid.add(new Label("Ghi chú:"), 0, 5);
        grid.add(txtKhNote, 1, 5);

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
        getChildren().addAll(lblTitle, root);

        // Events
        tableKh.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedKh = newVal;
                txtKhId.setText(newVal.getMaKhachHang());
                txtKhId.setEditable(false);
                txtKhName.setText(newVal.getTenKhachHang());
                txtKhPhone.setText(newVal.getSdt());
                txtKhAddress.setText(newVal.getDiaChiNha());
                txtKhTaxCode.setText(newVal.getMaSoThue());
                txtKhNote.setText(newVal.getGhiChu());
            }
        });

        btnSave.setOnAction(e -> {
            String id = txtKhId.getText().trim();
            String name = txtKhName.getText().trim();
            String phone = txtKhPhone.getText().trim();
            String addr = txtKhAddress.getText().trim();
            String tax = txtKhTaxCode.getText().trim();
            String note = txtKhNote.getText().trim();

            if (id.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng nhập đầy đủ Mã, Họ tên và Số điện thoại!");
                return;
            }

            if (selectedKh == null) {
                if (service.getKhachHangById(id) != null) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã khách hàng đã tồn tại!");
                    return;
                }
                KhachHang kh = new KhachHang(id, name, phone, addr, tax, note);
                service.addKhachHang(kh);
            } else {
                selectedKh.setTenKhachHang(name);
                selectedKh.setSdt(phone);
                selectedKh.setDiaChiNha(addr);
                selectedKh.setMaSoThue(tax);
                selectedKh.setGhiChu(note);
                service.updateKhachHang(selectedKh);
            }
            refreshData();
            clearForm();
        });

        btnDelete.setOnAction(e -> {
            if (selectedKh != null) {
                service.deleteKhachHang(selectedKh.getMaKhachHang());
                refreshData();
                clearForm();
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn khách hàng để xóa!");
            }
        });

        btnClear.setOnAction(e -> clearForm());

        // Context Menu (Hidden Menu) khi click chuột phải vào dòng khách hàng
        tableKh.setRowFactory(tv -> {
            TableRow<KhachHang> row = new TableRow<>();
            ContextMenu contextMenu = new ContextMenu();
            
            MenuItem editItem = new MenuItem("Chỉnh sửa");
            editItem.setOnAction(event -> {
                KhachHang rowData = row.getItem();
                if (rowData != null) {
                    tableKh.getSelectionModel().select(rowData);
                }
            });
            
            MenuItem deleteItem = new MenuItem("Xóa Khách Hàng");
            deleteItem.setOnAction(event -> {
                KhachHang rowData = row.getItem();
                if (rowData != null) {
                    tableKh.getSelectionModel().select(rowData);
                    btnDelete.fire();
                }
            });
            
            MenuItem createOrderItem = new MenuItem("Tạo đơn hàng");
            createOrderItem.setOnAction(event -> {
                KhachHang rowData = row.getItem();
                if (rowData != null && onCreateOrderCallback != null) {
                    onCreateOrderCallback.accept(rowData);
                }
            });

            MenuItem copyPhoneItem = new MenuItem("Sao chép số điện thoại");
            copyPhoneItem.setOnAction(event -> {
                KhachHang rowData = row.getItem();
                if (rowData != null && rowData.getSdt() != null) {
                    javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
                    javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
                    content.putString(rowData.getSdt());
                    clipboard.setContent(content);
                }
            });

            contextMenu.getItems().addAll(createOrderItem, new SeparatorMenuItem(), editItem, deleteItem, new SeparatorMenuItem(), copyPhoneItem);

            row.emptyProperty().addListener((obs, wasEmpty, isEmpty) -> {
                if (isEmpty) {
                    row.setContextMenu(null);
                } else {
                    row.setContextMenu(contextMenu);
                }
            });
            return row;
        });

        refreshData();
    }

    public void refreshData() {
        tableKh.setItems(FXCollections.observableArrayList(service.getAllKhachHang()));
    }

    private void clearForm() {
        selectedKh = null;
        txtKhId.clear();
        txtKhId.setEditable(true);
        txtKhName.clear();
        txtKhPhone.clear();
        txtKhAddress.clear();
        txtKhTaxCode.clear();
        txtKhNote.clear();
        tableKh.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
