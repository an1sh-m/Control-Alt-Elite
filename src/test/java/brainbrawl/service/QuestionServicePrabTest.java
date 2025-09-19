package brainbrawl.service;

import brainbrawl.dao.QuestionDao;
import brainbrawl.model.Question;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class QuestionServicePrabTest {
    @Test
    void addQuestionShouldRejectEmptyCategory() {
        QuestionDao mockDao = mock(QuestionDao.class);
        QuestionService svc = new QuestionService(mockDao);

        Question invalid = Question.mcq("", "Q?", List.of("A", "B"), 0, 1);

        assertThrows(IllegalArgumentException.class, () -> svc.addQuestion(invalid));
        verify(mockDao, never()).create(any());
    }

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
