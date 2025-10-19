package brainbrawl.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.fxml.FXMLLoader;
import javafx.scene.input.MouseEvent;

public class HomeController {

    // ---- Navigation handlers ----
    @FXML
    private void onMaths(MouseEvent e) {
        // TODO: replace with your real MathsCategory.fxml route
        // For now just navigate to a placeholder if you have one,
        // otherwise stay and print in console so it doesn’t crash.
        System.out.println("Go -> Maths Category");
        // navigate("/brainbrawl/ui/MathsCategory.fxml", e);
    }

    @FXML
    private void onGeneral(MouseEvent e) {
        System.out.println("Go -> General Knowledge");
        // navigate("/brainbrawl/ui/GeneralCategory.fxml", e);
    }

    @FXML
    private void onGeography(MouseEvent e) {
        System.out.println("Go -> Geography Category");
        // navigate("/brainbrawl/ui/GeographyCategory.fxml", e);
    }

    @FXML
    private void onMoreInsights(ActionEvent e) {
        System.out.println("Go -> Insights / Stats");
        // navigate("/brainbrawl/ui/Insights.fxml", e);
    }

    // ---- helper ----
    private void navigate(String fxmlPath, Object event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Scene scene = ((Node) (event instanceof MouseEvent ?
                    ((MouseEvent) event).getSource() : ((ActionEvent) event).getSource()))
                    .getScene();
            scene.setRoot(root);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
