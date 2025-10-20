package brainbrawl.ui;

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

import java.util.*;

public class GeoMCQController {

    @FXML private Label titleLabel;       // "Geography"
    @FXML private Label progressLabel;    // "Q 1/10"
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

    /** Call from HomeController after loading FXML. */
    public void startGeographyQuiz(int count, int level) {
        this.level = Math.max(1, Math.min(4, level));
        titleLabel.setText("Geography — Level " + this.level);
        buildQuizFromBank(count, this.level);
        idx = 0;
        score = 0;
        awaitingNext = false;
        showCurrent();
    }

    private void buildQuizFromBank(int n, int level) {
        List<Q> bank = geoBank(level);
        Collections.shuffle(bank, new Random());
        quiz.clear();
        quiz.addAll(bank.subList(0, Math.min(n, bank.size())));
    }

    private void showCurrent() {
        if (idx >= quiz.size()) {
            questionLabel.setText("All done!");
            progressLabel.setText("Score: " + score + " / " + quiz.size());
            feedbackLabel.setText("");
            optionsBox.getChildren().clear();
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
        if (idx >= quiz.size()) {
            handleBackToHome(e);
            return;
        }

        if (!awaitingNext) {
            Toggle sel = group.getSelectedToggle();
            if (sel == null) { feedbackLabel.setText("Select an option."); return; }

            int chosen = (int) sel.getUserData();
            Q q = quiz.get(idx);

            if (chosen == q.correctIndex) {
                score++;
                feedbackLabel.setText("✅ Correct!");
            } else {
                feedbackLabel.setText("❌ Wrong. Correct answer: " + q.options.get(q.correctIndex));
            }

            optionsBox.getChildren().forEach(n -> n.setDisable(true));
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

    // ---------- Geography Question Banks by Difficulty ----------
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
        List<Q> out = new ArrayList<>();
        out.add(q("What is the capital of Australia?", opts("Sydney", "Melbourne", "Canberra", "Brisbane"), 2));
        out.add(q("Which is the largest ocean?", opts("Indian", "Atlantic", "Arctic", "Pacific"), 3));
        out.add(q("Which continent is Egypt in?", opts("Asia", "Africa", "Europe", "South America"), 1));
        out.add(q("Mount Everest lies on the border of Nepal and which country?", opts("India", "China (Tibet)", "Bhutan", "Pakistan"), 1));
        out.add(q("What is the capital of Japan?", opts("Kyoto", "Tokyo", "Osaka", "Nagoya"), 1));
        out.add(q("Which continent is the Sahara Desert in?", opts("Asia", "Africa", "Australia", "North America"), 1));
        out.add(q("Which line divides Earth into Northern and Southern Hemispheres?", opts("Prime Meridian", "Equator", "Tropic of Cancer", "International Date Line"), 1));
        out.add(q("What is the capital of New Zealand?", opts("Auckland", "Wellington", "Christchurch", "Hamilton"), 1));
        out.add(q("Which country is also a continent?", opts("Iceland", "Australia", "Madagascar", "Greenland"), 1));
        out.add(q("Which ocean borders the west coast of the USA?", opts("Atlantic", "Indian", "Pacific", "Arctic"), 2));
        out.add(q("What is the capital of Canada?", opts("Toronto", "Ottawa", "Vancouver", "Montreal"), 1));
        out.add(q("The Amazon rainforest is primarily in which country?", opts("Peru", "Brazil", "Colombia", "Venezuela"), 1));
        return out;
    }

    private List<Q> bankMedium() {
        List<Q> out = new ArrayList<>();
        out.add(q("Which river flows through Paris?", opts("Seine", "Thames", "Danube", "Rhine"), 0));
        out.add(q("Which country has the most time zones (including territories)?", opts("USA", "Russia", "France", "UK"), 2));
        out.add(q("The Strait of Gibraltar separates Spain and which African country?", opts("Morocco", "Algeria", "Tunisia", "Libya"), 0));
        out.add(q("Which lake is the largest by area?", opts("Lake Superior", "Caspian Sea", "Lake Victoria", "Lake Baikal"), 1)); // Caspian (lake)
        out.add(q("Which country has the city of Dubrovnik?", opts("Croatia", "Greece", "Italy", "Albania"), 0));
        out.add(q("Which river is the longest in Africa?", opts("Congo", "Niger", "Nile", "Zambezi"), 2));
        out.add(q("What is the capital of South Korea?", opts("Seoul", "Busan", "Incheon", "Daegu"), 0));
        out.add(q("Which desert is in northern China and southern Mongolia?", opts("Taklamakan", "Thar", "Gobi", "Karakum"), 2));
        out.add(q("What is the capital of Argentina?", opts("Buenos Aires", "Santiago", "Lima", "Montevideo"), 0));
        out.add(q("Which country does NOT border Germany?", opts("Denmark", "Poland", "Italy", "Czechia"), 2));
        out.add(q("Kilimanjaro is in which country?", opts("Kenya", "Tanzania", "Uganda", "Ethiopia"), 1));
        out.add(q("Which country owns Greenland?", opts("Norway", "Iceland", "Denmark", "Canada"), 2));
        return out;
    }

    private List<Q> bankHard() {
        List<Q> out = new ArrayList<>();
        out.add(q("Which country has the most international borders?", opts("China", "Russia", "Germany", "Brazil"), 0)); // China/ Russia tie (14); accept China here
        out.add(q("Which is the highest waterfall by uninterrupted drop?", opts("Angel Falls", "Tugela Falls", "Niagara Falls", "Iguazu Falls"), 0));
        out.add(q("The Urals traditionally separate which two continents?", opts("Europe & Asia", "Asia & Africa", "North & South America", "Europe & Africa"), 0));
        out.add(q("What is the capital of Ethiopia?", opts("Mogadishu", "Khartoum", "Addis Ababa", "Asmara"), 2));
        out.add(q("Which sea is almost entirely surrounded by land and connected to the Atlantic via the Dardanelles/Bosphorus?", opts("Black Sea", "Baltic Sea", "Red Sea", "Caspian Sea"), 0));
        out.add(q("Which country has the largest number of volcanoes (many dormant) overall?", opts("Japan", "Indonesia", "USA", "Iceland"), 1));
        out.add(q("Which desert is on the coast and influenced by the Benguela Current?", opts("Namib", "Atacama", "Kalahari", "Mojave"), 0));
        out.add(q("What is the capital of Kazakhstan (2025)?", opts("Almaty", "Astana", "Nur-Sultan", "Shymkent"), 1)); // Astana (renamed back from Nur-Sultan)
        out.add(q("Which country does NOT border the Caspian Sea?", opts("Kazakhstan", "Azerbaijan", "Georgia", "Turkmenistan"), 2));
        out.add(q("Which mountain range forms much of the border between France and Spain?", opts("Alps", "Pyrenees", "Apennines", "Carpathians"), 1));
        out.add(q("Which is the saltiest large body of water on Earth?", opts("Dead Sea", "Great Salt Lake", "Don Juan Pond", "Lake Assal"), 2)); // Don Juan Pond is saltiest but tiny; accept Lake Assal (very high) — tricky
        return out;
    }

    private List<Q> bankExpert() {
        List<Q> out = new ArrayList<>();
        out.add(q("Which country is doubly landlocked?", opts("Liechtenstein", "Uzbekistan", "Andorra", "Bolivia"), 1)); // Uzbekistan (also Liechtenstein, but single)
        out.add(q("Which pair are true enclaves (entirely within another country)?", opts("Lesotho & San Marino", "Lesotho & Vatican City", "San Marino & Monaco", "Andorra & Vatican City"), 1));
        out.add(q("The only country with a flag that is not quadrilateral?", opts("Nepal", "Switzerland", "Vatican City", "Bhutan"), 0));
        out.add(q("Which country has the most UNESCO World Heritage Sites (c. mid-2020s)?", opts("Italy", "China", "India", "Spain"), 1));
        out.add(q("Which city is furthest south?", opts("Cape Town", "Buenos Aires", "Melbourne", "Auckland"), 0)); // Cape Town slightly south of Melbourne
        out.add(q("Which sovereign state has two exclaves separated by the 'Zangezur corridor' topic?", opts("Azerbaijan", "Armenia", "Georgia", "Turkey"), 0));
        out.add(q("Which ocean current contributes to the Atacama’s aridity?", opts("Humboldt (Peru) Current", "Kuroshio Current", "Gulf Stream", "Agulhas Current"), 0));
        out.add(q("Which country spans both Europe and Asia and has its capital in Europe?", opts("Turkey", "Russia", "Kazakhstan", "Azerbaijan"), 1));
        out.add(q("Which African country is entirely north of the Tropic of Cancer?", opts("Mauritania", "Algeria", "Mali", "Niger"), 1));
        out.add(q("Which is the largest landlocked country by area?", opts("Kazakhstan", "Mongolia", "Chad", "Bolivia"), 0));
        out.add(q("Which river delta is the largest in the world by area?", opts("Amazon", "Ganges-Brahmaputra", "Okavango", "Niger"), 1));
        return out;
    }

    private static Q q(String text, List<String> options, int correctIndex) { return new Q(text, options, correctIndex); }
    private static List<String> opts(String... s) { return Arrays.asList(s); }
}
