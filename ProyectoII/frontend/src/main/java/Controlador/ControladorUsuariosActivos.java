package main.java.Controlador;

import main.java.Vista.Interfaz;
import main.java.proxy.ProxyService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * ============================================================
 * 🧩 CONTROLADOR DE USUARIOS ACTIVOS Y MENSAJERÍA
 * ------------------------------------------------------------
 * Versión corregida para funcionar con Interfaz v2 + ProxyService.
 * Maneja envío, recepción y actualización de usuarios conectados.
 * ============================================================
 */
public class ControladorUsuariosActivos {
    private final Interfaz vista;
    private final ProxyService proxyService;

    public ControladorUsuariosActivos(Interfaz vista, ProxyService proxyService) {
        this.vista = vista;
        this.proxyService = proxyService;
        inicializarEventos();
    }

    /**
     * Inicializa los listeners de botones del panel lateral de chat
     */
    private void inicializarEventos() {
        // Enviar mensaje
        vista.getBtnSendMessage().addActionListener(e -> enviarMensaje());

        // Recibir mensajes manualmente
        vista.getBtnReceiveMessages().addActionListener(e -> recibirMensajes());
    }

    /**
     * Envía un mensaje al usuario seleccionado
     */
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

    /**
     * Recupera mensajes dirigidos al usuario actual
     */
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
            sb.append("📩 De: ").append(msg.optString("remitente"))
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

    /**
     * Actualiza la lista de usuarios conectados
     */
    public void actualizarUsuarios() {
        new Thread(() -> {
            JSONObject respuesta = proxyService.obtenerUsuariosConectados();
            if ("éxito".equalsIgnoreCase(respuesta.optString("estado"))) {
                JSONArray datos = respuesta.optJSONArray("datos");
                List<String> usuarios = new ArrayList<>();
                for (int i = 0; i < datos.length(); i++) {
                    usuarios.add(datos.getString(i));
                }
                vista.actualizarUsuariosConectados(usuarios);
            }
        }).start();
    }

    /**
     * Recibe notificaciones en tiempo real (llamado desde ProxyService)
     */
    public void manejarNotificacion(JSONObject notificacion) {
        String subtipo = notificacion.optString("subtipo", "");
        JSONObject datos = notificacion.optJSONObject("datos");

        if (datos == null) return;

        switch (subtipo) {
            case "usuario_conectado" -> {
                String nuevo = datos.optString("username", "(desconocido)");
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Se ha conectado: " + nuevo,
                        "Nuevo usuario", JOptionPane.INFORMATION_MESSAGE);
                actualizarUsuarios();
            }
            case "usuario_desconectado" -> {
                String off = datos.optString("username", "(desconocido)");
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Se ha desconectado: " + off,
                        "Usuario desconectado", JOptionPane.INFORMATION_MESSAGE);
                actualizarUsuarios();
            }
        }
    }
}
