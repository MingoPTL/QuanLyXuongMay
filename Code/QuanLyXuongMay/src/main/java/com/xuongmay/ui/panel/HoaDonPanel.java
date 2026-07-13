package com.xuongmay.ui.panel;

import com.xuongmay.model.*;
import com.xuongmay.service.BanHangService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.print.PrinterJob;
import com.xuongmay.ui.dialog.HoaDonQrDialog;
import java.time.LocalDate;
import java.util.List;

public class HoaDonPanel extends VBox {
    private final BanHangService service = new BanHangService();
    private TableView<HoaDon> tableHd;
    private TableView<ChiTietDonHang> tableCtdh;
    private TextField txtHdId, txtHdAmount;
    private ComboBox<PhuongThucThanhToan> comboPayMethod;
    private ComboBox<TrangThaiHoaDon> comboStatus;
    private DatePicker pickerDate;
    private Label lblOrderInfo;
    private HoaDon selectedHd;
    private TextField txtCustName, txtCustPhone, txtCustTaxCode, txtCustAddress, txtOrderNote;
    private Button btnDelete, btnPay;

    public HoaDonPanel() {
        setSpacing(15);
        setPadding(new Insets(15));
        VBox.setVgrow(this, Priority.ALWAYS);

        Label lblTitle = new Label("Quản Lý Hóa Đơn & Thanh Toán");
        lblTitle.getStyleClass().add("tab-title");

        SplitPane split = new SplitPane();
        VBox.setVgrow(split, Priority.ALWAYS);

        // LEFT: Invoices Table
        VBox left = new VBox(10);
        left.setPadding(new Insets(10));
        left.setPrefWidth(500);

        tableHd = new TableView<>();
        VBox.setVgrow(tableHd, Priority.ALWAYS);

        TableColumn<HoaDon, String> colId = new TableColumn<>("Mã Hóa Đơn");
        colId.setCellValueFactory(new PropertyValueFactory<>("maHoaDon"));
        colId.setPrefWidth(100);

        TableColumn<HoaDon, String> colOrder = new TableColumn<>("Mã Đơn Hàng");
        colOrder.setCellValueFactory(cellData -> {
            if (cellData.getValue().getDonHang() != null) {
                return new SimpleStringProperty(cellData.getValue().getDonHang().getMaDonHang());
            }
            return new SimpleStringProperty("N/A");
        });
        colOrder.setPrefWidth(100);

        TableColumn<HoaDon, Double> colAmount = new TableColumn<>("Tổng Tiền");
        colAmount.setCellValueFactory(new PropertyValueFactory<>("tongTienHoaDon"));
        colAmount.setPrefWidth(120);

        TableColumn<HoaDon, String> colPayMethod = new TableColumn<>("Thanh Toán");
        colPayMethod.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPhuongThucThanhToan().toString()));
        colPayMethod.setPrefWidth(110);

        TableColumn<HoaDon, String> colStatus = new TableColumn<>("Trạng Thái");
        colStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrangThaiHoaDon().toString()));
        colStatus.setPrefWidth(110);

        tableHd.getColumns().addAll(colId, colOrder, colAmount, colPayMethod, colStatus);
        left.getChildren().addAll(new Label("Danh sách hóa đơn bán hàng"), tableHd);

        // RIGHT: Invoice detail & modification
        VBox right = new VBox(15);
        right.setPadding(new Insets(10));
        right.setPrefWidth(650);

        VBox formBox = new VBox(10);
        formBox.setPrefWidth(600);
        formBox.setMinWidth(600);
        formBox.getStyleClass().add("card-panel");
        Label lblFormTitle = new Label("Chi Tiết Hóa Đơn");
        lblFormTitle.getStyleClass().add("sub-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints(95);
        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints(175);
        javafx.scene.layout.ColumnConstraints col3 = new javafx.scene.layout.ColumnConstraints(100);
        javafx.scene.layout.ColumnConstraints col4 = new javafx.scene.layout.ColumnConstraints(200);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        txtHdId = new TextField();
        txtHdId.setEditable(true);
        txtHdAmount = new TextField();
        txtHdAmount.setEditable(false);
        pickerDate = new DatePicker();
        pickerDate.setDisable(true);
        comboPayMethod = new ComboBox<>(FXCollections.observableArrayList(PhuongThucThanhToan.values()));
        comboPayMethod.setDisable(true);
        comboStatus = new ComboBox<>(FXCollections.observableArrayList(TrangThaiHoaDon.values()));
        comboStatus.setDisable(true);
        lblOrderInfo = new Label("Thông tin đơn hàng liên quan: ");
        lblOrderInfo.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");

        txtCustName = new TextField();
        txtCustName.setEditable(false);
        txtCustPhone = new TextField();
        txtCustPhone.setEditable(false);
        txtCustTaxCode = new TextField();
        txtCustTaxCode.setEditable(false);
        txtCustAddress = new TextField();
        txtCustAddress.setEditable(false);
        txtOrderNote = new TextField();
        txtOrderNote.setEditable(false);

        grid.add(new Label("Mã hóa đơn:"), 0, 0);
        grid.add(txtHdId, 1, 0);
        grid.add(new Label("Tổng tiền:"), 0, 1);
        grid.add(txtHdAmount, 1, 1);
        grid.add(new Label("Ngày lập:"), 0, 2);
        grid.add(pickerDate, 1, 2);
        grid.add(new Label("P.Thức T.Toán:"), 0, 3);
        grid.add(comboPayMethod, 1, 3);
        grid.add(new Label("Trạng thái:"), 0, 4);
        grid.add(comboStatus, 1, 4);

        grid.add(new Label("Khách hàng:"), 2, 0);
        grid.add(txtCustName, 3, 0);
        grid.add(new Label("Số điện thoại:"), 2, 1);
        grid.add(txtCustPhone, 3, 1);
        grid.add(new Label("Mã số thuế:"), 2, 2);
        grid.add(txtCustTaxCode, 3, 2);
        grid.add(new Label("Địa chỉ khách:"), 2, 3);
        grid.add(txtCustAddress, 3, 3);
        grid.add(new Label("Ghi chú đơn:"), 2, 4);
        grid.add(txtOrderNote, 3, 4);

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        Button btnSave = new Button("Cập Nhật HĐ");
        btnSave.getStyleClass().addAll("btn", "btn-success");

        btnDelete = new Button("Xóa HĐ");
        btnDelete.getStyleClass().addAll("btn", "btn-danger");
        btnDelete.setStyle("-fx-background-color: #ef4444; -fx-text-fill: white;");

        btnPay = new Button();
        btnPay.getStyleClass().addAll("btn", "btn-warning");
        btnPay.setStyle("-fx-background-color: #f59e0b; -fx-text-fill: white; -fx-font-weight: bold;");
        btnPay.setVisible(false);
        btnPay.setManaged(false);

        Button btnPrint = new Button("In Hóa Đơn");
        btnPrint.getStyleClass().addAll("btn", "btn-primary");

        btnBox.getChildren().addAll(btnSave, btnDelete, btnPay, btnPrint);
        formBox.getChildren().addAll(lblFormTitle, grid, lblOrderInfo, btnBox);

        // Details products inside order table
        tableCtdh = new TableView<>();
        tableCtdh.setPrefHeight(180);
        
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

        right.getChildren().addAll(formBox, new Label("Danh sách sản phẩm trong hóa đơn"), tableCtdh);
        split.getItems().addAll(left, right);

        getChildren().addAll(lblTitle, split);

        // Events
        tableHd.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedHd = newVal;
                txtHdId.setText(newVal.getMaHoaDon());
                txtHdId.setEditable(true);
                txtHdAmount.setText(String.format("%,.0f đ", newVal.getTongTienHoaDon()));
                pickerDate.setValue(newVal.getNgayLap());
                comboPayMethod.setValue(newVal.getPhuongThucThanhToan());
                comboStatus.setValue(newVal.getTrangThaiHoaDon());

                DonHang dh = newVal.getDonHang();
                if (dh != null) {
                    lblOrderInfo.setText("Mã Đơn: " + dh.getMaDonHang());
                    KhachHang kh = dh.getKhachHang();
                    if (kh != null) {
                        txtCustName.setText(kh.getTenKhachHang());
                        txtCustPhone.setText(kh.getSdt());
                        txtCustTaxCode.setText(kh.getMaSoThue() != null ? kh.getMaSoThue() : "");
                        txtCustAddress.setText(kh.getDiaChiNha() != null ? kh.getDiaChiNha() : "");
                    } else {
                        txtCustName.clear();
                        txtCustPhone.clear();
                        txtCustTaxCode.clear();
                        txtCustAddress.clear();
                    }
                    txtOrderNote.setText(dh.getGhiChu() != null ? dh.getGhiChu() : "");
                    tableCtdh.setItems(FXCollections.observableArrayList(service.getChiTietByDonHangId(dh.getMaDonHang())));
                } else {
                    lblOrderInfo.setText("Thông tin đơn hàng liên quan: Không có");
                    txtCustName.clear();
                    txtCustPhone.clear();
                    txtCustTaxCode.clear();
                    txtCustAddress.clear();
                    txtOrderNote.clear();
                    tableCtdh.setItems(FXCollections.emptyObservableList());
                }

                // Check payment button visibility and label
                if (newVal.getTrangThaiHoaDon() == TrangThaiHoaDon.ChuaThanhToan) {
                    if (newVal.getPhuongThucThanhToan() == PhuongThucThanhToan.TienMat) {
                        btnPay.setText("Xác Nhận Thanh Toán");
                        btnPay.setVisible(true);
                        btnPay.setManaged(true);
                    } else if (newVal.getPhuongThucThanhToan() == PhuongThucThanhToan.ChuyenKhoan) {
                        btnPay.setText("Thanh Toán QR");
                        btnPay.setVisible(true);
                        btnPay.setManaged(true);
                    } else {
                        btnPay.setVisible(false);
                        btnPay.setManaged(false);
                    }
                } else {
                    btnPay.setVisible(false);
                    btnPay.setManaged(false);
                }
            } else {
                btnPay.setVisible(false);
                btnPay.setManaged(false);
            }
        });

        btnSave.setOnAction(e -> {
            if (selectedHd == null) {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn hóa đơn để cập nhật!");
                return;
            }
            String newHdId = txtHdId.getText().trim();
            if (newHdId.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Mã hóa đơn không được để trống!");
                return;
            }

            // Nếu người dùng thay đổi mã hóa đơn
            if (!newHdId.equals(selectedHd.getMaHoaDon())) {
                boolean exists = service.getAllHoaDon().stream()
                        .anyMatch(h -> h.getMaHoaDon().equalsIgnoreCase(newHdId));
                if (exists) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Mã hóa đơn mới đã tồn tại!");
                    return;
                }
                
                // Lưu lại thông tin cũ và đổi mã hóa đơn
                service.deleteHoaDon(selectedHd.getMaHoaDon());
                selectedHd.setMaHoaDon(newHdId);
                service.addHoaDon(selectedHd);
            }

            refreshData();
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật mã hóa đơn!");
        });

        btnDelete.setOnAction(e -> {
            if (selectedHd == null) {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn hóa đơn để xóa!");
                return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Xác nhận xóa");
            confirm.setHeaderText(null);
            confirm.setContentText("Bạn có chắc chắn muốn xóa hóa đơn " + selectedHd.getMaHoaDon() + "?");
            if (confirm.showAndWait().orElse(null) == ButtonType.OK) {
                service.deleteHoaDon(selectedHd.getMaHoaDon());
                selectedHd = null;
                txtHdId.clear();
                txtHdAmount.clear();
                txtCustName.clear();
                txtCustPhone.clear();
                txtCustTaxCode.clear();
                txtCustAddress.clear();
                txtOrderNote.clear();
                tableCtdh.setItems(FXCollections.emptyObservableList());
                btnPay.setVisible(false);
                btnPay.setManaged(false);
                refreshData();
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xóa hóa đơn!");
            }
        });

        btnPay.setOnAction(e -> {
            if (selectedHd == null) return;
            if (selectedHd.getPhuongThucThanhToan() == PhuongThucThanhToan.TienMat) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Xác nhận thanh toán");
                confirm.setHeaderText(null);
                confirm.setContentText("Xác nhận khách hàng đã thanh toán tiền mặt cho hóa đơn " + selectedHd.getMaHoaDon() + "?");
                if (confirm.showAndWait().orElse(null) == ButtonType.OK) {
                    selectedHd.setTrangThaiHoaDon(TrangThaiHoaDon.DaThanhToan);
                    service.updateHoaDon(selectedHd);
                    
                    // Đồng bộ trạng thái thanh toán của Đơn Hàng tương ứng
                    DonHang dh = selectedHd.getDonHang();
                    if (dh != null) {
                        service.updateDonHang(dh);
                    }
                    
                    btnPay.setVisible(false);
                    btnPay.setManaged(false);
                    refreshData();
                    tableHd.getSelectionModel().clearSelection();
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã xác nhận thanh toán tiền mặt!");
                }
            } else if (selectedHd.getPhuongThucThanhToan() == PhuongThucThanhToan.ChuyenKhoan) {
                HoaDonQrDialog dialog = new HoaDonQrDialog(selectedHd, service, getScene().getWindow());
                dialog.showAndWait();
                refreshData();
                tableHd.getSelectionModel().clearSelection();
            }
        });

        btnPrint.setOnAction(e -> {
            if (selectedHd == null) {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn hóa đơn để in!");
                return;
            }
            StringBuilder receipt = new StringBuilder();
            receipt.append("=========================================\n");
            receipt.append("           XƯỞNG MAY ANTIGRAVITY         \n");
            receipt.append("             HÓA ĐƠN BÁN HÀNG            \n");
            receipt.append("=========================================\n");
            receipt.append("Mã HĐ: ").append(selectedHd.getMaHoaDon()).append("\n");
            receipt.append("Ngày lập: ").append(selectedHd.getNgayLap()).append("\n");
            DonHang dh = selectedHd.getDonHang();
            if (dh != null && dh.getKhachHang() != null) {
                receipt.append("Khách hàng: ").append(dh.getKhachHang().getTenKhachHang()).append("\n");
                receipt.append("SĐT: ").append(dh.getKhachHang().getSdt()).append("\n");
                receipt.append("MST: ").append(dh.getKhachHang().getMaSoThue() != null ? dh.getKhachHang().getMaSoThue() : "").append("\n");
                receipt.append("Địa chỉ: ").append(dh.getKhachHang().getDiaChiNha() != null ? dh.getKhachHang().getDiaChiNha() : "").append("\n");
            }
            if (dh != null && dh.getGhiChu() != null && !dh.getGhiChu().isEmpty()) {
                receipt.append("Ghi chú đơn: ").append(dh.getGhiChu()).append("\n");
            }
            receipt.append("-----------------------------------------\n");
            receipt.append(String.format("%-18s %-6s %-8s %-10s\n", "Sản phẩm", "SL Ri", "Đơn giá", "Thành tiền"));
            receipt.append("-----------------------------------------\n");
            List<ChiTietDonHang> items = tableCtdh.getItems();
            for (ChiTietDonHang item : items) {
                receipt.append(String.format("%-18.18s %-6d %,.0f %,.0f đ\n",
                        item.getSanPham().getTenSanPham(),
                        item.getSoLuongRi(),
                        item.getDonGiaRi(),
                        item.getThanhTien()));
            }
            receipt.append("-----------------------------------------\n");
            receipt.append(String.format("Tổng thanh toán: %,.0f đ\n", selectedHd.getTongTienHoaDon()));
            receipt.append("P.Thức T.Toán: ").append(selectedHd.getPhuongThucThanhToan()).append("\n");
            receipt.append("Trạng thái: ").append(selectedHd.getTrangThaiHoaDon()).append("\n");
            receipt.append("=========================================\n");
            receipt.append("          CẢM ƠN QUÝ KHÁCH!              \n");

            // Display in dialog
            TextArea area = new TextArea(receipt.toString());
            area.setFont(javafx.scene.text.Font.font("Courier New", 12));
            area.setEditable(false);
            area.setPrefSize(450, 520);

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("In Hóa Đơn");
            alert.setHeaderText("Hóa Đơn Xem Trước");
            alert.getDialogPane().setContent(area);
            alert.showAndWait();
        });

        refreshData();
    }



    public void refreshData() {
        tableHd.setItems(FXCollections.observableArrayList(service.getAllHoaDon()));
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
