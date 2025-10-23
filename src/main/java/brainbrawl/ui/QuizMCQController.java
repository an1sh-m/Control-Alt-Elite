package brainbrawl.ui;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

import brainbrawl.model.GameResult;
import brainbrawl.service.AppServices;

/**
 * Controller for the Multiple Choice Question (MCQ) Quiz screen.
 * <p>
 * Handles quiz generation, display of questions and options, user input,
 * timer countdown, scoring, and saving results.
 * Supports four difficulty levels with increasing complexity.
 * </p>
 */

public class QuizMCQController {

    @FXML private Label titleLabel;       // "General Knowledge — Level X"
    @FXML private Label progressLabel;    // "Q 1/10"
    @FXML private Label timerLabel;       // "00:30"
    @FXML private Label questionLabel;    // question text
    @FXML private VBox optionsBox;        // holds RadioButtons
    @FXML private Button primaryBtn;      // Submit / Next / Finish
    @FXML private Button backBtn;         // Back to Home
    @FXML private Label feedbackLabel;    // Correct!/Wrong...

    /** Inner class representing a single MCQ question. */
    private static class Q {
        final String text;
        final List<String> options;
        final int correctIndex;
        Q(String t, List<String> opts, int ci) { text = t; options = opts; correctIndex = ci; }
    }

    private final List<Q> quiz = new ArrayList<>();
    private int idx = 0;
    private int score = 0;
    private boolean awaitingNext = false;
    private ToggleGroup group;
    private int level = 1;

    // save meta
    private final String category = "General";
    private boolean saved = false;

    // Timer
    private Timeline countdown;
    private int remaining;
    private int QUESTION_SECONDS = 30;

    /**
     * Called by HomeController to start the MCQ quiz.
     * @param count number of questions
     * @param level difficulty level (1–4)
     */
    public void startGeneralQuiz(int count, int level) {
        this.level = Math.max(1, Math.min(4, level));
        titleLabel.setText("General Knowledge — Level " + this.level);
        QUESTION_SECONDS = switch (this.level) {
            case 1 -> 30;
            case 2 -> 60;
            case 3 -> 180;
            case 4 -> 300;
            default -> 30;
        };
        buildQuizFromBank(count, this.level);
        idx = 0;
        score = 0;
        awaitingNext = false;
        saved = false;
        showCurrent();
    }

    /**
     * Builds the quiz list by randomly selecting questions from the question bank.
     * @param n number of questions
     * @param level difficulty level
     */
    private void buildQuizFromBank(int n, int level) {
        // FIX: make a mutable copy before shuffling (banks may be List.of(...))
        List<Q> bank = new ArrayList<>(generalBank(level));
        Collections.shuffle(bank, new Random());
        quiz.clear();
        quiz.addAll(bank.subList(0, Math.min(n, bank.size())));
    }

    /**
     * Displays the current question or final score if all questions are done.
     */
    private void showCurrent() {
        stopTimer();

        if (idx >= quiz.size()) {
            saveOnce();
            questionLabel.setText("All done!");
            progressLabel.setText("Score: " + score + " / " + quiz.size());
            feedbackLabel.setText("");
            optionsBox.getChildren().clear();
            timerLabel.setText("--:--");
            primaryBtn.setText("Back to Home");
            primaryBtn.setOnAction(this::handleBackToHome);
            return;
        }

        Q q = quiz.get(idx);
        questionLabel.setText(q.text);
        progressLabel.setText("Q " + (idx + 1) + " / " + quiz.size());
        feedbackLabel.setText("");
        renderOptions(q);
        primaryBtn.setText("Submit");
        awaitingNext = false;

        startTimer();
    }

    /**
     * Renders RadioButtons for the options of a question.
     * @param q question to render
     */
    private void renderOptions(Q q) {
        optionsBox.getChildren().clear();
        group = new ToggleGroup();
        for (int i = 0; i < q.options.size(); i++) {
            RadioButton rb = new RadioButton(q.options.get(i));
            rb.setToggleGroup(group);
            rb.setWrapText(true);
            rb.setPadding(new Insets(6, 8, 6, 8));
            rb.setUserData(i);
            optionsBox.getChildren().add(rb);
        }
    }

    /**
     * Handles the primary button click (Submit / Next / Finish).
     */
    @FXML
    private void onPrimary(ActionEvent e) {
        if (idx >= quiz.size()) { handleBackToHome(e); return; }
        if (!awaitingNext) {
            evaluateNow(false);
        } else {
            idx++;
            showCurrent();
        }
    }

    /**
     * Evaluates the selected answer and updates score and feedback.
     * @param dueToTimeout true if evaluation triggered by timer expiration
     */
    private void evaluateNow(boolean dueToTimeout) {
        stopTimer();

        Toggle sel = group.getSelectedToggle();
        int chosen = (sel == null) ? -1 : (int) sel.getUserData();
        Q q = quiz.get(idx);

        if (!dueToTimeout && chosen == q.correctIndex) {
            score++;
            feedbackLabel.setText("✅ Correct!");
        } else {
            feedbackLabel.setText(dueToTimeout
                    ? "⏰ Time's up. Correct answer: " + q.options.get(q.correctIndex)
                    : "❌ Wrong. Correct answer: " + q.options.get(q.correctIndex));
        }

        optionsBox.getChildren().forEach(n -> n.setDisable(true));
        primaryBtn.setText(idx == quiz.size() - 1 ? "Finish" : "Next");
        awaitingNext = true;

        if (dueToTimeout) autoAdvanceSoon();
    }

    // ------------------- timer -------------------

    /** Starts the countdown timer for the current question. */
    private void startTimer() {
        remaining = QUESTION_SECONDS;
        timerLabel.setText(fmt(remaining));

        countdown = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            remaining--;
            timerLabel.setText(fmt(remaining));
            if (remaining <= 0) {
                evaluateNow(true);
            }
        }));
        countdown.setCycleCount(QUESTION_SECONDS);
        countdown.playFromStart();
    }

    /** Stops the current countdown timer. */
    private void stopTimer() {
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }
    }

    /** Advances to next question shortly after a timeout. */
    private void autoAdvanceSoon() {
        Timeline t = new Timeline(new KeyFrame(Duration.seconds(1.2), ev -> {
            idx++;
            showCurrent();
        }));
        t.setCycleCount(1);
        t.play();
    }

    /** Formats seconds into MM:SS string. */
    private static String fmt(int s) {
        if (s < 0) s = 0;
        int m = s / 60, ss = s % 60;
        return String.format("%02d:%02d", m, ss);
    }

    /**
     * Saves the quiz result to the database once.
     */
    private void saveOnce() {
        if (saved) return;
        saved = true;
        try {
            AppServices.results().save(GameResult.newUnstored(category, level, score, quiz.size(), QUESTION_SECONDS));
        } catch (Exception ex) {
            ex.printStackTrace(); // non-fatal
        }
    }

    /**
     * Handles navigation back to the home page.
     */
    @FXML
    public void handleBackToHome(ActionEvent event) {
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

    // ------------------- Difficulty banks -------------------

    /**
     * Returns the question bank for the specified level.
     * @param level difficulty level
     */
    private List<Q> generalBank(int level) {
        return switch (level) {
            case 1 -> bankEasy();
            case 2 -> bankMedium();
            case 3 -> bankHard();
            case 4 -> bankExpert();
            default -> bankEasy();
        };
    }

    private List<Q> bankEasy() {
        return List.of(
                q("What is the capital of Japan?", opts("Kyoto","Tokyo","Osaka","Sapporo"), 1),
                q("Which planet is known as the Red Planet?", opts("Mars","Jupiter","Venus","Mercury"), 0),
                q("Which ocean is the largest by surface area?", opts("Indian","Arctic","Atlantic","Pacific"), 3),
                q("What is the smallest prime number?", opts("0","1","2","3"), 2),
                q("Which city is nicknamed 'The Big Apple'?", opts("Los Angeles","Chicago","New York City","San Francisco"), 2),
                q("Water boils at what temperature (°C) at sea level?", opts("90","95","100","105"), 2),
                q("CPU stands for…", opts("Central Processing Unit","Computer Performance Utility","Core Processing Unit","Central Performance Unit"), 0),
                q("Which animal is the tallest?", opts("Elephant","Giraffe","Ostrich","Polar Bear"), 1),
                q("How many continents are there?", opts("5","6","7","8"), 2),
                q("What is the largest mammal?", opts("African Elephant","Blue Whale","Hippopotamus","Giraffe"), 1),
                q("How many sides does a square have?", opts("3","4","5","6"), 1),
                q("The currency of the United States is the…", opts("Euro","US Dollar","Pound","Yen"), 1),
                q("Which animal is famous in China and black-and-white?", opts("Giant panda","Raccoon","Skunk","Zebra"), 0),
                q("The chemical formula for water is…", opts("HO","H2O","OH2","H2O2"), 1)
        );
    }

    private List<Q> bankMedium() {
        return List.of(
                q("Who wrote '1984'?", opts("George Orwell","Aldous Huxley","J.K. Rowling","Ernest Hemingway"), 0),
                q("Who was the first person to walk on the Moon?", opts("Buzz Aldrin","Yuri Gagarin","Neil Armstrong","Michael Collins"), 2),
                q("Which element has atomic number 1?", opts("Helium","Hydrogen","Oxygen","Lithium"), 1),
                q("Which river flows through Egypt?", opts("Amazon","Nile","Danube","Rhine"), 1),
                q("What language is primarily spoken in Brazil?", opts("Spanish","Portuguese","French","English"), 1),
                q("What is the fastest land animal?", opts("Lion","Cheetah","Pronghorn","Greyhound"), 1),
                q("What is the hardest natural substance?", opts("Diamond","Quartz","Sapphire","Obsidian"), 0),
                q("Which is the largest hot desert?", opts("Gobi","Sahara","Kalahari","Arabian"), 1),
                q("Capital of Canada?", opts("Toronto","Vancouver","Ottawa","Montreal"), 2),
                q("Which country hosted the 2016 Summer Olympics?", opts("China","Brazil","UK","Russia"), 1),
                q("Which instrument has keys, pedals, and strings?", opts("Guitar","Piano","Violin","Flute"), 1),
                q("Which metal is liquid at room temperature?", opts("Mercury","Aluminium","Lead","Zinc"), 0),
                q("Which organ pumps blood through the body?", opts("Lungs","Heart","Liver","Kidneys"), 1),
                q("How many degrees are in a right angle?", opts("30","45","60","90"), 3)
        );
    }

    private List<Q> bankHard() {
        return List.of(
                q("World War II began in which year?", opts("1914","1918","1939","1945"), 2),
                q("Who painted 'The Starry Night'?", opts("Claude Monet","Pablo Picasso","Vincent van Gogh","Salvador Dalí"), 2),
                q("The currency of Japan is the…", opts("Won","Yuan","Yen","Ringgit"), 2),
                q("Which island is the largest in the world (not a continent)?", opts("Borneo","New Guinea","Great Britain","Greenland"), 3),
                q("SI unit of electrical resistance is the…", opts("Volt","Ampere","Ohm","Watt"), 2),
                q("Chemical formula for table salt is…", opts("NaCl","KCl","Na2CO3","CaCl2"), 0),
                q("Tallest mountain above sea level?", opts("K2","Everest","Kangchenjunga","Lhotse"), 1),
                q("Planet famous for its rings?", opts("Jupiter","Saturn","Uranus","Neptune"), 1),
                q("DNA has the shape of a…", opts("Single helix","Double helix","Beta sheet","Cube"), 1),
                q("Approximate speed of light in vacuum?", opts("3,000 km/s","30,000 km/s","300,000 km/s","3,000,000 km/s"), 2),
                q("Who proposed the general theory of relativity?", opts("Max Planck","Niels Bohr","Albert Einstein","Erwin Schrödinger"), 2),
                q("Which continent has the most countries?", opts("Europe","Asia","Africa","South America"), 2)
        );
    }

    private List<Q> bankExpert() {
        return List.of(
                q("Capital of Kazakhstan (2025)?", opts("Almaty","Astana","Nur-Sultan","Shymkent"), 1),
                q("Who discovered penicillin?", opts("Alexander Fleming","Louis Pasteur","Edward Jenner","Robert Koch"), 0),
                q("First woman to win a Nobel Prize?", opts("Marie Curie","Rosalind Franklin","Ada Lovelace","Lise Meitner"), 0),
                q("Rarest common ABO/Rh blood type globally?", opts("O−","AB−","A−","B−"), 1),
                q("Finnish belongs to which language family?", opts("Indo-European","Uralic","Altaic","Afroasiatic"), 1),
                q("Largest moon in the Solar System?", opts("Titan","Ganymede","Callisto","Europa"), 1),
                q("Who proved Fermat’s Last Theorem (1990s)?", opts("Terence Tao","Andrew Wiles","Grigori Perelman","Maryam Mirzakhani"), 1),
                q("Element named after a dwarf planet?", opts("Uranium","Neptunium","Plutonium","Mercury"), 2),
                q("Composer of 'The Four Seasons'?", opts("Bach","Vivaldi","Mozart","Handel"), 1),
                q("Timbuktu is in which country?", opts("Niger","Mali","Chad","Burkina Faso"), 1),
                q("Heisenberg’s uncertainty principle is in…", opts("Thermodynamics","Relativity","Quantum mechanics","Classical mechanics"), 2),
                q("Which country has the most UNESCO World Heritage Sites (c. mid-2020s)?", opts("Italy","China","India","Spain"), 1)
        );
    }

    /** Convenience method to create a Q object. */
    private static Q q(String text, List<String> options, int correctIndex) { return new Q(text, options, correctIndex); }

    /** Convenience method to create a List of options. */
    private static List<String> opts(String... s) { return Arrays.asList(s); }
}
