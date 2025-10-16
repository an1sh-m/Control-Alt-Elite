package brainbrawl.ui;

import brainbrawl.service.AppServices;
import brainbrawl.service.AuthService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    private final AuthService auth = AppServices.auth();

    private Runnable onLoginSuccess;

    public void setOnLoginSuccess(Runnable onLoginSuccess) {
        this.onLoginSuccess = onLoginSuccess;
    }

    @FXML
    public void onLoginClick() {
        String u = usernameField.getText().trim();
        String p = passwordField.getText();

        if (u.isEmpty() || p.isEmpty()) {
            alert("Please enter username and password.");
            return;
        }

        boolean ok = auth.login(u, p);
        if (ok) {
            alertInfo("Login successful. Welcome " + auth.getCurrentUser().getUsername() + "!");

            if (onLoginSuccess != null) {
                onLoginSuccess.run();
            }
        } else {
            alert("Incorrect username or password.");
        }
    }

    @FXML
    public void onGoToRegisterClick() {
        // TODO: navigate to register view
        // e.g., SceneNavigator.goTo("register.fxml");
    }

    private void alert(String msg) {
        var a = new Alert(Alert.AlertType.ERROR, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }

    private void alertInfo(String msg) {
        var a = new Alert(Alert.AlertType.INFORMATION, msg);
        a.setHeaderText(null);
        a.showAndWait();
    }
}
