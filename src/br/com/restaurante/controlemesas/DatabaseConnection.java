//DatabaseConnection.java
package br.com.restaurante.controlemesas;

import javax.swing.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:oracle:thin:@oracle.fiap.com.br:1521:ORCL";
    private String username;
    private String password;
    private Connection connection;

    public DatabaseConnection(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public boolean connect() {
        try {
            connection = DriverManager.getConnection(URL, username, password);
            if (connection != null && !connection.isClosed()) {
                DatabaseInitializer.initializeDatabase(connection);
            }
            return true;
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados: " + e.getMessage(), "Erro de Conexão", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return false;
        }
    }

    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                connect();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }
}
