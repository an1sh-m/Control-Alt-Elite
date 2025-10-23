package brainbrawl.dao;

import brainbrawl.db.Db;
import brainbrawl.model.User;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.*;
import java.util.Base64;
import java.util.Optional;

/**
 * JDBC implementation of the UserDao interface.
 * Manages user creation, authentication, and password security using SHA-256 hashing and random salts.
 */
public class UserDaoJdbc implements UserDao {

    private static final SecureRandom RNG = new SecureRandom();

    public UserDaoJdbc() {
        createUsersTableIfNeeded();
    }

    /**
     * Ensures the users table exists; creates it if missing.
     */
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

    /**
     * Generates a random cryptographic salt for password hashing.
     *
     * @return The Base64-encoded salt string.
     */
    private static String generateSalt() {
        byte[] salt = new byte[16];
        RNG.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a plain-text password using SHA-256 with a salt.
     *
     * @param plain The plain-text password.
     * @param base64Salt The Base64-encoded salt to use.
     * @return The Base64-encoded password hash.
     */
    private static String hash(String plain, String base64Salt) {
        try {
            byte[] salt = Base64.getDecoder().decode(base64Salt);
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salt);
            byte[] out = md.digest(plain.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(out);
        } catch (Exception e) {
            throw new RuntimeException("Password hashing failed", e);
        }
    }

    /**
     * Verifies whether a plain-text password matches a stored hash.
     *
     * @param plain The input password.
     * @param base64Salt The salt used for hashing.
     * @param expectedHash The stored password hash.
     * @return True if the password matches, false otherwise.
     */
    private static boolean verify(String plain, String base64Salt, String expectedHash) {
        return hash(plain, base64Salt).equals(expectedHash);
    }

    /**
     * Authenticates a user by checking username and password hash.
     */
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
                if (verify(plainPassword, salt, hash)) {
                    return Optional.of(new User(id, username));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Auth failed", e);
        }
    }

    /**
     * Creates a new user with a hashed password and random salt.
     */
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
            // likely UNIQUE(username) violation
            return false;
        }
    }

    /**
     * Finds a user by username.
     */
    @Override
    public Optional<User> findByUsername(String username) {
        String q = "SELECT id FROM users WHERE username = ?";
        try (Connection c = Db.connect(); PreparedStatement ps = c.prepareStatement(q)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new User(rs.getLong("id"), username));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Seeds a default admin user on first run if missing.
     */
    public void seedAdminIfMissing() {
        if (findByUsername("admin").isEmpty()) createUser("admin", "admin123");
    }
}