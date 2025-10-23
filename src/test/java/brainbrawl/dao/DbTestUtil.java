package brainbrawl.dao;

import brainbrawl.db.Db;
import java.sql.Connection;

/**
 * Utility class for database testing that allows overriding
 * the default database connection used by the {@link Db} class.
 * <p>
 * This class is mainly used in unit tests to inject a test-specific
 * {@link Connection} (e.g., an in-memory SQLite instance) without
 * modifying production database behavior.
 */
public class DbTestUtil {

    /** Holds a custom connection to be used for testing purposes. */
    private static Connection testConnection;

    /**
     * Overrides the default database connection with a custom one.
     * <p>
     * This method is typically called at the start of a test to inject
     * a mock or in-memory database connection.
     *
     * @param c the custom {@link Connection} to use during tests
     */
    public static void overrideConnection(Connection c) {
        testConnection = c;
    }

    /**
     * Returns the current active database connection.
     * <p>
     * If a test connection has been set using {@link #overrideConnection(Connection)},
     * it will be returned instead of the standard {@link Db#connect()} connection.
     *
     * @return the current {@link Connection} instance
     * @throws RuntimeException if a database connection cannot be established
     */
    public static Connection connect() {
        if (testConnection != null) return testConnection;
        try { return Db.connect(); } catch (Exception e) { throw new RuntimeException(e); }
    }
}
