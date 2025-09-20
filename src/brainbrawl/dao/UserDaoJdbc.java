package brainbrawl.dao;

import brainbrawl.db.Db;
import brainbrawl.model.User;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import java.util.Optional;

public class UserDaoJdbc implements UserDao {

    private static final SecureRandom RNG = new SecureRandom();

    public UserDaoJdbc() {
        createUsersTableIfNeeded();
    }

    private void createUsersTableIfNeeded() {
        String sql = """
            CREATE TABLE IF NOT EXISTS users(
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              username TEXT UNIQUE NOT NULL,
              password_hash TEXT NOT NULL,
              salt TEXT NOT NULL
            )
        """;
        try (Connection c = Db.connect(); Statement st = c.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Failed creating users table", e);
        }
    }

    private static String generateSalt() {
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    private static String hash(String plain, String base64Salt) {
        try {
            byte[] salt = Base64.getDecoder().decode(base64Salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] out = md.digest(plain.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    private static boolean verify(String plain, String base64Salt, String expectedHash) {
        return hash(plain, base64Salt).equals(expectedHash);
    }

    @Override
    public Optional<User> authenticate(String username, String plainPassword) {
        String q = "SELECT id, password_hash, salt FROM users WHERE username = ?";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                long id = rs.getLong("id");
                String hash = rs.getString("password_hash");
                String salt = rs.getString("salt");
                if (verify(plainPassword, salt, hash)) return Optional.of(new User(id, username));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException("Auth failed", e); }
    }

    @Override
    public boolean createUser(String username, String plainPassword) {
        String salt = generateSalt();
        String hash = hash(plainPassword, salt);
        String ins = "INSERT INTO users(username, password_hash, salt) VALUES(?,?,?)";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(ins)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, salt);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            // likely UNIQUE constraint violation
            return false;
        }
    }

    @Override
    public Optional<User> findByUsername(String username) {
        String q = "SELECT id FROM users WHERE username = ?";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new User(rs.getLong("id"), username));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // Optional: seed an admin user on first run
    public void seedAdminIfMissing() {
        if (findByUsername("admin").isEmpty()) createUser("admin", "admin123");
    }
}
