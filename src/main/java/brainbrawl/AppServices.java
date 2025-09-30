package brainbrawl;

import brainbrawl.dao.UserDaoJdbc;
import brainbrawl.service.AuthService;

public final class AppServices {
    private static final AuthService AUTH = new AuthService(new UserDaoJdbc());
    private AppServices() {}

    public static AuthService auth() {
        return AUTH;
    }
}
