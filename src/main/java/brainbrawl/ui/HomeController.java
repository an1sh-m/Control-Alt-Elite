package brainbrawl.ui;

import brainbrawl.model.GameResult;
import brainbrawl.service.AppServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.List;

/**
 * Controller for the BrainBrawl home screen.
 * <p>
 * Handles navigation to different quizzes (Maths, General Knowledge, Geography),
 * manages recent game history display, and handles difficulty selection dialogs.
 * </p>
 * <p>
 * Uses {@link AppServices} to fetch recent game results.
 * </p>
 *
 */

public class HomeController {

    @FXML private VBox historyBox;

    // ---- Navigation handlers ----

    /**
     * Starts a Maths quiz after prompting the user to select difficulty.
     *
     * @param e the mouse event triggered by clicking the Maths quiz box
     */
    @FXML
    private void onMaths(MouseEvent e) {
        Integer level = pickMathsDifficulty();
        if (level == null) return;
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/BrainBrawl/Views/Quiz.fxml"));
            Parent root = fx.load();
            QuizController qc = fx.getController();
            qc.startMathQuiz(10, level);
            ((Node) e.getSource()).getScene().setRoot(root);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * Starts a General Knowledge quiz after prompting the user to select difficulty.
     *
     * @param e the mouse event triggered by clicking the General Knowledge quiz box
     */
    @FXML
    private void onGeneral(MouseEvent e) {
        Integer level = pickGKDifficulty();
        if (level == null) return;
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/BrainBrawl/Views/GKQuiz.fxml"));
            Parent root = fx.load();
            QuizMCQController qc = fx.getController();
            qc.startGeneralQuiz(10, level);
            ((Node) e.getSource()).getScene().setRoot(root);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * Starts a Geography quiz after prompting the user to select difficulty.
     *
     * @param e the mouse event triggered by clicking the Geography quiz box
     */
    @FXML
    private void onGeography(MouseEvent e) {
        Integer level = pickGeoDifficulty();
        if (level == null) return;
        try {
            FXMLLoader fx = new FXMLLoader(getClass().getResource("/BrainBrawl/Views/GeoQuiz.fxml"));
            Parent root = fx.load();
            GeoMCQController qc = fx.getController();
            qc.startGeographyQuiz(10, level);
            ((Node) e.getSource()).getScene().setRoot(root);
        } catch (Exception ex) { ex.printStackTrace(); }
    }

    /**
     * Placeholder handler for "More Insights" action.
     *
     * @param e the action event triggered by clicking the Insights button
     */
    @FXML private void onMoreInsights(ActionEvent e) { System.out.println("Go -> Insights / Stats"); }

    // ---- Difficulty pickers (same as before) ----

    /**
     * Prompts the user to pick a Maths quiz difficulty level.
     *
     * @return selected difficulty level (1–4), or null if cancelled
     */
    private Integer pickMathsDifficulty() {
        return pickLevelDialog("Select Difficulty","Choose Maths difficulty",
                "1 — Year 12 Maths","2 — 1st-year Engineering","3 — 2nd-year Engineering","4 — Final-year Engineering",
                """
                Levels:
                1) +, −, × up to 12
                2) +, −, ×, ÷ (integer answers) up to ~30
                3) Two-step expressions with parentheses
                4) Solve linear equations: a*x + b = c
                """);
    }

    /**
     * Prompts the user to pick a General Knowledge quiz difficulty level.
     *
     * @return selected difficulty level (1–4), or null if cancelled
     */
    private Integer pickGKDifficulty() {
        return pickLevelDialog("Select Difficulty","Choose General Knowledge difficulty",
                "1 — Easy","2 — Medium","3 — Hard","4 — Expert",
                """
                Levels:
                1) Core facts & everyday knowledge
                2) Broader world knowledge
                3) Deeper science/history/geography
                4) Expert-level, trickier topics
                """);
    }

    /**
     * Prompts the user to pick a Geography quiz difficulty level.
     *
     * @return selected difficulty level (1–4), or null if cancelled
     */
    private Integer pickGeoDifficulty() {
        return pickLevelDialog("Select Difficulty","Choose Geography difficulty",
                "1 — Easy (capitals, continents)","2 — Medium (countries, rivers, flags)",
                "3 — Hard (ranges, borders, superlatives)","4 — Expert (tricky facts & geopolitics)",
                """
                Levels:
                1) Capitals, continents, oceans
                2) Rivers, countries, flags
                3) Mountain ranges, borders, extremes
                4) Landlocked states, enclaves, tricky details
                """);
    }

    /**
     * Shows a dialog with four difficulty options and help text.
     *
     * @param title the dialog title
     * @param header the dialog header text
     * @param r1t text for radio button 1
     * @param r2t text for radio button 2
     * @param r3t text for radio button 3
     * @param r4t text for radio button 4
     * @param helpText explanatory help text shown below the options
     * @return selected difficulty (1–4), or null if cancelled
     */
    private Integer pickLevelDialog(String title, String header, String r1t, String r2t, String r3t, String r4t, String helpText) {
        Dialog<Integer> dlg = new Dialog<>();
        dlg.setTitle(title);
        dlg.setHeaderText(header);
        ButtonType ok = new ButtonType("Start Quiz", ButtonBar.ButtonData.OK_DONE);
        dlg.getDialogPane().getButtonTypes().addAll(ok, ButtonType.CANCEL);

        ToggleGroup group = new ToggleGroup();
        RadioButton r1 = new RadioButton(r1t);
        RadioButton r2 = new RadioButton(r2t);
        RadioButton r3 = new RadioButton(r3t);
        RadioButton r4 = new RadioButton(r4t);
        r1.setToggleGroup(group); r2.setToggleGroup(group); r3.setToggleGroup(group); r4.setToggleGroup(group);
        r1.setSelected(true);

        Label help = new Label(helpText);
        help.setWrapText(true);
        help.setStyle("-fx-opacity: 0.8;");

        VBox box = new VBox(8, r1, r2, r3, r4, new Separator(), help);
        box.setPadding(new Insets(10));
        dlg.getDialogPane().setContent(box);

        dlg.setResultConverter(bt -> {
            if (bt != ok) return null;
            if (r4.isSelected()) return 4;
            if (r3.isSelected()) return 3;
            if (r2.isSelected()) return 2;
            return 1;
        });

        return dlg.showAndWait().orElse(null);
    }

    // ---- History rendering ----

    /**
     * Initializes the controller and refreshes the recent game history.
     */
    @FXML
    private void initialize() {
        refreshHistory();
    }

    /**
     * Refreshes the VBox showing recent game results.
     */
    private void refreshHistory() {
        historyBox.getChildren().clear();
        List<GameResult> recents = AppServices.results().recent(10);
        if (recents.isEmpty()) {
            Label empty = new Label("No games yet. Play a quiz to see results here!");
            empty.setStyle("-fx-opacity: 0.7;");
            historyBox.getChildren().add(empty);
            return;
        }

        for (GameResult r : recents) {
            HBox row = makeHistoryRow(r);
            historyBox.getChildren().add(row);
        }
    }

    /**
     * Builds a single row for the recent history list.
     *
     * @param r a {@link GameResult} object containing quiz result data
     * @return a HBox representing one row in the history panel
     */
    private HBox makeHistoryRow(GameResult r) {
        // Badge = first letter of category
        Label badge = new Label(r.getCategory().isEmpty() ? "?" : r.getCategory().substring(0,1).toUpperCase());
        badge.getStyleClass().add("badge");

        Label cat = new Label(r.getCategory() + "  •  L" + r.getDifficulty());
        Label score = new Label(r.getScore() + "/" + r.getTotal());
        Label when = new Label(safeDate(r.getCreatedAt()));

        String pct = String.format("%.0f%%", (100.0 * r.getScore()) / Math.max(1, r.getTotal()));
        Label pctLabel = new Label(pct);
        pctLabel.getStyleClass().add(r.getScore() * 2 >= r.getTotal() ? "resultWin" : "resultLoss");

        HBox row = new HBox(8, badge, cat, new Pane(), when, new Label("⏱ " + r.getSecondsPerQuestion() + "s"), new Label("•"), score, pctLabel);
        HBox.setHgrow(row.getChildren().get(2), Priority.ALWAYS);
        row.getStyleClass().add("historyRow");
        row.setPadding(new Insets(6, 10, 6, 10));
        return row;
    }

    /**
     * Safely extracts the date (YYYY-MM-DD) from a timestamp string.
     *
     * @param createdAt timestamp string
     * @return formatted date or empty string if input is null/invalid
     */
    private static String safeDate(String createdAt) {
        if (createdAt == null || createdAt.isBlank()) return "";
        // keep "YYYY-MM-DD" only
        return createdAt.length() >= 10 ? createdAt.substring(0,10) : createdAt;
    }
}
