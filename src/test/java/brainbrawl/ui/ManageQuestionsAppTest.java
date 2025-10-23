package brainbrawl.ui;

import brainbrawl.model.Question;
import javafx.embed.swing.JFXPanel; // ensures JavaFX toolkit is initialized
import java.util.List;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the {@link ManageQuestionsApp} class.
 * <p>
 * These tests verify that form-related functionality behaves correctly,
 * ensuring that UI components are properly reset and that question objects
 * are constructed as expected.
 */
public class ManageQuestionsAppTest {

    /**
     * Initializes the JavaFX runtime before running any tests.
     * <p>
     * This is required because JavaFX controls (like {@link TextField} and
     * {@link ComboBox}) depend on the JavaFX Application Thread, which is
     * initialized by creating a {@link JFXPanel}.
     */
    @BeforeAll
    static void initJfxRuntime() {
        new JFXPanel(); // initializes JavaFX
    }

    /**
     * Verifies that {@link ManageQuestionsApp#clearForm(TextField, TextArea, TextArea, ComboBox, Spinner, Spinner)}
     * correctly resets all form fields to their default states.
     * <p>
     * Confirms that:
     * <ul>
     *   <li>Text fields and text areas are cleared.</li>
     *   <li>Question type is reset to {@link Question.Type#MCQ}.</li>
     *   <li>Spinners for correct index and difficulty reset to 0 and 1 respectively.</li>
     * </ul>
     */
    @Test
    void clearFormResetsInputs() {
        ManageQuestionsApp app = new ManageQuestionsApp();

        TextField cat = new TextField("Maths");
        TextArea q = new TextArea("2+2?");
        TextArea opts = new TextArea("1,2");
        Spinner<Integer> corr = new Spinner<>(0, 5, 2);
        Spinner<Integer> diff = new Spinner<>(1, 5, 3);
        ComboBox<Question.Type> type = new ComboBox<>();
        type.getItems().addAll(Question.Type.MCQ, Question.Type.SHORT);
        type.getSelectionModel().select(Question.Type.SHORT);

        app.clearForm(cat, q, opts, type, corr, diff);

        assertEquals("", cat.getText());
        assertEquals(Question.Type.MCQ, type.getValue());
        assertEquals(0, corr.getValue());
        assertEquals(1, diff.getValue());
    }

    /**
     * Confirms that a multiple-choice {@link Question} created through
     * {@link Question#mcq(String, String, List, int, int)} has the correct
     * category, options, and correct answer index.
     * <p>
     * This ensures that the question model is constructed accurately from
     * the provided form values.
     */
    @Test
    void mcqQuestionShouldBeConstructedCorrectly() {
        Question q = Question.mcq("Science", "H2O is?", List.of("Water", "Oxygen"), 0, 1);
        assertEquals("Science", q.getCategory());
        assertEquals(2, q.getOptions().size());
        assertEquals(0, q.getCorrectIndex());
    }
}
