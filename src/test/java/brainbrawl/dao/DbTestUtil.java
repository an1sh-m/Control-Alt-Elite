package brainbrawl.dao;

import brainbrawl.db.Db;
import java.sql.Connection;

public class DbTestUtil {
    private static Connection testConnection;

    public static void overrideConnection(Connection c) {
        testConnection = c;
    }

    // Monkey patch Db.connect()
    public static Connection connect() {
        if (testConnection != null) return testConnection;
        try { return Db.connect(); } catch (Exception e) { throw new RuntimeException(e); }
    }
}
