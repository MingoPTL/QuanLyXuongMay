package com.xuongmay.ui.panel;

import com.xuongmay.service.ThongKeService;
import com.xuongmay.service.ThongKeService.*;
import com.xuongmay.util.NotificationUtils;
import com.xuongmay.util.NotificationUtils.NotificationType;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ThongKePanel extends VBox {

    private final ThongKeService service = new ThongKeService();

    // KPI labels
    private Label lblDoanhThu, lblSoDon, lblRiXuat, lblTiLe;
    private Label lblDoanhThuSub, lblSoDonSub;

    // Charts
    private LineChart<String, Number> lineChart;
    private BarChart<Number, String>  barTopSP;
    private BarChart<String, Number>  barCongSuat;
    private PieChart                  pieChart;

    // Tables
    private VBox tableTopSP, tableTopKH;

    // State
    private String currentRange = "Tuần";

    private static final String[] RANGE_LABELS = {"Tuần", "Tháng", "Quý", "Năm"};

    public ThongKePanel() {
        setSpacing(0);
        setPadding(new Insets(0));
        VBox.setVgrow(this, Priority.ALWAYS);

        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.getStyleClass().add("edge-to-edge-scroll");
        VBox.setVgrow(scroll, Priority.ALWAYS);

        VBox content = new VBox(20);
        content.setPadding(new Insets(22, 22, 30, 22));

        content.getChildren().addAll(
            buildHeader(),
            buildKpiRow(),
            buildChartsRow1(),
            buildChartsRow2(),
            buildTablesRow()
        );

        scroll.setContent(content);
        getChildren().add(scroll);

        refreshData();
    }

    // ── 1. Header ───────────────────────────────────────────────────
    private HBox buildHeader() {
        HBox header = new HBox(12);
        header.setAlignment(Pos.CENTER_LEFT);

        VBox titleBox = new VBox(2);
        Label title = new Label("Thống Kê & Báo Cáo");
        title.getStyleClass().add("tab-title");
        Label sub = new Label("Phân tích doanh thu, sản phẩm và công suất sản xuất theo kỳ.");
        sub.getStyleClass().add("tab-subtitle");
        titleBox.getChildren().addAll(title, sub);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Time-range toggle buttons
        HBox toggleGroup = new HBox(4);
        toggleGroup.setAlignment(Pos.CENTER);
        toggleGroup.setStyle("-fx-background-color: #f1f5f9; -fx-background-radius: 8; -fx-padding: 4;");

        for (String label : RANGE_LABELS) {
            Button btn = new Button(label);
            btn.setStyle(label.equals("Tuần")
                ? "-fx-background-color: #1e40af; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-weight: bold;"
                : "-fx-background-color: transparent; -fx-text-fill: #475569; -fx-background-radius: 6; -fx-padding: 6 14; -fx-cursor: hand;");
            btn.setOnAction(e -> {
                currentRange = label;
                // Reset all styles
                toggleGroup.getChildren().forEach(n -> ((Button) n).setStyle(
                    "-fx-background-color: transparent; -fx-text-fill: #475569; -fx-background-radius: 6; -fx-padding: 6 14; -fx-cursor: hand;"
                ));
                btn.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; -fx-background-radius: 6; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-weight: bold;");
                refreshData();
                NotificationUtils.show("Bộ lọc", "Đã cập nhật dữ liệu theo: " + label.toLowerCase(), NotificationType.INFO);
            });
            toggleGroup.getChildren().add(btn);
        }

        Button btnRefresh = new Button("↺  Làm mới");
        btnRefresh.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #334155; -fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand;");
        btnRefresh.setOnAction(e -> {
            refreshData();
            NotificationUtils.show("Làm mới", "Toàn bộ dữ liệu thống kê đã được cập nhật!", NotificationType.SUCCESS);
        });

        Button btnExport = new Button("🖨️  Xuất báo cáo");
        btnExport.setStyle("-fx-background-color: #1e40af; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 6 14; -fx-cursor: hand; -fx-font-weight: bold;");
        btnExport.setOnAction(e -> exportPdfReport());

        header.getChildren().addAll(titleBox, spacer, toggleGroup, btnRefresh, btnExport);
        return header;
    }

    // ── 2. KPI Cards ────────────────────────────────────────────────
    private HBox buildKpiRow() {
        HBox row = new HBox(14);
        row.setFillHeight(true);

        lblDoanhThu    = new Label("—");
        lblSoDon       = new Label("—");
        lblRiXuat      = new Label("—");
        lblTiLe        = new Label("—");
        lblDoanhThuSub = new Label("Trong kỳ");
        lblSoDonSub    = new Label("Trong kỳ");

        HBox c1 = kpiCard("📈", "Doanh thu", lblDoanhThu,    lblDoanhThuSub, "#1e40af", "#dbeafe");
        HBox c2 = kpiCard("🧾", "Số đơn hàng", lblSoDon,    lblSoDonSub,    "#7c3aed", "#ede9fe");
        HBox c3 = kpiCard("👕", "Ri xuất xưởng", lblRiXuat,  new Label("Tổng thực tế"), "#ea580c", "#ffedd5");
        HBox c4 = kpiCard("✅", "Tỷ lệ T.Toán",  lblTiLe,   new Label("Đã thu tiền"),  "#16a34a", "#dcfce7");

        for (HBox c : new HBox[]{c1, c2, c3, c4}) {
            HBox.setHgrow(c, Priority.ALWAYS);
            row.getChildren().add(c);
        }
        return row;
    }

    private HBox kpiCard(String icon, String title, Label valLbl, Label subLbl,
                         String colorHex, String bgHex) {
        HBox card = new HBox(14);
        card.setAlignment(Pos.CENTER_LEFT);
        card.setStyle(String.format(
            "-fx-background-color: white; -fx-background-radius: 12;" +
            "-fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-border-width: 1;" +
            "-fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);"));

        // Icon circle
        StackPane iconBox = new StackPane();
        iconBox.setMinSize(48, 48);
        iconBox.setMaxSize(48, 48);
        iconBox.setStyle("-fx-background-color: " + bgHex + "; -fx-background-radius: 12;");
        Label iconLbl = new Label(icon);
        iconLbl.setStyle("-fx-font-size: 22px;");
        iconBox.getChildren().add(iconLbl);

        VBox textBox = new VBox(3);
        Label titleLbl = new Label(title);
        titleLbl.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b; -fx-font-weight: bold;");
        valLbl.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: " + colorHex + ";");
        subLbl.setStyle("-fx-font-size: 10px; -fx-text-fill: #94a3b8;");
        textBox.getChildren().addAll(titleLbl, valLbl, subLbl);

        card.getChildren().addAll(iconBox, textBox);
        return card;
    }

    // ── 3. Charts Row 1: LineChart + BarChart Top SP ────────────────
    private HBox buildChartsRow1() {
        HBox row = new HBox(14);
        row.setFillHeight(true);

        // LineChart
        VBox lineCard = new VBox(10);
        lineCard.setStyle(cardStyle());
        lineCard.setPrefHeight(280);
        HBox.setHgrow(lineCard, Priority.ALWAYS);

        Label lineTitle = sectionTitle("📊  Doanh thu theo ngày");
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis   yAxis = new NumberAxis();
        xAxis.setLabel(null);
        yAxis.setLabel(null);
        yAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(yAxis) {
            @Override public String toString(Number n) {
                double v = n.doubleValue();
                if (v >= 1_000_000) return String.format("%.0fM", v / 1_000_000);
                if (v >= 1_000)     return String.format("%.0fK", v / 1_000);
                return String.format("%.0f", v);
            }
        });
        lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(false);
        lineChart.setAnimated(false);
        lineChart.setCreateSymbols(true);
        lineChart.setPrefHeight(230);
        VBox.setVgrow(lineChart, Priority.ALWAYS);
        lineChart.getStyleClass().add("revenue-chart");
        lineCard.getChildren().addAll(lineTitle, lineChart);

        // Horizontal BarChart: Top sản phẩm
        VBox barCard = new VBox(10);
        barCard.setStyle(cardStyle());
        barCard.setPrefHeight(280);
        barCard.setPrefWidth(340);

        Label barTitle = sectionTitle("🏆  Top sản phẩm bán chạy");
        NumberAxis numAxis = new NumberAxis();
        CategoryAxis catAxis = new CategoryAxis();
        numAxis.setLabel(null);
        catAxis.setLabel(null);
        barTopSP = new BarChart<>(numAxis, catAxis);
        barTopSP.setLegendVisible(false);
        barTopSP.setAnimated(false);
        barTopSP.setPrefHeight(230);
        VBox.setVgrow(barTopSP, Priority.ALWAYS);
        barTopSP.getStyleClass().add("revenue-chart");
        barCard.getChildren().addAll(barTitle, barTopSP);

        row.getChildren().addAll(lineCard, barCard);
        return row;
    }

    // ── 4. Charts Row 2: CongSuat BarChart + PieChart ───────────────
    private HBox buildChartsRow2() {
        HBox row = new HBox(14);
        row.setFillHeight(true);

        // BarChart công suất (grouped: dự kiến vs thực tế)
        VBox csCard = new VBox(10);
        csCard.setStyle(cardStyle());
        csCard.setPrefHeight(280);
        HBox.setHgrow(csCard, Priority.ALWAYS);

        Label csTitle = sectionTitle("⚙️  Công suất sản xuất (Ri)");
        CategoryAxis csX = new CategoryAxis();
        NumberAxis   csY = new NumberAxis();
        csY.setLabel(null); csX.setLabel(null);
        barCongSuat = new BarChart<>(csX, csY);
        barCongSuat.setAnimated(false);
        barCongSuat.setPrefHeight(230);
        barCongSuat.setLegendVisible(true);
        VBox.setVgrow(barCongSuat, Priority.ALWAYS);
        barCongSuat.getStyleClass().add("revenue-chart");
        csCard.getChildren().addAll(csTitle, barCongSuat);

        // PieChart đơn hàng
        VBox pieCard = new VBox(10);
        pieCard.setStyle(cardStyle());
        pieCard.setPrefHeight(280);
        pieCard.setPrefWidth(300);

        Label pieTitle = sectionTitle("📦  Trạng thái đơn hàng");
        pieChart = new PieChart();
        pieChart.setLegendVisible(true);
        pieChart.setAnimated(false);
        pieChart.setPrefHeight(230);
        VBox.setVgrow(pieChart, Priority.ALWAYS);
        pieCard.getChildren().addAll(pieTitle, pieChart);

        row.getChildren().addAll(csCard, pieCard);
        return row;
    }

    // ── 5. Tables Row ────────────────────────────────────────────────
    private HBox buildTablesRow() {
        HBox row = new HBox(14);
        row.setFillHeight(true);

        VBox t1Card = new VBox(10);
        t1Card.setStyle(cardStyle());
        HBox.setHgrow(t1Card, Priority.ALWAYS);
        t1Card.getChildren().add(sectionTitle("🥇  Top 5 sản phẩm bán chạy"));
        tableTopSP = new VBox(0);
        tableTopSP.getChildren().add(tableHeader(new String[]{"STT", "Sản phẩm", "SL (Ri)", "Doanh thu"}));
        t1Card.getChildren().add(tableTopSP);

        VBox t2Card = new VBox(10);
        t2Card.setStyle(cardStyle());
        HBox.setHgrow(t2Card, Priority.ALWAYS);
        t2Card.getChildren().add(sectionTitle("👤  Top 5 khách hàng"));
        tableTopKH = new VBox(0);
        tableTopKH.getChildren().add(tableHeader(new String[]{"STT", "Khách hàng", "Số đơn", "Tổng chi"}));
        t2Card.getChildren().add(tableTopKH);

        row.getChildren().addAll(t1Card, t2Card);
        return row;
    }

    // ── Refresh ──────────────────────────────────────────────────────
    public void refreshData() {
        LocalDate[] range = getRange();
        LocalDate from = range[0], to = range[1];

        // KPI
        double dt = service.getDoanhThu(from, to);
        long soDon = service.getSoDonHang(from, to);
        long riXuat = service.getTongRiXuatXuong(from, to);
        double tiLe = service.getTiLeThanhToan(from, to);

        lblDoanhThu.setText(formatMoney(dt));
        lblSoDon.setText(String.valueOf(soDon));
        lblRiXuat.setText(String.valueOf(riXuat) + " ri");
        lblTiLe.setText(String.format("%.0f%%", tiLe * 100));
        lblDoanhThuSub.setText("Trong " + currentRange.toLowerCase());
        lblSoDonSub.setText("Trong " + currentRange.toLowerCase());

        // LineChart
        Map<LocalDate, Double> dtNgay = service.getDoanhThuTheoNgay(from, to);
        lineChart.getData().clear();
        XYChart.Series<String, Number> lineSeries = new XYChart.Series<>();
        lineSeries.setName("Doanh thu");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
        dtNgay.forEach((d, v) -> lineSeries.getData().add(new XYChart.Data<>(d.format(fmt), v)));
        lineChart.getData().add(lineSeries);

        // Top SP BarChart (horizontal)
        List<SanPhamStat> topSP = service.getTopSanPhamBanChay(from, to, 5);
        barTopSP.getData().clear();
        XYChart.Series<Number, String> spSeries = new XYChart.Series<>();
        spSeries.setName("SL (Ri)");
        for (SanPhamStat s : topSP) {
            spSeries.getData().add(new XYChart.Data<>(s.soLuongRi, s.tenSanPham));
        }
        barTopSP.getData().add(spSeries);

        // Công suất BarChart
        List<CongSuatStat> cs = service.getCongSuatSanXuat(6);
        barCongSuat.getData().clear();
        XYChart.Series<String, Number> seriesDK = new XYChart.Series<>();
        seriesDK.setName("Dự kiến");
        XYChart.Series<String, Number> seriesTT = new XYChart.Series<>();
        seriesTT.setName("Thực tế");
        for (CongSuatStat c : cs) {
            String name = c.tenSanPham.length() > 14 ? c.tenSanPham.substring(0, 13) + "…" : c.tenSanPham;
            seriesDK.getData().add(new XYChart.Data<>(name, c.riDuKien));
            seriesTT.getData().add(new XYChart.Data<>(name, c.riThucTe));
        }
        barCongSuat.getData().addAll(seriesDK, seriesTT);

        // PieChart
        Map<String, Long> phan = service.getPhanLoaiDonHang(from, to);
        pieChart.getData().clear();
        phan.forEach((k, v) -> {
            if (v > 0) pieChart.getData().add(new PieChart.Data(k + " (" + v + ")", v));
        });

        // Table Top SP
        tableTopSP.getChildren().clear();
        tableTopSP.getChildren().add(tableHeader(new String[]{"STT", "Sản phẩm", "SL (Ri)", "Doanh thu"}));
        for (int i = 0; i < topSP.size(); i++) {
            SanPhamStat s = topSP.get(i);
            tableTopSP.getChildren().add(tableRow(i,
                String.valueOf(i + 1), s.tenSanPham,
                String.valueOf(s.soLuongRi), formatMoney(s.doanhThu)));
        }
        if (topSP.isEmpty()) tableTopSP.getChildren().add(emptyRow("Chưa có dữ liệu"));

        // Table Top KH
        List<KhachHangStat> topKH = service.getTopKhachHang(from, to, 5);
        tableTopKH.getChildren().clear();
        tableTopKH.getChildren().add(tableHeader(new String[]{"STT", "Khách hàng", "Số đơn", "Tổng chi"}));
        for (int i = 0; i < topKH.size(); i++) {
            KhachHangStat k = topKH.get(i);
            tableTopKH.getChildren().add(tableRow(i,
                String.valueOf(i + 1), k.tenKhachHang,
                String.valueOf(k.soDonHang), formatMoney(k.tongTien)));
        }
        if (topKH.isEmpty()) tableTopKH.getChildren().add(emptyRow("Chưa có dữ liệu"));

        final XYChart.Series<String, Number> finalLineSeries = lineSeries;
        final XYChart.Series<Number, String> finalSpSeries = spSeries;
        final XYChart.Series<String, Number> finalSeriesDK = seriesDK;
        final XYChart.Series<String, Number> finalSeriesTT = seriesTT;
        final List<SanPhamStat> finalTopSP = topSP;

        Platform.runLater(() -> Platform.runLater(() -> {
            addLineChartInteractivity(finalLineSeries);
            addBarTopSPInteractivity(finalSpSeries, finalTopSP);
            addBarCongSuatInteractivity(finalSeriesDK, finalSeriesTT);
            addPieChartInteractivity();
        }));
    }

    // ── Chart Interactivity ───────────────────────────────────────────

    private void addLineChartInteractivity(XYChart.Series<String, Number> series) {
        for (XYChart.Data<String, Number> data : series.getData()) {
            Node node = data.getNode();
            if (node != null) {
                bindLineNode(data, node);
            }
            data.nodeProperty().addListener((obs, old, n) -> {
                if (n != null) bindLineNode(data, n);
            });
        }
    }

    private void bindLineNode(XYChart.Data<String, Number> data, Node node) {
        String tip = data.getXValue() + "\n" + formatMoney(data.getYValue().doubleValue());
        Tooltip.install(node, createTooltip(tip));
        String baseStyle = "-fx-background-color: #1e40af, white; -fx-background-insets: 0,2; -fx-background-radius: 5em; -fx-padding: 5px;";
        String hoverStyle = "-fx-background-color: #f59e0b, white; -fx-background-insets: 0,2; -fx-background-radius: 5em; -fx-padding: 7px; -fx-effect: dropshadow(gaussian,rgba(245,158,11,0.6),8,0,0,0);";
        node.setStyle(baseStyle);
        node.setOnMouseEntered(e -> node.setStyle(hoverStyle));
        node.setOnMouseExited(e  -> node.setStyle(baseStyle));
    }

    private void addBarTopSPInteractivity(XYChart.Series<Number, String> series, List<SanPhamStat> topSP) {
        for (int i = 0; i < series.getData().size(); i++) {
            XYChart.Data<Number, String> data = series.getData().get(i);
            final int idx = i;
            final SanPhamStat stat = idx < topSP.size() ? topSP.get(idx) : null;
            if (stat == null) continue;

            Node node = data.getNode();
            if (node != null) {
                bindBarTopSPNode(node, stat, idx);
            }
            data.nodeProperty().addListener((obs, old, n) -> {
                if (n != null) bindBarTopSPNode(n, stat, idx);
            });
        }
    }

    private void bindBarTopSPNode(Node node, SanPhamStat stat, int idx) {
        String tip = "📦 " + stat.tenSanPham +
                     "\nSL: " + stat.soLuongRi + " ri" +
                     "\nDoanh thu: " + formatMoney(stat.doanhThu);
        Tooltip.install(node, createTooltip(tip));

        node.setOnMouseEntered(e -> {
            node.setStyle("-fx-bar-fill: #f59e0b; -fx-effect: dropshadow(gaussian,rgba(245,158,11,0.5),8,0,0,0);");
            highlightTableRow(tableTopSP, idx + 1);
        });
        node.setOnMouseExited(e -> {
            node.setStyle("");
            resetTableHighlight(tableTopSP);
        });
        node.setOnMouseClicked(e -> highlightTableRow(tableTopSP, idx + 1));
    }

    private void addBarCongSuatInteractivity(
            XYChart.Series<String, Number> seriesDK,
            XYChart.Series<String, Number> seriesTT) {

        String[] labels = {"Dự kiến", "Thực tế"};
        String[] hoverColors = {"#2563eb", "#059669"};

        for (int s = 0; s < 2; s++) {
            XYChart.Series<String, Number> series = s == 0 ? seriesDK : seriesTT;
            final String label = labels[s];
            final String hoverColor = hoverColors[s];

            for (XYChart.Data<String, Number> data : series.getData()) {
                Node node = data.getNode();
                if (node != null) {
                    bindBarCongSuatNode(node, data, label, hoverColor);
                }
                data.nodeProperty().addListener((obs, old, n) -> {
                    if (n != null) bindBarCongSuatNode(n, data, label, hoverColor);
                });
            }
        }
    }

    private void bindBarCongSuatNode(Node node, XYChart.Data<String, Number> data, String label, String hoverColor) {
        String tip = label + "\n" + data.getXValue() + ": " + data.getYValue().intValue() + " ri";
        Tooltip.install(node, createTooltip(tip));
        node.setOnMouseEntered(e -> node.setStyle("-fx-bar-fill: " + hoverColor +
            "; -fx-effect: dropshadow(gaussian,rgba(0,0,0,0.3),8,0,0,0);"));
        node.setOnMouseExited(e  -> node.setStyle(""));
    }

    private void addPieChartInteractivity() {
        double total = pieChart.getData().stream().mapToDouble(PieChart.Data::getPieValue).sum();
        for (PieChart.Data slice : pieChart.getData()) {
            Node node = slice.getNode();
            if (node != null) {
                bindPieSliceNode(node, slice, total);
            }
            slice.nodeProperty().addListener((obs, old, n) -> {
                if (n != null) bindPieSliceNode(n, slice, total);
            });
        }
    }

    private void bindPieSliceNode(Node node, PieChart.Data slice, double total) {
        double pct = total > 0 ? slice.getPieValue() / total * 100 : 0;
        String tip = slice.getName() + "\n" + String.format("%.1f%%", pct) +
                     "  " + (int) slice.getPieValue() + " đơn";
        Tooltip.install(node, createTooltip(tip));
        node.setOnMouseEntered(e -> node.setStyle(
            "-fx-opacity: 0.82; -fx-effect: dropshadow(gaussian,rgba(0,0,0,0.4),10,0,0,0);"));
        node.setOnMouseExited(e  -> node.setStyle("-fx-opacity: 1;"));
        node.setOnMouseClicked(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Chi tiết");
            alert.setHeaderText(null);
            alert.setContentText(tip);
            alert.showAndWait();
        });
    }

    /** Highlight một table row (theo index trong VBox, 0 = header) */
    private void highlightTableRow(VBox table, int rowIdx) {
        for (int i = 0; i < table.getChildren().size(); i++) {
            Node n = table.getChildren().get(i);
            if (i == rowIdx) {
                n.setStyle("-fx-background-color: #dbeafe; -fx-padding: 7 10;" +
                           "-fx-border-color: #93c5fd; -fx-border-width: 0 0 1 0;");
            }
        }
    }

    /** Reset table highlight về màu gốc */
    private void resetTableHighlight(VBox table) {
        for (int i = 1; i < table.getChildren().size(); i++) { // skip header
            Node n = table.getChildren().get(i);
            String bg = (i % 2 == 1) ? "#f8fafc" : "white";
            n.setStyle("-fx-background-color: " + bg + "; -fx-padding: 7 10;" +
                       "-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
        }
    }

    /** Tạo Tooltip với style đẹp */
    private Tooltip createTooltip(String text) {
        Tooltip tt = new Tooltip(text);
        tt.setStyle("-fx-background-color: #1e293b; -fx-text-fill: white;" +
                    "-fx-font-size: 12px; -fx-background-radius: 8; -fx-padding: 8 12;");
        tt.setShowDelay(javafx.util.Duration.millis(100));
        return tt;
    }

    // ── Helpers ──────────────────────────────────────────────────────
    private LocalDate[] getRange() {
        return switch (currentRange) {
            case "Tháng" -> ThongKeService.rangeOfMonth();
            case "Quý"   -> ThongKeService.rangeOfQuarter();
            case "Năm"   -> ThongKeService.rangeOfYear();
            default      -> ThongKeService.rangeOfWeek();
        };
    }

    private Label sectionTitle(String text) {
        Label lbl = new Label(text);
        lbl.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        return lbl;
    }

    private String cardStyle() {
        return "-fx-background-color: white; -fx-background-radius: 12;" +
               "-fx-border-color: #e2e8f0; -fx-border-radius: 12; -fx-border-width: 1;" +
               "-fx-padding: 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.06), 6, 0, 0, 2);";
    }

    private HBox tableHeader(String[] cols) {
        HBox row = new HBox();
        row.setStyle("-fx-background-color: #1e40af; -fx-padding: 8 10;");
        double[] pcts = {8, 46, 18, 28};
        for (int i = 0; i < cols.length; i++) {
            Label lbl = new Label(cols[i]);
            lbl.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 11px;");
            lbl.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(lbl, Priority.ALWAYS);
            lbl.setPrefWidth(pcts[i] * 5); // rough proportional sizing
            row.getChildren().add(lbl);
        }
        return row;
    }

    private HBox tableRow(int idx, String... cols) {
        HBox row = new HBox();
        String bg = idx % 2 == 0 ? "#f8fafc" : "white";
        row.setStyle("-fx-background-color: " + bg + "; -fx-padding: 7 10;" +
                     "-fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
        double[] pcts = {8, 46, 18, 28};
        String[] styles = {
            "-fx-text-fill: #64748b; -fx-font-size: 11px;",
            "-fx-text-fill: #1e293b; -fx-font-weight: bold; -fx-font-size: 11px;",
            "-fx-text-fill: #475569; -fx-font-size: 11px;",
            "-fx-text-fill: #1e40af; -fx-font-weight: bold; -fx-font-size: 11px;"
        };
        for (int i = 0; i < cols.length; i++) {
            Label lbl = new Label(cols[i]);
            lbl.setStyle(styles[i]);
            lbl.setMaxWidth(Double.MAX_VALUE);
            lbl.setPrefWidth(pcts[i] * 5);
            HBox.setHgrow(lbl, Priority.ALWAYS);
            row.getChildren().add(lbl);
        }
        return row;
    }

    private Label emptyRow(String msg) {
        Label l = new Label(msg);
        l.setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic; -fx-padding: 12 10;");
        return l;
    }

    private void exportPdfReport() {
        javafx.stage.FileChooser fileChooser = new javafx.stage.FileChooser();
        fileChooser.setTitle("Lưu báo cáo thống kê");
        fileChooser.getExtensionFilters().add(
            new javafx.stage.FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf")
        );
        fileChooser.setInitialFileName("BaoCaoThongKe_" + currentRange + "_" + LocalDate.now().toString() + ".pdf");
        
        java.io.File file = fileChooser.showSaveDialog(this.getScene().getWindow());
        if (file != null) {
            try {
                LocalDate[] range = getRange();
                LocalDate from = range[0], to = range[1];
                
                double dt = service.getDoanhThu(from, to);
                long soDon = service.getSoDonHang(from, to);
                long riXuat = service.getTongRiXuatXuong(from, to);
                double tiLe = service.getTiLeThanhToan(from, to);
                
                List<SanPhamStat> topSP = service.getTopSanPhamBanChay(from, to, 5);
                List<KhachHangStat> topKH = service.getTopKhachHang(from, to, 5);
                Map<String, Long> orderStatus = service.getPhanLoaiDonHang(from, to);
                
                com.xuongmay.util.ReportPdfExporter.exportReport(
                    currentRange, from, to,
                    dt, soDon, riXuat, tiLe,
                    topSP, topKH, orderStatus, file
                );
                
                NotificationUtils.show("Xuất báo cáo", "Đã xuất báo cáo PDF thành công!", NotificationType.SUCCESS);
                com.xuongmay.util.ReportPdfExporter.openPdf(file);
            } catch (Exception ex) {
                ex.printStackTrace();
                NotificationUtils.show("Lỗi", "Không thể xuất báo cáo: " + ex.getMessage(), NotificationType.ERROR);
            }
        }
    }

    private String formatMoney(double amount) {
        if (amount >= 1_000_000_000) return String.format("%.1fB đ", amount / 1_000_000_000.0);
        if (amount >= 1_000_000)     return String.format("%.1fM đ", amount / 1_000_000.0);
        if (amount >= 1_000)         return String.format("%.0fK đ", amount / 1_000.0);
        return String.format("%,.0f đ", amount);
    }
}
