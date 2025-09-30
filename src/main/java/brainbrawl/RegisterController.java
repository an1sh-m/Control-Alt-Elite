package brainbrawl.ui;

import brainbrawl.AppServices;
import brainbrawl.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField confirmField;

    private final AuthService auth = AppServices.auth();

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

    @FXML
    public void onBackToLoginClick() {
        // SceneNavigator.goTo("login.fxml");
    }

    private void error(String msg) { alert(Alert.AlertType.ERROR, msg); }
    private void info(String msg)  { alert(Alert.AlertType.INFORMATION, msg); }

    private void alert(Alert.AlertType type, String msg) {
        var a = new Alert(type, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
