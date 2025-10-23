package brainbrawl.dao;

import brainbrawl.db.Db;
import brainbrawl.model.Question;
import brainbrawl.model.Question.Type;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * JDBC implementation of the QuestionDao interface.
 * Handles database operations related to quiz questions using SQLite.
 */
public class QuestionDaoJdbc implements QuestionDao {

    /**
     * Inserts a new Question into the database.
     *
     * @param q The Question object to insert.
     * @return The generated ID of the new question.
     * @throws RuntimeException if an SQL error occurs.
     */
    @Override
    public long create(Question q) {
        String sql = """
            INSERT INTO questions(category,text,type,options_text,correct_index,difficulty)
            VALUES(?,?,?,?,?,?)
        """;
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, q.getCategory());
            ps.setString(2, q.getText());
            ps.setString(3, q.getType().name());
            ps.setString(4, q.getOptions() == null ? null : Question.joinOptions(q.getOptions()));
            if (q.getCorrectIndex() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, q.getCorrectIndex());
            ps.setInt(6, q.getDifficulty());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getLong(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Create failed", e);
        }
    }

    /**
     * Finds a Question by its ID.
     *
     * @param id The unique ID of the question.
     * @return An Optional containing the Question if found, otherwise empty.
     */
    @Override
    public Optional<Question> findById(long id) {
        String sql = "SELECT * FROM questions WHERE id = ?";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                return Optional.of(mapRow(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed", e);
        }
    }

    /**
     * Retrieves all Questions in the database, ordered by ID descending.
     *
     * @return A list of all Questions.
     */
    @Override
    public List<Question> findAll() {
        String sql = "SELECT * FROM questions ORDER BY id DESC";
        List<Question> out = new ArrayList<>();
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(mapRow(rs));
            return out;
        } catch (SQLException e) {
            throw new RuntimeException("findAll failed", e);
        }
    }

    /**
     * Updates an existing Question record in the database.
     *
     * @param q The Question object containing new values.
     * @return True if the update succeeded, false otherwise.
     */
    @Override
    public boolean update(Question q) {
        if (q.getId() == null) throw new IllegalArgumentException("ID required for update");
        String sql = """
        UPDATE questions
        SET category=?, text=?, type=?, options_text=?, correct_index=?, difficulty=?
        WHERE id=?
    """;
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, q.getCategory());
            ps.setString(2, q.getText());
            ps.setString(3, q.getType().name());
            ps.setString(4, q.getOptions() == null ? null : Question.joinOptions(q.getOptions()));
            if (q.getCorrectIndex() == null) ps.setNull(5, Types.INTEGER); else ps.setInt(5, q.getCorrectIndex());
            ps.setInt(6, q.getDifficulty());
            ps.setLong(7, q.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Update failed", e);
        }
    }

    /**
     * Deletes a Question from the database by its ID.
     *
     * @param id The ID of the question to delete.
     * @return True if a row was deleted, false otherwise.
     */
    @Override
    public boolean deleteById(long id) {
        String sql = "DELETE FROM questions WHERE id=?";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new RuntimeException("Delete failed", e);
        }
    }

    /**
     * Maps a single row of the ResultSet to a Question object.
     *
     * @param rs The ResultSet to read from.
     * @return A Question object populated with data from the row.
     * @throws SQLException if column reading fails.
     */
    private Question mapRow(ResultSet rs) throws SQLException {
        Long id = rs.getLong("id");
        String category = rs.getString("category");
        String text = rs.getString("text");
        Type type = Type.valueOf(rs.getString("type"));
        String optText = rs.getString("options_text");
        Integer correct = rs.getObject("correct_index") == null ? null : rs.getInt("correct_index");
        int difficulty = rs.getInt("difficulty");
        return new Question(id, category, text, type, Question.splitOptions(optText), correct, difficulty);
    }
}