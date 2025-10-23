package brainbrawl.service;

import brainbrawl.dao.ResultDaoJdbc;
import brainbrawl.dao.UserDaoJdbc;

/**
 * Centralized service registry for the BrainBrawl application.
 * <p>
 * Provides global access to singleton instances of core services such as
 * {@link AuthService} and {@link ResultService}.
 * <p>
 * This class is a utility class and cannot be instantiated.
 */
public final class AppServices {
    /** Singleton instance of {@link AuthService}. */
    private static final AuthService AUTH = new AuthService(new UserDaoJdbc());
    /** Singleton instance of {@link ResultService}. */
    private static final ResultService RESULTS = new ResultService(new ResultDaoJdbc());

    /** Private constructor to prevent instantiation. */
    private AppServices() {}

    /**
     * Provides global access to the authentication service.
     *
     * @return the shared {@link AuthService} instance
     */
    public static AuthService auth() { return AUTH; }

    /**
     * Provides global access to the results service.
     *
     * @return the shared {@link ResultService} instance
     */
    public static ResultService results() { return RESULTS; }
}