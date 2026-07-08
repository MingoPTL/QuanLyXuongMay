package com.xuongmay.ui.dialog;

import com.xuongmay.model.CayVai;
import com.xuongmay.model.LoVai;
import com.xuongmay.service.NguyenLieuService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Dialog for picking CayVai (fabric rolls) to assign to a LoVai (fabric lot).
 * Shows all available CayVai with checkboxes. Pre-selects ones already assigned.
 * Returns the list of selected CayVai on confirmation.
 */
public class CayVaiPickerDialog extends Dialog<List<CayVai>> {

    private final NguyenLieuService service;
    private final LoVai targetLo; // may be null if creating new lot

    // Model class to hold each row with its checked state
    private static class CayVaiRow {
        final CayVai cayVai;
        final SimpleBooleanProperty selected;

        CayVaiRow(CayVai cv, boolean preSelected) {
            this.cayVai = cv;
            this.selected = new SimpleBooleanProperty(preSelected);
        }
    }

    public CayVaiPickerDialog(LoVai targetLo, NguyenLieuService service, List<CayVai> alreadySelected) {
        this.targetLo = targetLo;
        this.service = service;

        setTitle("Chọn Cây Vải Cho Lô");
        setHeaderText(null);
        getDialogPane().getStyleClass().add("dialog-pane");
        getDialogPane().setPrefWidth(640);
        getDialogPane().setPrefHeight(480);

        VBox root = new VBox(14);
        root.setPadding(new Insets(18));

        // Header
        Label lblTitle = new Label("Danh sách cây vải còn lẻ (chưa gán lô)");
        lblTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #1e293b;");
        Label lblSub = new Label("Chỉ hiển thị cây vải chưa thuộc lô nào. Tích chọn để thêm vào lô hiện tại.");
        lblSub.setStyle("-fx-font-size: 11px; -fx-text-fill: #94a3b8;");

        // Search bar
        HBox searchBar = new HBox(10);
        searchBar.setAlignment(Pos.CENTER_LEFT);
        TextField txtSearch = new TextField();
        txtSearch.setPromptText("🔍  Tìm theo tên, màu sắc...");
        txtSearch.setMaxWidth(Double.MAX_VALUE);
        txtSearch.setStyle(
            "-fx-background-radius: 7; -fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 7; -fx-padding: 7 10; -fx-font-size: 12px;"
        );
        HBox.setHgrow(txtSearch, Priority.ALWAYS);

        Label lblCount = new Label();
        lblCount.setStyle("-fx-font-size: 11px; -fx-text-fill: #64748b;");

        searchBar.getChildren().addAll(txtSearch, lblCount);


        // Table
        TableView<CayVaiRow> table = new TableView<>();
        table.setEditable(true);
        VBox.setVgrow(table, Priority.ALWAYS);

        // --- Checkbox column ---
        TableColumn<CayVaiRow, Boolean> colCheck = new TableColumn<>("");
        colCheck.setPrefWidth(45);
        colCheck.setResizable(false);
        colCheck.setCellValueFactory(cell -> cell.getValue().selected);
        colCheck.setCellFactory(CheckBoxTableCell.forTableColumn(colCheck));
        colCheck.setEditable(true);

        // --- Tên cây ---
        TableColumn<CayVaiRow, String> colName = new TableColumn<>("Tên Cây Vải");
        colName.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().cayVai.getTenCayVai()));
        colName.setPrefWidth(160);

        // --- Màu sắc ---
        TableColumn<CayVaiRow, String> colColor = new TableColumn<>("Màu Sắc");
        colColor.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().cayVai.getMauSac()));
        colColor.setPrefWidth(90);

        // --- Chiều dài ---
        TableColumn<CayVaiRow, String> colLen = new TableColumn<>("Chiều Dài");
        colLen.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().cayVai.getChieuDai() + " m"));
        colLen.setPrefWidth(85);

        // --- Vị trí ---
        TableColumn<CayVaiRow, String> colPos = new TableColumn<>("Vị Trí");
        colPos.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().cayVai.getViTri()));
        colPos.setPrefWidth(90);

        table.getColumns().addAll(colCheck, colName, colColor, colLen, colPos);

        // Build row data — only show:
        //   (1) CayVai with no LoVai assigned (free/unassigned), OR
        //   (2) CayVai already assigned to the current targetLo (when editing)
        List<String> alreadySelectedNames = alreadySelected.stream()
            .map(CayVai::getTenCayVai)
            .collect(Collectors.toList());

        String targetLoId = (targetLo != null) ? targetLo.getMaLo() : null;

        List<CayVai> eligibleCayVai = service.getAllCayVai().stream()
            .filter(cv -> {
                if (cv.getLoVai() == null) return true; // free roll
                if (targetLoId != null && targetLoId.equals(cv.getLoVai().getMaLo())) return true; // already in this lot
                return false;
            })
            .collect(Collectors.toList());

        ObservableList<CayVaiRow> allRows = FXCollections.observableArrayList();
        for (CayVai cv : eligibleCayVai) {
            boolean preSelected = alreadySelectedNames.contains(cv.getTenCayVai());
            allRows.add(new CayVaiRow(cv, preSelected));
        }

        ObservableList<CayVaiRow> displayedRows = FXCollections.observableArrayList(allRows);
        table.setItems(displayedRows);

        // Update count label
        Runnable updateCount = () -> {
            long count = displayedRows.stream().filter(r -> r.selected.get()).count();
            lblCount.setText("Đã chọn: " + count + "/" + displayedRows.size() + " cây");
        };
        allRows.forEach(r -> r.selected.addListener((obs, o, n) -> updateCount.run()));
        updateCount.run();

        // Search filter
        txtSearch.textProperty().addListener((obs, oldVal, query) -> {
            displayedRows.clear();
            String lower = query.trim().toLowerCase();
            if (lower.isEmpty()) {
                displayedRows.addAll(allRows);
            } else {
                allRows.stream()
                    .filter(r ->
                        r.cayVai.getTenCayVai().toLowerCase().contains(lower) ||
                        (r.cayVai.getMauSac() != null && r.cayVai.getMauSac().toLowerCase().contains(lower)) ||
                        (r.cayVai.getViTri() != null && r.cayVai.getViTri().toLowerCase().contains(lower))
                    )
                    .forEach(displayedRows::add);
            }
            updateCount.run();
        });

        // Select All / Deselect All toolbar
        HBox toolbar = new HBox(10);
        toolbar.setAlignment(Pos.CENTER_LEFT);
        Button btnAll = new Button("☑ Chọn tất cả");
        btnAll.setStyle("-fx-background-color: #6366f1; -fx-text-fill: white; -fx-font-size: 11px; -fx-background-radius: 5; -fx-padding: 5 10; -fx-cursor: hand;");
        Button btnNone = new Button("☐ Bỏ chọn tất cả");
        btnNone.setStyle("-fx-background-color: #e2e8f0; -fx-text-fill: #475569; -fx-font-size: 11px; -fx-background-radius: 5; -fx-padding: 5 10; -fx-cursor: hand;");
        btnAll.setOnAction(e -> { displayedRows.forEach(r -> r.selected.set(true)); updateCount.run(); });
        btnNone.setOnAction(e -> { displayedRows.forEach(r -> r.selected.set(false)); updateCount.run(); });
        toolbar.getChildren().addAll(btnAll, btnNone);

        root.getChildren().addAll(lblTitle, lblSub, searchBar, toolbar, table);
        getDialogPane().setContent(root);

        // Buttons
        ButtonType btnConfirm = new ButtonType("✔ Xác nhận", ButtonBar.ButtonData.OK_DONE);
        ButtonType btnCancel  = new ButtonType("Hủy", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().addAll(btnConfirm, btnCancel);

        Button btnOk = (Button) getDialogPane().lookupButton(btnConfirm);
        btnOk.setStyle(
            "-fx-background-color: #22c55e; -fx-text-fill: white;" +
            "-fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 7;"
        );
        Button btnCancelBtn = (Button) getDialogPane().lookupButton(btnCancel);
        btnCancelBtn.setStyle(
            "-fx-background-color: #e2e8f0; -fx-text-fill: #475569;" +
            "-fx-font-weight: bold; -fx-padding: 8 18; -fx-background-radius: 7;"
        );

        // Result converter: return only selected CayVai
        setResultConverter(btn -> {
            if (btn == btnConfirm) {
                return allRows.stream()
                    .filter(r -> r.selected.get())
                    .map(r -> r.cayVai)
                    .collect(Collectors.toList());
            }
            return null;
        });
    }
}
