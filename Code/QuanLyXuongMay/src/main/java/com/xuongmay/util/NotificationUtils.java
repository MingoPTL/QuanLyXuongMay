package com.xuongmay.util;

import javafx.animation.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.util.Duration;

public class NotificationUtils {

    private static VBox toastContainer;

    public enum NotificationType {
        SUCCESS("✅", "#dcfce7", "#16a34a", "#14532d"),
        INFO("ℹ️", "#dbeafe", "#1e40af", "#1e3a8a"),
        WARNING("⚠️", "#fef9c3", "#ca8a04", "#713f12"),
        ERROR("❌", "#fee2e2", "#dc2626", "#7f1d1d");

        public final String icon;
        public final String bgHex;
        public final String borderHex;
        public final String textHex;

        NotificationType(String icon, String bgHex, String borderHex, String textHex) {
            this.icon = icon;
            this.bgHex = bgHex;
            this.borderHex = borderHex;
            this.textHex = textHex;
        }
    }

    public static void init(VBox container) {
        toastContainer = container;
    }

    public static void show(String title, String message, NotificationType type) {
        if (toastContainer == null) {
            System.out.println("Notification: " + title + " - " + message);
            return;
        }

        // Notification box
        HBox toast = new HBox(12);
        toast.setAlignment(Pos.CENTER_LEFT);
        toast.setMaxWidth(350);
        toast.setStyle(String.format(
            "-fx-background-color: %s; -fx-border-color: %s; -fx-border-width: 1.5; -fx-border-radius: 10; -fx-background-radius: 10;" +
            "-fx-padding: 12 16; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.12), 10, 0, 0, 4);",
            type.bgHex, type.borderHex
        ));

        // Icon
        Label iconLabel = new Label(type.icon);
        iconLabel.setStyle("-fx-font-size: 18px;");

        // Text
        VBox textFlow = new VBox(2);
        Label titleLabel = new Label(title);
        titleLabel.setStyle(String.format("-fx-font-weight: bold; -fx-font-size: 13px; -fx-text-fill: %s;", type.textHex));
        Label msgLabel = new Label(message);
        msgLabel.setWrapText(true);
        msgLabel.setStyle(String.format("-fx-font-size: 11.5px; -fx-text-fill: %s;", type.textHex));
        textFlow.getChildren().addAll(titleLabel, msgLabel);
        HBox.setHgrow(textFlow, Priority.ALWAYS);

        // Close button
        Label closeBtn = new Label("✕");
        closeBtn.setStyle(String.format("-fx-text-fill: %s; -fx-cursor: hand; -fx-font-weight: bold; -fx-font-size: 12px; -fx-padding: 0 0 0 6;", type.textHex));

        toast.getChildren().addAll(iconLabel, textFlow, closeBtn);

        // Slide/Fade-in animation
        toast.setOpacity(0.0);
        toast.setTranslateY(-30);
        toastContainer.getChildren().add(0, toast); // Add to top

        FadeTransition fadeIn = new FadeTransition(Duration.millis(250), toast);
        fadeIn.setToValue(1.0);

        TranslateTransition slideIn = new TranslateTransition(Duration.millis(250), toast);
        slideIn.setToY(0);

        ParallelTransition showAnim = new ParallelTransition(fadeIn, slideIn);
        showAnim.play();

        // Dismiss action
        Runnable dismiss = () -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(250), toast);
            fadeOut.setToValue(0.0);
            TranslateTransition slideOut = new TranslateTransition(Duration.millis(250), toast);
            slideOut.setToY(-30);
            ParallelTransition hideAnim = new ParallelTransition(fadeOut, slideOut);
            hideAnim.setOnFinished(e -> toastContainer.getChildren().remove(toast));
            hideAnim.play();
        };

        closeBtn.setOnMouseClicked(e -> dismiss.run());

        // Auto hide timeline
        Timeline autoHide = new Timeline(new KeyFrame(Duration.seconds(3.5), e -> dismiss.run()));
        autoHide.play();
    }
}
