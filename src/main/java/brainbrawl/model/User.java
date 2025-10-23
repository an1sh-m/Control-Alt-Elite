package brainbrawl.model;

/**
 * Represents a user in the BrainBrawl application.
 * <p>
 * A user has a unique ID and username. Passwords are securely handled
 * within {@link brainbrawl.dao.UserDaoJdbc}, not stored in this model.
 */
public class User {
    private final long id;
    private final String username;

    /**
     * Constructs a new {@code User}.
     *
     * @param id the unique user ID from the database
     * @param username the username chosen by the user
     */
    public User(long id, String username) {
        this.id = id;
        this.username = username;
    }

    /** @return the user’s unique ID */
    public long getId() {
        return id;
    }

    /** @return the user’s username */
    public String getUsername() {
        return username;
    }

    /** @return a readable string representation of the user */
    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "'}";
    }
}