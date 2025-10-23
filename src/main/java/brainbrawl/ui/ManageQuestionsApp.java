// File: src/main/java/brainbrawl/ui/ManageQuestionsApp.java
package brainbrawl.ui;

import brainbrawl.db.Db;
import brainbrawl.model.Question;
import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * UI application for managing questions in the BrainBrawl database.
 * <p>
 * Allows viewing, adding, updating, and deleting questions. Supports both MCQ and short-answer types.
 * Uses direct JDBC for database operations and a TableView for displaying existing questions.
 * </p>
 * <p>
 * Launched only from within the main BrainBrawl application (Login → Home → Manage Questions).
 * </p>
 *
 */
public class ManageQuestionsApp extends Application {

    /** TableView displaying all questions. */
    private final TableView<Question> table = new TableView<>();

    /**
     * Entry point for the JavaFX application.
     * <p>
     * Sets up the UI (table, form, actions), binds form fields to table selection, and attaches event handlers.
     * </p>
     *
     * @param stage the primary stage
     */
    @Override
    public void start(Stage stage) {

        // ===== Table =====
        TableColumn<Question, Number> cId = new TableColumn<>("ID");
        cId.setCellValueFactory(q -> new SimpleLongProperty(q.getValue().getId() == null ? -1 : q.getValue().getId()));

        TableColumn<Question, String> cCat = new TableColumn<>("Category");
        cCat.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getCategory()));

        TableColumn<Question, String> cText = new TableColumn<>("Text");
        cText.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getText()));

        TableColumn<Question, String> cType = new TableColumn<>("Type");
        cType.setCellValueFactory(q -> new SimpleStringProperty(q.getValue().getType().name()));

        TableColumn<Question, Number> cDiff = new TableColumn<>("Difficulty");
        cDiff.setCellValueFactory(q -> new SimpleIntegerProperty(q.getValue().getDifficulty()));

        table.getColumns().addAll(cId, cCat, cText, cType, cDiff);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        table.setPrefHeight(420);

        // ===== Form =====
        TextField tfCategory = new TextField();
        tfCategory.setPromptText("Category (e.g., General)");

        TextArea taText = new TextArea();
        taText.setPromptText("Question text");
        taText.setPrefRowCount(3);

        ComboBox<Question.Type> cbType = new ComboBox<>();
        cbType.getItems().addAll(Question.Type.values());
        cbType.getSelectionModel().select(Question.Type.MCQ);

        TextArea taOptions = new TextArea();
        taOptions.setPromptText("Options for MCQ — one per line");
        taOptions.setPrefRowCount(4);

        Spinner<Integer> spCorrect = new Spinner<>(0, 9, 0); // 0-based
        spCorrect.setEditable(true);

        Spinner<Integer> spDiff = new Spinner<>(1, 5, 1);
        spDiff.setEditable(true);

        cbType.valueProperty().addListener((obs, ov, nv) -> {
            boolean mcq = nv == Question.Type.MCQ;
            taOptions.setDisable(!mcq);
            spCorrect.setDisable(!mcq);
        });

        Button btnAdd = new Button("Add");
        Button btnUpdate = new Button("Update");
        Button btnDelete = new Button("Delete");
        Button btnClear = new Button("Clear");

        HBox actions = new HBox(10, btnAdd, btnUpdate, btnDelete, btnClear);
        actions.setAlignment(Pos.CENTER_RIGHT);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Category"), 0, 0);
        form.add(tfCategory, 1, 0);
        form.add(new Label("Text"), 0, 1);
        form.add(taText, 1, 1);
        form.add(new Label("Type"), 0, 2);
        form.add(cbType, 1, 2);
        form.add(new Label("Options (MCQ)"), 0, 3);
        form.add(taOptions, 1, 3);
        form.add(new Label("Correct Index (0-based)"), 0, 4);
        form.add(spCorrect, 1, 4);
        form.add(new Label("Difficulty (1–5)"), 0, 5);
        form.add(spDiff, 1, 5);

        VBox right = new VBox(12, form, actions);
        right.setPadding(new Insets(12));
        right.setMaxWidth(520);

        // Prefill form on row select
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldQ, q) -> {
            if (q == null) return;
            tfCategory.setText(q.getCategory());
            taText.setText(q.getText());
            cbType.getSelectionModel().select(q.getType());
            if (q.getType() == Question.Type.MCQ) {
                List<String> opts = q.getOptions() != null ? q.getOptions() : new ArrayList<>();
                taOptions.setText(String.join("\n", opts));
                spCorrect.getValueFactory().setValue(q.getCorrectIndex() == null ? 0 : q.getCorrectIndex());
            } else {
                taOptions.clear();
                spCorrect.getValueFactory().setValue(0);
            }
            spDiff.getValueFactory().setValue(q.getDifficulty());
        });

        // ===== Actions =====
        btnAdd.setOnAction(e -> {
            Optional<Question> q = buildFromForm(null, tfCategory, taText, cbType, taOptions, spCorrect, spDiff);
            q.ifPresent(newQ -> {
                insertQuestion(newQ);
                refreshTable();
                clearForm(tfCategory, taText, taOptions, cbType, spCorrect, spDiff);
            });
        });

        btnUpdate.setOnAction(e -> {
            Question selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { info("Select a row to update."); return; }
            Optional<Question> q = buildFromForm(selected.getId(), tfCategory, taText, cbType, taOptions, spCorrect, spDiff);
            q.ifPresent(upd -> {
                updateQuestion(upd);
                refreshTable();
                table.getSelectionModel().clearSelection();
                clearForm(tfCategory, taText, taOptions, cbType, spCorrect, spDiff);
            });
        });

        btnDelete.setOnAction(e -> {
            Question selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) { info("Select a row to delete."); return; }
            if (confirm("Delete question ID " + selected.getId() + "?")) {
                if (selected.getId() != null) deleteQuestion(selected.getId());
                refreshTable();
                table.getSelectionModel().clearSelection();
                clearForm(tfCategory, taText, taOptions, cbType, spCorrect, spDiff);
            }
        });

        btnClear.setOnAction(e -> {
            table.getSelectionModel().clearSelection();
            clearForm(tfCategory, taText, taOptions, cbType, spCorrect, spDiff);
        });

        // ===== Layout =====
        BorderPane root = new BorderPane();
        root.setCenter(table);
        root.setRight(right);
        BorderPane.setMargin(table, new Insets(12));

        Scene scene = new Scene(root, 980, 620);
        var cssUrl = ManageQuestionsApp.class.getResource("/brainbrawl/ui/login.css");
        if (cssUrl != null) scene.getStylesheets().add(cssUrl.toExternalForm());

        stage.setTitle("BrainBrawl — Manage Questions");
        stage.setScene(scene);
        stage.show();

        refreshTable();
    }

    // ====================== DB LAYER (direct JDBC) ======================

    /** Refreshes the TableView with the latest questions from the database. */
    private void refreshTable() {
        table.setItems(FXCollections.observableArrayList(listAllQuestions()));
    }

    /** Retrieves all questions from the database. */
    private List<Question> listAllQuestions() {
        String sql = "SELECT id, category, text, type, options_text, correct_index, difficulty FROM questions ORDER BY id";
        List<Question> out = new ArrayList<>();
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(mapRow(rs));
        } catch (SQLException e) {
            error("Failed to load questions: " + e.getMessage());
        }
        return out;
    }

    /** Inserts a new question into the database. */
    private void insertQuestion(Question q) {
        String sql = """
            INSERT INTO questions(category, text, type, options_text, correct_index, difficulty)
            VALUES(?,?,?,?,?,?)
            """;
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            bindQuestion(ps, q);
            ps.executeUpdate();
        } catch (SQLException e) {
            error("Insert failed: " + e.getMessage());
        }
    }

    /** Updates an existing question in the database. */
    private void updateQuestion(Question q) {
        if (q.getId() == null) { error("Update failed: missing ID"); return; }
        String sql = """
            UPDATE questions
               SET category=?, text=?, type=?, options_text=?, correct_index=?, difficulty=?
             WHERE id=?
            """;
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            bindQuestion(ps, q);
            ps.setLong(7, q.getId());
            ps.executeUpdate();
        } catch (SQLException e) {
            error("Update failed: " + e.getMessage());
        }
    }

    /** Deletes a question from the database by ID. */
    private void deleteQuestion(long id) {
        String sql = "DELETE FROM questions WHERE id=?";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            error("Delete failed: " + e.getMessage());
        }
    }

    // ----- helpers for DB mapping -----

    /** Maps a ResultSet row to a Question object. */
    private Question mapRow(ResultSet rs) throws SQLException {
        long id = rs.getLong("id");
        String category = rs.getString("category");
        String text = rs.getString("text");
        Question.Type type = Question.Type.valueOf(rs.getString("type"));
        String optionsText = rs.getString("options_text");
        Integer correctIndex = (rs.getObject("correct_index") == null) ? null : rs.getInt("correct_index");
        int difficulty = rs.getInt("difficulty");

        Question q;
        if (type == Question.Type.MCQ) {
            List<String> opts = splitOptions(optionsText);
            q = Question.mcq(category, text, opts, correctIndex == null ? 0 : correctIndex, difficulty);
        } else {
            q = Question.shortAns(category, text, difficulty);
        }
        q.setId(id);
        return q;
    }

    /** Binds a Question object's fields to a PreparedStatement for DB operations. */
    private void bindQuestion(PreparedStatement ps, Question q) throws SQLException {
        ps.setString(1, q.getCategory());
        ps.setString(2, q.getText());
        ps.setString(3, q.getType().name());
        if (q.getType() == Question.Type.MCQ) {
            ps.setString(4, joinOptions(q.getOptions()));
            ps.setObject(5, q.getCorrectIndex(), Types.INTEGER);
        } else {
            ps.setNull(4, Types.VARCHAR);
            ps.setNull(5, Types.INTEGER);
        }
        ps.setInt(6, q.getDifficulty());
    }

    // ====================== UI helpers ======================

    /** Clears all form inputs and resets defaults. */
    void clearForm(TextField tfCategory,
                   TextArea taText,
                   TextArea taOptions,
                   ComboBox<Question.Type> cbType,
                   Spinner<Integer> spCorrect,
                   Spinner<Integer> spDiff) {
        tfCategory.clear();
        taText.clear();
        taOptions.clear();
        cbType.getSelectionModel().select(Question.Type.MCQ);
        spCorrect.getValueFactory().setValue(0);
        spDiff.getValueFactory().setValue(1);
    }

    /** Builds a Question object form inputs, validates entries, returns Optional. */
    private Optional<Question> buildFromForm(Long id,
                                             TextField tfCategory,
                                             TextArea taText,
                                             ComboBox<Question.Type> cbType,
                                             TextArea taOptions,
                                             Spinner<Integer> spCorrect,
                                             Spinner<Integer> spDiff) {
        String category = safeTrim(tfCategory.getText());
        String text = safeTrim(taText.getText());
        Question.Type type = cbType.getValue();
        int diff = spDiff.getValue();

        if (category.isEmpty() || text.isEmpty()) { info("Category and Text are required."); return Optional.empty(); }
        if (diff < 1 || diff > 5) { info("Difficulty must be between 1 and 5."); return Optional.empty(); }

        Question q;
        if (type == Question.Type.MCQ) {
            List<String> opts = new ArrayList<>();
            Arrays.stream((taOptions.getText() == null ? "" : taOptions.getText()).split("\\R"))
                    .map(String::trim).filter(s -> !s.isEmpty()).forEach(opts::add);
            if (opts.size() < 2) { info("Enter at least two options for MCQ."); return Optional.empty(); }
            int correct = spCorrect.getValue();
            if (correct < 0 || correct >= opts.size()) { info("Correct index must be 0.." + (opts.size() - 1)); return Optional.empty(); }
            q = Question.mcq(category, text, opts, correct, diff);
        } else {
            q = Question.shortAns(category, text, diff);
        }
        q.setId(id);
        return Optional.of(q);
    }

    private static String safeTrim(String s) { return s == null ? "" : s.trim(); }

    private static void info(String msg) { new Alert(Alert.AlertType.INFORMATION, msg).showAndWait(); }

    private static void error(String msg) { new Alert(Alert.AlertType.ERROR, msg).showAndWait(); }

    private static boolean confirm(String msg) {
        return new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL)
                .showAndWait().filter(btn -> btn == ButtonType.OK).isPresent();
    }

    /** Joins MCQ options into a single string for DB storage. */
    private static String joinOptions(List<String> opts) {
        if (opts == null || opts.isEmpty()) return null;
        return String.join("|", opts);
    }

    /** Splits stored options string into a List. */
    private static List<String> splitOptions(String s) {
        if (s == null || s.isEmpty()) return new ArrayList<>();
        return Arrays.asList(s.split("\\|",-1));
    }

    // IMPORTANT: No main() here. Launched only after Login.
}
