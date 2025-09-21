package brainbrawl.model;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class QuestionTest {
    // joinOptions and splitOptions symmetry
    @Test
    void joinAndSplitOptionsShouldBeSymmetric() {
        List<String> opts = List.of("A", "B", "C");
        String joined = Question.joinOptions(opts);
        List<String> split = Question.splitOptions(joined);
        assertEquals(opts, split);
    }

    // shortAns factory should create SHORT type without options
    @Test
    void shortAnsFactoryCreatesShortQuestion() {
        Question q = Question.shortAns("Maths", "2+2?", 2);
        assertEquals(Question.Type.SHORT, q.getType());
        assertNull(q.getOptions());
        assertNull(q.getCorrectIndex());
    }
}
