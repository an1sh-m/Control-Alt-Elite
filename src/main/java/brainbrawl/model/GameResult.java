package brainbrawl.model;

/**
 * Represents a record of a completed quiz game, including the user's performance.
 * <p>
 * Each result stores the quiz category, difficulty level, score, and timestamp.
 */
public class GameResult {
    private final Long id; // may be null before insert
    private final String category;
    private final int difficulty;
    private final int score;
    private final int total;
    private final int secondsPerQuestion;
    private final String createdAt; // ISO-like "YYYY-MM-DD HH:MM:SS" from SQLite

    /**
     * Constructs a {@code GameResult} object.
     *
     * @param id the database ID (null if not yet saved)
     * @param category the quiz category
     * @param difficulty the difficulty level
     * @param score the player's score
     * @param total the total number of questions
     * @param secondsPerQuestion average time per question
     * @param createdAt timestamp of when the game was completed
     */
    public GameResult(Long id, String category, int difficulty, int score, int total, int secondsPerQuestion, String createdAt) {
        this.id = id;
        this.category = category;
        this.difficulty = difficulty;
        this.score = score;
        this.total = total;
        this.secondsPerQuestion = secondsPerQuestion;
        this.createdAt = createdAt;
    }

    /**
     * Factory method for creating a new unsaved result.
     *
     * @param category quiz category
     * @param difficulty difficulty level
     * @param score score achieved
     * @param total total number of questions
     * @param secondsPerQuestion average seconds per question
     * @return a new {@code GameResult} without an assigned ID or timestamp
     */
    public static GameResult newUnstored(String category, int difficulty, int score, int total, int secondsPerQuestion) {
        return new GameResult(null, category, difficulty, score, total, secondsPerQuestion, null);
    }

    public Long getId() { return id; }
    public String getCategory() { return category; }
    public int getDifficulty() { return difficulty; }
    public int getScore() { return score; }
    public int getTotal() { return total; }
    public int getSecondsPerQuestion() { return secondsPerQuestion; }
    public String getCreatedAt() { return createdAt; }
}
