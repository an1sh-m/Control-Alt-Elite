package brainbrawl.service;

import brainbrawl.dao.UserDao;
import brainbrawl.model.User;

import java.util.Optional;

/**
 * Handles user authentication and registration logic.
 * <p>
 * This service wraps {@link UserDao} to manage user login sessions and
 * provides methods to register, log in, and log out users.
 */
public class AuthService {
    private final UserDao userDao;
    private User currentUser;

    /**
     * Constructs a new {@code AuthService}.
     *
     * @param userDao the DAO responsible for user persistence and authentication
     */
    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    /**
     * Attempts to log in a user with the given credentials.
     *
     * @param username the username entered
     * @param password the password entered
     * @return {@code true} if authentication succeeded, {@code false} otherwise
     */
    public boolean login(String username, String password) {
        Optional<User> u = userDao.authenticate(username, password);
        u.ifPresent(user -> currentUser = user);
        return u.isPresent();
    }

    /**
     * Registers a new user account.
     *
     * @param username desired username
     * @param password desired password
     * @return {@code true} if registration succeeded, {@code false} if the username already exists
     */
    public boolean register(String username, String password) {
        return userDao.createUser(username, password);
    }

    /**
     * Retrieves the currently logged-in user, if any.
     *
     * @return the current {@link User}, or {@code null} if no user is logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Logs out the current user, clearing their session.
     */
    public void logout() {
        currentUser = null;
    }
}
