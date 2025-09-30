package brainbrawl.service;

import brainbrawl.dao.UserDaoJdbc;

public final class AppServices {
    private static final AuthService AUTH = new AuthService(new UserDaoJdbc());
    private AppServices() {}

    public static AuthService auth() {
        return AUTH;
    }
}
