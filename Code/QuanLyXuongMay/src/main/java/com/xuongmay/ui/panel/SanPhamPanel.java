package com.xuongmay.ui.panel;

import com.xuongmay.model.*;
import com.xuongmay.service.SanPhamService;
import com.xuongmay.service.BanHangService;
import com.xuongmay.service.SanXuatService;
import com.xuongmay.ui.dialog.SanPhamDetailsDialog;
import com.xuongmay.ui.dialog.SanPhamFormDialog;
import com.xuongmay.ui.dialog.SanPhamProgressDialog;
import com.xuongmay.ui.dialog.SanPhamActualQtyDialog;
import com.xuongmay.util.NotificationUtils;
import com.xuongmay.util.NotificationUtils.NotificationType;
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
import java.util.ArrayList;
import java.util.List;

public class SanPhamPanel extends VBox {
    private final SanPhamService spService = new SanPhamService();
    private final BanHangService bhService = new BanHangService();
    private final SanXuatService sxService = new SanXuatService();

    // Tab 1: Products (Card Layout components)
    private TilePane spCardsPane;
    private ComboBox<String> comboFilterStatusSanPham;
    private ComboBox<String> comboFilterTypeSanPham;

    // Tab 2: Process Kanban
    private VBox colCatContainer;
    private VBox colMayContainer;
    private VBox colUiContainer;
    private VBox colHoanThanhContainer;
    private Label lblCatCount;
    private Label lblMayCount;
    private Label lblUiCount;
    private Label lblHoanThanhCount;

    // Tab 3: Orders
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
    private TabPane tabPane;
    private java.util.function.Consumer<SanPham> onStatusChangedToAssignCallback;

    public void setOnStatusChangedToAssignCallback(java.util.function.Consumer<SanPham> callback) {
        this.onStatusChangedToAssignCallback = callback;
    }

    public SanPhamPanel() {
        setSpacing(15);
        setPadding(new Insets(15));
        VBox.setVgrow(this, Priority.ALWAYS);

        Label lblTitle = new Label("Quản Lý Sản Phẩm & Đơn Hàng");
        lblTitle.getStyleClass().add("tab-title");

        tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        VBox.setVgrow(tabPane, Priority.ALWAYS);

        Tab tabProduct = new Tab("Sản Phẩm", createProductView());
        Tab tabProcess = new Tab("Quy trình", createProcessView());
        Tab tabOrder = new Tab("Đơn Bán Hàng", createOrderView());

        tabPane.getTabs().addAll(tabProduct, tabProcess, tabOrder);
        getChildren().addAll(lblTitle, tabPane);

        // Initial load after all components are initialized
        refreshProducts();
        refreshOrders();
    }

    private Node createProductView() {
        VBox root = new VBox(15);
        root.setPadding(new Insets(15));
        VBox.setVgrow(root, Priority.ALWAYS);

        HBox headerBox = new HBox(12);
        headerBox.setAlignment(Pos.CENTER_LEFT);
        Label lblListTitle = new Label("Danh sách Sản Phẩm");
        lblListTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 16px; -fx-text-fill: #0f172a;");

        // Filter status combo
        comboFilterStatusSanPham = new ComboBox<>();
        comboFilterStatusSanPham.getItems().add("Tất cả khâu");
        for (TrangThaiSanPham val : TrangThaiSanPham.values()) {
            comboFilterStatusSanPham.getItems().add(val.toString());
        }
        comboFilterStatusSanPham.setValue("Tất cả khâu");
        comboFilterStatusSanPham.setPrefWidth(160);
        comboFilterStatusSanPham.getStyleClass().add("combo-box");
        comboFilterStatusSanPham.valueProperty().addListener((obs, oldVal, newVal) -> refreshProducts());

        // Filter type combo
        comboFilterTypeSanPham = new ComboBox<>();
        comboFilterTypeSanPham.setPrefWidth(160);
        comboFilterTypeSanPham.getStyleClass().add("combo-box");
        comboFilterTypeSanPham.valueProperty().addListener((obs, oldVal, newVal) -> refreshProducts());

        Button btnAddSp = new Button("➕ Thêm Sản Phẩm");
        btnAddSp.getStyleClass().addAll("btn", "btn-primary");
        btnAddSp.setOnAction(e -> showSanPhamFormDialog(null));

        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        headerBox.getChildren().addAll(lblListTitle, headerSpacer, comboFilterTypeSanPham, comboFilterStatusSanPham, btnAddSp);

        spCardsPane = new TilePane();
        spCardsPane.setHgap(20);
        spCardsPane.setVgap(20);
        spCardsPane.setPadding(new Insets(10));
        spCardsPane.setPrefColumns(5);
        spCardsPane.setAlignment(Pos.TOP_LEFT);

        ScrollPane scrollPane = new ScrollPane(spCardsPane);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("scroll-pane");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        root.getChildren().addAll(headerBox, scrollPane);

        // Populate initial categories list in filter
        refreshTypeFilter();

        return root;
    }

    private Node createProcessView() {
        HBox columnsBox = new HBox(16);
        columnsBox.setPadding(new Insets(10));
        HBox.setHgrow(columnsBox, Priority.ALWAYS);
        columnsBox.setAlignment(Pos.TOP_CENTER);

        // Define columns containers
        colCatContainer = new VBox(10);
        colMayContainer = new VBox(10);
        colUiContainer = new VBox(10);
        colHoanThanhContainer = new VBox(10);

        // Count labels
        lblCatCount = new Label("0");
        lblMayCount = new Label("0");
        lblUiCount = new Label("0");
        lblHoanThanhCount = new Label("0");

        Node colCat = createKanbanColumn("Cắt", "dot-blue", lblCatCount, colCatContainer);
        Node colMay = createKanbanColumn("May", "dot-purple", lblMayCount, colMayContainer);
        Node colUi = createKanbanColumn("Ủi", "dot-teal", lblUiCount, colUiContainer);
        Node colHoanThanh = createKanbanColumn("Hoàn thành", "dot-green", lblHoanThanhCount, colHoanThanhContainer);

        columnsBox.getChildren().addAll(colCat, colMay, colUi, colHoanThanh);

        return columnsBox;
    }

    private Node createKanbanColumn(String title, String dotClass, Label countLbl, VBox container) {
        VBox column = new VBox(10);
        column.setPrefWidth(270);
        column.setMinWidth(250);
        HBox.setHgrow(column, Priority.ALWAYS);
        column.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-background-radius: 12;" +
            "-fx-padding: 12;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 12;"
        );

        HBox header = new HBox(8);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(0, 0, 8, 0));

        StackPane dot = new StackPane();
        dot.getStyleClass().addAll("step-dot", dotClass);

        Label lblTitle = new Label(title);
        lblTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #1e293b;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        countLbl.setStyle(
            "-fx-background-color: #e2e8f0;" +
            "-fx-text-fill: #475569;" +
            "-fx-font-size: 11px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 2 8;" +
            "-fx-background-radius: 10;"
        );

        header.getChildren().addAll(dot, lblTitle, spacer, countLbl);

        ScrollPane scroll = new ScrollPane(container);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        column.getChildren().addAll(header, scroll);
        return column;
    }

    private Node createProcessMiniCard(SanPham sp) {
        VBox card = new VBox(8);
        card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 4, 0, 0, 1);" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;"
        );

        // Hover animations
        card.setOnMouseEntered(e -> card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(99,102,241,0.15), 6, 0, 0, 2);" +
            "-fx-border-color: #6366f1;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;"
        ));
        card.setOnMouseExited(e -> card.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 10;" +
            "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.03), 4, 0, 0, 1);" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 8;"
        ));

        HBox topInfo = new HBox(8);
        topInfo.setAlignment(Pos.CENTER_LEFT);

        StackPane imgBox = new StackPane();
        imgBox.setPrefSize(40, 40);
        imgBox.setMinSize(40, 40);
        imgBox.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-background-radius: 6;"
        );

        boolean hasImg = sp.getHinhAnh() != null && !sp.getHinhAnh().isEmpty() && new File(sp.getHinhAnh()).exists();
        if (hasImg) {
            ImageView iv = new ImageView(new Image(new File(sp.getHinhAnh()).toURI().toString()));
            iv.setFitWidth(38);
            iv.setFitHeight(38);
            iv.setPreserveRatio(false);
            iv.setSmooth(true);
            imgBox.getChildren().add(iv);
        } else {
            Label lblPlaceholder = new Label("👕");
            lblPlaceholder.setStyle("-fx-font-size: 20px;");
            imgBox.getChildren().add(lblPlaceholder);
        }

        VBox textInfo = new VBox(2);
        HBox.setHgrow(textInfo, Priority.ALWAYS);

        Label lblName = new Label(sp.getTenSanPham());
        lblName.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #1e293b;");
        lblName.setWrapText(true);
        lblName.setMaxWidth(180);

        Label lblCodeAndType = new Label(sp.getMaSanPham() + " • " + (sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoai() : "N/A"));
        lblCodeAndType.setStyle("-fx-font-size: 10px; -fx-text-fill: #64748b;");

        Label lblQty = new Label("SL: " + sp.getTongSoBo() + " bộ / " + sp.getTongSoRi() + " ri");
        lblQty.setStyle("-fx-font-size: 10px; -fx-font-weight: bold; -fx-text-fill: #475569;");

        Label lblWorkers = new Label();
        lblWorkers.setStyle("-fx-font-size: 9px; -fx-text-fill: #4338ca; -fx-font-style: italic;");
        lblWorkers.setWrapText(true);
        lblWorkers.setMaxWidth(180);

        List<PhanCongSanPham> spAssignments = new java.util.ArrayList<>();
        for (PhanCongSanPham pc : sxService.getAllPhanCong()) {
            if (pc.getSanPham() != null && pc.getSanPham().getMaSanPham().equals(sp.getMaSanPham())) {
                spAssignments.add(pc);
            }
        }
        if (!spAssignments.isEmpty()) {
            java.util.List<String> workerList = new java.util.ArrayList<>();
            for (PhanCongSanPham pc : spAssignments) {
                if (pc.getNhanVien() != null) {
                    String qty = pc.getSoLuong();
                    if ("Tất cả".equalsIgnoreCase(qty)) {
                        int otherAssigned = 0;
                        for (PhanCongSanPham other : spAssignments) {
                            if (other != pc && other.getNhanVien() != null) {
                                String oQty = other.getSoLuong();
                                if (!"Tất cả".equalsIgnoreCase(oQty)) {
                                    try {
                                        otherAssigned += Integer.parseInt(oQty);
                                    } catch (NumberFormatException e) {
                                        // ignore
                                    }
                                }
                            }
                        }
                        qty = String.valueOf(Math.max(0, sp.getTongSoBoDuKien() - otherAssigned));
                    }
                    workerList.add(pc.getNhanVien().getTenNhanVien() + " (" + qty + " bộ)");
                }
            }
            lblWorkers.setText("Thợ: " + String.join(", ", workerList));
        } else {
            lblWorkers.setText("Chưa phân công thợ");
        }

        textInfo.getChildren().addAll(lblName, lblCodeAndType, lblQty, lblWorkers);
        topInfo.getChildren().addAll(imgBox, textInfo);

        // Actions
        HBox actionsRow = new HBox(6);
        actionsRow.setAlignment(Pos.CENTER_LEFT);

        Button btnView = new Button("👁");
        btnView.setStyle(
            "-fx-background-color: #f1f5f9;" +
            "-fx-text-fill: #475569;" +
            "-fx-font-size: 10px;" +
            "-fx-padding: 3 8;" +
            "-fx-background-radius: 5;" +
            "-fx-cursor: hand;"
        );
        btnView.setOnAction(e -> showSanPhamDetailsDialog(sp));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        actionsRow.getChildren().addAll(btnView, spacer);

        // Shift stage indicators
        TrangThaiSanPham current = sp.getTrangThaiSanPham();
        if (current != TrangThaiSanPham.DangCat && current != TrangThaiSanPham.DaHoanThanh) {
            Button btnPrev = new Button("◀");
            btnPrev.setStyle(
                "-fx-background-color: #f1f5f9;" +
                "-fx-text-fill: #6366f1;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 9px;" +
                "-fx-padding: 3 8;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
            );
            btnPrev.setOnAction(e -> {
                TrangThaiSanPham prev = getPrevStatus(current);
                if (prev != null) changeProductStatus(sp, prev);
            });
            actionsRow.getChildren().add(btnPrev);
        }

        if (current != TrangThaiSanPham.DaHoanThanh) {
            Button btnNext = new Button("▶");
            btnNext.setStyle(
                "-fx-background-color: #6366f1;" +
                "-fx-text-fill: white;" +
                "-fx-font-weight: bold;" +
                "-fx-font-size: 9px;" +
                "-fx-padding: 3 8;" +
                "-fx-background-radius: 5;" +
                "-fx-cursor: hand;"
            );
            btnNext.setOnAction(e -> {
                TrangThaiSanPham next = getNextStatus(current);
                if (next != null) changeProductStatus(sp, next);
            });
            actionsRow.getChildren().add(btnNext);
        }

        card.getChildren().addAll(topInfo, actionsRow);
        return card;
    }

    private TrangThaiSanPham getNextStatus(TrangThaiSanPham current) {
        switch (current) {
            case DangCat: return TrangThaiSanPham.DangMay;
            case DangMay: return TrangThaiSanPham.DangUi;
            case DangUi: return TrangThaiSanPham.DaHoanThanh;
            default: return null;
        }
    }

    private TrangThaiSanPham getPrevStatus(TrangThaiSanPham current) {
        switch (current) {
            case DangMay: return TrangThaiSanPham.DangCat;
            case DangUi: return TrangThaiSanPham.DangMay;
            case DaHoanThanh: return TrangThaiSanPham.DangUi;
            default: return null;
        }
    }

    private void refreshProcessView() {
        if (colCatContainer == null || colMayContainer == null || colUiContainer == null || colHoanThanhContainer == null) {
            return;
        }

        colCatContainer.getChildren().clear();
        colMayContainer.getChildren().clear();
        colUiContainer.getChildren().clear();
        colHoanThanhContainer.getChildren().clear();

        List<SanPham> spList = spService.getAllSanPham();
        int cat = 0, may = 0, ui = 0, done = 0;

        for (SanPham sp : spList) {
            if (sp.getTrangThaiSanPham() == null) continue;
            Node miniCard = createProcessMiniCard(sp);
            switch (sp.getTrangThaiSanPham()) {
                case DangCat:
                    colCatContainer.getChildren().add(miniCard);
                    cat++;
                    break;
                case DangMay:
                    colMayContainer.getChildren().add(miniCard);
                    may++;
                    break;
                case DangUi:
                    colUiContainer.getChildren().add(miniCard);
                    ui++;
                    break;
                case DaHoanThanh:
                    colHoanThanhContainer.getChildren().add(miniCard);
                    done++;
                    break;
            }
        }

        lblCatCount.setText(String.valueOf(cat));
        lblMayCount.setText(String.valueOf(may));
        lblUiCount.setText(String.valueOf(ui));
        lblHoanThanhCount.setText(String.valueOf(done));
    }

    private void refreshTypeFilter() {
        if (comboFilterTypeSanPham == null) return;
        String selected = comboFilterTypeSanPham.getValue();
        List<LoaiSanPham> types = spService.getAllLoaiSanPham();
        ObservableList<String> items = FXCollections.observableArrayList();
        items.add("Tất cả loại");
        for (LoaiSanPham t : types) {
            items.add(t.getTenLoai());
        }
        comboFilterTypeSanPham.setItems(items);
        if (selected != null && items.contains(selected)) {
            comboFilterTypeSanPham.setValue(selected);
        } else {
            comboFilterTypeSanPham.setValue("Tất cả loại");
        }
    }

    private void showSanPhamFormDialog(SanPham sp) {
        SanPhamFormDialog dialog = new SanPhamFormDialog(sp, spService);
        dialog.showAndWait().ifPresent(result -> {
            if (dialog.handleSave()) {
                refreshProducts();
                NotificationUtils.show("Sản phẩm", sp == null ? "Đã thêm sản phẩm mới thành công!" : "Đã cập nhật thông tin sản phẩm!", NotificationType.SUCCESS);
            }
        });
    }

    private void showSanPhamDetailsDialog(SanPham sp) {
        SanPhamDetailsDialog dialog = new SanPhamDetailsDialog(sp, spService);
        dialog.showAndWait();
    }

    private void changeProductStatus(SanPham sp, TrangThaiSanPham newStatus) {
        if (newStatus == TrangThaiSanPham.DaHoanThanh) {
            SanPhamActualQtyDialog dialog = new SanPhamActualQtyDialog(sp, this.getScene().getWindow());
            java.util.Optional<Boolean> result = dialog.showAndWait();
            if (!result.isPresent() || !result.get()) {
                // User cancelled or validation failed
                return;
            }
        }
        sp.setTrangThaiSanPham(newStatus);
        spService.updateSanPham(sp);
        refreshProducts();
        NotificationUtils.show("Quy trình", "Đã chuyển '" + sp.getTenSanPham() + "' sang khâu '" + newStatus.toString() + "'.", NotificationType.SUCCESS);

        if ((newStatus == TrangThaiSanPham.DangCat || newStatus == TrangThaiSanPham.DangMay || newStatus == TrangThaiSanPham.DangUi)
                && onStatusChangedToAssignCallback != null) {
            onStatusChangedToAssignCallback.accept(sp);
        }
    }

    private Node createSanPhamCard(SanPham sp) {
        StackPane card = new StackPane();
        card.getStyleClass().add("fabric-card");

        // Content
        VBox cardContent = new VBox();

        StackPane imgPlaceholder = new StackPane();
        imgPlaceholder.getStyleClass().add("fabric-card-image-placeholder");

        // Try to load real image
        boolean hasImg = sp.getHinhAnh() != null && !sp.getHinhAnh().isEmpty() && new File(sp.getHinhAnh()).exists();
        if (hasImg) {
            ImageView iv = new ImageView(new Image(new File(sp.getHinhAnh()).toURI().toString()));
            iv.setFitWidth(210);
            iv.setFitHeight(130);
            iv.setPreserveRatio(false);
            iv.setSmooth(true);
            imgPlaceholder.getChildren().add(iv);
        } else {
            VBox imgBox = new VBox(5);
            imgBox.setAlignment(Pos.CENTER);
            Label iconLabel = new Label("👕");
            iconLabel.setStyle("-fx-font-size: 38px;");
            Label lblImgText = new Label(sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoai() : "Sản phẩm");
            lblImgText.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #475569;");
            imgBox.getChildren().addAll(iconLabel, lblImgText);
            imgPlaceholder.getChildren().add(imgBox);
        }

        // Status badge overlay on image
        Label statusBadge = new Label();
        String badgeClass = "badge-orange";
        if (sp.getTrangThaiSanPham() != null) {
            statusBadge.setText(sp.getTrangThaiSanPham().toString());
            switch (sp.getTrangThaiSanPham()) {
                case DaHoanThanh:
                    badgeClass = "badge-green";
                    break;
                case DangMay:
                case DangUi:
                case DangCat:
                default:
                    badgeClass = "badge-orange";
                    break;
            }
        } else {
            statusBadge.setText("N/A");
        }
        statusBadge.getStyleClass().addAll("fabric-card-badge", badgeClass);
        StackPane.setMargin(statusBadge, new Insets(8));
        StackPane.setAlignment(statusBadge, Pos.TOP_RIGHT);
        imgPlaceholder.getChildren().add(statusBadge);

        VBox infoBox = new VBox(4);
        infoBox.getStyleClass().add("fabric-card-info");

        Label titleLabel = new Label(sp.getTenSanPham());
        titleLabel.getStyleClass().add("fabric-card-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxHeight(40);

        Label codeLabel = new Label("Mã: " + sp.getMaSanPham());
        codeLabel.getStyleClass().add("fabric-card-detail");

        Label qtyLabel = new Label("Số lượng: " + sp.getTongSoBo() + " bộ / " + sp.getTongSoRi() + " ri");
        qtyLabel.getStyleClass().add("fabric-card-detail");

        Label priceLabel = new Label("Giá bán: " + String.format("%,.0f VNĐ", sp.getGiaThucTe()));
        priceLabel.getStyleClass().add("fabric-card-detail");

        Label typeLabel = new Label("Loại: " + (sp.getLoaiSanPham() != null ? sp.getLoaiSanPham().getTenLoai() : "N/A"));
        typeLabel.getStyleClass().add("fabric-card-detail");
        typeLabel.setWrapText(true);

        infoBox.getChildren().addAll(titleLabel, codeLabel, qtyLabel, priceLabel, typeLabel);
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
            card.setStyle("-fx-effect: dropshadow(gaussian, rgba(99,102,241,0.22), 15, 0, 0, 4); -fx-border-color: -color-accent;");
        });

        card.setOnMouseExited(e -> {
            hoverOverlay.setMouseTransparent(true);
            scaleUp.stop();
            fadeIn.stop();
            scaleDown.play();
            fadeOut.play();
            card.setStyle("");
        });

        btnView.setOnAction(e -> showSanPhamDetailsDialog(sp));

        btnOptions.setOnAction(e -> {
            ContextMenu contextMenu = new ContextMenu();

            MenuItem progressItem = new MenuItem("📊 Xem tiến độ");
            progressItem.setOnAction(ev -> {
                SanPhamProgressDialog progressDialog = new SanPhamProgressDialog(sp);
                progressDialog.showAndWait();
            });

            MenuItem editItem = new MenuItem("✏️ Chỉnh sửa");
            editItem.setOnAction(ev -> showSanPhamFormDialog(sp));

            // Sub-menu thay đổi trạng thái
            Menu statusMenu = new Menu("🔄 Thay đổi trạng thái");
            statusMenu.setDisable(sp.getTrangThaiSanPham() == TrangThaiSanPham.DaHoanThanh);
            MenuItem itemDangCat = new MenuItem("Đang cắt");
            MenuItem itemDangMay = new MenuItem("Đang may");
            MenuItem itemDangUi = new MenuItem("Đang ủi");
            MenuItem itemDaHoanThanh = new MenuItem("Đã hoàn thành");

            if (sp.getTrangThaiSanPham() == TrangThaiSanPham.DangCat) itemDangCat.setDisable(true);
            else if (sp.getTrangThaiSanPham() == TrangThaiSanPham.DangMay) itemDangMay.setDisable(true);
            else if (sp.getTrangThaiSanPham() == TrangThaiSanPham.DangUi) itemDangUi.setDisable(true);
            else if (sp.getTrangThaiSanPham() == TrangThaiSanPham.DaHoanThanh) itemDaHoanThanh.setDisable(true);

            itemDangCat.setOnAction(ev -> changeProductStatus(sp, TrangThaiSanPham.DangCat));
            itemDangMay.setOnAction(ev -> changeProductStatus(sp, TrangThaiSanPham.DangMay));
            itemDangUi.setOnAction(ev -> changeProductStatus(sp, TrangThaiSanPham.DangUi));
            itemDaHoanThanh.setOnAction(ev -> changeProductStatus(sp, TrangThaiSanPham.DaHoanThanh));

            statusMenu.getItems().addAll(itemDangCat, itemDangMay, itemDangUi, itemDaHoanThanh);

            MenuItem deleteItem = new MenuItem("🗑️ Xóa Sản Phẩm");
            deleteItem.setOnAction(ev -> {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Bạn có chắc chắn muốn xóa sản phẩm này?",
                        ButtonType.YES, ButtonType.NO);
                confirm.setHeaderText(null);
                confirm.showAndWait().ifPresent(response -> {
                    if (response == ButtonType.YES) {
                        spService.deleteSanPham(sp.getMaSanPham());
                        refreshProducts();
                    }
                });
            });

            contextMenu.getItems().addAll(progressItem, new SeparatorMenuItem(), editItem, statusMenu, new SeparatorMenuItem(), deleteItem);
            contextMenu.show(btnOptions, javafx.geometry.Side.BOTTOM, 0, 0);
        });

        return card;
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
        colDhId.setPrefWidth(70);

        TableColumn<DonHang, String> colDhKh = new TableColumn<>("Khách Hàng");
        colDhKh.setCellValueFactory(cellData -> {
            if (cellData.getValue().getKhachHang() != null) {
                return new SimpleStringProperty(cellData.getValue().getKhachHang().getTenKhachHang());
            }
            return new SimpleStringProperty("N/A");
        });
        colDhKh.setPrefWidth(130);

        TableColumn<DonHang, Double> colDhTotal = new TableColumn<>("Tổng Tiền");
        colDhTotal.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        colDhTotal.setPrefWidth(100);

        TableColumn<DonHang, String> colDhStatus = new TableColumn<>("Trạng Thái");
        colDhStatus.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTrangThaiDonHang().toString()));
        colDhStatus.setPrefWidth(90);

        TableColumn<DonHang, String> colDhPayment = new TableColumn<>("Thanh Toán");
        colDhPayment.setCellValueFactory(cellData -> {
            String status = "Chưa thanh toán";
            String dhId = cellData.getValue().getMaDonHang();
            for (HoaDon hd : bhService.getAllHoaDon()) {
                if (hd.getDonHang() != null && hd.getDonHang().getMaDonHang().equals(dhId)) {
                    status = hd.getTrangThaiHoaDon().toString();
                    break;
                }
            }
            return new SimpleStringProperty(status);
        });
        colDhPayment.setCellFactory(column -> new TableCell<DonHang, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    if ("Đã thanh toán".equals(item)) {
                        setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                    } else {
                        setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                    }
                }
            }
        });
        colDhPayment.setPrefWidth(120);

        tableDh.getColumns().addAll(colDhId, colDhKh, colDhTotal, colDhStatus, colDhPayment);

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
        
        HBox formHeader = new HBox(10);
        formHeader.setAlignment(Pos.CENTER_LEFT);
        Label lblFormTitle = new Label("Thông Tin Đơn Hàng");
        lblFormTitle.getStyleClass().add("sub-title");
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);
        Button btnCreateOrder = new Button("➕ Tạo đơn hàng");
        btnCreateOrder.getStyleClass().addAll("btn", "btn-primary");
        btnCreateOrder.setOnAction(e -> clearOrderForm());
        formHeader.getChildren().addAll(lblFormTitle, headerSpacer, btnCreateOrder);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        txtDhId = new TextField();
        txtDhId.setEditable(false);
        txtDhId.setPromptText("Tự động tạo khi lưu");
        comboCustomer = new ComboBox<>();
        pickerOrderDate = new DatePicker(LocalDate.now());
        comboDhStatus = new ComboBox<>(FXCollections.observableArrayList(TrangThaiDonHang.values()));
        comboDhStatus.setValue(TrangThaiDonHang.ChuaGiao);
        txtDhNote = new TextField();
        comboPayMethod = new ComboBox<>(FXCollections.observableArrayList(PhuongThucThanhToan.values()));
        comboPayMethod.setValue(PhuongThucThanhToan.TienMat);
        comboInvoiceStatus = new ComboBox<>(FXCollections.observableArrayList(TrangThaiHoaDon.values()));
        comboInvoiceStatus.setValue(TrangThaiHoaDon.DaThanhToan);

        // Ràng buộc nghiệp vụ: Chưa giao thì bắt buộc Chưa thanh toán và disable combo, Đã giao thì tự do chọn
        comboDhStatus.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == TrangThaiDonHang.ChuaGiao) {
                comboInvoiceStatus.setValue(TrangThaiHoaDon.ChuaThanhToan);
                comboInvoiceStatus.setDisable(true);
            } else {
                comboInvoiceStatus.setDisable(false);
            }
        });
        if (comboDhStatus.getValue() == TrangThaiDonHang.ChuaGiao) {
            comboInvoiceStatus.setValue(TrangThaiHoaDon.ChuaThanhToan);
            comboInvoiceStatus.setDisable(true);
        }

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
        
        Button btnReset = new Button("Làm mới");
        btnReset.getStyleClass().addAll("btn", "btn-secondary");
        btnReset.setOnAction(e -> clearOrderForm());

        lblTotalAmount = new Label("Tổng tiền: 0 đ");
        lblTotalAmount.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #10b981;");
        
        Button btnSaveOrder = new Button("Lưu Đơn");
        btnSaveOrder.getStyleClass().addAll("btn", "btn-success");
        summaryBox.getChildren().addAll(btnReset, lblTotalAmount, btnSaveOrder);

        orderForm.getChildren().addAll(formHeader, grid, sep, addBox, tableCtdh, summaryBox);
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
                
                // Hiển thị phương thức thanh toán và trạng thái hóa đơn đi kèm
                for (HoaDon hd : bhService.getAllHoaDon()) {
                    if (hd.getDonHang() != null && hd.getDonHang().getMaDonHang().equals(newVal.getMaDonHang())) {
                        comboPayMethod.setValue(hd.getPhuongThucThanhToan());
                        comboInvoiceStatus.setValue(hd.getTrangThaiHoaDon());
                        break;
                    }
                }
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
                double unitPrice = sp.getGiaThucTe();
                ChiTietDonHang ct = new ChiTietDonHang(null, sp, qty, unitPrice, qty * unitPrice);
                tempDetailsList.add(ct);
            }
            tableCtdh.refresh();
            updateCartTotal();
            txtAddQty.clear();
        });

        btnSaveOrder.setOnAction(e -> {
            KhachHang kh = comboCustomer.getValue();
            LocalDate oDate = pickerOrderDate.getValue();
            TrangThaiDonHang dhStatus = comboDhStatus.getValue();
            String note = txtDhNote.getText().trim();
            PhuongThucThanhToan pt = comboPayMethod.getValue();
            TrangThaiHoaDon tthd = comboInvoiceStatus.getValue();

            if (kh == null || oDate == null || dhStatus == null || tempDetailsList.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Lỗi dữ liệu", "Vui lòng chọn khách hàng và thêm ít nhất một sản phẩm vào giỏ!");
                return;
            }

            if (selectedDh == null) {
                // Tự động sinh mã đơn hàng duy nhất (tránh lỗi effectively-final trong lambda)
                String tempId = "DH" + (System.currentTimeMillis() % 1000000);
                boolean exists = true;
                while (exists) {
                    final String checkId = tempId;
                    exists = bhService.getAllDonHang().stream().anyMatch(d -> d.getMaDonHang().equals(checkId));
                    if (exists) {
                        tempId = "DH" + ((System.currentTimeMillis() + (int)(Math.random() * 1000)) % 1000000);
                    }
                }
                String newDhId = tempId;

                DonHang dh = new DonHang(newDhId, kh, oDate, 0.0, dhStatus, note);
                bhService.addDonHang(dh, new ArrayList<>(tempDetailsList), pt, tthd);
                
                txtDhId.setText(newDhId); // Hiện mã đơn hàng ra sau khi lưu
                showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã lưu đơn hàng thành công với Mã đơn: " + newDhId);
            } else {
                showAlert(Alert.AlertType.INFORMATION, "Thông báo", "Không hỗ trợ sửa trực tiếp đơn hàng để bảo toàn hóa đơn. Hãy hủy đơn và tạo lại.");
                return;
            }
            refreshOrders();
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
        if (spCardsPane == null) return;
        spCardsPane.getChildren().clear();

        List<SanPham> spList = spService.getAllSanPham();

        // Update add order combo
        if (comboAddSp != null) {
            comboAddSp.setItems(FXCollections.observableArrayList(spList));
        }

        // Apply filters
        String statusFilter = comboFilterStatusSanPham != null ? comboFilterStatusSanPham.getValue() : "Tất cả khâu";
        String typeFilter = comboFilterTypeSanPham != null ? comboFilterTypeSanPham.getValue() : "Tất cả loại";

        for (SanPham sp : spList) {
            boolean statusMatches = "Tất cả khâu".equals(statusFilter) || 
                    (sp.getTrangThaiSanPham() != null && sp.getTrangThaiSanPham().toString().equals(statusFilter));
            
            boolean typeMatches = "Tất cả loại".equals(typeFilter) || 
                    (sp.getLoaiSanPham() != null && sp.getLoaiSanPham().getTenLoai().equals(typeFilter));

            if (statusMatches && typeMatches) {
                spCardsPane.getChildren().add(createSanPhamCard(sp));
            }
        }

        // Re-load the process Kanban board
        refreshProcessView();
    }

    private void refreshOrders() {
        if (tableDh != null) {
            tableDh.setItems(FXCollections.observableArrayList(bhService.getAllDonHang()));
        }
        if (comboCustomer != null) {
            comboCustomer.setItems(FXCollections.observableArrayList(bhService.getAllKhachHang()));
        }
    }

    private void clearOrderForm() {
        selectedDh = null;
        txtDhId.clear();
        txtDhId.setEditable(false);
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

    public void selectCustomerAndOrderTab(KhachHang kh) {
        clearOrderForm();
        if (comboCustomer != null) {
            comboCustomer.setValue(kh);
        }
        if (tabPane != null && tabPane.getTabs().size() > 2) {
            tabPane.getSelectionModel().select(2); // index 2 is "Đơn Bán Hàng"
        }
        if (txtDhId != null) {
            txtDhId.clear();
        }
    }

    /**
     * Gọi từ NhanSuPanel sau khi lưu phân công — refresh cả danh sách sản phẩm và Quy trình Kanban.
     */
    public void refreshForPhanCong() {
        refreshProducts(); // refreshProducts() đã gọi refreshProcessView() bên trong
    }
}
