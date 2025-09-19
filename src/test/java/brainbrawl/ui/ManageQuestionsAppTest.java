package brainbrawl.ui;

import brainbrawl.model.Question;
import javafx.embed.swing.JFXPanel; // ensures JavaFX toolkit is initialized
import java.util.List;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
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
        TextField q = new TextField("2+2?");
        TextField opts = new TextField("1,2");
        Spinner<Integer> corr = new Spinner<>(0, 5, 2);
        Spinner<Integer> diff = new Spinner<>(1, 5, 3);
        ComboBox<String> type = new ComboBox<>();
        type.getItems().addAll("MCQ","SHORT");
        type.getSelectionModel().select("SHORT");

        app.clearForm(cat, q, opts, corr, diff, type);

        assertEquals("", cat.getText());
        assertEquals("MCQ", type.getValue());
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
