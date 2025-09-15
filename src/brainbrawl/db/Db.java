package brainbrawl.db;

import java.sql.*;

public class Db {
    private static final String URL = "jdbc:sqlite:brainbrawl.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

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
        try (Connection c = connect(); Statement st = c.createStatement()) {
            st.execute(createQuestions);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to init DB", e);
        }
    }
}