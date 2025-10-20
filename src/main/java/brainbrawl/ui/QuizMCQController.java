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

public class QuizMCQController {

    @FXML private Label titleLabel;       // "General Knowledge"
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

    /** Call this from HomeController after loading the FXML. */
    public void startGeneralQuiz(int count, int level) {
        this.level = Math.max(1, Math.min(4, level));
        titleLabel.setText("General Knowledge — Level " + this.level);
        buildQuizFromBank(count, this.level);
        idx = 0;
        score = 0;
        awaitingNext = false;
        showCurrent();
    }

    private void buildQuizFromBank(int n, int level) {
        List<Q> bank = generalBank(level);
        Collections.shuffle(bank, new Random());
        quiz.clear();
        quiz.addAll(bank.subList(0, Math.min(n, bank.size())));
    }

    private void showCurrent() {
        if (idx >= quiz.size()) {
            // finished
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
            rb.setUserData(i); // store index for easy retrieval
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
            // evaluate
            Toggle sel = group.getSelectedToggle();
            if (sel == null) {
                feedbackLabel.setText("Select an option.");
                return;
            }
            int chosen = (int) sel.getUserData();
            Q q = quiz.get(idx);

            if (chosen == q.correctIndex) {
                score++;
                feedbackLabel.setText("✅ Correct!");
            } else {
                String correctText = q.options.get(q.correctIndex);
                feedbackLabel.setText("❌ Wrong. Correct answer: " + correctText);
            }

            // lock options
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

    // ---------- General Knowledge Question Banks by Difficulty ----------
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
        List<Q> out = new ArrayList<>();
        out.add(q("What is the capital of Japan?", opts("Kyoto", "Tokyo", "Osaka", "Sapporo"), 1));
        out.add(q("Which planet is known as the Red Planet?", opts("Mars", "Jupiter", "Venus", "Mercury"), 0));
        out.add(q("Which ocean is the largest by surface area?", opts("Indian", "Arctic", "Atlantic", "Pacific"), 3));
        out.add(q("What is the smallest prime number?", opts("0", "1", "2", "3"), 2));
        out.add(q("Which city is nicknamed 'The Big Apple'?", opts("Los Angeles", "Chicago", "New York City", "San Francisco"), 2));
        out.add(q("Water boils at what temperature (°C) at sea level?", opts("90", "95", "100", "105"), 2));
        out.add(q("CPU stands for…", opts("Central Processing Unit", "Computer Performance Utility", "Core Processing Unit", "Central Performance Unit"), 0));
        out.add(q("Which animal is the tallest?", opts("Elephant", "Giraffe", "Ostrich", "Polar Bear"), 1));
        out.add(q("How many continents are there?", opts("5", "6", "7", "8"), 2));
        out.add(q("What is the largest mammal?", opts("African Elephant", "Blue Whale", "Hippopotamus", "Giraffe"), 1));
        out.add(q("How many sides does a square have?", opts("3", "4", "5", "6"), 1));
        out.add(q("The currency of the United States is the…", opts("Euro", "US Dollar", "Pound", "Yen"), 1));
        out.add(q("Which animal is famous in China and black-and-white?", opts("Giant panda", "Raccoon", "Skunk", "Zebra"), 0));
        out.add(q("The chemical formula for water is…", opts("HO", "H2O", "OH2", "H2O2"), 1));
        return out;
    }

    private List<Q> bankMedium() {
        List<Q> out = new ArrayList<>();
        out.add(q("Who wrote '1984'?", opts("George Orwell", "Aldous Huxley", "J.K. Rowling", "Ernest Hemingway"), 0));
        out.add(q("Who was the first person to walk on the Moon?", opts("Buzz Aldrin", "Yuri Gagarin", "Neil Armstrong", "Michael Collins"), 2));
        out.add(q("Which element has atomic number 1?", opts("Helium", "Hydrogen", "Oxygen", "Lithium"), 1));
        out.add(q("Which river flows through Egypt?", opts("Amazon", "Nile", "Danube", "Rhine"), 1));
        out.add(q("What language is primarily spoken in Brazil?", opts("Spanish", "Portuguese", "French", "English"), 1));
        out.add(q("What is the fastest land animal?", opts("Lion", "Cheetah", "Pronghorn", "Greyhound"), 1));
        out.add(q("What is the hardest natural substance?", opts("Diamond", "Quartz", "Sapphire", "Obsidian"), 0));
        out.add(q("Which is the largest hot desert?", opts("Gobi", "Sahara", "Kalahari", "Arabian"), 1));
        out.add(q("Capital of Canada?", opts("Toronto", "Vancouver", "Ottawa", "Montreal"), 2));
        out.add(q("Which country hosted the 2016 Summer Olympics?", opts("China", "Brazil", "UK", "Russia"), 1));
        out.add(q("Which instrument has keys, pedals, and strings?", opts("Guitar", "Piano", "Violin", "Flute"), 1));
        out.add(q("Which metal is liquid at room temperature?", opts("Mercury", "Aluminium", "Lead", "Zinc"), 0));
        out.add(q("Which organ pumps blood through the body?", opts("Lungs", "Heart", "Liver", "Kidneys"), 1));
        out.add(q("How many degrees are in a right angle?", opts("30", "45", "60", "90"), 3));
        return out;
    }

    private List<Q> bankHard() {
        List<Q> out = new ArrayList<>();
        out.add(q("World War II began in which year?", opts("1914", "1918", "1939", "1945"), 2));
        out.add(q("Who painted 'The Starry Night'?", opts("Claude Monet", "Pablo Picasso", "Vincent van Gogh", "Salvador Dalí"), 2));
        out.add(q("The currency of Japan is the…", opts("Won", "Yuan", "Yen", "Ringgit"), 2));
        out.add(q("Which island is the largest in the world (not a continent)?", opts("Borneo", "New Guinea", "Great Britain", "Greenland"), 3));
        out.add(q("SI unit of electrical resistance is the…", opts("Volt", "Ampere", "Ohm", "Watt"), 2));
        out.add(q("Chemical formula for table salt is…", opts("NaCl", "KCl", "Na2CO3", "CaCl2"), 0));
        out.add(q("Tallest mountain above sea level?", opts("K2", "Everest", "Kangchenjunga", "Lhotse"), 1));
        out.add(q("Planet famous for its rings?", opts("Jupiter", "Saturn", "Uranus", "Neptune"), 1));
        out.add(q("DNA has the shape of a…", opts("Single helix", "Double helix", "Beta sheet", "Cube"), 1));
        out.add(q("Approximate speed of light in vacuum?", opts("3,000 km/s", "30,000 km/s", "300,000 km/s", "3,000,000 km/s"), 2));
        out.add(q("Who proposed the general theory of relativity?", opts("Max Planck", "Niels Bohr", "Albert Einstein", "Erwin Schrödinger"), 2));
        out.add(q("Which continent has the most countries?", opts("Europe", "Asia", "Africa", "South America"), 2));
        return out;
    }

    private List<Q> bankExpert() {
        List<Q> out = new ArrayList<>();
        out.add(q("Capital of Kazakhstan (2025)?", opts("Almaty", "Astana", "Nur-Sultan", "Shymkent"), 1));
        out.add(q("Who discovered penicillin?", opts("Alexander Fleming", "Louis Pasteur", "Edward Jenner", "Robert Koch"), 0));
        out.add(q("First woman to win a Nobel Prize?", opts("Marie Curie", "Rosalind Franklin", "Ada Lovelace", "Lise Meitner"), 0));
        out.add(q("Rarest common ABO/Rh blood type globally?", opts("O−", "AB−", "A−", "B−"), 1));
        out.add(q("Finnish belongs to which language family?", opts("Indo-European", "Uralic", "Altaic", "Afroasiatic"), 1));
        out.add(q("Largest moon in the Solar System?", opts("Titan", "Ganymede", "Callisto", "Europa"), 1));
        out.add(q("Who proved Fermat’s Last Theorem (1990s)?", opts("Terence Tao", "Andrew Wiles", "Grigori Perelman", "Maryam Mirzakhani"), 1));
        out.add(q("Element named after a dwarf planet?", opts("Uranium", "Neptunium", "Plutonium", "Mercury"), 2));
        out.add(q("Composer of 'The Four Seasons'?", opts("Bach", "Vivaldi", "Mozart", "Handel"), 1));
        out.add(q("Timbuktu is in which country?", opts("Niger", "Mali", "Chad", "Burkina Faso"), 1));
        out.add(q("Heisenberg’s uncertainty principle is in…", opts("Thermodynamics", "Relativity", "Quantum mechanics", "Classical mechanics"), 2));
        out.add(q("Which country has the most UNESCO World Heritage Sites (c. mid-2020s)?", opts("Italy", "China", "India", "Spain"), 1)); // China edges Italy recently
        return out;
    }

    private static Q q(String text, List<String> options, int correctIndex) { return new Q(text, options, correctIndex); }
    private static List<String> opts(String... s) { return Arrays.asList(s); }
}
