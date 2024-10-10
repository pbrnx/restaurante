//LoginView.java
package br.com.restaurante.controlemesas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginView extends JFrame {
    private JTextField userField;
    private JPasswordField passwordField;

    public LoginView() {
        setTitle("Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(3, 2));

        JLabel userLabel = new JLabel("Usuário:");
        userField = new JTextField();

        JLabel passwordLabel = new JLabel("Senha:");
        passwordField = new JPasswordField();

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new LoginButtonListener());

        add(userLabel);
        add(userField);
        add(passwordLabel);
        add(passwordField);
        add(new JLabel()); // Placeholder
        add(loginButton);
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String user = userField.getText();
            String password = new String(passwordField.getPassword());
            DatabaseConnection connection = new DatabaseConnection(user, password);
            if (connection.connect()) {
                TableController controller = new TableController(connection);
                TableView view = new TableView(controller);
                view.setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(LoginView.this, "Usuário ou senha inválidos", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}