package brainbrawl.service;

import brainbrawl.dao.QuestionDao;
import brainbrawl.model.Question;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the {@link QuestionService} class.
 * <p>
 * These tests verify that the service correctly validates
 * question data and interacts properly with the {@link QuestionDao}.
 * Mock objects are used to isolate service-layer logic from
 * the persistence layer.
 */
public class QuestionServiceTest {

    /**
     * Ensures that {@link QuestionService#addQuestion(Question)} rejects
     * invalid questions with an empty category.
     * <p>
     * Verifies that:
     * <ul>
     *   <li>An {@link IllegalArgumentException} is thrown.</li>
     *   <li>No call is made to {@link QuestionDao#create(Question)}.</li>
     * </ul>
     */
    @Test
    void addQuestionShouldRejectEmptyCategory() {
        QuestionDao mockDao = mock(QuestionDao.class);
        QuestionService svc = new QuestionService(mockDao);

        Question invalid = Question.mcq("", "Q?", List.of("A", "B"), 0, 1);

        assertThrows(IllegalArgumentException.class, () -> svc.addQuestion(invalid));
        verify(mockDao, never()).create(any());
    }

    /**
     * Verifies that {@link QuestionService#addQuestion(Question)} correctly
     * delegates to the {@link QuestionDao} when the question data is valid.
     * <p>
     * Confirms that:
     * <ul>
     *   <li>The DAO's {@link QuestionDao#create(Question)} method is called once.</li>
     *   <li>The returned ID matches the mocked value.</li>
     * </ul>
     */
    @Test
    void addQuestionShouldDelegateToDao() {
        QuestionDao mockDao = mock(QuestionDao.class);
        QuestionService svc = new QuestionService(mockDao);

        Question valid = Question.mcq("Maths", "1+1?", List.of("2", "3"), 0, 1);
        when(mockDao.create(valid)).thenReturn(42L);

        long id = svc.addQuestion(valid);

        assertEquals(42L, id);
        verify(mockDao).create(valid);
    }
}
