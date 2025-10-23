package brainbrawl.ui;

import brainbrawl.service.AppServices;
import brainbrawl.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

/**
 * Controller for the user registration screen.
 * <p>
 * Handles user input for creating a new account, validates input fields,
 * communicates with the AuthService to register users, and displays feedback.
 * </p>
 */
public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;

    /** Authentication service used for registering users. */
    private final AuthService auth = AppServices.auth();

    /**
     * Handles the Register button click event.
     * <p>
     * Validates input fields:
     * <ul>
     *     <li>All fields must be non-empty</li>
     *     <li>Password and confirm password must match</li>
     * </ul>
     * Attempts to create the account via {@link AuthService#register(String, String)}.
     * Displays an information alert if successful, or an error alert otherwise.
     */
    @FXML
    public void onRegisterClick() {
        String u = usernameField.getText().trim();
        String p = passwordField.getText();
        String c = confirmField.getText();

        if (u.isEmpty() || p.isEmpty() || c.isEmpty()) {
            error("All fields are required.");
            return;
        }
        if (!p.equals(c)) {
            error("Passwords do not match.");
            return;
        }

        boolean created = auth.register(u, p);
        if (created) {
            info("Account created. You can log in now.");
            // Option A: go to login screen
            // SceneNavigator.goTo("login.fxml");
            // Option B: auto-login then go to lobby:
            // auth.login(u, p); SceneNavigator.goTo("lobby.fxml");
        } else {
            error("Username already exists or could not create account.");
        }
    }

    /**
     * Handles the Back to Login button click event.
     * <p>
     * Intended to navigate the user back to the login screen.
     * Currently commented out; implement navigation via SceneNavigator.
     */
    @FXML
    public void onBackToLoginClick() {
        // SceneNavigator.goTo("login.fxml");
    }

    /**
     * Displays an error alert with the given message.
     * @param msg The error message to show
     */
    private void error(String msg) { alert(Alert.AlertType.ERROR, msg); }

    /**
     * Displays an information alert with the given message.
     * @param msg The informational message to show
     */
    private void info(String msg)  { alert(Alert.AlertType.INFORMATION, msg); }

    /**
     * Displays an alert dialog with the specified type and message.
     * @param type The type of alert (ERROR, INFORMATION, etc.)
     * @param msg The message to display
     */
    private void alert(Alert.AlertType type, String msg) {
        var a = new Alert(type, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
