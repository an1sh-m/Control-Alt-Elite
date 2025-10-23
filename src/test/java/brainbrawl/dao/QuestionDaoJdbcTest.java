package brainbrawl.dao;

import brainbrawl.db.Db;
import brainbrawl.model.Question;
import org.junit.jupiter.api.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link QuestionDaoJdbc} class.
 * <p>
 * This test suite verifies the correct behavior of CRUD operations
 * for the questions table using an in-memory SQLite database.
 * The {@link DbTestUtil} utility is used to override the default
 * database connection during testing.
 */
public class QuestionDaoJdbcTest {

    /** Data access object under test. */
    private QuestionDao dao;

    /**
     * Sets up a fresh in-memory SQLite database before each test.
     * <p>
     * The schema for the {@code questions} table is created, and
     * {@link DbTestUtil#overrideConnection(Connection)} is used to
     * ensure all DAO operations use this temporary test database.
     *
     * @throws Exception if a database setup error occurs
     */
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

    /**
     * Tests that a question can be successfully created and retrieved by ID.
     * <p>
     * Verifies that:
     * <ul>
     *   <li>The {@link QuestionDaoJdbc#create(Question)} method inserts correctly.</li>
     *   <li>The retrieved question matches the original values.</li>
     * </ul>
     */
    @Test
    void createAndFindByIdShouldWork() {
        Question q = Question.mcq("Maths", "2+2?", List.of("3", "4"), 1, 1);
        long id = dao.create(q);

        var found = dao.findById(id).orElseThrow();
        assertEquals("2+2?", found.getText());
        assertEquals(2, found.getOptions().size());
    }

    /**
     * Tests that an existing question can be updated correctly in the database.
     * <p>
     * Ensures that:
     * <ul>
     *   <li>The {@link QuestionDaoJdbc#update(Question)} method modifies the correct row.</li>
     *   <li>Updated fields (such as text) persist correctly.</li>
     * </ul>
     */
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

