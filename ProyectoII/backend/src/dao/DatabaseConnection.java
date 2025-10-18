package dao;

import config.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static String url = Config.DB_URL;
    private static String user = Config.DB_USER;
    private static String password;

    public static void setPassword(String pwd) {
        password = pwd;
    }

    public static Connection getConnection() throws SQLException {
        if (password == null) {
            throw new SQLException("La contraseña de la base de datos no ha sido configurada. Llame a DatabaseConnection.setPassword() primero.");
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL Driver not found", e);
        }
    }
}