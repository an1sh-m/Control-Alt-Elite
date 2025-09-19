package brainbrawl.dao;

import brainbrawl.model.Question;
import java.util.List;
import java.util.Optional;

public interface QuestionDao {
    long create(Question q);
    Optional<Question> findById(long id);
    List<Question> findAll();
    // teammate will add: update(Question q), deleteById(long id)

    boolean update(Question q);      // returns true if a row was updated
    boolean deleteById(long id);     // returns true if a row was deleted

}
package brainbrawl.dao;

import brainbrawl.db.Db;
import brainbrawl.model.Question;
import org.junit.jupiter.api.*;
        import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionDaoJdbcTest {
    private QuestionDao dao;

    @BeforeEach
    void setupDb() throws Exception {
        // Override Db.connect() with in-memory DB
        Connection c = DriverManager.getConnection("jdbc:sqlite::memory:");
        try (Statement st = c.createStatement()) {
            st.execute("""
                CREATE TABLE questions (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    category TEXT NOT NULL,
                    text TEXT NOT NULL,
                    type TEXT NOT NULL,
                    options_text TEXT,
                    correct_index INTEGER,
                    difficulty INTEGER NOT NULL
                )
            """);
        }
        DbTestUtil.overrideConnection(c); // Utility method to swap Db.connect()
        dao = new QuestionDaoJdbc();
    }

    // create and findById
    @Test
    void createAndFindByIdShouldWork() {
        Question q = Question.mcq("Maths", "2+2?", List.of("3", "4"), 1, 1);
        long id = dao.create(q);

        var found = dao.findById(id).orElseThrow();
        assertEquals("2+2?", found.getText());
        assertEquals(2, found.getOptions().size());
    }

    // update should change row
    @Test
    void updateShouldModifyExistingRow() {
        Question q = Question.mcq("Geo", "Capital of France?", List.of("Paris", "Rome"), 0, 2);
        long id = dao.create(q);
        q.setId(id);

        Question updated = new Question(id, "Geo", "Capital of Italy?", q.getType(), q.getOptions(), 1, 2);
        boolean success = dao.update(updated);

        assertTrue(success);
        assertEquals("Capital of Italy?", dao.findById(id).get().getText());
    }
}
