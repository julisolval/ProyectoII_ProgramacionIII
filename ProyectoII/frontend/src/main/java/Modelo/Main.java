package main.java.Modelo;

import main.java.Vista.LoginInterface;
import main.java.Controlador.ControladorLogin;

import javax.swing.*;


//mensaje prueba de funcionamiento, lo ven?
public class Main {
    public static void main(String[] args) {
        try {
            // FORMA CORRECTA: Establecer look and feel del sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            SwingUtilities.invokeLater(() -> {
                LoginInterface loginView = new LoginInterface();
                new ControladorLogin(loginView);
                loginView.getFrameLogin().setVisible(true);

                System.out.println("✅ Aplicación Frontend iniciada correctamente");
                System.out.println("🔌 Conectando al backend en localhost:12345...");
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "Error al iniciar la aplicación: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}