package br.com.restaurante.controlemesas;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TableView extends JFrame {
    private TableController controller;
    private JTable table;
    private DefaultTableModel tableModel;
    private JLabel statusLabel;

    public TableView(TableController controller) {
        this.controller = controller;
        setTitle("Controle de Mesas");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initializeUI();
    }

    private void initializeUI() {
        tableModel = new DefaultTableModel(new Object[]{"Número da Mesa", "Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new StatusColorRenderer());
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);

        loadTableData();

        JScrollPane scrollPane = new JScrollPane(table);

        JButton btnUpdate = new JButton("Atualizar Status");
        btnUpdate.addActionListener(e -> {
            updateStatus();
            updateStatusLabel();
        });

        JButton btnAdd = new JButton("Adicionar Mesa");
        btnAdd.addActionListener(e -> {
            addNewTables();
            updateStatusLabel();
        });

        JButton btnRemove = new JButton("Remover Mesa");
        btnRemove.addActionListener(e -> {
            removeTable();
            updateStatusLabel();
        });

        JPanel panel = new JPanel();
        panel.add(btnUpdate);
        panel.add(btnAdd);
        panel.add(btnRemove);

        // Adiciona a label para mostrar as porcentagens
        statusLabel = new JLabel();
        updateStatusLabel(); // Inicializa a label com as porcentagens atuais

        // Adiciona os componentes ao layout
        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH); // Adiciona a label na parte superior da janela
    }

    private void updateStatusLabel() {
        int totalMesas = tableModel.getRowCount();
        int disponivel = 0;
        int indisponivel = 0;
        int reservada = 0;

        for (int i = 0; i < totalMesas; i++) {
            String status = (String) tableModel.getValueAt(i, 1);
            switch (status) {
                case "Disponível":
                    disponivel++;
                    break;
                case "Indisponível":
                    indisponivel++;
                    break;
                case "Reservada":
                    reservada++;
                    break;
            }
        }

        String labelText = String.format(
                "Disponível: %.2f%% | Indisponível: %.2f%% | Reservada: %.2f%%",
                100.0 * disponivel / totalMesas,
                100.0 * indisponivel / totalMesas,
                100.0 * reservada / totalMesas
        );
        statusLabel.setText(labelText);
    }

    private void loadTableData() {
        List<Table> tables = controller.getTables();
        Collections.sort(tables, Comparator.comparingInt(Table::getTableNumber)); // Ordena as mesas por número

        for (Table t : tables) {
            tableModel.addRow(new Object[]{t.getTableNumber(), t.getStatus()});
        }
    }

    private void updateStatus() {
        String input = JOptionPane.showInputDialog(this, "Digite o número da mesa para atualizar o status:");
        if (input != null && !input.trim().isEmpty()) {
            int tableNumber;
            try {
                tableNumber = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Número inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String newStatus = (String) JOptionPane.showInputDialog(
                    this,
                    "Selecione o novo status:",
                    "Atualizar Status",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Disponível", "Indisponível", "Reservada"},
                    "Disponível"
            );

            if (newStatus != null) {
                try {
                    controller.updateTableStatus(tableNumber, newStatus);
                    updateTableModel();
                    JOptionPane.showMessageDialog(this, "Status atualizado com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (TableException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void addNewTables() {
        String input = JOptionPane.showInputDialog(this, "Quantas mesas você gostaria de adicionar?");
        if (input != null && !input.trim().isEmpty()) {
            int numberOfTables;
            try {
                numberOfTables = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Número inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            for (int i = 0; i < numberOfTables; i++) {
                addSingleTable();
            }
        }
    }

    private void addSingleTable() {
        String input = JOptionPane.showInputDialog(this, "Digite o número da nova mesa:");
        if (input != null && !input.trim().isEmpty()) {
            int tableNumber;
            try {
                tableNumber = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Número inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String status = (String) JOptionPane.showInputDialog(
                    this,
                    "Selecione o status inicial:",
                    "Adicionar Nova Mesa",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    new String[]{"Disponível", "Indisponível", "Reservada"},
                    "Disponível"
            );

            if (status != null) {
                try {
                    controller.addNewTable(tableNumber, status);
                    updateTableModel();
                    JOptionPane.showMessageDialog(this, "Mesa adicionada com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                } catch (TableException ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private void removeTable() {
        String input = JOptionPane.showInputDialog(this, "Digite o número da mesa que deseja remover:");
        if (input != null && !input.trim().isEmpty()) {
            int tableNumber;
            try {
                tableNumber = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Número inválido.", "Erro", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                controller.removeTable(tableNumber);
                updateTableModel();
                JOptionPane.showMessageDialog(this, "Mesa removida com sucesso.", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
            } catch (TableException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateTableModel() {
        tableModel.setRowCount(0);
        loadTableData();
    }

    private class StatusColorRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            if (column == 1) {
                String status = (String) value;
                switch (status) {
                    case "Disponível":
                        cell.setForeground(Color.GREEN);
                        break;
                    case "Indisponível":
                        cell.setForeground(Color.RED);
                        break;
                    case "Reservada":
                        cell.setForeground(Color.orange);
                        break;
                    default:
                        cell.setForeground(Color.BLACK);
                        break;
                }
            } else {
                cell.setForeground(Color.BLACK);
            }

            cell.setBackground(Color.WHITE);
            return cell;
        }
    }
}
