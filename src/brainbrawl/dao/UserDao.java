package brainbrawl.dao;

import brainbrawl.model.User;
import java.util.Optional;

public interface UserDao {
    Optional<User> authenticate(String username, String plainPassword);
    boolean createUser(String username, String plainPassword);
    Optional<User> findByUsername(String username);
}