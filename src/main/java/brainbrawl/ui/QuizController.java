package brainbrawl.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import brainbrawl.model.GameResult;
import brainbrawl.service.AppServices;

public class QuizController {

    @FXML private Label titleLabel;
    @FXML private Label progressLabel;
    @FXML private Label timerLabel;
    @FXML private Label questionLabel;
    @FXML private TextField answerField;
    @FXML private Button primaryBtn;
    @FXML private Button backBtn;
    @FXML private Label feedbackLabel;

    private static class Q { final String text; final int answer; Q(String t,int a){text=t;answer=a;} }

    private final List<Q> quiz = new ArrayList<>();
    private int idx = 0, score = 0;
    private boolean awaitingNext = false;

    private Timeline countdown;
    private int remaining;
    private int QUESTION_SECONDS = 30;

    private int level = 1;
    private final String category = "Maths";
    private boolean saved = false;

    public void startMathQuiz(int count, int difficultyLevel) {
        this.level = Math.max(1, Math.min(4, difficultyLevel));
        titleLabel.setText("Maths Quiz — Level " + this.level);
        QUESTION_SECONDS = switch (this.level) { case 1->30; case 2->60; case 3->180; case 4->300; default->30; };
        generateMathQuestions(count, this.level);
        idx = 0; score = 0; awaitingNext = false; saved = false;
        showCurrent();
    }

    private void generateMathQuestions(int n, int level) {
        quiz.clear(); Random r = new Random();
        switch (level) {
            case 1 -> {
                String[] ops = {"+","-","×"};
                for (int i=0;i<n;i++){ int a=r.nextInt(12)+1,b=r.nextInt(12)+1; String op=ops[r.nextInt(ops.length)];
                    int ans = switch(op){case"+"->a+b;case"-"->a-b;default->a*b;};
                    quiz.add(new Q(a+" "+op+" "+b+" = ?", ans)); }
            }
            case 2 -> {
                for (int i=0;i<n;i++){
                    int type=r.nextInt(4),a,b,ans; String text;
                    if(type==3){a=r.nextInt(29)+2;b=r.nextInt(28)+2; int dividend=a*b; ans=b; text=dividend+" ÷ "+a+" = ?";}
                    else if(type==2){a=r.nextInt(20)+2;b=r.nextInt(20)+2; ans=a*b; text=a+" × "+b+" = ?";}
                    else if(type==1){a=r.nextInt(40)+10;b=r.nextInt(40)+10; ans=a-b; text=a+" - "+b+" = ?";}
                    else{a=r.nextInt(40)+10;b=r.nextInt(40)+10; ans=a+b; text=a+" + "+b+" = ?";}
                    quiz.add(new Q(text, ans));
                }
            }
            case 3 -> {
                String[] ops = {"+","-","×","÷"};
                for (int i=0;i<n;i++){
                    String op1=ops[r.nextInt(ops.length)], op2=ops[r.nextInt(ops.length)];
                    int a=r.nextInt(20)+2, b=r.nextInt(20)+2;
                    int mid; String midText;
                    if(op1.equals("÷")){int dividend=a*b; mid=dividend/a; midText=dividend+" ÷ "+a;}
                    else { mid = switch(op1){case"+"->a+b;case"-"->a-b;default->a*b;}; midText=a+" "+op1+" "+b; }
                    int c=r.nextInt(20)+2;
                    int ans; String text;
                    if(op2.equals("÷")){ text="("+(mid*c)+" ÷ "+c+") = ?"; ans=mid; }
                    else { ans = switch(op2){case"+"->mid+c;case"-"->mid-c;default->mid*c;}; text="("+midText+") "+op2+" "+c+" = ?"; }
                    quiz.add(new Q(text, ans));
                }
            }
            case 4 -> {
                for (int i=0;i<n;i++){
                    int a=r.nextInt(9)+2, x=r.nextInt(21)-10, b=r.nextInt(41)-20, c=a*x+b;
                    quiz.add(new Q("Solve for x: "+a+"x + "+b+" = "+c, x));
                }
            }
        }
    }

    private void showCurrent() {
        stopTimer();
        if (idx >= quiz.size()) {
            saveOnce(); // <-- save to DB exactly once
            questionLabel.setText("All done!");
            progressLabel.setText("Score: " + score + " / " + quiz.size());
            feedbackLabel.setText("");
            answerField.setDisable(true); answerField.clear();
            timerLabel.setText("--:--");
            primaryBtn.setText("Back to Home");
            primaryBtn.setOnAction(this::handleBackToHome);
            return;
        }
        Q q = quiz.get(idx);
        questionLabel.setText(q.text);
        progressLabel.setText("Q " + (idx + 1) + " / " + quiz.size());
        feedbackLabel.setText("");
        answerField.clear(); answerField.setDisable(false);
        primaryBtn.setText("Submit"); awaitingNext=false;
        startTimer();
    }

    @FXML private void onPrimary(ActionEvent e) {
        if (idx >= quiz.size()) { handleBackToHome(e); return; }
        if (!awaitingNext) { evaluateNow(false); } else { idx++; showCurrent(); }
    }

    private void evaluateNow(boolean timeout) {
        stopTimer();
        int correct = quiz.get(idx).answer;
        String input = answerField.getText().trim();
        Integer given = (input.matches("-?\\d+")) ? Integer.parseInt(input) : null;
        if (!timeout && given != null && given == correct) { score++; feedbackLabel.setText("✅ Correct!"); }
        else { feedbackLabel.setText(timeout ? "⏰ Time's up. Correct: " + correct : "❌ Wrong. Correct: " + correct); }
        answerField.setDisable(true);
        primaryBtn.setText(idx == quiz.size() - 1 ? "Finish" : "Next");
        awaitingNext = true;
        if (timeout) autoAdvanceSoon();
    }

    private void startTimer() {
        remaining = QUESTION_SECONDS;
        timerLabel.setText(fmt(remaining));
        countdown = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            remaining--; timerLabel.setText(fmt(remaining));
            if (remaining <= 0) evaluateNow(true);
        }));
        countdown.setCycleCount(QUESTION_SECONDS);
        countdown.playFromStart();
    }
    private void stopTimer(){ if(countdown!=null){ countdown.stop(); countdown=null; } }
    private void autoAdvanceSoon(){ new Timeline(new KeyFrame(Duration.seconds(1.2), ev -> { idx++; showCurrent(); })).play(); }
    private static String fmt(int s){ int m=s/60, ss=s%60; return String.format("%02d:%02d", m, ss); }

    private void saveOnce() {
        if (saved) return;
        saved = true;
        try {
            var result = GameResult.newUnstored(category, level, score, quiz.size(), QUESTION_SECONDS);
            AppServices.results().save(result);
        } catch (Exception ex) {
            ex.printStackTrace(); // non-fatal
        }
    }

    @FXML public void handleBackToHome(ActionEvent event) {
        stopTimer();
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
