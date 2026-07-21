package com.xuongmay.ui;

import javafx.animation.FadeTransition;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class LoginFrame {
    private final Stage stage;
    private static final Map<String, String> registeredUsers = new HashMap<>();
    private static final Map<String, String> userFullNames = new HashMap<>();

    static {
        // Initial mock users
        registeredUsers.put("admin", "123");
        userFullNames.put("admin", "Quản trị viên");
        
        registeredUsers.put("minh", "123");
        userFullNames.put("minh", "Minh");
    }

    // Container for forms to switch with transitions
    private StackPane formContainer;
    private VBox loginForm;
    private VBox registerForm;

    // Login fields
    private TextField txtLoginUser;
    private PasswordField txtLoginPass;
    private Label lblLoginError;

    // Register fields
    private TextField txtRegFullName;
    private TextField txtRegUser;
    private PasswordField txtRegPass;
    private PasswordField txtRegConfirmPass;
    private Label lblRegError;
    private Label lblRegSuccess;
    private HBox strengthBarBox;
    private Label lblStrengthText;

    public LoginFrame(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        // Split view container
        HBox container = new HBox();
        container.getStyleClass().add("login-container");
        container.setPrefSize(900, 550);
        
        // 1. LEFT SIDE - BRAND BANNER
        VBox leftBanner = new VBox(25);
        leftBanner.getStyleClass().add("login-left-banner");
        leftBanner.setPrefWidth(400);
        leftBanner.setAlignment(Pos.CENTER_LEFT);

        // Logo
        StackPane logoBox = new StackPane();
        logoBox.getStyleClass().add("login-logo-box");
        Label logoText = new Label("XM");
        logoText.getStyleClass().add("login-logo-text");
        logoBox.getChildren().add(logoText);

        VBox titleBox = new VBox(8);
        Label bannerTitle = new Label("HỆ THỐNG QUẢN LÝ\nXƯỞNG MAY");
        bannerTitle.getStyleClass().add("login-banner-title");
        Label bannerDesc = new Label("Giải pháp số hóa toàn diện quy trình nhập nguyên liệu, theo dõi tiến độ sản xuất đơn hàng và tối ưu hiệu suất nhân sự.");
        bannerDesc.getStyleClass().add("login-banner-desc");
        bannerDesc.setWrapText(true);
        titleBox.getChildren().addAll(bannerTitle, bannerDesc);

        // Feature list
        VBox features = new VBox(12);
        features.getChildren().addAll(
            createFeatureItem("✦  Đồng bộ hóa quy trình sản xuất"),
            createFeatureItem("✦  Theo dõi trạng thái lô vải, sản phẩm"),
            createFeatureItem("✦  Quản lý đơn hàng & doanh thu tức thời")
        );

        leftBanner.getChildren().addAll(logoBox, titleBox, features);

        // 2. RIGHT SIDE - FORM CONTAINER
        formContainer = new StackPane();
        formContainer.getStyleClass().add("login-right-form");
        HBox.setHgrow(formContainer, Priority.ALWAYS);

        // Init forms
        initLoginForm();
        initRegisterForm();

        // Show login by default
        formContainer.getChildren().addAll(registerForm, loginForm);
        registerForm.setVisible(false);

        container.getChildren().addAll(leftBanner, formContainer);

        Scene scene = new Scene(container, 900, 550);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        
        stage.setScene(scene);
        stage.setTitle("Đăng nhập - Hệ thống quản lý xưởng may");
        stage.setResizable(false);
        stage.centerOnScreen();
    }

    private HBox createFeatureItem(String text) {
        HBox row = new HBox(8);
        Label lbl = new Label(text);
        lbl.setStyle("-fx-text-fill: #cbd5e1; -fx-font-size: 12px;");
        row.getChildren().add(lbl);
        return row;
    }

    // ================== LOGIN FORM ==================
    private void initLoginForm() {
        loginForm = new VBox(18);
        loginForm.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(4);
        Label title = new Label("Đăng nhập");
        title.getStyleClass().add("login-form-title");
        Label subtitle = new Label("Chào mừng quay trở lại! Vui lòng điền thông tin.");
        subtitle.getStyleClass().add("login-form-subtitle");
        header.getChildren().addAll(title, subtitle);

        VBox userField = new VBox(6);
        Label lblUser = new Label("Tên đăng nhập");
        lblUser.getStyleClass().add("login-label");
        txtLoginUser = new TextField();
        txtLoginUser.setPromptText("Nhập tên đăng nhập...");
        txtLoginUser.getStyleClass().add("login-input");
        userField.getChildren().addAll(lblUser, txtLoginUser);

        VBox passField = new VBox(6);
        Label lblPass = new Label("Mật khẩu");
        lblPass.getStyleClass().add("login-label");
        txtLoginPass = new PasswordField();
        txtLoginPass.setPromptText("Nhập mật khẩu...");
        txtLoginPass.getStyleClass().add("login-input");
        passField.getChildren().addAll(lblPass, txtLoginPass);

        lblLoginError = new Label();
        lblLoginError.getStyleClass().add("login-error-lbl");
        lblLoginError.setManaged(false);

        Button btnLogin = new Button("Đăng nhập");
        btnLogin.getStyleClass().add("login-btn");
        btnLogin.setMaxWidth(Double.MAX_VALUE);
        btnLogin.setOnAction(e -> handleLogin());

        // Keyboard press Enter
        txtLoginUser.setOnAction(e -> txtLoginPass.requestFocus());
        txtLoginPass.setOnAction(e -> handleLogin());

        HBox footer = new HBox(6);
        footer.setAlignment(Pos.CENTER);
        Label lblNoAcc = new Label("Chưa có tài khoản?");
        lblNoAcc.setStyle("-fx-text-fill: #6b7280;");
        Button btnToReg = new Button("Đăng ký ngay");
        btnToReg.getStyleClass().add("login-link-btn");
        btnToReg.setOnAction(e -> switchToForm(registerForm));
        footer.getChildren().addAll(lblNoAcc, btnToReg);

        loginForm.getChildren().addAll(header, userField, passField, lblLoginError, btnLogin, footer);
    }

    // ================== REGISTER FORM ==================
    private void initRegisterForm() {
        registerForm = new VBox(14);
        registerForm.setAlignment(Pos.CENTER_LEFT);

        VBox header = new VBox(4);
        Label title = new Label("Đăng ký");
        title.getStyleClass().add("login-form-title");
        Label subtitle = new Label("Tạo tài khoản mới cho chủ xưởng may.");
        subtitle.getStyleClass().add("login-form-subtitle");
        header.getChildren().addAll(title, subtitle);

        VBox nameField = new VBox(4);
        Label lblName = new Label("Họ và tên");
        lblName.getStyleClass().add("login-label");
        txtRegFullName = new TextField();
        txtRegFullName.setPromptText("Nhập họ và tên...");
        txtRegFullName.getStyleClass().add("login-input");
        nameField.getChildren().addAll(lblName, txtRegFullName);

        VBox userField = new VBox(4);
        Label lblUser = new Label("Tên đăng nhập");
        lblUser.getStyleClass().add("login-label");
        txtRegUser = new TextField();
        txtRegUser.setPromptText("Nhập tên đăng nhập mới...");
        txtRegUser.getStyleClass().add("login-input");
        userField.getChildren().addAll(lblUser, txtRegUser);

        VBox passField = new VBox(4);
        Label lblPass = new Label("Mật khẩu");
        lblPass.getStyleClass().add("login-label");
        txtRegPass = new PasswordField();
        txtRegPass.setPromptText("Nhập mật khẩu...");
        txtRegPass.getStyleClass().add("login-input");

        // Password strength bar setup
        strengthBarBox = new HBox();
        strengthBarBox.getStyleClass().add("strength-bar-box");
        for (int i = 0; i < 4; i++) {
            Pane segment = new Pane();
            segment.getStyleClass().add("strength-segment");
            strengthBarBox.getChildren().add(segment);
        }

        lblStrengthText = new Label("Độ bảo mật: Chưa nhập");
        lblStrengthText.getStyleClass().add("strength-lbl");

        HBox strengthContainer = new HBox();
        strengthContainer.getStyleClass().add("strength-container");
        strengthContainer.getChildren().addAll(strengthBarBox, lblStrengthText);

        txtRegPass.textProperty().addListener((ob, oldVal, newVal) -> updatePasswordStrength(newVal));

        passField.getChildren().addAll(lblPass, txtRegPass, strengthContainer);

        VBox confirmField = new VBox(4);
        Label lblConfirm = new Label("Xác nhận mật khẩu");
        lblConfirm.getStyleClass().add("login-label");
        txtRegConfirmPass = new PasswordField();
        txtRegConfirmPass.setPromptText("Xác nhận lại mật khẩu...");
        txtRegConfirmPass.getStyleClass().add("login-input");
        confirmField.getChildren().addAll(lblConfirm, txtRegConfirmPass);

        lblRegError = new Label();
        lblRegError.getStyleClass().add("login-error-lbl");
        lblRegError.setManaged(false);

        lblRegSuccess = new Label();
        lblRegSuccess.getStyleClass().add("login-success-lbl");
        lblRegSuccess.setManaged(false);
        lblRegSuccess.setVisible(false);

        Button btnRegister = new Button("Đăng ký tài khoản");
        btnRegister.getStyleClass().add("login-btn");
        btnRegister.setMaxWidth(Double.MAX_VALUE);
        btnRegister.setOnAction(e -> handleRegister());

        HBox footer = new HBox(6);
        footer.setAlignment(Pos.CENTER);
        Label lblHasAcc = new Label("Đã có tài khoản?");
        lblHasAcc.setStyle("-fx-text-fill: #6b7280;");
        Button btnToLogin = new Button("Đăng nhập");
        btnToLogin.getStyleClass().add("login-link-btn");
        btnToLogin.setOnAction(e -> switchToForm(loginForm));
        footer.getChildren().addAll(lblHasAcc, btnToLogin);

        registerForm.getChildren().addAll(header, nameField, userField, passField, confirmField, lblRegError, lblRegSuccess, btnRegister, footer);
    }

    // ================== ACTION HANDLERS ==================
    private void handleLogin() {
        String username = txtLoginUser.getText().trim().toLowerCase();
        String password = txtLoginPass.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showLoginError("Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu.");
            return;
        }

        if (!registeredUsers.containsKey(username)) {
            showLoginError("Tên đăng nhập không tồn tại.");
            return;
        }

        String storedPass = registeredUsers.get(username);
        if (!storedPass.equals(password)) {
            showLoginError("Mật khẩu không chính xác.");
            return;
        }

        // Login successful!
        lblLoginError.setVisible(false);
        lblLoginError.setManaged(false);
        
        String fullName = userFullNames.get(username);
        
        // Open MainFrame on same stage
        stage.setResizable(true);
        MainFrame mainFrame = new MainFrame(stage, fullName);
        stage.show();
    }

    private void handleRegister() {
        String fullName = txtRegFullName.getText().trim();
        String username = txtRegUser.getText().trim().toLowerCase();
        String password = txtRegPass.getText();
        String confirm = txtRegConfirmPass.getText();

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showRegError("Vui lòng điền đầy đủ tất cả các trường.");
            return;
        }

        if (username.length() < 3) {
            showRegError("Tên đăng nhập phải chứa ít nhất 3 ký tự.");
            return;
        }

        if (password.length() < 3) {
            showRegError("Mật khẩu phải chứa ít nhất 3 ký tự.");
            return;
        }

        if (!password.equals(confirm)) {
            showRegError("Mật khẩu xác nhận không khớp.");
            return;
        }

        if (registeredUsers.containsKey(username)) {
            showRegError("Tên đăng nhập đã được sử dụng.");
            return;
        }

        // Mock registration success
        registeredUsers.put(username, password);
        userFullNames.put(username, fullName);

        lblRegError.setVisible(false);
        lblRegError.setManaged(false);

        lblRegSuccess.setText("Đăng ký thành công! Đang chuyển sang màn hình đăng nhập...");
        lblRegSuccess.setVisible(true);
        lblRegSuccess.setManaged(true);

        // Transition back to login page after 1.5 seconds
        Timeline jumpTimeline = new Timeline(new javafx.animation.KeyFrame(Duration.seconds(1.5), ev -> {
            txtLoginUser.setText(username);
            txtLoginPass.setText(password);
            lblRegSuccess.setVisible(false);
            lblRegSuccess.setManaged(false);
            
            // Clear inputs
            txtRegFullName.clear();
            txtRegUser.clear();
            txtRegPass.clear();
            txtRegConfirmPass.clear();
            
            switchToForm(loginForm);
        }));
        jumpTimeline.play();
    }

    private void showLoginError(String msg) {
        lblLoginError.setText("⚠️ " + msg);
        lblLoginError.setVisible(true);
        lblLoginError.setManaged(true);
    }

    private void showRegError(String msg) {
        lblRegError.setText("⚠️ " + msg);
        lblRegError.setVisible(true);
        lblRegError.setManaged(true);
    }

    private void switchToForm(VBox targetForm) {
        VBox currentForm = (targetForm == loginForm) ? registerForm : loginForm;

        // Simple smooth transition
        FadeTransition fadeOut = new FadeTransition(Duration.millis(150), currentForm);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        
        fadeOut.setOnFinished(e -> {
            currentForm.setVisible(false);
            targetForm.setVisible(true);
            targetForm.setOpacity(0.0);
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(150), targetForm);
            fadeIn.setFromValue(0.0);
            fadeIn.setToValue(1.0);
            fadeIn.play();
        });
        
        fadeOut.play();
    }

    private void updatePasswordStrength(String password) {
        int score = checkPasswordStrength(password);
        
        // Reset classes for all segments
        for (int i = 0; i < strengthBarBox.getChildren().size(); i++) {
            javafx.scene.Node node = strengthBarBox.getChildren().get(i);
            node.getStyleClass().setAll("strength-segment");
        }
        
        lblStrengthText.getStyleClass().setAll("strength-lbl");
        
        if (password == null || password.isEmpty()) {
            lblStrengthText.setText("Độ bảo mật: Chưa nhập");
            return;
        }
        
        switch (score) {
            case 1:
                lblStrengthText.setText("Độ bảo mật: Yếu");
                lblStrengthText.getStyleClass().add("strength-lbl-weak");
                strengthBarBox.getChildren().get(0).getStyleClass().add("strength-segment-weak");
                break;
            case 2:
                lblStrengthText.setText("Độ bảo mật: Trung bình");
                lblStrengthText.getStyleClass().add("strength-lbl-medium");
                strengthBarBox.getChildren().get(0).getStyleClass().add("strength-segment-medium");
                strengthBarBox.getChildren().get(1).getStyleClass().add("strength-segment-medium");
                break;
            case 3:
                lblStrengthText.setText("Độ bảo mật: Mạnh");
                lblStrengthText.getStyleClass().add("strength-lbl-strong");
                strengthBarBox.getChildren().get(0).getStyleClass().add("strength-segment-strong");
                strengthBarBox.getChildren().get(1).getStyleClass().add("strength-segment-strong");
                strengthBarBox.getChildren().get(2).getStyleClass().add("strength-segment-strong");
                break;
            case 4:
                lblStrengthText.setText("Độ bảo mật: Rất mạnh");
                lblStrengthText.getStyleClass().add("strength-lbl-very-strong");
                strengthBarBox.getChildren().get(0).getStyleClass().add("strength-segment-very-strong");
                strengthBarBox.getChildren().get(1).getStyleClass().add("strength-segment-very-strong");
                strengthBarBox.getChildren().get(2).getStyleClass().add("strength-segment-very-strong");
                strengthBarBox.getChildren().get(3).getStyleClass().add("strength-segment-very-strong");
                break;
        }
    }

    private int checkPasswordStrength(String password) {
        if (password == null || password.isEmpty()) {
            return 0;
        }
        if (password.length() < 6) {
            return 1;
        }
        
        int score = 1;
        
        boolean hasUppercase = !password.equals(password.toLowerCase());
        boolean hasLowercase = !password.equals(password.toUpperCase());
        boolean hasDigit = password.matches(".*\\d.*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\",./<>?].*");
        
        if (password.length() >= 8) {
            score++;
        }
        
        int categories = 0;
        if (hasUppercase) categories++;
        if (hasLowercase) categories++;
        if (hasDigit) categories++;
        if (hasSpecial) categories++;
        
        if (categories >= 3) {
            score++;
        }
        if (categories == 4 && password.length() >= 10) {
            score++;
        }
        
        return Math.min(score, 4);
    }
}
