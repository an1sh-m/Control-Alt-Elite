package brainbrawl.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

public class HomeController {

    // ==== CATEGORY CLICKS ====

    // Maths: ask for difficulty first
    @FXML
    private void onMaths(MouseEvent e) {
        Integer level = pickMathsDifficulty();
        if (level == null) return; // cancelled

        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/BrainBrawl/Views/Quiz.fxml")); // capital V
            Parent root = fx.load();

            QuizController qc = fx.getController();
            qc.startMathQuiz(10, level);

            Scene scene = ((Node) e.getSource()).getScene();
            scene.setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // General Knowledge: ask for difficulty (1..4), then launch MCQ quiz
    @FXML
    private void onGeneral(MouseEvent e) {
        Integer level = pickGKDifficulty();
        if (level == null) return;

        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/BrainBrawl/Views/GKQuiz.fxml"));
            Parent root = fx.load();

            QuizMCQController qc = fx.getController();
            qc.startGeneralQuiz(10, level);

            Scene scene = ((Node) e.getSource()).getScene();
            scene.setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    // Geography: ask for difficulty (1..4), then launch MCQ quiz
    @FXML
    private void onGeography(MouseEvent e) {
        Integer level = pickGeoDifficulty();
        if (level == null) return;

        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/BrainBrawl/Views/GeoQuiz.fxml"));
            Parent root = fx.load();

            GeoMCQController qc = fx.getController();
            qc.startGeographyQuiz(10, level);

            Scene scene = ((Node) e.getSource()).getScene();
            scene.setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML private void onMoreInsights(ActionEvent e) { System.out.println("Go -> Insights / Stats"); }

    // ==== Difficulty pickers ====

    private Integer pickMathsDifficulty() {
        Dialog<Integer> dlg = new Dialog<>();
        dlg.setTitle("Select Difficulty");
        dlg.setHeaderText("Choose Maths difficulty");

        ButtonType ok = new ButtonType("Start Quiz", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        ToggleGroup group = new ToggleGroup();

        RadioButton r1 = new RadioButton("1 — Year 12 Maths");
        RadioButton r2 = new RadioButton("2 — 1st-year Engineering");
        RadioButton r3 = new RadioButton("3 — 2nd-year Engineering");
        RadioButton r4 = new RadioButton("4 — Final-year Engineering");

        r1.setToggleGroup(group); r2.setToggleGroup(group);
        r3.setToggleGroup(group); r4.setToggleGroup(group);
        r1.setSelected(true);

        Label help = new Label(
                """
                Levels:
                1) +, −, × up to 12
                2) +, −, ×, ÷ (integer answers) up to ~30
                3) Two-step expressions with parentheses
                4) Solve linear equations: a*x + b = c
                """
        );
        help.setWrapText(true);
        help.setStyle("-fx-opacity: 0.8;");

        VBox box = new VBox(8, r1, r2, r3, r4, new Separator(), help);
        box.setPadding(new Insets(10));

        GridPane container = new GridPane();
        container.add(box, 0, 0);
        dlg.getDialogPane().setContent(container);

        dlg.setResultConverter(bt -> {
            if (bt != ok) return null;
            if (r4.isSelected()) return 4;
            if (r3.isSelected()) return 3;
            if (r2.isSelected()) return 2;
            return 1;
        });

        return dlg.showAndWait().orElse(null);
    }

    private Integer pickGKDifficulty() {
        Dialog<Integer> dlg = new Dialog<>();
        dlg.setTitle("Select Difficulty");
        dlg.setHeaderText("Choose General Knowledge difficulty");

        ButtonType ok = new ButtonType("Start Quiz", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        ToggleGroup group = new ToggleGroup();

        RadioButton r1 = new RadioButton("1 — Easy");
        RadioButton r2 = new RadioButton("2 — Medium");
        RadioButton r3 = new RadioButton("3 — Hard");
        RadioButton r4 = new RadioButton("4 — Expert");

        r1.setToggleGroup(group); r2.setToggleGroup(group);
        r3.setToggleGroup(group); r4.setToggleGroup(group);
        r1.setSelected(true);

        Label help = new Label(
                """
                Levels:
                1) Core facts & everyday knowledge
                2) Broader world knowledge
                3) Deeper science/history/geography
                4) Expert-level, trickier topics
                """
        );
        help.setWrapText(true);
        help.setStyle("-fx-opacity: 0.8;");

        VBox box = new VBox(8, r1, r2, r3, r4, new Separator(), help);
        box.setPadding(new Insets(10));

        GridPane container = new GridPane();
        container.add(box, 0, 0);
        dlg.getDialogPane().setContent(container);

        dlg.setResultConverter(bt -> {
            if (bt != ok) return null;
            if (r4.isSelected()) return 4;
            if (r3.isSelected()) return 3;
            if (r2.isSelected()) return 2;
            return 1;
        });

        return dlg.showAndWait().orElse(null);
    }

    private Integer pickGeoDifficulty() {
        Dialog<Integer> dlg = new Dialog<>();
        dlg.setTitle("Select Difficulty");
        dlg.setHeaderText("Choose Geography difficulty");

        ButtonType ok = new ButtonType("Start Quiz", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        ToggleGroup group = new ToggleGroup();

        RadioButton r1 = new RadioButton("1 — Easy (capitals, continents)");
        RadioButton r2 = new RadioButton("2 — Medium (countries, rivers, flags)");
        RadioButton r3 = new RadioButton("3 — Hard (ranges, borders, superlatives)");
        RadioButton r4 = new RadioButton("4 — Expert (tricky facts & geopolitics)");

        r1.setToggleGroup(group); r2.setToggleGroup(group);
        r3.setToggleGroup(group); r4.setToggleGroup(group);
        r1.setSelected(true);

        Label help = new Label(
                """
                Levels:
                1) Capitals, continents, oceans
                2) Rivers, countries, flags
                3) Mountain ranges, borders, extremes
                4) Landlocked states, enclaves, tricky details
                """
        );
        help.setWrapText(true);
        help.setStyle("-fx-opacity: 0.8;");

        VBox box = new VBox(8, r1, r2, r3, r4, new Separator(), help);
        box.setPadding(new Insets(10));

        GridPane container = new GridPane();
        container.add(box, 0, 0);
        dlg.getDialogPane().setContent(container);

        dlg.setResultConverter(bt -> {
            if (bt != ok) return null;
            if (r4.isSelected()) return 4;
            if (r3.isSelected()) return 3;
            if (r2.isSelected()) return 2;
            return 1;
        });

        return dlg.showAndWait().orElse(null);
    }
}
