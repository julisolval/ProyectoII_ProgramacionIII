package main.java.Controlador;

import org.json.JSONObject;
import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorUsuariosActivos {
    private List<String> usuariosConectados = new ArrayList<>();
    private DefaultListModel<String> listaUsuariosModel;

    public ControladorUsuariosActivos() {
        this.listaUsuariosModel = new DefaultListModel<>();
    }

    public void actualizarListaUsuarios(JSONObject notificacion) {
        String subtipo = notificacion.optString("subtipo", "");
        JSONObject datos = notificacion.optJSONObject("datos");

        if (datos == null) return;

        String username = datos.optString("username", "");
        String nombre = datos.optString("nombre", username);

        if (username.isEmpty()) return;

        SwingUtilities.invokeLater(() -> {
            if ("usuario_conectado".equals(subtipo)) {
                if (!usuariosConectados.contains(username)) {
                    usuariosConectados.add(username);
                    listaUsuariosModel.addElement(username + " - " + nombre);
                }
            } else if ("usuario_desconectado".equals(subtipo)) {
                usuariosConectados.remove(username);
                // Remover de la lista
                for (int i = 0; i < listaUsuariosModel.size(); i++) {
                    if (listaUsuariosModel.get(i).startsWith(username + " - ")) {
                        listaUsuariosModel.remove(i);
                        break;
                    }
                }
            }
            actualizarUIUsuarios();
        });
    }

    public void recibirMensaje(String remitente, String mensaje) {
        JOptionPane.showMessageDialog(null,
                "Mensaje de " + remitente + ":\n" + mensaje,
                "Nuevo Mensaje",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void actualizarUIUsuarios() {
        // Actualizar contador
        System.out.println("Usuarios conectados: " + usuariosConectados.size());
    }

    public List<String> getUsuariosConectados() {
        return new ArrayList<>(usuariosConectados);
    }

    public void manejarNotificacion(JSONObject notificacion) {
        String tipo = notificacion.optString("tipo", "");
        String subtipo = notificacion.optString("subtipo", "");

        switch (subtipo) {
            case "usuario_conectado":
            case "usuario_desconectado":
                actualizarListaUsuarios(notificacion);
                break;
            case "mensaje_recibido":
                JSONObject datos = notificacion.optJSONObject("datos");
                if (datos != null) {
                    recibirMensaje(
                            datos.getString("remitente"),
                            datos.getString("mensaje")
                    );
                }
                break;
        }
    }

    public DefaultListModel<String> getListaUsuariosModel() {
        return listaUsuariosModel;
    }
}