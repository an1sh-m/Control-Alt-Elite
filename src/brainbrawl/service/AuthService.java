package brainbrawl.service;

import brainbrawl.model.User;
import brainbrawl.dao.UserDao;
import java.util.Optional;

public class AuthService {
    private final UserDao userDao;
    private User currentUser;

    public AuthService(UserDao userDao) { this.userDao = userDao; }

    public boolean login(String username, String password) {
        Optional<User> u = userDao.authenticate(username, password);
        u.ifPresent(user -> currentUser = user);
        return u.isPresent();
    }

    public void logout() { currentUser = null; }
    public boolean isLoggedIn() { return currentUser != null; }
    public User getCurrentUser() { return currentUser; }
}
