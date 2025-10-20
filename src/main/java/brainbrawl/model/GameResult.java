package brainbrawl.model;

public class GameResult {
    private final Long id; // may be null before insert
    private final String category;
    private final int difficulty;
    private final int score;
    private final int total;
    private final int secondsPerQuestion;
    private final String createdAt; // ISO-like "YYYY-MM-DD HH:MM:SS" from SQLite

    public GameResult(Long id, String category, int difficulty, int score, int total, int secondsPerQuestion, String createdAt) {
        this.id = id;
        this.category = category;
        this.difficulty = difficulty;
        this.score = score;
        this.total = total;
        this.secondsPerQuestion = secondsPerQuestion;
        this.createdAt = createdAt;
    }

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
