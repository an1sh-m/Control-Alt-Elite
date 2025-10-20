package brainbrawl.dao;

import brainbrawl.db.Db;
import brainbrawl.model.GameResult;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDaoJdbc implements ResultDao {

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
