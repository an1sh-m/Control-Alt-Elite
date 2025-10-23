package brainbrawl.dao;

import brainbrawl.model.Question;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object (DAO) interface for managing quiz questions.
 * Defines CRUD operations for creating, retrieving, updating, and deleting Question records.
 */
public interface QuestionDao {

    /**
     * Creates a new question in the database.
     *
     * @param q The Question object to insert.
     * @return The generated ID of the inserted question.
     */
    long create(Question q);

    /**
     * Finds a question by its unique ID.
     *
     * @param id The ID of the question to find.
     * @return An Optional containing the Question if found, otherwise empty.
     */
    Optional<Question> findById(long id);

    /**
     * Retrieves all questions from the database.
     *
     * @return A list of all Question objects.
     */
    List<Question> findAll();

    /**
     * Updates an existing question in the database.
     *
     * @param q The Question object with updated values.
     * @return True if the update succeeded, false otherwise.
     */
    boolean update(Question q);

    /**
     * Deletes a question by its unique ID.
     *
     * @param id The ID of the question to delete.
     * @return True if a row was deleted, false otherwise.
     */
    boolean deleteById(long id);
}
