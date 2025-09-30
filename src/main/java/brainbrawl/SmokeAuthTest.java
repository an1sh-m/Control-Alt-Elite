package brainbrawl;

import brainbrawl.dao.UserDaoJdbc;
import brainbrawl.service.AuthService;
import brainbrawl.db.Db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SmokeAuthTest {
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
