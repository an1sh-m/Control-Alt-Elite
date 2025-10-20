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

public class ManageQuestionsAppTest {
    @BeforeAll
    static void initJfxRuntime() {
        new JFXPanel(); // initializes JavaFX
    }

    // clearForm should reset fields
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

    // creating MCQ question via form values
    @Test
    void mcqQuestionShouldBeConstructedCorrectly() {
        Question q = Question.mcq("Science", "H2O is?", List.of("Water", "Oxygen"), 0, 1);
        assertEquals("Science", q.getCategory());
        assertEquals(2, q.getOptions().size());
        assertEquals(0, q.getCorrectIndex());
    }
}
