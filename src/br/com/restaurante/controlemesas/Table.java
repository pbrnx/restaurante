//Table.java
package br.com.restaurante.controlemesas;

public class Table {
    private int tableNumber;
    private String status;
    private int numeroComanda;
    private int quantidadePessoas;

    public Table(int tableNumber, String status, int numeroComanda, int quantidadePessoas) {
        this.tableNumber = tableNumber;
        this.status = status;
        this.numeroComanda = numeroComanda;
        this.quantidadePessoas = quantidadePessoas;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumeroComanda() {
        return numeroComanda;
    }

    public void setNumeroComanda(int numeroComanda) {
        this.numeroComanda = numeroComanda;
    }

    public int getQuantidadePessoas() {
        return quantidadePessoas;
    }

    public void setQuantidadePessoas(int quantidadePessoas) {
        this.quantidadePessoas = quantidadePessoas;
    }
}
