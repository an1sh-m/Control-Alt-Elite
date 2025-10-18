// File: src/main/java/brainbrawl/ui/LoginApp.java
package brainbrawl.ui;

import brainbrawl.dao.UserDaoJdbc;
import brainbrawl.db.Db;
import brainbrawl.model.User;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Optional;

public class LoginApp extends Application {
    private final UserDaoJdbc userDao = new UserDaoJdbc();

    @Override
    public void start(Stage stage) {
        Db.init();
        userDao.seedAdminIfMissing();

        // --- Header: logo + titles ---
        ImageView logo = loadLogo("/BrainBrawl/brainbrawl-logo.png", 56);
        Label title = new Label("BrainBrawl");
        title.getStyleClass().add("title");
        Label subtitle = new Label("Sign in to continue");
        subtitle.getStyleClass().add("subtitle");

        VBox titles = new VBox(4, title, subtitle);
        HBox header = (logo != null) ? new HBox(12, logo, titles) : new HBox(titles);
        header.setAlignment(Pos.CENTER_LEFT);

        // --- Inputs ---
        TextField username = new TextField();
        username.setPromptText("Username");

        PasswordField passwordHidden = new PasswordField();
        passwordHidden.setPromptText("Password");

        TextField passwordShown = new TextField();
        passwordShown.setPromptText("Password");
        passwordShown.setManaged(false);
        passwordShown.setVisible(false);
        passwordShown.textProperty().bindBidirectional(passwordHidden.textProperty());

        CheckBox showPw = new CheckBox("Show password");
        showPw.selectedProperty().addListener((o, a, b) -> {
            passwordShown.setManaged(b);
            passwordShown.setVisible(b);
            passwordHidden.setManaged(!b);
            passwordHidden.setVisible(!b);
        });

        Button loginBtn = new Button("Login");
        loginBtn.getStyleClass().addAll("primary-btn");

        Button registerBtn = new Button("Register");
        registerBtn.getStyleClass().addAll("ghost-btn");

        Label error = new Label();
        error.getStyleClass().add("error-text");
        error.setWrapText(true);

        // --- Form (fixed width column) ---
        VBox form = new VBox(12,
                new Label("Welcome back 👋"),
                username,
                new StackPane(passwordHidden, passwordShown),
                showPw,
                error
        );
        form.setMaxWidth(420);
        form.setFillWidth(true);

        HBox actions = new HBox(10, loginBtn, registerBtn);
        actions.setAlignment(Pos.CENTER_RIGHT);
        actions.setMaxWidth(420);

        VBox cardInner = new VBox(18, header, form, actions);
        cardInner.setAlignment(Pos.TOP_LEFT);
        cardInner.setMaxWidth(520);

        // --- Card container (centered) ---
        VBox card = new VBox(cardInner);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(card);
        root.getStyleClass().add("app-root");
        StackPane.setAlignment(card, Pos.CENTER);

        Scene scene = new Scene(root, 980, 620);
        addCss(scene, "/BrainBrawl/login.css");

        // Actions
        loginBtn.setDefaultButton(true);
        loginBtn.setOnAction(e -> {
            error.setText("");
            String u = username.getText().trim();
            String p = (passwordHidden.isVisible() ? passwordHidden : passwordShown).getText();
            if (u.isEmpty() || p.isEmpty()) { error.setText("Please enter both username and password."); return; }
            Optional<User> user = userDao.authenticate(u, p);
            if (user.isPresent()) openApp(stage, user.get());
            else error.setText("Invalid username or password.");
        });

        registerBtn.setOnAction(e -> {
            RegisterDialog dialog = new RegisterDialog(userDao);
            dialog.initOwner(stage);
            dialog.showAndWait().ifPresent(created -> {
                if (created) error.setText("Account created. You can now log in.");
            });
        });

        stage.setTitle("BrainBrawl – Login");
        stage.setScene(scene);
        stage.show();
    }

    // Helpers
    private static void addCss(Scene scene, String path) {
        URL url = LoginApp.class.getResource(path);
        if (url != null) scene.getStylesheets().add(url.toExternalForm());
        else System.err.println("WARN: CSS not found: " + path);
    }

    private static ImageView loadLogo(String path, double height) {
        URL url = LoginApp.class.getResource(path);
        if (url == null) { System.err.println("WARN: Logo not found: " + path); return null; }
        Image img = new Image(url.toExternalForm(), 0, height, true, true); // constrain by height
        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        return iv;
    }

    private void openApp(Stage loginStage, User user) {
        ManageQuestionsApp app = new ManageQuestionsApp();
        Stage appStage = new Stage();
        try {
            app.start(appStage);
            loginStage.close();
        } catch (Exception ex) {
            new Alert(Alert.AlertType.ERROR, "Failed to open app: " + ex.getMessage()).showAndWait();
        }
    }
}
