package brainbrawl.model;

import java.util.Arrays;
import java.util.List;

/**
 * Represents a quiz question, which may be a multiple-choice (MCQ) or short-answer question.
 * <p>
 * Each question includes its category, text, type, difficulty, and possible options.
 */
public class Question {
    /** Enum representing the type of question. */
    public enum Type { MCQ, SHORT }

    private Long id;                  // null when not persisted
    private String category;
    private String text;
    private Type type;
    private List<String> options;     // only used for MCQ
    private Integer correctIndex;     // 0..n-1 (MCQ); null for SHORT
    private int difficulty;           // 1..5 (keep simple)

    /**
     * Constructs a new {@code Question}.
     *
     * @param id the database ID (null if not yet saved)
     * @param category the category of the question
     * @param text the question text
     * @param type the type (MCQ or SHORT)
     * @param options the list of possible answers for MCQs
     * @param correctIndex the index of the correct answer (for MCQs)
     * @param difficulty the difficulty level of the question
     */
    public Question(Long id, String category, String text, Type type, List<String> options, Integer correctIndex, int difficulty) {
        this.id = id;
        this.category = category;
        this.text = text;
        this.type = type;
        this.options = options;
        this.correctIndex = correctIndex;
        this.difficulty = difficulty;
    }

    /**
     * Creates a new multiple-choice question.
     */
    public static Question mcq(String category, String text, List<String> options, int correctIndex, int difficulty) {
        return new Question(null, category, text, Type.MCQ, options, correctIndex, difficulty);
    }

    /**
     * Creates a new short-answer question.
     */
    public static Question shortAns(String category, String text, int difficulty) {
        return new Question(null, category, text, Type.SHORT, null, null, difficulty);
    }

    /**
     * Joins multiple options into a single delimited string (for database storage).
     */
    public static String joinOptions(List<String> opts) { return String.join("||", opts); }

    /**
     * Splits a delimited string back into a list of options.
     */
    public static List<String> splitOptions(String s) {
        return s == null || s.isBlank() ? List.of() : Arrays.asList(s.split("\\|\\|", -1));
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCategory() { return category; }
    public String getText() { return text; }
    public Type getType() { return type; }
    public List<String> getOptions() { return options; }
    public Integer getCorrectIndex() { return correctIndex; }
    public int getDifficulty() { return difficulty; }
}
