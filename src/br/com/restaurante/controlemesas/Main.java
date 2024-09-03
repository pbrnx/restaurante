package br.com.restaurante.controlemesas;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                TableController controller = new TableController();
                TableView view = new TableView(controller);
                view.setVisible(true);
            }
        });
    }
}
