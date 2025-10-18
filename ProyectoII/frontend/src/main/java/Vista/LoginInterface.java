package main.java.Vista;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class LoginInterface extends JPanel {
    private JFrame frameLogin;
    private JFrame frameChangePassword;
    private JTextField textFieldIDLogin;
    private JPasswordField passFieldPasswordLogin;
    private JButton buttonSalirLogin;
    private JButton buttonCancelarLogin;
    private JButton buttonCambiarLogin;
    private JButton buttonAceptarLogin;

    public LoginInterface() {
        mostrarLogin();
    }

    public void mostrarLogin() {
        frameLogin = new JFrame("Recetas");
        frameLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frameLogin.setSize(500, 400);
        frameLogin.setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));


        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;

        JLabel userLabel = new JLabel("ID");
        userLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 0;
        inputPanel.add(userLabel, gbc);

        textFieldIDLogin = new JTextField();
        textFieldIDLogin.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 0;
        inputPanel.add(textFieldIDLogin, gbc);

        JLabel passLabel = new JLabel("CLAVE");
        passLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridx = 0;
        gbc.gridy = 1;
        inputPanel.add(passLabel, gbc);

        passFieldPasswordLogin = new JPasswordField();
        passFieldPasswordLogin.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        inputPanel.add(passFieldPasswordLogin, gbc);

        mainPanel.add(inputPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setPreferredSize(new Dimension(450, 45));
        buttonAceptarLogin = new JButton("Aceptar");
        buttonAceptarLogin.setPreferredSize(new Dimension(100, 35));
        buttonAceptarLogin.setFocusPainted(false);

        buttonCancelarLogin = new JButton("Cancelar");
        buttonCancelarLogin.setPreferredSize(new Dimension(100, 35));
        buttonCancelarLogin.setFocusPainted(false);

        buttonCambiarLogin = new JButton("Cambiar clave");
        buttonCambiarLogin.setPreferredSize(new Dimension(160, 35));
        buttonCambiarLogin.setFocusPainted(false);
        buttonCambiarLogin.addActionListener(e -> {
            frameLogin.setVisible(false);
            mostrarCambiarPassword();
        });

        buttonSalirLogin = new JButton("Salir");
        buttonSalirLogin.setPreferredSize(new Dimension(100, 35));
        buttonSalirLogin.setFocusPainted(false);

        buttonPanel.add(buttonAceptarLogin);
        buttonPanel.add(buttonCancelarLogin);
        buttonPanel.add(buttonCambiarLogin);
        buttonPanel.add(buttonSalirLogin);


        mainPanel.add(buttonPanel, BorderLayout.SOUTH);


        buttonSalirLogin.addActionListener(e -> System.exit(0));
        buttonCancelarLogin.addActionListener(e -> {
            textFieldIDLogin.setText("");
            passFieldPasswordLogin.setText("");
        });
        buttonAceptarLogin.addActionListener(e -> {
            String usuario = textFieldIDLogin.getText();
            String password = new String(passFieldPasswordLogin.getPassword());

            if (usuario.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frameLogin,
                        "Debe completar todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        frameLogin.add(mainPanel);
        frameLogin.setVisible(true);
    }

    public void mostrarCambiarPassword() {
        if (frameChangePassword == null) {
            frameChangePassword = new JFrame("Cambiar Clave");
            frameChangePassword.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frameChangePassword.setSize(450, 350);
            frameChangePassword.setLocationRelativeTo(null);

            JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

            JPanel inputPanel = new JPanel(new GridBagLayout());
            inputPanel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 10, 10, 10);
            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 1.0;

            JLabel currentPassLabel = new JLabel("Clave Actual");
            currentPassLabel.setFont(new Font("Arial", Font.BOLD, 14));
            gbc.gridx = 0; gbc.gridy = 0;
            inputPanel.add(currentPassLabel, gbc);

            currentPassField = new JPasswordField();
            currentPassField.setPreferredSize(new Dimension(200, 30));
            gbc.gridx = 1; gbc.gridy = 0;
            inputPanel.add(currentPassField, gbc);

            JLabel newPassLabel = new JLabel("Clave Nueva");
            newPassLabel.setFont(new Font("Arial", Font.BOLD, 14));
            gbc.gridx = 0; gbc.gridy = 1;
            inputPanel.add(newPassLabel, gbc);

            newPassField = new JPasswordField();
            newPassField.setPreferredSize(new Dimension(200, 30));
            gbc.gridx = 1; gbc.gridy = 1;
            inputPanel.add(newPassField, gbc);

            JLabel confirmPassLabel = new JLabel("Confirmar Clave");
            confirmPassLabel.setFont(new Font("Arial", Font.BOLD, 14));
            gbc.gridx = 0; gbc.gridy = 2;
            inputPanel.add(confirmPassLabel, gbc);

            confirmPassField = new JPasswordField();
            confirmPassField.setPreferredSize(new Dimension(200, 30));
            gbc.gridx = 1; gbc.gridy = 2;
            inputPanel.add(confirmPassField, gbc);

            mainPanel.add(inputPanel, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
            aceptarButtonChange = new JButton("Aceptar");
            aceptarButtonChange.setPreferredSize(new Dimension(100, 35));
            aceptarButtonChange.setFocusPainted(false);
            buttonPanel.add(aceptarButtonChange);

            cancelarButtonChange = new JButton("Cancelar");
            cancelarButtonChange.setPreferredSize(new Dimension(100, 35));
            cancelarButtonChange.addActionListener(e -> frameChangePassword.dispose());
            buttonPanel.add(cancelarButtonChange);

            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            frameChangePassword.setContentPane(mainPanel);
        }

        currentPassField.setText("");
        newPassField.setText("");
        confirmPassField.setText("");

        frameChangePassword.setVisible(true);
    }

    public JFrame getFrameLogin() { return frameLogin; }
    public JTextField getTextFieldIDLogin() { return textFieldIDLogin; }
    public JPasswordField getPassFieldPasswordLogin() { return passFieldPasswordLogin; }
    public JButton getButtonSalirLogin() { return buttonSalirLogin; }
    public JButton getButtonCancelarLogin() { return buttonCancelarLogin; }
    public JButton getButtonCambiarLogin() { return buttonCambiarLogin; }
    public JButton getButtonAceptarLogin() { return buttonAceptarLogin; }

    private JPasswordField currentPassField;
    private JPasswordField newPassField;
    private JPasswordField confirmPassField;
    private JButton aceptarButtonChange;
    private JButton cancelarButtonChange;

    public JFrame getFrameChangePassword() { return frameChangePassword; }
    public JPasswordField getCurrentPassField() { return currentPassField; }
    public JPasswordField getNewPassField() { return newPassField; }
    public JPasswordField getConfirmPassField() { return confirmPassField; }
    public JButton getAceptarButtonChange() { return aceptarButtonChange; }

}
