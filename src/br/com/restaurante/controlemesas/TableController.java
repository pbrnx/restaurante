//TableController.java
package br.com.restaurante.controlemesas;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.ArrayList;

public class TableController {
    private List<Table> tables;
    private DatabaseConnection dbConnection;

    public TableController(DatabaseConnection dbConnection) {
        this.dbConnection = dbConnection;
        tables = new ArrayList<>();
        loadTablesFromDatabase();
    }

    private void loadTablesFromDatabase() {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT ID, STATUS, NUMERO_COMANDA, QUANTIDADE_PESSOAS FROM MESAS");
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int id = rs.getInt("ID");
                String status = rs.getString("STATUS");
                int numeroComanda = rs.getInt("NUMERO_COMANDA");
                int quantidadePessoas = rs.getInt("QUANTIDADE_PESSOAS");
                tables.add(new Table(id, status, numeroComanda, quantidadePessoas));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Table> getTables() {
        return tables;
    }

    public void updateTableStatus(int tableNumber, String status, int numeroComanda, int quantidadePessoas) throws TableException {
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                table.setStatus(status);
                table.setNumeroComanda(numeroComanda);
                table.setQuantidadePessoas(quantidadePessoas);
                updateTableInDatabase(tableNumber, status, numeroComanda, quantidadePessoas);
                return;
            }
        }
        throw new TableException("Mesa número " + tableNumber + " não encontrada.");
    }

    private void updateTableInDatabase(int tableNumber, String status, int numeroComanda, int quantidadePessoas) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE MESAS SET STATUS = ?, NUMERO_COMANDA = ?, QUANTIDADE_PESSOAS = ? WHERE ID = ?")) {
            stmt.setString(1, status);
            stmt.setInt(2, numeroComanda);
            stmt.setInt(3, quantidadePessoas);
            stmt.setInt(4, tableNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addNewTable(int tableNumber, String status, int numeroComanda, int quantidadePessoas) throws TableException {
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                throw new TableException("Mesa número " + tableNumber + " já existe.");
            }
        }
        if (!isValidStatus(status)) {
            throw new TableException("Status inválido. Os status permitidos são: Disponível, Indisponível, Reservada.");
        }
        tables.add(new Table(tableNumber, status, numeroComanda, quantidadePessoas));
        addTableToDatabase(tableNumber, status, numeroComanda, quantidadePessoas);
    }

    private void addTableToDatabase(int tableNumber, String status, int numeroComanda, int quantidadePessoas) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO MESAS (ID, STATUS, NUMERO_COMANDA, QUANTIDADE_PESSOAS) VALUES (?, ?, ?, ?)")) {
            stmt.setInt(1, tableNumber);
            stmt.setString(2, status);
            stmt.setInt(3, numeroComanda);
            stmt.setInt(4, quantidadePessoas);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void removeTable(int tableNumber) throws TableException {
        Table tableToRemove = null;
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                tableToRemove = table;
                break;
            }
        }
        if (tableToRemove != null) {
            tables.remove(tableToRemove);
            removeTableFromDatabase(tableNumber);
        } else {
            throw new TableException("Mesa número " + tableNumber + " não encontrada.");
        }
    }

    private void removeTableFromDatabase(int tableNumber) {
        try (Connection conn = dbConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM MESAS WHERE ID = ?")) {
            stmt.setInt(1, tableNumber);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean isValidStatus(String status) {
        return status.equals("Disponível") || status.equals("Indisponível") || status.equals("Reservada");
    }
}
