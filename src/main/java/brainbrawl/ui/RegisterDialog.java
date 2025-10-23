package brainbrawl.ui;

import brainbrawl.dao.UserDaoJdbc;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.regex.Pattern;

/**
 * A modal dialog for user registration.
 * <p>
 * Provides input fields for username, password, and password confirmation.
 * Validates user input and interacts with {@link UserDaoJdbc} to create new accounts.
 * Returns {@code true} if registration succeeds, {@code false} otherwise.
 * </p>
 */
public class RegisterDialog extends Dialog<Boolean> {

    /** Regex pattern to validate acceptable usernames (3–32 characters, letters, digits, . _ -). */
    private static final Pattern USERNAME_OK = Pattern.compile("^[a-zA-Z0-9._-]{3,32}$");

    /**
     * Constructs a registration dialog.
     *
     * @param userDao The data access object used to create new users in the database
     */
    public RegisterDialog(UserDaoJdbc userDao) {
        setTitle("Create Account");
        setHeaderText("Register a new account");

        // Apply our dark theme to the dialog pane
        DialogPane pane = getDialogPane();
        URL css = LoginApp.class.getResource("/BrainBrawl/login.css");
        if (css != null) pane.getStylesheets().add(css.toExternalForm());
        pane.getStyleClass().add("bb-dialog");   // class used by our CSS
        pane.setPrefWidth(420);                  // comfortable width

        TextField username = new TextField();
        username.setPromptText("Username (3–32 chars, letters/digits/._-)");

        PasswordField pw1 = new PasswordField();
        pw1.setPromptText("Password (min 6 chars)");

        PasswordField pw2 = new PasswordField();
        pw2.setPromptText("Confirm password");

        Label status = new Label();
        status.getStyleClass().add("error-text");

        VBox box = new VBox(10, username, pw1, pw2, status);
        box.setPadding(new Insets(16));
        box.setAlignment(Pos.CENTER_LEFT);
        pane.setContent(box);

        ButtonType registerType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        pane.getButtonTypes().addAll(registerType, ButtonType.CANCEL);

        Node registerBtn = pane.lookupButton(registerType);
        registerBtn.addEventFilter(javafx.event.ActionEvent.ACTION, evt -> {
            String u = username.getText().trim(), p1 = pw1.getText(), p2 = pw2.getText();

            if (!USERNAME_OK.matcher(u).matches()) { status.setText("Invalid username format."); evt.consume(); return; }
            if (p1.length() < 6)                { status.setText("Password too short (min 6)."); evt.consume(); return; }
            if (!p1.equals(p2))                 { status.setText("Passwords do not match.");    evt.consume(); return; }

            boolean created = userDao.createUser(u, p1);
            if (!created) { status.setText("Username already exists."); evt.consume(); }
            else setResult(Boolean.TRUE);
        });

        setResultConverter(btn -> btn == registerType ? Boolean.TRUE : Boolean.FALSE);
    }
}
