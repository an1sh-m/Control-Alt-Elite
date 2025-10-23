package brainbrawl.dao;

import brainbrawl.db.Db;
import brainbrawl.model.GameResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of the ResultDao interface.
 * Handles all SQL operations for game results using SQLite.
 */
public class ResultDaoJdbc implements ResultDao {

    /**
     * Inserts a new game result into the database.
     *
     * @param r The GameResult to insert.
     * @return The generated ID of the inserted record.
     */
    @Override
    public long create(GameResult r) {
        String sql = """
            INSERT INTO results(category, difficulty, score, total, seconds_per_question)
            VALUES(?,?,?,?,?)
        """;
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, r.getCategory());
            ps.setInt(2, r.getDifficulty());
            ps.setInt(3, r.getScore());
            ps.setInt(4, r.getTotal());
            ps.setInt(5, r.getSecondsPerQuestion());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) return keys.getLong(1);
                throw new SQLException("No generated key");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Create result failed", e);
        }
    }

    /**
     * Retrieves the most recent game results from the database.
     *
     * @param limit The maximum number of results to retrieve.
     * @return A list of recent GameResult objects.
     */
    @Override
    public List<GameResult> findRecent(int limit) {
        String sql = "SELECT * FROM results ORDER BY datetime(created_at) DESC, id DESC LIMIT ?";
        List<GameResult> out = new ArrayList<>();
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findRecent failed", e);
        }
        return out;
    }

    /**
     * Maps a ResultSet row to a GameResult object.
     *
     * @param rs The ResultSet containing data from the database.
     * @return A populated GameResult object.
     * @throws SQLException if column extraction fails.
     */
    private GameResult mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String category = rs.getString("category");
        int difficulty = rs.getInt("difficulty");
        int score = rs.getInt("score");
        int total = rs.getInt("total");
        int spq = rs.getInt("seconds_per_question");
        String created = rs.getString("created_at");
        return new GameResult(id, category, difficulty, score, total, spq, created);
    }
}
