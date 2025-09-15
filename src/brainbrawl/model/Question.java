package brainbrawl.model;

import java.util.Arrays;
import java.util.List;

public class Question {
    public enum Type { MCQ, SHORT }

    private Long id;                  // null when not persisted
    private String category;
    private String text;
    private Type type;
    private List<String> options;     // only used for MCQ
    private Integer correctIndex;     // 0..n-1 (MCQ); null for SHORT
    private int difficulty;           // 1..5 (keep simple)

    public Question(Long id, String category, String text, Type type, List<String> options, Integer correctIndex, int difficulty) {
        this.id = id;
        this.category = category;
        this.text = text;
        this.type = type;
        this.options = options;
        this.correctIndex = correctIndex;
        this.difficulty = difficulty;
    }

    // Convenience ctor for new MCQ
    public static Question mcq(String category, String text, List<String> options, int correctIndex, int difficulty) {
        return new Question(null, category, text, Type.MCQ, options, correctIndex, difficulty);
    }

    // Convenience ctor for new SHORT
    public static Question shortAns(String category, String text, int difficulty) {
        return new Question(null, category, text, Type.SHORT, null, null, difficulty);
    }

    // Simple serialization of options to a single string (no JSON dep)
    public static String joinOptions(List<String> opts) { return String.join("||", opts); }
    public static List<String> splitOptions(String s) {
        return s == null || s.isBlank() ? List.of() : Arrays.asList(s.split("\\|\\|", -1));
    }

    // Getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public String getText() { return text; }
    public Type getType() { return type; }
    public List<String> getOptions() { return options; }
    public Integer getCorrectIndex() { return correctIndex; }
    public int getDifficulty() { return difficulty; }
}
