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
        right.setPrefWidth(550);

        VBox formBox = new VBox(10);
        formBox.setPrefWidth(320);
        formBox.setMinWidth(320);
        formBox.getStyleClass().add("card-panel");
        Label lblFormTitle = new Label("Chi Tiết Hóa Đơn");
        lblFormTitle.getStyleClass().add("sub-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints(95);
        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints(175);
        grid.getColumnConstraints().addAll(col1, col2);

        txtHdId = new TextField();
        txtHdId.setEditable(false);
        txtHdAmount = new TextField();
        txtHdAmount.setEditable(false);
        pickerDate = new DatePicker();
        comboPayMethod = new ComboBox<>(FXCollections.observableArrayList(PhuongThucThanhToan.values()));
        comboStatus = new ComboBox<>(FXCollections.observableArrayList(TrangThaiHoaDon.values()));
        lblOrderInfo = new Label("Thông tin đơn hàng liên quan: ");
        lblOrderInfo.setStyle("-fx-font-weight: bold; -fx-text-fill: #475569;");

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

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        Button btnSave = new Button("Cập Nhật HĐ");
        btnSave.getStyleClass().addAll("btn", "btn-success");
        Button btnPrint = new Button("In Hóa Đơn");
        btnPrint.getStyleClass().addAll("btn", "btn-primary");

        btnBox.getChildren().addAll(btnSave, btnPrint);
        formBox.getChildren().addAll(lblFormTitle, grid, lblOrderInfo, btnBox);

        // Details products inside order table
        tableCtdh = new TableView<>();
        tableCtdh.setPrefHeight(180);
        
        TableColumn<ChiTietDonHang, String> colCtSp = new TableColumn<>("Sản Phẩm");
        colCtSp.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSanPham().getTenSanPham()));
        colCtSp.setPrefWidth(180);

        TableColumn<ChiTietDonHang, Integer> colCtQty = new TableColumn<>("SL Ri");
        colCtQty.setCellValueFactory(new PropertyValueFactory<>("soLuongRi"));
        colCtQty.setPrefWidth(100);

        TableColumn<ChiTietDonHang, Double> colCtTotal = new TableColumn<>("Thành Tiền");
        colCtTotal.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));
        colCtTotal.setPrefWidth(130);

        tableCtdh.getColumns().addAll(colCtSp, colCtQty, colCtTotal);

        right.getChildren().addAll(formBox, new Label("Danh sách sản phẩm trong hóa đơn"), tableCtdh);
        split.getItems().addAll(left, right);

        getChildren().addAll(lblTitle, split);

        // Events
        tableHd.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedHd = newVal;
                txtHdId.setText(newVal.getMaHoaDon());
                txtHdAmount.setText(String.format("%,.0f đ", newVal.getTongTienHoaDon()));
                pickerDate.setValue(newVal.getNgayLap());
                comboPayMethod.setValue(newVal.getPhuongThucThanhToan());
                comboStatus.setValue(newVal.getTrangThaiHoaDon());

                DonHang dh = newVal.getDonHang();
                if (dh != null) {
                    lblOrderInfo.setText("Mã Đơn: " + dh.getMaDonHang() + " | KH: " + (dh.getKhachHang() != null ? dh.getKhachHang().getTenKhachHang() : "N/A"));
                    tableCtdh.setItems(FXCollections.observableArrayList(service.getChiTietByDonHangId(dh.getMaDonHang())));
                } else {
                    lblOrderInfo.setText("Thông tin đơn hàng liên quan: Không có");
                    tableCtdh.setItems(FXCollections.emptyObservableList());
                }
            }
        });

        btnSave.setOnAction(e -> {
            if (selectedHd == null) {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn hóa đơn để cập nhật!");
                return;
            }
            LocalDate date = pickerDate.getValue();
            PhuongThucThanhToan pm = comboPayMethod.getValue();
            TrangThaiHoaDon status = comboStatus.getValue();

            if (date == null || pm == null || status == null) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            selectedHd.setNgayLap(date);
            selectedHd.setPhuongThucThanhToan(pm);
            selectedHd.setTrangThaiHoaDon(status);
            service.updateHoaDon(selectedHd);

            refreshData();
            showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin hóa đơn!");
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
            }
            receipt.append("-----------------------------------------\n");
            receipt.append(String.format("%-20s %-8s %-12s\n", "Sản phẩm", "SL Ri", "Thành tiền"));
            receipt.append("-----------------------------------------\n");
            List<ChiTietDonHang> items = tableCtdh.getItems();
            for (ChiTietDonHang item : items) {
                receipt.append(String.format("%-20.20s %-8d %,.0f đ\n",
                        item.getSanPham().getTenSanPham(),
                        item.getSoLuongRi(),
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
            area.setPrefSize(420, 480);

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
