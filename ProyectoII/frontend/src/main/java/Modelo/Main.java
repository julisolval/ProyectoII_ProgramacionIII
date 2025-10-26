package main.java.Modelo;

import main.java.Vista.LoginInterface;
import main.java.Controlador.ControladorLogin;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            SwingUtilities.invokeLater(() -> {
                LoginInterface loginView = new LoginInterface();
                new ControladorLogin(loginView);
                loginView.getFrameLogin().setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al iniciar la aplicación: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}