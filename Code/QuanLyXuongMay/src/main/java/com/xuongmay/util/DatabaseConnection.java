package com.xuongmay.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * Singleton JDBC connection manager.
 * Đọc cấu hình từ src/main/resources/db.properties.
 */
public class DatabaseConnection {

    private static Connection connection;
    private static String url;
    private static String user;
    private static String password;

    static {
        try (InputStream is = DatabaseConnection.class
                .getClassLoader().getResourceAsStream("db.properties")) {
            Properties props = new Properties();
            props.load(is);
            // Load driver explicitly
            Class.forName(props.getProperty("db.driver"));
            url      = props.getProperty("db.url");
            user     = props.getProperty("db.user");
            password = props.getProperty("db.password");
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khởi tạo kết nối DB: " + e.getMessage(), e);
        }
    }

    /**
     * Trả về Connection dùng chung cho toàn ứng dụng.
     * Tự kết nối lại nếu connection bị đóng hoặc null.
     */
    public static Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connection = DriverManager.getConnection(url, user, password);
            }
        } catch (Exception e) {
            throw new RuntimeException("Không kết nối được SQL Server: " + e.getMessage(), e);
        }
        return connection;
    }

    /** Đóng connection khi tắt ứng dụng. */
    public static void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (Exception ignored) {}
    }
}
