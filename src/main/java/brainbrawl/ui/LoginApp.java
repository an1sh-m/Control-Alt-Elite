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

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

import java.net.URL;
import java.util.Optional;

/**
 * Main login application for BrainBrawl.
 * <p>
 * Displays a login form with username/password input, optional "show password" feature,
 * and buttons for login and registration. Handles authentication and opening the Home page
 * upon successful login.
 * </p>
 * <p>
 * Seeds an admin account if missing during initialization.
 * </p>
 *
 */
public class LoginApp extends Application {
    /** DAO for user-related database operations. */
    private final UserDaoJdbc userDao = new UserDaoJdbc();

    /**
     * Entry point for the JavaFX application.
     * <p>
     * Initializes the database, sets up the login form UI, and handles login and registration actions.
     * </p>
     *
     * @param stage the primary stage for this application
     */
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

        VBox card = new VBox(cardInner);
        card.getStyleClass().add("card");
        card.setPadding(new Insets(24));
        card.setAlignment(Pos.CENTER);

        StackPane root = new StackPane(card);
        root.getStyleClass().add("app-root");
        Scene scene = new Scene(root, 980, 620);
        addCss(scene, "/BrainBrawl/login.css");

        // Handle login
        loginBtn.setDefaultButton(true);
        loginBtn.setOnAction(e -> {
            error.setText("");
            String u = username.getText().trim();
            String p = (passwordHidden.isVisible() ? passwordHidden : passwordShown).getText();
            if (u.isEmpty() || p.isEmpty()) { error.setText("Please enter both username and password."); return; }
            Optional<User> user = userDao.authenticate(u, p);
            if (user.isPresent()) openHome(stage);
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


    /**
     * Opens the Home page in the same stage after successful login.
     *
     * @param stage the current stage
     */
    private void openHome(Stage stage) {
        try {
            // NOTE: HomePage.fxml is under /BrainBrawl/ (no "ui" folder)
            URL url = LoginApp.class.getResource("/BrainBrawl/HomePage.fxml");
            if (url == null)
                throw new IllegalStateException("HomePage.fxml not found at /BrainBrawl/HomePage.fxml");
            Parent home = FXMLLoader.load(url);
            Scene homeScene = new Scene(home, 1200, 720);
            stage.setTitle("BrainBrawl – Home");
            stage.setScene(homeScene);
            stage.show();
        } catch (Exception ex) {
            ex.printStackTrace();
            new Alert(Alert.AlertType.ERROR, "Failed to open Home: " + ex.getMessage()).showAndWait();
        }
    }

    /**
     * Adds a CSS stylesheet to the provided scene.
     *
     * @param scene the scene to style
     * @param path path to the CSS file relative to the classpath
     */
    private static void addCss(Scene scene, String path) {
        URL url = LoginApp.class.getResource(path);
        if (url != null) scene.getStylesheets().add(url.toExternalForm());
        else System.err.println("WARN: CSS not found: " + path);
    }

    /**
     * Loads an ImageView for the given image path and sets its height.
     *
     * @param path path to the image resource
     * @param height desired image height
     * @return an ImageView, or null if the resource is missing
     */
    private static ImageView loadLogo(String path, double height) {
        URL url = LoginApp.class.getResource(path);
        if (url == null) return null;
        Image img = new Image(url.toExternalForm(), 0, height, true, true);
        ImageView iv = new ImageView(img);
        iv.setPreserveRatio(true);
        return iv;
    }
}
