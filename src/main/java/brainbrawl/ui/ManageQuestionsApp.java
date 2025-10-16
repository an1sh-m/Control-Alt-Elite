package brainbrawl.ui;

import brainbrawl.dao.QuestionDaoJdbc;
import brainbrawl.db.Db;
import brainbrawl.model.Question;
import brainbrawl.service.QuestionService;
import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;

/**
 *
 */
public class ManageQuestionsApp extends Application {

    private final QuestionService svc = new QuestionService(new QuestionDaoJdbc());
    private final TableView<Question> table = new TableView<>();

    @Override
    public void start(Stage stage) throws IOException {
        Db.init();

        // load login first
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/brainbrawl/ui/login.fxml"));
        Scene loginScene = new Scene(loader.load());
        stage.setScene(loginScene);
        stage.setTitle("BrainBrawl — Login");
        stage.show();

        LoginController controller = loader.getController();
        controller.setOnLoginSuccess(() -> showMainApp(stage));
    }

    /**
     *
     * @param stage
     */
    private void showMainApp(Stage stage) {
        // build your table + CRUD UI (same as before)
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
        table.getColumns().setAll(colId, colCat, colText, colType, colDiff);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
        refreshTable();

        // form fields
        TextField tfCategory = new TextField(); tfCategory.setPromptText("Category");
        TextField tfQuestion = new TextField(); tfQuestion.setPromptText("Question text");
        ComboBox<String> cbType = new ComboBox<>(FXCollections.observableArrayList("MCQ", "SHORT"));
        cbType.getSelectionModel().select("MCQ");
        TextField tfOptions = new TextField(); tfOptions.setPromptText("Options (comma-separated)");
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

        VBox root = new VBox(12,
                new Label("Manage Questions"),
                table,
                form,
                status
        );
        root.setPadding(new Insets(12));

        Scene scene = new Scene(root, 920, 600);
        stage.setScene(scene);
        stage.setTitle("BrainBrawl — Manage Questions");
        stage.show();
    }

    private void refreshTable() {
        table.getItems().setAll(svc.listAll());
    }

    public void clearForm(TextField cat, TextField q, TextField opts,
                          Spinner<Integer> corr, Spinner<Integer> diff,
                          ComboBox<String> type) {
        cat.clear(); q.clear(); opts.clear();
        corr.getValueFactory().setValue(0);
        diff.getValueFactory().setValue(1);
        type.getSelectionModel().select("MCQ");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
