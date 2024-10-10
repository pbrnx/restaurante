//TableView.java
package br.com.restaurante.controlemesas;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
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
        tableModel = new DefaultTableModel(new Object[]{"Número da Mesa", "Status", "Número da Comanda", "Quantidade de Pessoas"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Todas as colunas são editáveis
                return true;
            }

            @Override
            public void setValueAt(Object aValue, int row, int column) {
                // Converte o valor para o tipo correto antes de definir
                if (column == 0 || column == 2 || column == 3) {
                    aValue = Integer.parseInt(aValue.toString());
                }
                super.setValueAt(aValue, row, column);
            }
        };
        table = new JTable(tableModel);
        table.setDefaultRenderer(Object.class, new StatusColorRenderer());
        table.setRowHeight(30);
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(150);

        loadTableData();

        // Adicionar JComboBox como editor para a coluna de status
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"Disponível", "Indisponível", "Reservada"});
        table.getColumnModel().getColumn(1).setCellEditor(new DefaultCellEditor(statusComboBox));

        // Listener para atualizar o banco de dados ao finalizar a edição de uma célula
        tableModel.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getType() == TableModelEvent.UPDATE) {
                    int row = e.getFirstRow();
                    int tableNumber = Integer.parseInt(tableModel.getValueAt(row, 0).toString());
                    String status = tableModel.getValueAt(row, 1).toString();
                    int numeroComanda = Integer.parseInt(tableModel.getValueAt(row, 2).toString());
                    int quantidadePessoas = Integer.parseInt(tableModel.getValueAt(row, 3).toString());
                    try {
                        controller.updateTableStatus(tableNumber, status, numeroComanda, quantidadePessoas);
                    } catch (TableException ex) {
                        JOptionPane.showMessageDialog(TableView.this, ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);

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
        panel.add(btnAdd);
        panel.add(btnRemove);

        statusLabel = new JLabel();
        updateStatusLabel();

        add(scrollPane, BorderLayout.CENTER);
        add(panel, BorderLayout.SOUTH);
        add(statusLabel, BorderLayout.NORTH);
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
        Collections.sort(tables, Comparator.comparingInt(Table::getTableNumber));

        for (Table t : tables) {
            tableModel.addRow(new Object[]{t.getTableNumber(), t.getStatus(), t.getNumeroComanda(), t.getQuantidadePessoas()});
        }
    }

    private void addNewTables() {
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
                String comandaInput = JOptionPane.showInputDialog(this, "Digite o número da comanda:");
                int numeroComanda = comandaInput != null && !comandaInput.trim().isEmpty() ? Integer.parseInt(comandaInput) : 0;

                String pessoasInput = JOptionPane.showInputDialog(this, "Digite a quantidade de pessoas:");
                int quantidadePessoas = pessoasInput != null && !pessoasInput.trim().isEmpty() ? Integer.parseInt(pessoasInput) : 0;

                try {
                    controller.addNewTable(tableNumber, status, numeroComanda, quantidadePessoas);
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
                        cell.setForeground(Color.ORANGE);
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
