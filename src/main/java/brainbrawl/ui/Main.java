package brainbrawl.ui;

/**
 * Main launcher class for the BrainBrawl application.
 * <p>
 * Acts as the entry point for the JavaFX runtime. Delegates startup to
 * the {@link LoginApp}, which presents the initial login window.
 * </p>
 *
 * <p>Ensures consistent application startup regardless of build environment.</p>
 *
 */

public class Main {
    /**
     * Launches the BrainBrawl application by invoking the login window.
     *
     * @param args command-line arguments (unused)
     */
    public static void main(String[] args) {
        LoginApp.launch(LoginApp.class, args);
    }
}
