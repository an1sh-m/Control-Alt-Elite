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
        final int answer;  // keep integer answers for simple UX
        Q(String t, int a) { text = t; answer = a; }
    }

    private final List<Q> quiz = new ArrayList<>();
    private int idx = 0;
    private int score = 0;
    private boolean awaitingNext = false;

    private int level = 1;

    /** Called by HomeController right after loading FXML. */
    public void startMathQuiz(int count, int difficultyLevel) {
        this.level = Math.max(1, Math.min(4, difficultyLevel));
        titleLabel.setText("Maths Quiz — Level " + this.level);
        generateMathQuestions(count, this.level);
        idx = 0;
        score = 0;
        awaitingNext = false;
        showCurrent();
    }

    // ===================== QUESTION GENERATORS =====================
    private void generateMathQuestions(int n, int level) {
        quiz.clear();
        Random r = new Random();

        switch (level) {
            case 1 -> {
                // Year 12: +, −, × with small numbers (1..12)
                for (int i = 0; i < n; i++) {
                    int a = r.nextInt(12) + 1;
                    int b = r.nextInt(12) + 1;
                    String op = new String[]{"+", "-", "×"}[r.nextInt(3)];
                    int ans = switch (op) {
                        case "+" -> a + b;
                        case "-" -> a - b;
                        default  -> a * b;
                    };
                    quiz.add(new Q(a + " " + op + " " + b + " = ?", ans));
                }
            }
            case 2 -> {
                // 1st-year Eng: include ÷ with integer results, bigger range
                for (int i = 0; i < n; i++) {
                    int type = r.nextInt(4); // 0:+ 1:- 2:× 3:÷
                    int a, b, ans;
                    String text;
                    if (type == 3) {
                        // build integer division: (a*b)/a = b
                        a = r.nextInt(29) + 2; // 2..30
                        b = r.nextInt(28) + 2; // 2..29
                        int dividend = a * b;
                        ans = b;
                        text = dividend + " ÷ " + a + " = ?";
                    } else if (type == 2) {
                        a = r.nextInt(20) + 2;
                        b = r.nextInt(20) + 2;
                        ans = a * b;
                        text = a + " × " + b + " = ?";
                    } else if (type == 1) {
                        a = r.nextInt(40) + 10;
                        b = r.nextInt(40) + 10;
                        ans = a - b;
                        text = a + " - " + b + " = ?";
                    } else {
                        a = r.nextInt(40) + 10;
                        b = r.nextInt(40) + 10;
                        ans = a + b;
                        text = a + " + " + b + " = ?";
                    }
                    quiz.add(new Q(text, ans));
                }
            }
            case 3 -> {
                // 2nd-year Eng: two-step expressions with parentheses, integer ÷
                String[] ops = {"+", "-", "×", "÷"};
                for (int i = 0; i < n; i++) {
                    String op1 = ops[r.nextInt(ops.length)];
                    String op2 = ops[r.nextInt(ops.length)];

                    // Build middle part with safe integer division when needed
                    int a = r.nextInt(20) + 2;  // 2..21
                    int b = r.nextInt(20) + 2;  // 2..21
                    int mid;
                    String midText;
                    if (op1.equals("÷")) {
                        int dividend = a * b; // ensures integer
                        mid = dividend / a;   // = b
                        midText = dividend + " ÷ " + a;
                    } else {
                        mid = switch (op1) {
                            case "+" -> a + b;
                            case "-" -> a - b;
                            default  -> a * b;
                        };
                        midText = a + " " + op1 + " " + b;
                    }

                    int c = r.nextInt(20) + 2;
                    int ans;
                    String text;
                    if (op2.equals("÷")) {
                        int dividend = mid * c; // ensures integer
                        ans = dividend / c / 1; // equals mid
                        text = "(" + midText + ") ÷ " + c + " = ?";
                        // But to match ans, we need integer: (mid * c) ÷ c = mid
                        // Present as ((midText) × "c") ÷ c? That's weird UX.
                        // Instead present: (dividend ÷ "c") where dividend = mid*c
                        text = "(" + (mid * c) + " ÷ " + c + ") = ?";
                        ans = mid;
                    } else {
                        ans = switch (op2) {
                            case "+" -> mid + c;
                            case "-" -> mid - c;
                            default  -> mid * c;
                        };
                        text = "(" + midText + ") " + op2 + " " + c + " = ?";
                    }
                    quiz.add(new Q(text, ans));
                }
            }
            case 4 -> {
                // Final-year Eng: solve linear eq a*x + b = c for x (integer)
                for (int i = 0; i < n; i++) {
                    int a = r.nextInt(9) + 2;   // 2..10
                    int x = r.nextInt(21) - 10; // -10..10
                    int b = r.nextInt(41) - 20; // -20..20
                    int c = a * x + b;
                    String text = "Solve for x: " + a + "x + " + b + " = " + c;
                    quiz.add(new Q(text, x));
                }
            }
        }
    }
    // =============================================================

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
