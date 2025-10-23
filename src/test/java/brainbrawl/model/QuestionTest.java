package brainbrawl.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link Question} model class.
 * <p>
 * These tests ensure the correctness of utility methods
 * and factory methods within the {@link Question} class,
 * including option encoding and short-answer question creation.
 */
public class QuestionTest {

    /**
     * Tests that the {@link Question#joinOptions(List)} and
     * {@link Question#splitOptions(String)} methods are symmetrical.
     * <p>
     * Verifies that when a list of options is joined into a single
     * string and then split back, the resulting list matches the original.
     */
    @Test
    void joinAndSplitOptionsShouldBeSymmetric() {
        List<String> opts = List.of("A", "B", "C");
        String joined = Question.joinOptions(opts);
        List<String> split = Question.splitOptions(joined);
        assertEquals(opts, split);
    }

    /**
     * Tests that the {@link Question#shortAns(String, String, int)} factory
     * method correctly constructs a short-answer question.
     * <p>
     * Verifies that:
     * <ul>
     *   <li>The question type is set to {@link Question.Type#SHORT}.</li>
     *   <li>No multiple-choice options are attached.</li>
     *   <li>The correct index is {@code null} (not applicable).</li>
     * </ul>
     */
    @Test
    void shortAnsFactoryCreatesShortQuestion() {
        Question q = Question.shortAns("Maths", "2+2?", 2);
        assertEquals(Question.Type.SHORT, q.getType());
        assertNull(q.getOptions());
        assertNull(q.getCorrectIndex());
    }
}
