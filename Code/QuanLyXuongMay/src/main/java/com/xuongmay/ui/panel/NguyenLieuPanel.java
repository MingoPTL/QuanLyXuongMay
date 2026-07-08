package com.xuongmay.ui.panel;

import com.xuongmay.model.CayVai;
import com.xuongmay.model.LoVai;
import com.xuongmay.model.NhaCungCap;
import com.xuongmay.model.TrangThaiLoVai;
import com.xuongmay.service.NguyenLieuService;
import com.xuongmay.ui.dialog.LoVaiDetailsDialog;
import com.xuongmay.ui.dialog.LoVaiFormDialog;
import com.xuongmay.ui.dialog.QuyTrinhLoVaiDialog;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.io.File;
import java.time.LocalDate;
import java.util.List;

public class NguyenLieuPanel extends VBox {
    private final NguyenLieuService service = new NguyenLieuService();

    // Supplier UI components
    private TableView<NhaCungCap> supplierTable;
    private TextField txtNccId, txtNccName, txtNccPhone, txtNccAddress;
    private NhaCungCap selectedSupplier;

    // LoVai UI components
    private TilePane loCardsPane;
    private ComboBox<String> comboFilterStatusLoVai;

    // CayVai UI components
    private TableView<CayVai> cvTable;
    private TextField txtCvName, txtCvColor, txtCvLength, txtCvPosition, txtCvLayers, txtCvNote;
    private ComboBox<LoVai> comboFormLoVai;
    private ComboBox<LoVai> comboFilterLoVai;
    private CheckBox chkFree;
    private CayVai selectedCv;

    public NguyenLieuPanel() {
        setSpacing(15);
        setPadding(new Insets(15));
        VBox.setVgrow(this, Priority.ALWAYS);

        Label lblTitle = new Label("Quản Lý Nguyên Liệu");
        lblTitle.getStyleClass().add("tab-title");

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab tabLoVai = new Tab("Lô Vải", createLoVaiView());
        Tab tabCayVai = new Tab("Cây Vải", createCayVaiView());
        Tab tabNcc = new Tab("Nhà Cung Cấp", createSupplierView());

        tabPane.getTabs().addAll(tabLoVai, tabCayVai, tabNcc);
        getChildren().addAll(lblTitle, tabPane);

        // Load data initially
        refreshLoVais();
        refreshSuppliers();
        refreshCayVais();
    }

    // --- SUPPLIER TAB ---
    private Node createSupplierView() {
        HBox root = new HBox(20);
        root.setPadding(new Insets(15));
        HBox.setHgrow(root, Priority.ALWAYS);

        // Table List (Left)
        VBox tableBox = new VBox(10);
        HBox.setHgrow(tableBox, Priority.ALWAYS);

        // Header for Table List
        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Label lblListTitle = new Label("Danh sách nhà cung cấp");
        lblListTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Button btnAddNcc = new Button("➕ Thêm Nhà Cung Cấp");
        btnAddNcc.getStyleClass().addAll("btn", "btn-primary");
        btnAddNcc.setOnAction(e -> {
            clearSupplierForm();
            txtNccId.requestFocus();
        });

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        headerBox.getChildren().addAll(lblListTitle, headerSpacer, btnAddNcc);

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
        tableBox.getChildren().addAll(headerBox, supplierTable);

        // Form inputs (Right)
        VBox formBox = new VBox(12);
        formBox.setPrefWidth(360);
        formBox.setMinWidth(360);
        formBox.getStyleClass().add("card-panel");

        Label lblFormTitle = new Label("Thông Tin Nhà Cung Cấp");
        lblFormTitle.getStyleClass().add("sub-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        javafx.scene.layout.ColumnConstraints col1 = new javafx.scene.layout.ColumnConstraints(90);
        javafx.scene.layout.ColumnConstraints col2 = new javafx.scene.layout.ColumnConstraints(220);
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
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa nhà cung cấp này?",
                        ButtonType.YES, ButtonType.NO);
                confirm.setHeaderText(null);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        service.deleteNhaCungCap(selectedSupplier.getMaNhaCungCap());
                        refreshSuppliers();
                        clearSupplierForm();
                    }
                });
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn một nhà cung cấp để xóa!");
            }
        });

        btnClear.setOnAction(e -> clearSupplierForm());

        return root;
    }

    private void refreshSuppliers() {
        ObservableList<NhaCungCap> items = FXCollections.observableArrayList(service.getAllNhaCungCap());
        if (supplierTable != null) {
            supplierTable.setItems(items);
        }
    }

    private void clearSupplierForm() {
        selectedSupplier = null;
        txtNccId.clear();
        txtNccId.setEditable(true);
        txtNccName.clear();
        txtNccPhone.clear();
        txtNccAddress.clear();
        if (supplierTable != null) {
            supplierTable.getSelectionModel().clearSelection();
        }
    }

    // --- LO VAI TAB (CARD UI GRID) ---
    private Node createLoVaiView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        VBox.setVgrow(root, Priority.ALWAYS);

        HBox headerBox = new HBox(10);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Label lblListTitle = new Label("Danh sách Lô Vải (Chưa Cắt)");
        lblListTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #0f172a;");

        // Bộ lọc trạng thái lô vải
        comboFilterStatusLoVai = new ComboBox<>();
        comboFilterStatusLoVai.getItems().addAll("Tất cả trạng thái", "Chưa sử dụng", "Đang sử dụng", "Ra sản phẩm");
        comboFilterStatusLoVai.setValue("Tất cả trạng thái");
        comboFilterStatusLoVai.setPrefWidth(160);
        comboFilterStatusLoVai.getStyleClass().add("combo-box");
        comboFilterStatusLoVai.valueProperty().addListener((obs, oldVal, newVal) -> refreshLoVais());

        Button btnAddLo = new Button("➕ Thêm Lô Vải");
        btnAddLo.getStyleClass().addAll("btn", "btn-primary");
        btnAddLo.setOnAction(e -> showLoVaiFormDialog(null));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        headerBox.getChildren().addAll(lblListTitle, headerSpacer, comboFilterStatusLoVai, btnAddLo);

        loCardsPane = new TilePane();
        loCardsPane.setHgap(20);
        loCardsPane.setVgap(20);
        loCardsPane.setPadding(new Insets(10));
        loCardsPane.setPrefColumns(5);
        loCardsPane.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(loCardsPane);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(headerBox, scrollPane);
        return root;
    }

    private void showLoVaiFormDialog(LoVai lo) {
        LoVaiFormDialog dialog = new LoVaiFormDialog(lo, service);
        dialog.showAndWait().ifPresent(result -> {
            if (dialog.handleSave()) {
                refreshLoVais();
            }
        });
    }

    private Node createLoVaiCard(LoVai lo) {
        StackPane card = new StackPane();
        card.getStyleClass().add("fabric-card");

        // Content
        VBox cardContent = new VBox();

        StackPane imgPlaceholder = new StackPane();
        imgPlaceholder.getStyleClass().add("fabric-card-image-placeholder");

        // Try to load real image
        boolean hasImg = lo.getHinhAnh() != null && !lo.getHinhAnh().isEmpty() && new File(lo.getHinhAnh()).exists();
        if (hasImg) {
            ImageView iv = new ImageView(new Image(new File(lo.getHinhAnh()).toURI().toString()));
            iv.setFitWidth(210);
            iv.setFitHeight(130);
            iv.setPreserveRatio(false);
            iv.setSmooth(true);
            imgPlaceholder.getChildren().add(iv);
        } else {
            VBox imgBox = new VBox(5);
            imgBox.setAlignment(Pos.CENTER);
            Label iconLabel = new Label("🧵");
            iconLabel.setStyle("-fx-font-size: 38px;");
            Label lblImgText = new Label(lo.getLoaiVai());
            lblImgText.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #475569;");
            imgBox.getChildren().addAll(iconLabel, lblImgText);
            imgPlaceholder.getChildren().add(imgBox);
        }

        // Status badge overlay on image (always shown)
        Label statusBadge = new Label();
        switch (lo.getTrangThaiLoVai()) {
            case ChuaSuDung:
                statusBadge.setText("Chưa sử dụng");
                statusBadge.getStyleClass().addAll("fabric-card-badge", "badge-orange");
                break;
            case DangSuDung:
                statusBadge.setText("Đang sử dụng");
                statusBadge.getStyleClass().addAll("fabric-card-badge", "badge-green");
                break;
            default:
                statusBadge.setText("Ra sản phẩm");
                statusBadge.getStyleClass().addAll("fabric-card-badge", "badge-green");
                break;
        }
        StackPane.setMargin(statusBadge, new Insets(8));
        StackPane.setAlignment(statusBadge, Pos.TOP_RIGHT);
        imgPlaceholder.getChildren().add(statusBadge);

        VBox infoBox = new VBox(4);
        infoBox.getStyleClass().add("fabric-card-info");

        Label titleLabel = new Label(lo.getTenLo());
        titleLabel.getStyleClass().add("fabric-card-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxHeight(40);

        Label codeLabel = new Label("Mã: " + lo.getMaLo());
        codeLabel.getStyleClass().add("fabric-card-detail");

        int cvSize = service.getCayVaiByLoVaiId(lo.getMaLo()).size();
        Label cvCountLabel = new Label("Số cây hiện có: " + cvSize + " cây");
        cvCountLabel.getStyleClass().add("fabric-card-detail");

        Label priceLabel = new Label("Giá nhập: " + String.format("%,.0f VNĐ", lo.getGiaNhap()));
        priceLabel.getStyleClass().add("fabric-card-detail");

        Label nccLabel = new Label(
                "NCC: " + (lo.getNhaCungCap() != null ? lo.getNhaCungCap().getTenNhaCungCap() : "N/A"));
        nccLabel.getStyleClass().add("fabric-card-detail");
        nccLabel.setWrapText(true);

        infoBox.getChildren().addAll(titleLabel, codeLabel, cvCountLabel, priceLabel, nccLabel);
        cardContent.getChildren().addAll(imgPlaceholder, infoBox);
        card.getChildren().add(cardContent);

        // Hover Overlay
        VBox hoverOverlay = new VBox(12);
        hoverOverlay.getStyleClass().add("fabric-card-hover-overlay");
        hoverOverlay.setOpacity(0.0);
        hoverOverlay.setMouseTransparent(true);

        Button btnView = new Button("👁 Xem chi tiết");
        btnView.getStyleClass().addAll("card-btn", "card-btn-view");

        Button btnOptions = new Button("⚙ Tùy chọn");
        btnOptions.getStyleClass().addAll("card-btn", "card-btn-options");

        hoverOverlay.getChildren().addAll(btnView, btnOptions);
        card.getChildren().add(hoverOverlay);

        // Premium Transitions
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(150), card);
        scaleUp.setToX(1.04);
        scaleUp.setToY(1.04);

        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(150), card);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);

        FadeTransition fadeIn = new FadeTransition(Duration.millis(150), hoverOverlay);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), hoverOverlay);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        card.setOnMouseEntered(e -> {
            hoverOverlay.setMouseTransparent(false);
            scaleDown.stop();
            fadeOut.stop();
            scaleUp.play();
            fadeIn.play();
            card.setStyle(
                    "-fx-effect: dropshadow(gaussian, rgba(99,102,241,0.22), 15, 0, 0, 4); -fx-border-color: -color-accent;");
        });

        card.setOnMouseExited(e -> {
            hoverOverlay.setMouseTransparent(true);
            scaleUp.stop();
            fadeIn.stop();
            scaleDown.play();
            fadeOut.play();
            card.setStyle("");
        });

        btnView.setOnAction(e -> showLoVaiDetailsDialog(lo));

        btnOptions.setOnAction(e -> {
            ContextMenu contextMenu = new ContextMenu();
            MenuItem editItem = new MenuItem("Chỉnh sửa");
            if (lo.getTrangThaiLoVai() != TrangThaiLoVai.ChuaSuDung) {
                editItem.setDisable(true);
            }
            editItem.setOnAction(ev -> showLoVaiFormDialog(lo));

            // Sub-menu thay đổi trạng thái
            Menu statusMenu = new Menu("Thay đổi trạng thái");
            MenuItem itemChuaSuDung = new MenuItem("Chưa sử dụng");
            MenuItem itemDangSuDung = new MenuItem("Đang sử dụng");
            MenuItem itemRaSanPham = new MenuItem("Ra sản phẩm");

            if (lo.getTrangThaiLoVai() == TrangThaiLoVai.ChuaSuDung) {
                itemChuaSuDung.setDisable(true);
            } else if (lo.getTrangThaiLoVai() == TrangThaiLoVai.DangSuDung) {
                itemDangSuDung.setDisable(true);
            } else if (lo.getTrangThaiLoVai() == TrangThaiLoVai.RaSanPham) {
                itemRaSanPham.setDisable(true);
            }

            itemChuaSuDung.setOnAction(ev -> changeLotStatus(lo, TrangThaiLoVai.ChuaSuDung));
            itemDangSuDung.setOnAction(ev -> changeLotStatus(lo, TrangThaiLoVai.DangSuDung));
            itemRaSanPham.setOnAction(ev -> changeLotStatus(lo, TrangThaiLoVai.RaSanPham));
            statusMenu.getItems().addAll(itemChuaSuDung, itemDangSuDung, itemRaSanPham);

            MenuItem processItem = new MenuItem("Xem quy trình");
            processItem.setOnAction(ev -> showLoVaiProcessDialog(lo));

            MenuItem deleteItem = new MenuItem("Xóa Lô Vải");
            deleteItem.setOnAction(ev -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa lô vải này?",
                        ButtonType.YES, ButtonType.NO);
                confirm.setHeaderText(null);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        service.deleteLoVai(lo.getMaLo());
                        refreshLoVais();
                    }
                });
            });

            contextMenu.getItems().addAll(editItem, statusMenu, processItem, deleteItem);
            contextMenu.show(btnOptions, javafx.geometry.Side.BOTTOM, 0, 0);
        });

        return card;
    }

    private void showLoVaiDetailsDialog(LoVai lo) {
        LoVaiDetailsDialog dialog = new LoVaiDetailsDialog(lo, service);
        dialog.showAndWait();
    }

    private void showLoVaiProcessDialog(LoVai lo) {
        QuyTrinhLoVaiDialog dialog = new QuyTrinhLoVaiDialog(lo, service);
        dialog.showAndWait();
    }

    private void changeLotStatus(LoVai lo, TrangThaiLoVai newStatus) {
        lo.setTrangThaiLoVai(newStatus);

        // Nếu thay đổi sang trạng thái khác "Đang sử dụng", reset lượt trải của các cây
        // vải trong lô về 0
        if (newStatus != TrangThaiLoVai.DangSuDung) {
            List<CayVai> rolls = service.getCayVaiByLoVaiId(lo.getMaLo());
            for (CayVai cv : rolls) {
                cv.setLuotTraiVai(0);
                service.updateCayVai(cv);
            }
        }

        service.updateLoVai(lo);
        refreshLoVais();
        refreshCayVais();
        showAlert(Alert.AlertType.INFORMATION, "Thành công",
                "Đã cập nhật trạng thái lô vải '" + lo.getTenLo() + "' thành '" + newStatus.toString() + "'.");
    }

    private void refreshLoVais() {
        if (loCardsPane == null)
            return;

        loCardsPane.getChildren().clear();
        List<LoVai> loList = service.getAllLoVai();

        String filterValue = comboFilterStatusLoVai != null ? comboFilterStatusLoVai.getValue() : "Tất cả trạng thái";
        for (LoVai lo : loList) {
            boolean matches = false;
            if ("Tất cả trạng thái".equals(filterValue)) {
                matches = true;
            } else if ("Chưa sử dụng".equals(filterValue) && lo.getTrangThaiLoVai() == TrangThaiLoVai.ChuaSuDung) {
                matches = true;
            } else if ("Đang sử dụng".equals(filterValue) && lo.getTrangThaiLoVai() == TrangThaiLoVai.DangSuDung) {
                matches = true;
            } else if ("Ra sản phẩm".equals(filterValue) && lo.getTrangThaiLoVai() == TrangThaiLoVai.RaSanPham) {
                matches = true;
            }

            if (matches) {
                loCardsPane.getChildren().add(createLoVaiCard(lo));
            }
        }

        // Update ComboBoxes in other tabs
        List<LoVai> allLo = service.getAllLoVai();
        ObservableList<LoVai> allItems = FXCollections.observableArrayList(allLo);
        if (comboFormLoVai != null) {
            comboFormLoVai.setItems(allItems);
        }
        if (comboFilterLoVai != null) {
            comboFilterLoVai.setItems(allItems);
        }
    }

    // --- CAY VAI TAB ---
    private Node createCayVaiView() {
        HBox root = new HBox(20);
        root.setPadding(new Insets(15));
        HBox.setHgrow(root, Priority.ALWAYS);

        // Left Pane: Table and Filter
        VBox tableBox = new VBox(10);
        HBox.setHgrow(tableBox, Priority.ALWAYS);

        // Filter & Add HBox
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        Label lblFilter = new Label("Lọc theo lô vải:");
        comboFilterLoVai = new ComboBox<>();
        comboFilterLoVai.setPromptText("Tất cả");
        comboFilterLoVai.setPrefWidth(160);
        comboFilterLoVai.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            refreshCayVais();
        });

        Button btnClearFilter = new Button("Tất cả");
        btnClearFilter.getStyleClass().addAll("btn", "btn-secondary");
        btnClearFilter.setOnAction(e -> {
            comboFilterLoVai.getSelectionModel().clearSelection();
            refreshCayVais();
        });

        Button btnFilterFree = new Button("🟡 Cây lẻ");
        btnFilterFree.getStyleClass().addAll("btn", "btn-secondary");
        btnFilterFree.setOnAction(e -> {
            comboFilterLoVai.getSelectionModel().clearSelection();
            // Show only free/unassigned
            cvTable.setItems(FXCollections.observableArrayList(
                    service.getAllCayVai().stream()
                            .filter(cv -> cv.getLoVai() == null)
                            .collect(java.util.stream.Collectors.toList())));
        });

        Region filterSpacer = new Region();
        HBox.setHgrow(filterSpacer, Priority.ALWAYS);

        Button btnAddCv = new Button("➕ Thêm Cây Vải");
        btnAddCv.getStyleClass().addAll("btn", "btn-primary");
        btnAddCv.setOnAction(e -> {
            clearCayVaiForm();
            txtCvName.requestFocus();
        });

        filterBox.getChildren().addAll(lblFilter, comboFilterLoVai, btnClearFilter, btnFilterFree, filterSpacer,
                btnAddCv);

        cvTable = new TableView<>();
        VBox.setVgrow(cvTable, Priority.ALWAYS);

        TableColumn<CayVai, String> colCvName = new TableColumn<>("Tên Cây");
        colCvName.setCellValueFactory(new PropertyValueFactory<>("tenCayVai"));
        colCvName.setPrefWidth(100);

        TableColumn<CayVai, String> colCvLo = new TableColumn<>("Thuộc Lô Vải");
        colCvLo.setCellValueFactory(cellData -> {
            LoVai lo = cellData.getValue().getLoVai();
            return new SimpleStringProperty(lo != null ? lo.getTenLo() : "🟡 Cây lẻ");
        });
        colCvLo.setPrefWidth(150);

        TableColumn<CayVai, String> colCvColor = new TableColumn<>("Màu Sắc");
        colCvColor.setCellValueFactory(new PropertyValueFactory<>("mauSac"));
        colCvColor.setPrefWidth(90);

        TableColumn<CayVai, Double> colCvLength = new TableColumn<>("Chiều Dài");
        colCvLength.setCellValueFactory(new PropertyValueFactory<>("chieuDai"));
        colCvLength.setPrefWidth(85);

        TableColumn<CayVai, String> colCvPos = new TableColumn<>("Vị Trí");
        colCvPos.setCellValueFactory(new PropertyValueFactory<>("viTri"));
        colCvPos.setPrefWidth(90);

        cvTable.getColumns().addAll(colCvName, colCvLo, colCvColor, colCvLength, colCvPos);
        tableBox.getChildren().addAll(filterBox, new Label("Danh sách Cây Vải"), cvTable);

        // Right Pane: CayVai Form
        VBox formBox = new VBox(12);
        formBox.setPrefWidth(360);
        formBox.setMinWidth(360);
        formBox.getStyleClass().add("card-panel");

        Label lblFormTitle = new Label("Thông Tin Cây Vải trong Lô");
        lblFormTitle.getStyleClass().add("sub-title");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        javafx.scene.layout.ColumnConstraints c1 = new javafx.scene.layout.ColumnConstraints(90);
        javafx.scene.layout.ColumnConstraints c2 = new javafx.scene.layout.ColumnConstraints(220);
        grid.getColumnConstraints().addAll(c1, c2);

        txtCvName = new TextField();
        comboFormLoVai = new ComboBox<>();
        comboFormLoVai.setMaxWidth(Double.MAX_VALUE);

        txtCvColor = new TextField();
        txtCvLength = new TextField();
        txtCvPosition = new TextField();
        txtCvLayers = new TextField();
        txtCvNote = new TextField();

        // Checkbox "Cây lẻ"
        chkFree = new CheckBox("Cây lẻ (không thuộc lô nào)");
        chkFree.setStyle("-fx-font-size: 12px; -fx-text-fill: #f59e0b; -fx-font-weight: bold;");
        chkFree.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            comboFormLoVai.setDisable(isSelected);
            if (isSelected) {
                comboFormLoVai.setValue(null);
            }
            updateLayersFieldState();
        });

        comboFormLoVai.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateLayersFieldState();
        });

        grid.add(new Label("Tên cây:"), 0, 0);
        grid.add(txtCvName, 1, 0);

        grid.add(new Label("Lô vải:"), 0, 1);
        // Wrap comboFormLoVai + checkbox in a VBox
        VBox loBox = new VBox(4, comboFormLoVai, chkFree);
        grid.add(loBox, 1, 1);

        grid.add(new Label("Màu sắc:"), 0, 2);
        grid.add(txtCvColor, 1, 2);

        grid.add(new Label("Chiều dài:"), 0, 3);
        grid.add(txtCvLength, 1, 3);

        grid.add(new Label("Vị trí:"), 0, 4);
        grid.add(txtCvPosition, 1, 4);

        grid.add(new Label("Lượt trải:"), 0, 5);
        grid.add(txtCvLayers, 1, 5);

        grid.add(new Label("Ghi chú:"), 0, 6);
        grid.add(txtCvNote, 1, 6);

        HBox btnBox = new HBox(10);
        btnBox.setAlignment(Pos.CENTER_RIGHT);
        Button btnSave = new Button("Lưu Cây");
        btnSave.getStyleClass().addAll("btn", "btn-success");
        Button btnDelete = new Button("Xóa Cây");
        btnDelete.getStyleClass().addAll("btn", "btn-danger");
        Button btnClear = new Button("Làm mới");
        btnClear.getStyleClass().addAll("btn", "btn-secondary");

        btnBox.getChildren().addAll(btnSave, btnDelete, btnClear);
        formBox.getChildren().addAll(lblFormTitle, grid, btnBox);

        root.getChildren().addAll(tableBox, formBox);

        // Selection Listener
        cvTable.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedCv = newVal;
                txtCvName.setText(newVal.getTenCayVai());
                txtCvName.setEditable(false);
                txtCvColor.setText(newVal.getMauSac());
                txtCvLength.setText(String.valueOf(newVal.getChieuDai()));
                txtCvPosition.setText(newVal.getViTri());
                txtCvNote.setText(newVal.getGhiChu());
                comboFormLoVai.setValue(newVal.getLoVai());
                // Reflect "cây lẻ" status in the checkbox
                chkFree.setSelected(newVal.getLoVai() == null);

                updateLayersFieldState();

                LoVai lo = newVal.getLoVai();
                boolean canHaveLayers = lo != null && lo.getTrangThaiLoVai() == TrangThaiLoVai.DangSuDung;
                if (canHaveLayers) {
                    txtCvLayers.setText(String.valueOf(newVal.getLuotTraiVai()));
                }
            }
        });

        // Save Button Action
        btnSave.setOnAction(e -> {
            String cvName = txtCvName.getText().trim();
            // If "Cây lẻ" checkbox is ticked, force null — a disabled ComboBox still
            // returns its stale value
            LoVai lo = chkFree.isSelected() ? null : comboFormLoVai.getValue();
            String color = txtCvColor.getText().trim();
            String lenStr = txtCvLength.getText().trim();
            String pos = txtCvPosition.getText().trim();
            String layersStr = txtCvLayers.getText().trim();
            String note = txtCvNote.getText().trim();

            if (cvName.isEmpty() || color.isEmpty() || lenStr.isEmpty() || pos.isEmpty() || layersStr.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu",
                        "Vui lòng nhập đầy đủ thông tin Cây vải (trừ lô vải)!");
                return;
            }

            double len;
            try {
                len = Double.parseDouble(lenStr);
                if (len <= 0)
                    throw new NumberFormatException();
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Chiều dài phải > 0!");
                return;
            }

            int layers = 0;
            boolean canHaveLayers = lo != null && lo.getTrangThaiLoVai() == TrangThaiLoVai.DangSuDung;
            if (canHaveLayers) {
                try {
                    layers = Integer.parseInt(layersStr);
                    if (layers < 0)
                        throw new NumberFormatException();
                } catch (NumberFormatException ex) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi định dạng", "Lượt trải phải >= 0!");
                    return;
                }
            }

            if (selectedCv == null) {
                if (service.getAllCayVai().stream().anyMatch(c -> c.getTenCayVai().equals(cvName))) {
                    showAlert(Alert.AlertType.ERROR, "Lỗi", "Tên/Mã cây vải đã tồn tại!");
                    return;
                }
                CayVai cv = new CayVai(cvName, color, lo, len, pos, note, layers);
                service.addCayVai(cv);
            } else {
                selectedCv.setLoVai(lo);
                selectedCv.setMauSac(color);
                selectedCv.setChieuDai(len);
                selectedCv.setViTri(pos);
                selectedCv.setLuotTraiVai(layers);
                selectedCv.setGhiChu(note);
                service.updateCayVai(selectedCv);
            }
            refreshCayVais();
            refreshLoVais(); // To refresh actual count in card
            clearCayVaiForm();
        });

        btnDelete.setOnAction(e -> {
            if (selectedCv != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa cây vải này?",
                        ButtonType.YES, ButtonType.NO);
                confirm.setHeaderText(null);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        service.deleteCayVai(selectedCv.getTenCayVai());
                        refreshCayVais();
                        refreshLoVais(); // To refresh actual count in card
                        clearCayVaiForm();
                    }
                });
            } else {
                showAlert(Alert.AlertType.WARNING, "Lỗi thao tác", "Vui lòng chọn một Cây vải để xóa!");
            }
        });

        btnClear.setOnAction(e -> clearCayVaiForm());

        updateLayersFieldState();

        return root;
    }

    private void refreshCayVais() {
        if (cvTable == null)
            return;

        LoVai filterLo = comboFilterLoVai != null ? comboFilterLoVai.getValue() : null;
        if (filterLo != null) {
            cvTable.setItems(FXCollections.observableArrayList(service.getCayVaiByLoVaiId(filterLo.getMaLo())));
        } else {
            cvTable.setItems(FXCollections.observableArrayList(service.getAllCayVai()));
        }
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
        if (comboFormLoVai != null) {
            comboFormLoVai.setValue(null);
        }
        if (chkFree != null) {
            chkFree.setSelected(false);
        }
        if (cvTable != null) {
            cvTable.getSelectionModel().clearSelection();
        }
        updateLayersFieldState();
    }

    private void updateLayersFieldState() {
        boolean isFree = chkFree != null && chkFree.isSelected();
        LoVai lo = isFree ? null : (comboFormLoVai != null ? comboFormLoVai.getValue() : null);
        boolean canHaveLayers = lo != null && lo.getTrangThaiLoVai() == TrangThaiLoVai.DangSuDung;
        if (txtCvLayers != null) {
            if (canHaveLayers) {
                txtCvLayers.setDisable(false);
            } else {
                txtCvLayers.setText("0");
                txtCvLayers.setDisable(true);
            }
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
