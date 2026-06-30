package com.xuongmay;

import com.xuongmay.ui.LoginFrame;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * JavaFX App
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        LoginFrame loginFrame = new LoginFrame(stage);
        loginFrame.show();
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}