package brainbrawl.service;

import brainbrawl.dao.QuestionDao;
import brainbrawl.model.Question;
import java.util.List;

/**
 * Provides business logic for managing quiz questions.
 * <p>
 * This service validates question data and delegates persistence
 * operations to the {@link QuestionDao}.
 */
public class QuestionService {
    private final QuestionDao dao;

    /**
     * Constructs a new {@code QuestionService}.
     *
     * @param dao the data access object responsible for question persistence
     */
    public QuestionService(QuestionDao dao) {
        this.dao = dao;
    }

    /**
     * Validates and adds a new question to the database.
     *
     * @param q the question to add
     * @return the generated database ID of the new question
     * @throws IllegalArgumentException if any validation rule fails
     */
    public long addQuestion(Question q) {
        if (q.getCategory() == null || q.getCategory().isBlank())
            throw new IllegalArgumentException("Category required");
        if (q.getText() == null || q.getText().isBlank())
            throw new IllegalArgumentException("Question text required");
        if (q.getDifficulty() < 1 || q.getDifficulty() > 5)
            throw new IllegalArgumentException("Difficulty 1..5");

        switch (q.getType()) {
            case MCQ -> {
                if (q.getOptions() == null || q.getOptions().size() < 2)
                    throw new IllegalArgumentException("At least 2 options");
                if (q.getCorrectIndex() == null || q.getCorrectIndex() < 0 || q.getCorrectIndex() >= q.getOptions().size())
                    throw new IllegalArgumentException("Correct index out of range");
            }
            case SHORT -> {
                // No extra validation needed for short-answer questions
            }
        }

        return dao.create(q);
    }

    /**
     * Retrieves all questions from the database.
     *
     * @return a list of all stored {@link Question} objects
     */
    public List<Question> listAll() { return dao.findAll(); }

    /**
     * Updates an existing question in the database.
     *
     * @param q the question with updated data
     * @return {@code true} if the update succeeded, {@code false} otherwise
     * @throws IllegalArgumentException if the question ID is missing
     */
    public boolean updateQuestion(Question q) {
        if (q.getId() == null) throw new IllegalArgumentException("ID required for update");
        return dao.update(q);
    }

    /**
     * Deletes a question by its database ID.
     *
     * @param id the unique ID of the question to delete
     * @return {@code true} if the deletion succeeded, {@code false} otherwise
     */
    public boolean deleteQuestion(long id) { return dao.deleteById(id); }
}
