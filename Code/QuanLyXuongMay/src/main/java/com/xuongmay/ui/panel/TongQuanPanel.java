package com.xuongmay.ui.panel;

import com.xuongmay.model.*;
import com.xuongmay.service.NguyenLieuService;
import com.xuongmay.service.BanHangService;
import com.xuongmay.service.SanPhamService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class TongQuanPanel extends VBox {
    private final NguyenLieuService nguyenLieuService = new NguyenLieuService();
    private final BanHangService banHangService = new BanHangService();
    private final SanPhamService sanPhamService = new SanPhamService();

    // 6 Stat Cards
    private Label lblLoChuaSuDungVal, lblLoDangSuDungVal, lblLoRaSanPhamVal;
    private Label lblDonChuaGiaoVal, lblDonDaGiaoVal, lblDoanhThuVal;

    // 7 Process Steps
    private Label lblStepNhapVai, lblStepCat, lblStepMay, lblStepUi;
    private Label lblStepHoanThanh, lblStepGiaoHang, lblStepDaGiao;

    // Chart
    private BarChart<String, Number> revenueChart;

    // Recent Orders & Activity
    private VBox recentOrdersContainer;
    private VBox activityContainer;

    public TongQuanPanel() {
        setSpacing(18);
        setPadding(new Insets(20));
        VBox.setVgrow(this, Priority.ALWAYS);

        // Use ScrollPane to prevent overflow
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.getStyleClass().add("edge-to-edge-scroll");
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        VBox contentBox = new VBox(18);
        contentBox.setPadding(new Insets(0, 2, 20, 0));

        // 1. Header
        VBox headerBox = new VBox(4);
        Label lblTitle = new Label("Tổng quan");
        lblTitle.getStyleClass().add("tab-title");
        Label lblSubtitle = new Label("Đây là tổng quan hoạt động xưởng may hôm nay. Cho bạn cái nhìn tổng quát nhất.");
        lblSubtitle.getStyleClass().add("tab-subtitle");
        headerBox.getChildren().addAll(lblTitle, lblSubtitle);

        // 2. Stats Row (6 cards)
        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(14);
        for (int i = 0; i < 6; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 6.0);
            statsGrid.getColumnConstraints().add(col);
        }

        lblLoChuaSuDungVal = new Label("0");
        lblLoDangSuDungVal = new Label("0");
        lblLoRaSanPhamVal = new Label("0");
        lblDonChuaGiaoVal = new Label("0");
        lblDonDaGiaoVal = new Label("0");
        lblDoanhThuVal = new Label("0");

        VBox card1 = createStatCard("📦", "stat-icon-blue", lblLoChuaSuDungVal, "Lô chưa sử dụng");
        VBox card2 = createStatCard("⚙️", "stat-icon-teal", lblLoDangSuDungVal, "Đang sử dụng");
        VBox card3 = createStatCard("🏷️", "stat-icon-green", lblLoRaSanPhamVal, "Đã ra sản phẩm");
        VBox card4 = createStatCard("📝", "stat-icon-orange", lblDonChuaGiaoVal, "Đơn chưa giao");
        VBox card5 = createStatCard("🚚", "stat-icon-purple", lblDonDaGiaoVal, "Đã giao");
        VBox card6 = createStatCard("💰", "stat-icon-gold", lblDoanhThuVal, "Doanh thu");

        statsGrid.add(card1, 0, 0);
        statsGrid.add(card2, 1, 0);
        statsGrid.add(card3, 2, 0);
        statsGrid.add(card4, 3, 0);
        statsGrid.add(card5, 4, 0);
        statsGrid.add(card6, 5, 0);

        // 3. Middle Row: Process (compact) | Revenue Chart | Recent Orders
        HBox middleRow = new HBox(16);
        HBox.setHgrow(middleRow, Priority.ALWAYS);

        // --- LEFT: Compact Process Flow ---
        VBox processCard = createProcessSection();
        processCard.setPrefWidth(260);
        processCard.setMinWidth(240);

        // --- CENTER: Revenue Chart ---
        VBox chartCard = createRevenueChart();
        HBox.setHgrow(chartCard, Priority.ALWAYS);

        // --- RIGHT: Recent Orders ---
        VBox recentCard = createRecentOrdersSection();
        recentCard.setPrefWidth(300);
        recentCard.setMinWidth(270);

        middleRow.getChildren().addAll(processCard, chartCard, recentCard);

        // 4. Bottom Row: Activity Log (full width)
        VBox activityCard = createActivitySection();

        contentBox.getChildren().addAll(headerBox, statsGrid, middleRow, activityCard);
        scrollPane.setContent(contentBox);
        getChildren().add(scrollPane);

        refreshData();
    }

    // ========== STAT CARD (Vertical, matching hotel style) ==========
    private VBox createStatCard(String iconStr, String iconColorClass, Label numberLbl, String descStr) {
        VBox card = new VBox(6);
        card.getStyleClass().add("stat-card-vertical");
        card.setAlignment(Pos.CENTER);

        StackPane iconContainer = new StackPane();
        iconContainer.getStyleClass().addAll("stat-icon-container", iconColorClass);
        Label iconLabel = new Label(iconStr);
        iconLabel.getStyleClass().add("stat-icon-lbl");
        iconContainer.getChildren().add(iconLabel);

        Label descLabel = new Label(descStr);
        descLabel.getStyleClass().add("stat-desc-lbl-top");

        numberLbl.getStyleClass().add("stat-number-lbl");

        card.getChildren().addAll(iconContainer, numberLbl, descLabel);
        return card;
    }

    // ========== COMPACT PROCESS FLOW ==========
    private VBox createProcessSection() {
        VBox card = new VBox(10);
        card.getStyleClass().add("process-card");

        Label title = new Label("Quy trình sản xuất");
        title.getStyleClass().add("section-title");

        lblStepNhapVai = new Label("0");
        lblStepCat = new Label("0");
        lblStepMay = new Label("0");
        lblStepUi = new Label("0");
        lblStepHoanThanh = new Label("0");
        lblStepGiaoHang = new Label("0");
        lblStepDaGiao = new Label("0");

        VBox stepsContainer = new VBox(4);
        stepsContainer.getChildren().addAll(
            createCompactStep("dot-orange", "Nhập vải", lblStepNhapVai),
            createCompactStep("dot-blue", "Cắt", lblStepCat),
            createCompactStep("dot-purple", "May", lblStepMay),
            createCompactStep("dot-teal", "Ủi", lblStepUi),
            createCompactStep("dot-green", "Hoàn thành", lblStepHoanThanh),
            createCompactStep("dot-yellow", "Giao hàng", lblStepGiaoHang),
            createCompactStep("dot-emerald", "Đã giao", lblStepDaGiao)
        );

        card.getChildren().addAll(title, stepsContainer);
        return card;
    }

    private HBox createCompactStep(String dotClass, String name, Label valueLbl) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        row.getStyleClass().add("compact-step-row");

        StackPane dot = new StackPane();
        dot.getStyleClass().addAll("step-dot", dotClass);

        Label nameLabel = new Label(name);
        nameLabel.getStyleClass().add("compact-step-name");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        valueLbl.getStyleClass().add("compact-step-value");

        row.getChildren().addAll(dot, nameLabel, spacer, valueLbl);
        return row;
    }

    // ========== REVENUE CHART ==========
    private VBox createRevenueChart() {
        VBox card = new VBox(10);
        card.getStyleClass().add("process-card");
        HBox.setHgrow(card, Priority.ALWAYS);

        HBox chartHeader = new HBox();
        chartHeader.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Doanh thu 7 ngày");
        title.getStyleClass().add("section-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label hint = new Label("Chi tiết →");
        hint.getStyleClass().add("link-btn");
        chartHeader.getChildren().addAll(title, sp, hint);

        // BarChart
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel(null);
        yAxis.setLabel(null);
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override
            public String toString(Number object) {
                double val = object.doubleValue();
                if (val >= 1_000_000) return String.format("%.0fM", val / 1_000_000);
                if (val >= 1_000) return String.format("%.0fK", val / 1_000);
                return String.format("%.0f", val);
            }
        });

        revenueChart = new BarChart<>(xAxis, yAxis);
        revenueChart.setLegendVisible(false);
        revenueChart.setAnimated(true);
        revenueChart.setPrefHeight(220);
        revenueChart.getStyleClass().add("revenue-chart");
        VBox.setVgrow(revenueChart, Priority.ALWAYS);

        // Legend row
        HBox legendBox = new HBox(16);
        legendBox.setAlignment(Pos.CENTER_LEFT);
        legendBox.getChildren().addAll(
            createLegendItem("dot-blue", "Thu nhập"),
            createLegendItem("dot-green", "Chi phí"),
            createLegendItem("dot-teal", "Lợi nhuận")
        );

        card.getChildren().addAll(chartHeader, revenueChart, legendBox);
        return card;
    }

    private HBox createLegendItem(String dotClass, String text) {
        HBox item = new HBox(5);
        item.setAlignment(Pos.CENTER_LEFT);
        StackPane dot = new StackPane();
        dot.getStyleClass().addAll("step-dot", dotClass);
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #6b7280;");
        item.getChildren().addAll(dot, lbl);
        return item;
    }

    // ========== RECENT ORDERS ==========
    private VBox createRecentOrdersSection() {
        VBox card = new VBox(10);
        card.getStyleClass().add("process-card");

        Label title = new Label("Đơn hàng gần đây");
        title.getStyleClass().add("section-title");

        recentOrdersContainer = new VBox(4);
        VBox.setVgrow(recentOrdersContainer, Priority.ALWAYS);

        card.getChildren().addAll(title, recentOrdersContainer);
        return card;
    }

    // ========== ACTIVITY LOG ==========
    private VBox createActivitySection() {
        VBox card = new VBox(10);
        card.getStyleClass().add("process-card");

        HBox actHeader = new HBox();
        actHeader.setAlignment(Pos.CENTER_LEFT);
        Label title = new Label("Hoạt động gần đây");
        title.getStyleClass().add("section-title");
        Region sp = new Region();
        HBox.setHgrow(sp, Priority.ALWAYS);
        Label viewAll = new Label("Xem tất cả →");
        viewAll.getStyleClass().add("link-btn");
        actHeader.getChildren().addAll(title, sp, viewAll);

        activityContainer = new VBox(2);

        card.getChildren().addAll(actHeader, activityContainer);
        return card;
    }

    // ========== REFRESH DATA ==========
    public void refreshData() {
        List<LoVai> lots = nguyenLieuService.getAllLoVai();
        List<DonHang> orders = banHangService.getAllDonHang();
        List<SanPham> products = sanPhamService.getAllSanPham();
        List<HoaDon> invoices = banHangService.getAllHoaDon();

        // === STAT CARDS ===
        long loChuaSuDung = lots.stream().filter(l -> l.getTrangThaiLoVai() == TrangThaiLoVai.ChuaSuDung).count();
        long loDangSuDung = lots.stream().filter(l -> l.getTrangThaiLoVai() == TrangThaiLoVai.DangSuDung).count();
        long loRaSanPham = lots.stream().filter(l -> l.getTrangThaiLoVai() == TrangThaiLoVai.RaSanPham).count();
        long donChuaGiao = orders.stream().filter(o -> o.getTrangThaiDonHang() == TrangThaiDonHang.ChuaGiao).count();
        long donDaGiao = orders.stream().filter(o -> o.getTrangThaiDonHang() == TrangThaiDonHang.DaGiao).count();

        double doanhThu = invoices.stream()
                .filter(hd -> hd.getTrangThaiHoaDon() == TrangThaiHoaDon.DaThanhToan)
                .mapToDouble(HoaDon::getTongTienHoaDon)
                .sum();

        lblLoChuaSuDungVal.setText(String.valueOf(loChuaSuDung));
        lblLoDangSuDungVal.setText(String.valueOf(loDangSuDung));
        lblLoRaSanPhamVal.setText(String.valueOf(loRaSanPham));
        lblDonChuaGiaoVal.setText(String.valueOf(donChuaGiao));
        lblDonDaGiaoVal.setText(String.valueOf(donDaGiao));
        lblDoanhThuVal.setText(formatCompact(doanhThu));

        // === PROCESS STEPS ===
        lblStepNhapVai.setText(String.valueOf(loChuaSuDung));
        lblStepCat.setText(String.valueOf(products.stream().filter(sp -> sp.getTrangThaiSanPham() == TrangThaiSanPham.DangCat).count()));
        lblStepMay.setText(String.valueOf(products.stream().filter(sp -> sp.getTrangThaiSanPham() == TrangThaiSanPham.DangMay).count()));
        lblStepUi.setText(String.valueOf(products.stream().filter(sp -> sp.getTrangThaiSanPham() == TrangThaiSanPham.DangUi).count()));
        lblStepHoanThanh.setText(String.valueOf(products.stream().filter(sp -> sp.getTrangThaiSanPham() == TrangThaiSanPham.DaHoanThanh).count()));
        lblStepGiaoHang.setText(String.valueOf(donChuaGiao));
        lblStepDaGiao.setText(String.valueOf(donDaGiao));

        // === REVENUE CHART (Mock 7-day data) ===
        revenueChart.getData().clear();
        XYChart.Series<String, Number> revenueSeries = new XYChart.Series<>();
        revenueSeries.setName("Doanh thu");

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        Random rnd = new Random(42); // fixed seed for consistent mock data
        LocalDate today = LocalDate.now();

        // Generate revenue based on actual total + random variation
        double baseRevenue = doanhThu > 0 ? doanhThu / 7.0 : 500_000;
        for (int i = 6; i >= 0; i--) {
            String dayLabel = today.minusDays(i).format(fmt);
            double dayRevenue = baseRevenue * (0.5 + rnd.nextDouble() * 1.2);
            revenueSeries.getData().add(new XYChart.Data<>(dayLabel, dayRevenue));
        }
        revenueChart.getData().add(revenueSeries);

        // === RECENT ORDERS ===
        recentOrdersContainer.getChildren().clear();
        List<DonHang> recent = orders.stream()
                .sorted((o1, o2) -> o2.getNgayDat().compareTo(o1.getNgayDat()))
                .limit(5)
                .collect(Collectors.toList());

        if (recent.isEmpty()) {
            Label noOrders = new Label("Không có đơn hàng");
            noOrders.setStyle("-fx-text-fill: #9ca3af; -fx-font-style: italic; -fx-padding: 10 0;");
            recentOrdersContainer.getChildren().add(noOrders);
        } else {
            for (DonHang dh : recent) {
                recentOrdersContainer.getChildren().add(createOrderRow(dh));
            }
        }

        // === ACTIVITY LOG ===
        activityContainer.getChildren().clear();
        // Build activity from real data
        int actCount = 0;
        for (DonHang dh : orders.stream()
                .sorted((a, b) -> b.getNgayDat().compareTo(a.getNgayDat()))
                .limit(3)
                .collect(Collectors.toList())) {
            String customerName = dh.getKhachHang() != null ? dh.getKhachHang().getTenKhachHang() : "Khách lẻ";
            activityContainer.getChildren().add(
                createActivityRow("📋", "Tạo đơn hàng " + dh.getMaDonHang() + " — " + customerName, dh.getNgayDat().toString())
            );
            actCount++;
        }
        for (LoVai lo : lots.stream()
                .sorted((a, b) -> b.getNgayNhap().compareTo(a.getNgayNhap()))
                .limit(3)
                .collect(Collectors.toList())) {
            activityContainer.getChildren().add(
                createActivityRow("📦", "Nhập lô vải " + lo.getMaLo() + " — " + lo.getTenLo(), lo.getNgayNhap().toString())
            );
            actCount++;
        }
        for (HoaDon hd : invoices.stream().limit(2).collect(Collectors.toList())) {
            activityContainer.getChildren().add(
                createActivityRow("🧾", "Xuất hóa đơn " + hd.getMaHoaDon() + " — " + formatCompact(hd.getTongTienHoaDon()),
                    hd.getNgayLap().toString())
            );
        }
        if (activityContainer.getChildren().isEmpty()) {
            Label noAct = new Label("Chưa có hoạt động nào");
            noAct.setStyle("-fx-text-fill: #9ca3af; -fx-font-style: italic;");
            activityContainer.getChildren().add(noAct);
        }
    }

    // ========== HELPER: Order Row ==========
    private HBox createOrderRow(DonHang dh) {
        HBox row = new HBox(10);
        row.getStyleClass().add("recent-order-item");
        row.setAlignment(Pos.CENTER_LEFT);

        VBox info = new VBox(1);
        Label id = new Label(dh.getMaDonHang());
        id.getStyleClass().add("recent-order-id");
        Label customer = new Label(dh.getKhachHang() != null ? dh.getKhachHang().getTenKhachHang() : "Khách Lẻ");
        customer.getStyleClass().add("recent-order-customer");
        info.getChildren().addAll(id, customer);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Label badge;
        if (dh.getTrangThaiDonHang() == TrangThaiDonHang.DaGiao) {
            badge = new Label("Đã giao");
            badge.getStyleClass().add("badge-green");
        } else {
            badge = new Label("Chưa giao");
            badge.getStyleClass().add("badge-orange");
        }

        row.getChildren().addAll(info, spacer, badge);
        return row;
    }

    // ========== HELPER: Activity Row ==========
    private HBox createActivityRow(String icon, String text, String time) {
        HBox row = new HBox(10);
        row.getStyleClass().add("activity-row");
        row.setAlignment(Pos.CENTER_LEFT);

        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 16px;");

        VBox textBox = new VBox(1);
        Label desc = new Label(text);
        desc.getStyleClass().add("activity-desc");
        Label timeLbl = new Label(time);
        timeLbl.getStyleClass().add("activity-time");
        textBox.getChildren().addAll(desc, timeLbl);

        row.getChildren().addAll(iconLbl, textBox);
        return row;
    }

    private String formatCompact(double amount) {
        if (amount >= 1_000_000_000) return String.format("%.1fB đ", amount / 1_000_000_000.0);
        if (amount >= 1_000_000) return String.format("%.1fM đ", amount / 1_000_000.0);
        if (amount >= 1_000) return String.format("%.0fK đ", amount / 1_000.0);
        return String.format("%,.0f đ", amount);
    }
}
