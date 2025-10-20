package brainbrawl.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuizController {

    @FXML private Label titleLabel;       // "Maths Quiz"
    @FXML private Label progressLabel;    // "Q 1/10"
    @FXML private Label questionLabel;    // "12 + 7 = ?"
    @FXML private TextField answerField;
    @FXML private Button primaryBtn;      // Submit / Next / Finish
    @FXML private Button backBtn;         // Back to Home
    @FXML private Label feedbackLabel;    // Correct!/Wrong...

    private static class Q {
        final String text;
        final int answer;
        Q(String t, int a) { text = t; answer = a; }
    }

    private final List<Q> quiz = new ArrayList<>();
    private int idx = 0;
    private int score = 0;
    private boolean awaitingNext = false;

    /** Call right after loading FXML (HomeController does this). */
    public void startMathQuiz(int count) {
        titleLabel.setText("Maths Quiz");
        generateMathQuestions(count);
        idx = 0;
        score = 0;
        awaitingNext = false;
        showCurrent();
    }

    private void generateMathQuestions(int n) {
        quiz.clear();
        Random r = new Random();
        String[] ops = {"+", "-", "×"};
        for (int i = 0; i < n; i++) {
            int a = r.nextInt(12) + 1; // 1..12
            int b = r.nextInt(12) + 1;
            String op = ops[r.nextInt(ops.length)];
            int ans = switch (op) {
                case "+" -> a + b;
                case "-" -> a - b;
                default  -> a * b; // ×
            };
            quiz.add(new Q(a + " " + op + " " + b + " = ?", ans));
        }
    }

    private void showCurrent() {
        if (idx >= quiz.size()) {
            questionLabel.setText("All done!");
            progressLabel.setText("Score: " + score + " / " + quiz.size());
            feedbackLabel.setText("");
            answerField.setDisable(true);
            answerField.clear();
            primaryBtn.setText("Back to Home");
            primaryBtn.setOnAction(this::handleBackToHome);
            return;
        }
        Q q = quiz.get(idx);
        questionLabel.setText(q.text);
        progressLabel.setText("Q " + (idx + 1) + " / " + quiz.size());
        feedbackLabel.setText("");
        answerField.clear();
        answerField.setDisable(false);
        primaryBtn.setText("Submit");
        awaitingNext = false;
    }

    @FXML
    private void onPrimary(ActionEvent e) {
        if (idx >= quiz.size()) {
            handleBackToHome(e);
            return;
        }

        if (!awaitingNext) {
            String input = answerField.getText().trim();
            if (input.isEmpty() || !input.matches("-?\\d+")) {
                feedbackLabel.setText("Enter a whole number.");
                return;
            }
            int given = Integer.parseInt(input);
            int correct = quiz.get(idx).answer;
            if (given == correct) {
                score++;
                feedbackLabel.setText("✅ Correct!");
            } else {
                feedbackLabel.setText("❌ Wrong. Correct answer: " + correct);
            }
            answerField.setDisable(true);
            primaryBtn.setText(idx == quiz.size() - 1 ? "Finish" : "Next");
            awaitingNext = true;
        } else {
            idx++;
            showCurrent();
        }
    }

    @FXML
    public void handleBackToHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/BrainBrawl/HomePage.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root, 1200, 720));
            stage.setTitle("BrainBrawl – Home");
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
