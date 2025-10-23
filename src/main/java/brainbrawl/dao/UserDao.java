package brainbrawl.dao;

import brainbrawl.model.User;
import java.util.Optional;

/**
 * DAO interface for managing user data and authentication.
 * Defines methods for creating users and verifying login credentials.
 */
public interface UserDao {

    /**
     * Authenticates a user using their username and password.
     *
     * @param username The username entered by the user.
     * @param plainPassword The plain text password entered by the user.
     * @return An Optional containing the User if authentication succeeds, otherwise empty.
     */
    Optional<User> authenticate(String username, String plainPassword);

    /**
     * Creates a new user account in the database.
     *
     * @param username The desired username.
     * @param plainPassword The plain text password (will be hashed and salted before storage).
     * @return True if the user was successfully created, false otherwise.
     */
    boolean createUser(String username, String plainPassword);

    /**
     * Finds a user by their username.
     *
     * @param username The username to search for.
     * @return An Optional containing the User if found, otherwise empty.
     */
    Optional<User> findByUsername(String username);
}