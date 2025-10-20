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
    @FXML
    private void onMaths(MouseEvent e) {
        Integer level = pickDifficulty();
        if (level == null) return; // cancelled

        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/BrainBrawl/Views/Quiz.fxml")); // capital V
            Parent root = fx.load();

            // boot a 10-question maths quiz at selected difficulty
            QuizController qc = fx.getController();
            qc.startMathQuiz(10, level);

            Scene scene = ((Node) e.getSource()).getScene();
            scene.setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML private void onGeneral(MouseEvent e)  { System.out.println("Go -> General Knowledge"); }
    @FXML private void onGeography(MouseEvent e){ System.out.println("Go -> Geography"); }
    @FXML private void onMoreInsights(ActionEvent e) { System.out.println("Go -> Insights/Stats"); }

    // ==== Difficulty picker dialog ====
    private Integer pickDifficulty() {
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

        r1.setToggleGroup(group);
        r2.setToggleGroup(group);
        r3.setToggleGroup(group);
        r4.setToggleGroup(group);
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
        dlg.getDialogPane().setContent(new GridPane(){{
            add(box, 0, 0);
        }});

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
