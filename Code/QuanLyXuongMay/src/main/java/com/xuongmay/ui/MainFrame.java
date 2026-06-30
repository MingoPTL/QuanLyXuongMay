package com.xuongmay.ui;

import com.xuongmay.ui.panel.*;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.Interpolator;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainFrame {
    private final BorderPane root;
    private final StackPane contentArea;

    // Panels
    private TongQuanPanel tongQuanPanel;
    private NguyenLieuPanel nguyenLieuPanel;
    private SanPhamPanel sanPhamPanel;
    private NhanSuPanel nhanSuPanel;
    private HeThongPanel heThongPanel;
    private KhachHangPanel khachHangPanel;
    private HoaDonPanel hoaDonPanel;

    // Sidebar buttons
    private Button btnTongQuan;
    private Button btnNguyenLieu;
    private Button btnSanPham;
    private Button btnNhanSu;
    private Button btnHeThong;
    private Button btnKhachHang;
    private Button btnHoaDon;
    private Button btnLogout;
    private boolean isSidebarCollapsed = false;
    private final String userName;

    public MainFrame(Stage stage, String userName) {
        this.userName = userName;
        root = new BorderPane();

        // 1. Sidebar (Left)
        VBox sidebar = new VBox(6);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPrefWidth(240);

        // Brand Logo container
        VBox brandContainer = new VBox(0);
        brandContainer.getStyleClass().add("brand-container");

        HBox brandInner = new HBox(12);
        brandInner.setAlignment(Pos.CENTER_LEFT);

        // Icon: rounded square with monogram "XM"
        StackPane brandIcon = new StackPane();
        brandIcon.getStyleClass().add("brand-icon");
        Label iconLabel = new Label("XM");
        iconLabel.getStyleClass().add("brand-icon-text");
        brandIcon.getChildren().add(iconLabel);

        // Text block: name + tagline
        VBox brandTextBlock = new VBox(2);
        brandTextBlock.setAlignment(Pos.CENTER_LEFT);
        Label brandName = new Label("Xưởng May");
        brandName.getStyleClass().add("brand-text");
        Label brandTagline = new Label("Quản lý sản xuất");
        brandTagline.getStyleClass().add("brand-tagline");
        brandTextBlock.getChildren().addAll(brandName, brandTagline);

        brandInner.getChildren().addAll(brandIcon, brandTextBlock);

        // Separator line below logo
        javafx.scene.control.Separator brandSep = new javafx.scene.control.Separator();
        brandSep.getStyleClass().add("brand-separator");

        brandContainer.getChildren().addAll(brandInner, brandSep);

        Label lblSection = new Label("Quản lý");
        lblSection.getStyleClass().add("sidebar-section-title");

        btnTongQuan = new Button("📊  Tổng quan");
        btnTongQuan.getStyleClass().add("sidebar-button");

        btnSanPham = new Button("📋  Đơn hàng");
        btnSanPham.getStyleClass().add("sidebar-button");

        btnNguyenLieu = new Button("📦  Kho vải");
        btnNguyenLieu.getStyleClass().add("sidebar-button");

        btnNhanSu = new Button("👥  Nhân sự");
        btnNhanSu.getStyleClass().add("sidebar-button");

        btnKhachHang = new Button("🤝  Khách hàng");
        btnKhachHang.getStyleClass().add("sidebar-button");

        btnHoaDon = new Button("🧾  Hóa đơn");
        btnHoaDon.getStyleClass().add("sidebar-button");

        btnHeThong = new Button("⚙️  Cài đặt");
        btnHeThong.getStyleClass().add("sidebar-button");

        // Profile & Logout at bottom
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        VBox profileBox = new VBox(4);
        profileBox.getStyleClass().add("profile-box");
        Label profileName = new Label(userName);
        profileName.getStyleClass().add("profile-name");
        Label profileRole = new Label("Nhân viên");
        profileRole.getStyleClass().add("profile-role");
        profileBox.getChildren().addAll(profileName, profileRole);

        btnLogout = new Button("🚪  Đăng xuất");
        btnLogout.getStyleClass().add("btn-logout");
        btnLogout.setOnAction(e -> {
            LoginFrame loginFrame = new LoginFrame(stage);
            loginFrame.show();
        });

        sidebar.getChildren().addAll(
            brandContainer, lblSection, 
            btnTongQuan, btnSanPham, btnNguyenLieu, btnNhanSu, btnKhachHang, btnHoaDon, btnHeThong,
            spacer, profileBox, btnLogout
        );
        root.setLeft(sidebar);

        // 2. Top Header Bar
        HBox headerBar = new HBox(14);
        headerBar.getStyleClass().add("top-header-bar");
        HBox.setHgrow(headerBar, Priority.ALWAYS);

        // Collapse Sidebar button
        Button btnToggleSidebar = new Button("☰");
        btnToggleSidebar.getStyleClass().add("sidebar-toggle-btn");
        btnToggleSidebar.setOnAction(e -> toggleSidebar(sidebar, brandTextBlock, lblSection, profileBox));

        // Left: Dynamic greeting
        Label lblGreeting = new Label();
        lblGreeting.getStyleClass().add("header-greeting");

        // Right: Live clock
        Region headerSpacer = new Region();
        HBox.setHgrow(headerSpacer, Priority.ALWAYS);

        Label lblDate = new Label();
        lblDate.getStyleClass().add("header-date");

        Label lblTime = new Label();
        lblTime.getStyleClass().add("header-time");

        Label lblSeparator = new Label("|");
        lblSeparator.getStyleClass().add("header-sep");

        HBox clockBox = new HBox(10, lblDate, lblSeparator, lblTime);
        clockBox.setAlignment(Pos.CENTER_RIGHT);
        clockBox.getStyleClass().add("header-clock-box");

        headerBar.getChildren().addAll(btnToggleSidebar, lblGreeting, headerSpacer, clockBox);
        root.setTop(headerBar);

        // Timeline: update clock + greeting every second
        DateTimeFormatter dateFmt = DateTimeFormatter.ofPattern("EEEE, dd/MM/yyyy");
        DateTimeFormatter timeFmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timeline clock = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            LocalDateTime now = LocalDateTime.now();
            lblDate.setText(now.format(dateFmt));
            lblTime.setText(now.format(timeFmt));
            int hour = now.getHour();
            String greet;
            if (hour >= 5 && hour < 11)      greet = "☀️  Chào buổi sáng, " + userName + "!";
            else if (hour >= 11 && hour < 13) greet = "🌤️  Chào buổi trưa, " + userName + "!";
            else if (hour >= 13 && hour < 18) greet = "🌅  Chào buổi chiều, " + userName + "!";
            else                              greet = "🌙  Chào buổi tối, " + userName + "!";
            lblGreeting.setText(greet);
        }));
        clock.setCycleCount(Timeline.INDEFINITE);
        clock.play();
        // Set initial value immediately
        LocalDateTime now = LocalDateTime.now();
        lblDate.setText(now.format(dateFmt));
        lblTime.setText(now.format(timeFmt));
        int initHour = now.getHour();
        String initGreet;
        if (initHour >= 5 && initHour < 11)       initGreet = "☀️  Chào buổi sáng, " + userName + "!";
        else if (initHour >= 11 && initHour < 13)  initGreet = "🌤️  Chào buổi trưa, " + userName + "!";
        else if (initHour >= 13 && initHour < 18)  initGreet = "🌅  Chào buổi chiều, " + userName + "!";
        else                                        initGreet = "🌙  Chào buổi tối, " + userName + "!";
        lblGreeting.setText(initGreet);

        // 3. Content Area (Center)
        contentArea = new StackPane();
        contentArea.getStyleClass().add("content-area");
        root.setCenter(contentArea);

        // Initialize Panels
        initPanels();

        // Bind Sidebar Button Clicks
        btnTongQuan.setOnAction(e -> {
            tongQuanPanel.refreshData();
            showPanel(tongQuanPanel, btnTongQuan);
        });
        btnNguyenLieu.setOnAction(e -> showPanel(nguyenLieuPanel, btnNguyenLieu));
        btnSanPham.setOnAction(e -> showPanel(sanPhamPanel, btnSanPham));
        btnNhanSu.setOnAction(e -> showPanel(nhanSuPanel, btnNhanSu));
        btnHeThong.setOnAction(e -> showPanel(heThongPanel, btnHeThong));
        btnKhachHang.setOnAction(e -> {
            khachHangPanel.refreshData();
            showPanel(khachHangPanel, btnKhachHang);
        });
        btnHoaDon.setOnAction(e -> {
            hoaDonPanel.refreshData();
            showPanel(hoaDonPanel, btnHoaDon);
        });

        // Default panel to show
        showPanel(tongQuanPanel, btnTongQuan);

        // 3. Scene and Stage Setup
        Scene scene = new Scene(root, 1280, 800);
        // Load external CSS stylesheet
        String cssPath = getClass().getResource("/style.css") != null 
                ? getClass().getResource("/style.css").toExternalForm() 
                : null;
        if (cssPath != null) {
            scene.getStylesheets().add(cssPath);
        } else {
            System.err.println("Warning: style.css not found in classpath!");
        }

        stage.setTitle("Hệ Thống Quản Lý Xưởng May - Antigravity Dashboard");
        stage.setScene(scene);
        stage.setMinWidth(1100);
        stage.setMinHeight(700);
        stage.setMaximized(true);
    }

    private void initPanels() {
        tongQuanPanel = new TongQuanPanel();
        nguyenLieuPanel = new NguyenLieuPanel();
        sanPhamPanel = new SanPhamPanel();
        nhanSuPanel = new NhanSuPanel();
        heThongPanel = new HeThongPanel();
        khachHangPanel = new KhachHangPanel();
        hoaDonPanel = new HoaDonPanel();

        contentArea.getChildren().addAll(
            tongQuanPanel, nguyenLieuPanel, sanPhamPanel, nhanSuPanel, heThongPanel, khachHangPanel, hoaDonPanel
        );
    }

    private void showPanel(Pane panel, Button activeButton) {
        // Hide all panels
        tongQuanPanel.setVisible(false);
        nguyenLieuPanel.setVisible(false);
        sanPhamPanel.setVisible(false);
        nhanSuPanel.setVisible(false);
        heThongPanel.setVisible(false);
        khachHangPanel.setVisible(false);
        hoaDonPanel.setVisible(false);

        // Show active panel
        panel.setVisible(true);

        // Reset sidebar button styles
        btnTongQuan.getStyleClass().remove("sidebar-button-active");
        btnNguyenLieu.getStyleClass().remove("sidebar-button-active");
        btnSanPham.getStyleClass().remove("sidebar-button-active");
        btnNhanSu.getStyleClass().remove("sidebar-button-active");
        btnHeThong.getStyleClass().remove("sidebar-button-active");
        btnKhachHang.getStyleClass().remove("sidebar-button-active");
        btnHoaDon.getStyleClass().remove("sidebar-button-active");

        // Set active button style
        activeButton.getStyleClass().add("sidebar-button-active");
    }

    private void toggleSidebar(VBox sidebar, VBox brandTextBlock, Label lblSection, VBox profileBox) {
        isSidebarCollapsed = !isSidebarCollapsed;
        
        double targetWidth = isSidebarCollapsed ? 74 : 240;
        
        // Setup transition animation for sidebar width
        KeyValue widthVal = new KeyValue(sidebar.prefWidthProperty(), targetWidth, Interpolator.EASE_BOTH);
        KeyFrame frame = new KeyFrame(Duration.millis(200), widthVal);
        Timeline timeline = new Timeline(frame);
        
        if (isSidebarCollapsed) {
            // Collapse: hide text labels immediately to avoid wrapping during width shrink
            btnTongQuan.setText("📊");
            btnSanPham.setText("📋");
            btnNguyenLieu.setText("📦");
            btnNhanSu.setText("👥");
            btnKhachHang.setText("🤝");
            btnHoaDon.setText("🧾");
            btnHeThong.setText("⚙️");
            btnLogout.setText("🚪");
            
            btnTongQuan.setAlignment(Pos.CENTER);
            btnSanPham.setAlignment(Pos.CENTER);
            btnNguyenLieu.setAlignment(Pos.CENTER);
            btnNhanSu.setAlignment(Pos.CENTER);
            btnKhachHang.setAlignment(Pos.CENTER);
            btnHoaDon.setAlignment(Pos.CENTER);
            btnHeThong.setAlignment(Pos.CENTER);
            btnLogout.setAlignment(Pos.CENTER);
            
            brandTextBlock.setVisible(false);
            brandTextBlock.setManaged(false);
            lblSection.setVisible(false);
            lblSection.setManaged(false);
            
            // Collapsed profile view (Avatar-like representation)
            profileBox.getChildren().clear();
            Label avatar = new Label("M");
            avatar.getStyleClass().add("profile-avatar-collapsed");
            profileBox.getChildren().add(avatar);
            profileBox.setAlignment(Pos.CENTER);
        } else {
            // Expand: display structural labels
            brandTextBlock.setVisible(true);
            brandTextBlock.setManaged(true);
            lblSection.setVisible(true);
            lblSection.setManaged(true);
            
            // Restore profile detail
            profileBox.getChildren().clear();
            Label profileName = new Label(userName);
            profileName.getStyleClass().add("profile-name");
            Label profileRole = new Label("Nhân viên");
            profileRole.getStyleClass().add("profile-role");
            profileBox.getChildren().addAll(profileName, profileRole);
            profileBox.setAlignment(Pos.CENTER_LEFT);
            
            // Restore button labels once animation finishes to keep layout clean
            timeline.setOnFinished(ev -> {
                btnTongQuan.setText("📊  Tổng quan");
                btnSanPham.setText("📋  Đơn hàng");
                btnNguyenLieu.setText("📦  Kho vải");
                btnNhanSu.setText("👥  Nhân sự");
                btnKhachHang.setText("🤝  Khách hàng");
                btnHoaDon.setText("🧾  Hóa đơn");
                btnHeThong.setText("⚙️  Cài đặt");
                btnLogout.setText("🚪  Đăng xuất");
                
                btnTongQuan.setAlignment(Pos.CENTER_LEFT);
                btnSanPham.setAlignment(Pos.CENTER_LEFT);
                btnNguyenLieu.setAlignment(Pos.CENTER_LEFT);
                btnNhanSu.setAlignment(Pos.CENTER_LEFT);
                btnKhachHang.setAlignment(Pos.CENTER_LEFT);
                btnHoaDon.setAlignment(Pos.CENTER_LEFT);
                btnHeThong.setAlignment(Pos.CENTER_LEFT);
                btnLogout.setAlignment(Pos.CENTER_LEFT);
            });
        }
        
        timeline.play();
    }
}
