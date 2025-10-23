package brainbrawl.ui;

import brainbrawl.dao.UserDaoJdbc;
import brainbrawl.service.AuthService;
import brainbrawl.db.Db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A simple smoke test for the authentication flow using {@link AuthService}.
 * <p>
 * This standalone test verifies basic user registration and login functionality
 * against the connected SQLite database. It ensures that:
 * <ul>
 *   <li>A test user can be created and logged in successfully.</li>
 *   <li>Incorrect password attempts are correctly rejected.</li>
 *   <li>The database being used is printed for verification purposes.</li>
 * </ul>
 * <p>
 * This is not a formal JUnit test — it is designed to be run manually
 * to confirm that authentication and database connectivity work end-to-end.
 */
public class SmokeAuthTest {

    /**
     * Entry point for manually testing authentication operations.
     * <p>
     * The test performs the following steps:
     * <ol>
     *   <li>Logs which SQLite database file is currently connected.</li>
     *   <li>Deletes any existing test user from the database to ensure repeatable runs.</li>
     *   <li>Registers a new user using {@link AuthService#register(String, String)}.</li>
     *   <li>Attempts to log in with both correct and incorrect passwords to verify validation logic.</li>
     * </ol>
     *
     * @param args ignored command-line arguments
     * @throws Exception if a database or SQL error occurs during testing
     */
    public static void main(String[] args) throws Exception {
        logDbPath(); // show which SQLite file is being used

        UserDaoJdbc dao = new UserDaoJdbc();
        AuthService auth = new AuthService(dao);

        String u = "test_user";
        String p = "pass123";

        // Clean user if exists (so you can re-run test)
        try (Connection c = Db.connect(); Statement st = c.createStatement()) {
            st.executeUpdate("DELETE FROM users WHERE username = '" + u + "'");
        }

        boolean created = auth.register(u, p);
        System.out.println("register(" + u + "): " + created);

        boolean loginOk = auth.login(u, p);
        System.out.println("login correct pw: " + loginOk);

        boolean loginBad = auth.login(u, "wrongpw");
        System.out.println("login wrong pw: " + loginBad);
    }

    /**
     * Logs the currently attached SQLite databases and their file paths.
     * <p>
     * Uses the {@code PRAGMA database_list} command to print which database
     * file is being used by the {@link Db} connection. This helps confirm
     * that the correct SQLite instance is being tested.
     */
    private static void logDbPath() {
        try (Connection c = Db.connect();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("PRAGMA database_list")) {
            while (rs.next()) {
                String name = rs.getString("name");
                String file = rs.getString("file");
                System.out.println("SQLite attached: " + name + " -> " + file);
            }
        } catch (Exception e) {
            System.out.println("Could not query PRAGMA database_list: " + e.getMessage());
        }
    }
}
