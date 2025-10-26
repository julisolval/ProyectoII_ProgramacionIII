package main.java.Controlador;

import main.java.Vista.Interfaz;
import main.java.proxy.ProxyService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class ControladorUsuariosActivos {
    private final Interfaz vista;
    private final ProxyService proxyService;
    private Timer timerActualizacion;

    public ControladorUsuariosActivos(Interfaz vista, ProxyService proxyService) {
        this.vista = vista;
        this.proxyService = proxyService;

        this.vista.setControladorUsuarios(this);

        inicializarEventos();
        iniciarActualizacionAutomatica();
    }

    public void iniciarActualizacionAutomatica() {
        timerActualizacion = new Timer(5000, e -> {
            if (proxyService.isConectado()) {
                actualizarUsuarios();
            }
        });
        timerActualizacion.start();

        actualizarUsuarios();
    }


    private void inicializarEventos() {
        vista.getBtnSendMessage().addActionListener(e -> enviarMensaje());

        vista.getBtnReceiveMessages().addActionListener(e -> recibirMensajes());
    }

    private void enviarMensaje() {
        String destinatario = vista.getUsersList().getSelectedValue();
        if (destinatario == null) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Seleccione un usuario para enviar el mensaje.",
                    "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mensaje = JOptionPane.showInputDialog(vista.getFrame(),
                "Escriba el mensaje para " + destinatario + ":",
                "Enviar mensaje", JOptionPane.PLAIN_MESSAGE);

        if (mensaje == null || mensaje.trim().isEmpty()) return;

        JSONObject respuesta = proxyService.enviarMensaje(destinatario, mensaje);

        if ("éxito".equalsIgnoreCase(respuesta.optString("estado"))) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Mensaje enviado correctamente.",
                    "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error al enviar mensaje: " + respuesta.optString("mensaje"),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void recibirMensajes() {
        String usuarioActual = proxyService.getUsuarioActual();
        if (usuarioActual == null) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Sesión no válida.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JSONObject respuesta = proxyService.obtenerMensajes(usuarioActual);

        if (!"éxito".equalsIgnoreCase(respuesta.optString("estado"))) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error al obtener mensajes: " + respuesta.optString("mensaje"),
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JSONArray mensajes = respuesta.optJSONArray("datos");
        if (mensajes == null || mensajes.isEmpty()) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "No hay mensajes nuevos.",
                    "Información", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mensajes.length(); i++) {
            JSONObject msg = mensajes.getJSONObject(i);
            sb.append("De: ").append(msg.optString("remitente"))
                    .append("\nMensaje: ").append(msg.optString("texto"))
                    .append("\n───────────────\n");
        }

        JTextArea area = new JTextArea(sb.toString());
        area.setEditable(false);
        area.setFont(new java.awt.Font("Consolas", java.awt.Font.PLAIN, 12));
        JScrollPane scroll = new JScrollPane(area);
        scroll.setPreferredSize(new java.awt.Dimension(400, 250));

        JOptionPane.showMessageDialog(vista.getFrame(), scroll,
                "Mensajes recibidos", JOptionPane.INFORMATION_MESSAGE);
    }

    public void actualizarUsuarios() {
        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerUsuariosConectados();
                System.out.println("Respuesta completa de usuarios: " + respuesta.toString(2));

                if ("éxito".equalsIgnoreCase(respuesta.optString("estado"))) {
                    JSONArray datos = respuesta.optJSONArray("datos");
                    System.out.println("Número de usuarios recibidos: " + (datos != null ? datos.length() : 0));

                    List<String> usuarios = new ArrayList<>();
                    String usuarioActual = getUsuarioActual();

                    for (int i = 0; i < datos.length(); i++) {
                        String usuario = datos.getString(i);
                        System.out.println("Usuario " + i + ": " + usuario);

                        if (usuario != null && !usuario.trim().isEmpty() &&
                                !usuario.equals(usuarioActual) &&
                                !usuario.startsWith(usuarioActual + " -") &&
                                !usuario.contains("(null)") && !"null".equals(usuario)) {
                            usuarios.add(usuario);
                        }
                    }

                    System.out.println("Usuarios después de filtrar: " + usuarios.size());
                    vista.actualizarUsuariosConectados(usuarios);
                } else {
                    System.err.println("Error al obtener usuarios: " + respuesta.optString("mensaje"));
                }
            } catch (Exception e) {
                System.err.println("Error en actualizarUsuarios: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }


    public void manejarNotificacion(JSONObject notificacion) {
        System.out.println("Notificación recibida: " + notificacion.toString(2));

        String subtipo = notificacion.optString("subtipo", "");
        JSONObject datos = notificacion.optJSONObject("datos");

        if (datos == null) {
            System.out.println("Notificación sin datos");
            return;
        }

        switch (subtipo) {
            case "usuario_conectado":
                String nuevo = datos.optString("username", "(desconocido)");
                System.out.println("Usuario conectado: " + nuevo);
                actualizarUsuarios();
                break;

            case "usuario_desconectado":
                String off = datos.optString("username", "(desconocido)");
                System.out.println("Usuario desconectado: " + off);
                actualizarUsuarios();
                break;

            case "mensaje_recibido":
                String remitente = datos.optString("remitente");
                String texto = datos.optString("texto");
                String fecha = datos.optString("fecha", new Date().toString());

                System.out.println("Mensaje automático de " + remitente + ": " + texto);
                mostrarMensajeRecibido(remitente, texto);
                break;

            case "lista_usuarios_actualizada":
                JSONArray usuariosArray = datos.optJSONArray("usuarios");
                if (usuariosArray != null) {
                    List<String> usuarios = new ArrayList<>();
                    for (int i = 0; i < usuariosArray.length(); i++) {
                        usuarios.add(usuariosArray.getString(i));
                    }
                    vista.actualizarUsuariosConectados(usuarios);
                }
                break;

            default:
                System.out.println("Notificación desconocida: " + subtipo);
                break;
        }
    }

    private void mostrarMensajeRecibido(String remitente, String mensaje) {
        SwingUtilities.invokeLater(() -> {
            JDialog dialog = new JDialog(vista.getFrame(), "Nuevo mensaje", false);
            dialog.setLayout(new BorderLayout(10, 10));
            dialog.setSize(400, 200);
            dialog.setLocationRelativeTo(vista.getFrame());

            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

            JLabel lblRemitente = new JLabel("Mensaje de: " + remitente);
            lblRemitente.setFont(new Font("Arial", Font.BOLD, 14));

            JTextArea txtMensaje = new JTextArea(mensaje, 4, 30);
            txtMensaje.setWrapStyleWord(true);
            txtMensaje.setLineWrap(true);
            txtMensaje.setEditable(false);
            txtMensaje.setBackground(new Color(245, 245, 245));
            txtMensaje.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

            JButton btnCerrar = new JButton("Cerrar");
            btnCerrar.addActionListener(e -> dialog.dispose());

            panel.add(lblRemitente, BorderLayout.NORTH);
            panel.add(new JScrollPane(txtMensaje), BorderLayout.CENTER);

            JPanel panelSur = new JPanel(new FlowLayout());
            panelSur.add(btnCerrar);
            panel.add(panelSur, BorderLayout.SOUTH);

            dialog.add(panel);
            dialog.setVisible(true);
        });
    }

    public String getUsuarioActual() {
        if (proxyService != null) {
            String usuario = proxyService.getUsuarioActual();
            System.out.println("ProxyService retorna usuario actual: " + usuario);
            return usuario;
        }
        System.out.println("ProxyService es null al obtener usuario actual");
        return "";
    }
}
