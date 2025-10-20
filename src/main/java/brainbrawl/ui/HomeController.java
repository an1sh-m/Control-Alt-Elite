package brainbrawl.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;

public class HomeController {

    // ==== CATEGORY CLICKS ====
    @FXML
    private void onMaths(MouseEvent e) {
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/BrainBrawl/Views/Quiz.fxml")); // capital V
            Parent root = fx.load();

            // boot a 10-question maths quiz
            QuizController qc = fx.getController();
            qc.startMathQuiz(10);

            Scene scene = ((Node) e.getSource()).getScene();
            scene.setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    @FXML private void onGeneral(MouseEvent e) { System.out.println("Go -> General Knowledge"); }
    @FXML private void onGeography(MouseEvent e) { System.out.println("Go -> Geography"); }
    @FXML private void onMoreInsights(ActionEvent e) { System.out.println("Go -> Insights/Stats"); }
}
