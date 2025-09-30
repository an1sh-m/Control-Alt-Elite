package brainbrawl.service;

import brainbrawl.dao.UserDao;
import brainbrawl.model.User;

import java.util.Optional;

public class AuthService {
    private final UserDao userDao;
    private User currentUser;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }

    public boolean login(String username, String password) {
        Optional<User> u = userDao.authenticate(username, password);
        u.ifPresent(user -> currentUser = user);
        return u.isPresent();
    }

    public boolean register(String username, String password) {
        return userDao.createUser(username, password);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void logout() {
        currentUser = null;
    }
}
