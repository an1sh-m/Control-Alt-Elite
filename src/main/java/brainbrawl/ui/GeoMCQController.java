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

public class GeoMCQController {

    @FXML private Label titleLabel;       // "Geography — Level X"
    @FXML private Label progressLabel;    // "Q 1/10"
    @FXML private Label timerLabel;       // "00:30"
    @FXML private Label questionLabel;    // question text
    @FXML private VBox optionsBox;        // holds RadioButtons
    @FXML private Button primaryBtn;      // Submit / Next / Finish
    @FXML private Button backBtn;         // Back to Home
    @FXML private Label feedbackLabel;    // Correct!/Wrong...

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
    private final String category = "Geography";
    private boolean saved = false;

    // Timer
    private Timeline countdown;
    private int remaining;
    private int QUESTION_SECONDS = 30;

    /** Called by HomeController. */
    public void startGeographyQuiz(int count, int level) {
        this.level = Math.max(1, Math.min(4, level));
        titleLabel.setText("Geography — Level " + this.level);
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

    private void buildQuizFromBank(int n, int level) {
        // FIX: make a mutable copy before shuffling (banks may be List.of(...))
        List<Q> bank = new ArrayList<>(geoBank(level));
        Collections.shuffle(bank, new Random());
        quiz.clear();
        quiz.addAll(bank.subList(0, Math.min(n, bank.size())));
    }

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

    private void stopTimer() {
        if (countdown != null) {
            countdown.stop();
            countdown = null;
        }
    }

    private void autoAdvanceSoon() {
        Timeline t = new Timeline(new KeyFrame(Duration.seconds(1.2), ev -> {
            idx++;
            showCurrent();
        }));
        t.setCycleCount(1);
        t.play();
    }

    private static String fmt(int s) {
        if (s < 0) s = 0;
        int m = s / 60, ss = s % 60;
        return String.format("%02d:%02d", m, ss);
    }

    private void saveOnce() {
        if (saved) return;
        saved = true;
        try {
            AppServices.results().save(GameResult.newUnstored(category, level, score, quiz.size(), QUESTION_SECONDS));
        } catch (Exception ex) {
            ex.printStackTrace(); // non-fatal
        }
    }

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
    private List<Q> geoBank(int level) {
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
                q("What is the capital of Australia?", opts("Sydney","Melbourne","Canberra","Brisbane"), 2),
                q("Which is the largest ocean?", opts("Indian","Atlantic","Arctic","Pacific"), 3),
                q("Which continent is Egypt in?", opts("Asia","Africa","Europe","South America"), 1),
                q("Mount Everest lies on the border of Nepal and which country?", opts("India","China (Tibet)","Bhutan","Pakistan"), 1),
                q("What is the capital of Japan?", opts("Kyoto","Tokyo","Osaka","Nagoya"), 1),
                q("Which continent is the Sahara Desert in?", opts("Asia","Africa","Australia","North America"), 1),
                q("Which line divides Earth into Northern and Southern Hemispheres?", opts("Prime Meridian","Equator","Tropic of Cancer","International Date Line"), 1),
                q("What is the capital of New Zealand?", opts("Auckland","Wellington","Christchurch","Hamilton"), 1),
                q("Which country is also a continent?", opts("Iceland","Australia","Madagascar","Greenland"), 1),
                q("Which ocean borders the west coast of the USA?", opts("Atlantic","Indian","Pacific","Arctic"), 2),
                q("What is the capital of Canada?", opts("Toronto","Ottawa","Vancouver","Montreal"), 1),
                q("The Amazon rainforest is primarily in which country?", opts("Peru","Brazil","Colombia","Venezuela"), 1)
        );
    }

    private List<Q> bankMedium() {
        return List.of(
                q("Which river flows through Paris?", opts("Seine","Thames","Danube","Rhine"), 0),
                q("Which country has the most time zones (including territories)?", opts("USA","Russia","France","UK"), 2),
                q("The Strait of Gibraltar separates Spain and which African country?", opts("Morocco","Algeria","Tunisia","Libya"), 0),
                q("Which lake is the largest by area?", opts("Lake Superior","Caspian Sea","Lake Victoria","Lake Baikal"), 1),
                q("Which country has the city of Dubrovnik?", opts("Croatia","Greece","Italy","Albania"), 0),
                q("Which river is the longest in Africa?", opts("Congo","Niger","Nile","Zambezi"), 2),
                q("What is the capital of South Korea?", opts("Seoul","Busan","Incheon","Daegu"), 0),
                q("Which desert is in northern China and southern Mongolia?", opts("Taklamakan","Thar","Gobi","Karakum"), 2),
                q("What is the capital of Argentina?", opts("Buenos Aires","Santiago","Lima","Montevideo"), 0),
                q("Which country does NOT border Germany?", opts("Denmark","Poland","Italy","Czechia"), 2),
                q("Kilimanjaro is in which country?", opts("Kenya","Tanzania","Uganda","Ethiopia"), 1),
                q("Which country owns Greenland?", opts("Norway","Iceland","Denmark","Canada"), 2)
        );
    }

    private List<Q> bankHard() {
        return List.of(
                q("Which country has the most international borders?", opts("China","Russia","Germany","Brazil"), 0),
                q("Which is the highest waterfall by uninterrupted drop?", opts("Angel Falls","Tugela Falls","Niagara Falls","Iguazu Falls"), 0),
                q("The Urals traditionally separate which two continents?", opts("Europe & Asia","Asia & Africa","North & South America","Europe & Africa"), 0),
                q("What is the capital of Ethiopia?", opts("Mogadishu","Khartoum","Addis Ababa","Asmara"), 2),
                q("Which sea is almost entirely surrounded by land and connected to the Atlantic via the Dardanelles/Bosphorus?", opts("Black Sea","Baltic Sea","Red Sea","Caspian Sea"), 0),
                q("Which country has the largest number of volcanoes overall?", opts("Japan","Indonesia","USA","Iceland"), 1),
                q("Which desert is on the coast and influenced by the Benguela Current?", opts("Namib","Atacama","Kalahari","Mojave"), 0),
                q("What is the capital of Kazakhstan (2025)?", opts("Almaty","Astana","Nur-Sultan","Shymkent"), 1),
                q("Which country does NOT border the Caspian Sea?", opts("Kazakhstan","Azerbaijan","Georgia","Turkmenistan"), 2),
                q("Which mountain range forms much of the border between France and Spain?", opts("Alps","Pyrenees","Apennines","Carpathians"), 1),
                q("Which is the saltiest large body of water on Earth?", opts("Dead Sea","Great Salt Lake","Don Juan Pond","Lake Assal"), 2)
        );
    }

    private List<Q> bankExpert() {
        return List.of(
                q("Which country is doubly landlocked?", opts("Liechtenstein","Uzbekistan","Andorra","Bolivia"), 1),
                q("Which pair are true enclaves (entirely within another country)?", opts("Lesotho & San Marino","Lesotho & Vatican City","San Marino & Monaco","Andorra & Vatican City"), 1),
                q("The only country with a flag that is not quadrilateral?", opts("Nepal","Switzerland","Vatican City","Bhutan"), 0),
                q("Which country has the most UNESCO World Heritage Sites (c. mid-2020s)?", opts("Italy","China","India","Spain"), 1),
                q("Which city is furthest south?", opts("Cape Town","Buenos Aires","Melbourne","Auckland"), 0),
                q("Which sovereign state has two exclaves separated by the 'Zangezur corridor' topic?", opts("Azerbaijan","Armenia","Georgia","Turkey"), 0),
                q("Which ocean current contributes to the Atacama’s aridity?", opts("Humboldt (Peru) Current","Kuroshio Current","Gulf Stream","Agulhas Current"), 0),
                q("Which country spans both Europe and Asia and has its capital in Europe?", opts("Turkey","Russia","Kazakhstan","Azerbaijan"), 1),
                q("Which African country is entirely north of the Tropic of Cancer?", opts("Mauritania","Algeria","Mali","Niger"), 1),
                q("Which is the largest landlocked country by area?", opts("Kazakhstan","Mongolia","Chad","Bolivia"), 0),
                q("Which river delta is the largest in the world by area?", opts("Amazon","Ganges-Brahmaputra","Okavango","Niger"), 1)
        );
    }

    private static Q q(String text, List<String> options, int correctIndex) { return new Q(text, options, correctIndex); }
    private static List<String> opts(String... s) { return Arrays.asList(s); }
}
