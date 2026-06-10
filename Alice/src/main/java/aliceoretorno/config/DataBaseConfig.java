package aliceoretorno.config;

import java.sql.*;

public class DataBaseConfig {
    private static final String URL = "jdbc:postgresql://localhost:5432/alice_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}