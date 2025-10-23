package brainbrawl.db;

import java.sql.*;

/**
 * Utility class for managing the SQLite database connection and initialization.
 * <p>
 * This class handles:
 * <ul>
 *   <li>Connecting to the SQLite database via JDBC</li>
 *   <li>Creating necessary tables for questions and results if they do not exist</li>
 * </ul>
 */
public class Db {
    /** The database URL for SQLite. */
    private static final String URL = "jdbc:sqlite:brainbrawl.db";

    /**
     * Opens a connection to the SQLite database.
     *
     * @return a {@link Connection} object to the database
     * @throws SQLException if a connection cannot be established
     */
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    /**
     * Initializes the database by creating required tables if they do not already exist.
     * <p>
     * Tables created:
     * <ul>
     *   <li><b>questions</b> – stores quiz questions</li>
     *   <li><b>results</b> – stores player quiz results</li>
     * </ul>
     */
    public static void init() {
        String createQuestions = """
            CREATE TABLE IF NOT EXISTS questions (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              category TEXT NOT NULL,
              text TEXT NOT NULL,
              type TEXT NOT NULL,           -- MCQ / SHORT
              options_text TEXT,            -- "||" delimited for MCQ
              correct_index INTEGER,        -- null for SHORT
              difficulty INTEGER NOT NULL DEFAULT 1
            );
        """;

        String createResults = """
            CREATE TABLE IF NOT EXISTS results (
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              category TEXT NOT NULL,
              difficulty INTEGER NOT NULL,
              score INTEGER NOT NULL,
              total INTEGER NOT NULL,
              seconds_per_question INTEGER NOT NULL,
              created_at TEXT NOT NULL DEFAULT (datetime('now'))
            );
        """;

        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.execute(createQuestions);
            st.execute(createResults);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to init DB", e);
        }
    }
}
