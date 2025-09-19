package brainbrawl.ui;

import brainbrawl.dao.QuestionDaoJdbc;
import brainbrawl.db.Db;
import brainbrawl.model.Question;
import brainbrawl.service.QuestionService;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Arrays;

public class ManageQuestionsApp extends Application {
    private final QuestionService svc = new QuestionService(new QuestionDaoJdbc());
    private final TableView<Question> table = new TableView<>();

    @Override
    public void start(Stage stage) {
        Db.init();

        // Table setup
        var colId = new TableColumn<Question, String>("ID");
        colId.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getId())));
        var colCat = new TableColumn<Question, String>("Category");
        colCat.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCategory()));
        var colText = new TableColumn<Question, String>("Question");
        colText.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getText()));
        var colType = new TableColumn<Question, String>("Type");
        colType.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getType().name()));
        var colDiff = new TableColumn<Question, String>("Diff");
        colDiff.setCellValueFactory(c -> new SimpleStringProperty(String.valueOf(c.getValue().getDifficulty())));
        table.getColumns().addAll(colId, colCat, colText, colType, colDiff);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        refreshTable();

        // Add form (CREATE)
        TextField tfCategory = new TextField(); tfCategory.setPromptText("Category (e.g., Maths)");
        TextField tfQuestion = new TextField(); tfQuestion.setPromptText("Question text");
        ComboBox<String> cbType = new ComboBox<>(FXCollections.observableArrayList("MCQ","SHORT"));
        cbType.getSelectionModel().select("MCQ");
        TextField tfOptions = new TextField(); tfOptions.setPromptText("Options (comma-separated for MCQ)");
        Spinner<Integer> spCorrect = new Spinner<>(0, 10, 0); spCorrect.setEditable(true);
        Spinner<Integer> spDifficulty = new Spinner<>(1, 5, 1); spDifficulty.setEditable(true);

        Button btnAdd = new Button("Add Question");
        Label status = new Label();

        btnAdd.setOnAction(e -> {
            try {
                String cat = tfCategory.getText().trim();
                String qtext = tfQuestion.getText().trim();
                String type = cbType.getValue();
                int diff = spDifficulty.getValue();

                Question q;
                if ("MCQ".equals(type)) {
                    var options = Arrays.stream(tfOptions.getText().split(","))
                            .map(String::trim).filter(s -> !s.isBlank()).toList();
                    int correct = spCorrect.getValue();
                    q = Question.mcq(cat, qtext, options, correct, diff);
                } else {
                    q = Question.shortAns(cat, qtext, diff);
                }

                long id = svc.addQuestion(q);
                status.setText("Created question ID " + id);
                clearForm(tfCategory, tfQuestion, tfOptions, spCorrect, spDifficulty, cbType);
                refreshTable();
            } catch (Exception ex) {
                new Alert(Alert.AlertType.ERROR, ex.getMessage()).showAndWait();
            }
        });

        GridPane form = new GridPane();
        form.setHgap(8); form.setVgap(8);
        form.addRow(0, new Label("Category"), tfCategory);
        form.addRow(1, new Label("Question"), tfQuestion);
        form.addRow(2, new Label("Type"), cbType);
        form.addRow(3, new Label("Options"), tfOptions);
        form.addRow(4, new Label("Correct Index"), spCorrect);
        form.addRow(5, new Label("Difficulty (1–5)"), spDifficulty);
        form.add(btnAdd, 1, 6);

        // Update + Delete buttons
        Button btnUpdate = new Button("Update Selected");
        Button btnDelete = new Button("Delete Selected");

        btnUpdate.setOnAction(e -> {
            Question selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "No question selected").showAndWait();
                return;
            }
            Question updated = new Question(
                    selected.getId(),
                    selected.getCategory(),
                    selected.getText(),
                    selected.getType(),
                    selected.getOptions(),
                    selected.getCorrectIndex(),
                    selected.getDifficulty() + 1
            );
            if (svc.updateQuestion(updated)) {
                refreshTable();
                status.setText("Updated question ID " + selected.getId());
            }
        });

        btnDelete.setOnAction(e -> {
            Question selected = table.getSelectionModel().getSelectedItem();
            if (selected == null) {
                new Alert(Alert.AlertType.WARNING, "No question selected").showAndWait();
                return;
            }
            if (svc.deleteQuestion(selected.getId())) {
                refreshTable();
                status.setText("Deleted question ID " + selected.getId());
            }
        });

        HBox crudButtons = new HBox(10, btnUpdate, btnDelete);

        // --- Final root layout (only once!) ---
        VBox root = new VBox(12,
                new Label("Manage Questions (Read)"),
                table,
                crudButtons,
                new Separator(),
                new Label("Add Question (Create)"),
                form,
                status
        );
        root.setPadding(new Insets(12));

        Scene scene = new Scene(root, 920, 600);
        stage.setTitle("Brain Brawl — Manage Questions");
        stage.setScene(scene);
        stage.show();

        // UX tweak
        cbType.valueProperty().addListener((obs, oldV, newV) -> {
            boolean mcq = "MCQ".equals(newV);
            tfOptions.setDisable(!mcq);
            spCorrect.setDisable(!mcq);
        });
    }

    private void refreshTable() {
        table.getItems().setAll(svc.listAll());
    }

    public void clearForm(TextField cat, TextField q, TextField opts, Spinner<Integer> corr, Spinner<Integer> diff, ComboBox<String> type) {
        cat.clear(); q.clear(); opts.clear();
        corr.getValueFactory().setValue(0);
        diff.getValueFactory().setValue(1);
        type.getSelectionModel().select("MCQ");
    }

    public static void main(String[] args) { launch(args); }
}
