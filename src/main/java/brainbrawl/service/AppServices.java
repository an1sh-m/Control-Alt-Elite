package brainbrawl.service;

import brainbrawl.dao.ResultDaoJdbc;
import brainbrawl.dao.UserDaoJdbc;

public final class AppServices {
    private static final AuthService AUTH = new AuthService(new UserDaoJdbc());
    private static final ResultService RESULTS = new ResultService(new ResultDaoJdbc());

    private AppServices() {}

    public static AuthService auth() { return AUTH; }

    public static ResultService results() { return RESULTS; }
}
