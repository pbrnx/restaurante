//DatabaseInitializer.java
package br.com.restaurante.controlemesas;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseInitializer {
    public static void initializeDatabase(Connection connection) {
        try (Statement stmt = connection.createStatement()) {
            String createTableSQL = "CREATE TABLE MESAS (" +
                    "ID NUMBER PRIMARY KEY, " +
                    "STATUS VARCHAR2(50) NOT NULL, " +
                    "NUMERO_COMANDA NUMBER, " +
                    "QUANTIDADE_PESSOAS NUMBER)";
            stmt.executeUpdate(createTableSQL);

            String insertDefaultTablesSQL = "BEGIN " +
                    "FOR i IN 1..5 LOOP " +
                    "BEGIN " +
                    "INSERT INTO MESAS (ID, STATUS, NUMERO_COMANDA, QUANTIDADE_PESSOAS) VALUES (i, 'Disponível', NULL, 0); " +
                    "EXCEPTION WHEN DUP_VAL_ON_INDEX THEN NULL; " +
                    "END; " +
                    "END LOOP; " +
                    "END;";
            stmt.execute(insertDefaultTablesSQL);
        } catch (SQLException e) {
            if (e.getErrorCode() != 955) { // ORA-00955: nome já está sendo usado por um objeto existente
                e.printStackTrace();
            }
        }
    }
}
