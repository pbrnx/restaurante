package br.com.restaurante.controlemesas;

import java.util.List;
import java.util.ArrayList;

public class TableController {
    private List<Table> tables;

    public TableController() {
        tables = new ArrayList<>();
        // Inicializa com 5 mesas com status predefinidos
        tables.add(new Table(1, "Indisponível"));
        tables.add(new Table(2, "Disponível"));
        tables.add(new Table(3, "Reservada"));
        tables.add(new Table(4, "Disponível"));
        tables.add(new Table(5, "Disponível"));
    }



    public List<Table> getTables() {
        return tables;
    }

    public void updateTableStatus(int tableNumber, String status) throws TableException {
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                table.setStatus(status);
                return;
            }
        }
        throw new TableException("Mesa número " + tableNumber + " não encontrada.");
    }

    public void addNewTable(int tableNumber, String status) throws TableException {
        // Verifica se o número da mesa já existe
        for (Table table : tables) {
            if (table.getTableNumber() == tableNumber) {
                throw new TableException("Mesa número " + tableNumber + " já existe.");
            }
        }
        // Verifica se o status é válido
        if (!isValidStatus(status)) {
            throw new TableException("Status inválido. Os status permitidos são: Disponível, Indisponível, Reservada.");
        }
        // Adiciona a nova mesa
        tables.add(new Table(tableNumber, status));
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
        } else {
            throw new TableException("Mesa número " + tableNumber + " não encontrada.");
        }
    }

    private boolean isValidStatus(String status) {
        return status.equals("Disponível") || status.equals("Indisponível") || status.equals("Reservada");
    }
}
