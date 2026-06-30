package aliceoretorno.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBaseConfig {

    private static final String URL      = "jdbc:postgresql://localhost:5433/Alice";
    private static final String USER     = "postgres";
    private static final String PASSWORD = "sarah123";
    // ============================================================

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}