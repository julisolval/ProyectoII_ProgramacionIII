package main.java.Controlador;

import main.java.Vista.Interfaz;
import main.java.proxy.ProxyService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ControladorDespacho {
    private Interfaz vista;
    private ProxyService proxyService;
    private DefaultTableModel modeloTablaRecetas;
    private String usuarioActual;

    public ControladorDespacho(Interfaz vista, ProxyService proxyService) {
        this.vista = vista;
        this.proxyService = proxyService;
        this.modeloTablaRecetas = (DefaultTableModel) vista.getTblRecetas().getModel();
        this.usuarioActual = proxyService.getUsuarioActual(); // Id del farmacéutico actual (debería venir del login)
        inicializar();
        cargarRecetasDesdeBackend();
    }

    private void inicializar() {
        vista.getBtnPonerProceso().addActionListener(e -> actualizarEstadoReceta("PROCESO"));
        vista.getBtnMarcarLista().addActionListener(e -> actualizarEstadoReceta("LISTA"));
        vista.getBtnEntregar().addActionListener(e -> actualizarEstadoReceta("ENTREGADA"));

        vista.getBtnBuscarRecetas().addActionListener(e -> buscarRecetasPorFiltro());

        vista.getBtnLimpiarRecetas().addActionListener(e -> limpiarCampos());
    }


    public void cargarRecetasDesdeBackend() {
        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerRecetas();
                SwingUtilities.invokeLater(() -> {
                    modeloTablaRecetas.setRowCount(0);

                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONArray array = respuesta.getJSONArray("datos");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject receta = array.getJSONObject(i);

                            Object[] fila = {
                                    receta.optInt("id"),
                                    receta.optString("id_paciente", ""),
                                    receta.optString("nombre_paciente", ""),
                                    receta.optString("fecha_retiro", ""),
                                    receta.optString("estado", "CONFECCIONADA")
                            };
                            modeloTablaRecetas.addRow(fila);
                        }
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al cargar recetas: " + respuesta.optString("mensaje"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(
                        vista.getFrame(), "Error al cargar recetas: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private void buscarRecetasPorFiltro() {
        String filtroCedula = vista.getTfCedulaPaciente().getText().trim();
        String filtroNombre = vista.getTfNombrePaciente().getText().trim().toLowerCase();

        if (filtroCedula.isEmpty() && filtroNombre.isEmpty()) {
            // Si no hay filtro, cargamos todo
            cargarRecetasDesdeBackend();
            return;
        }

        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerRecetas();
                SwingUtilities.invokeLater(() -> {
                    modeloTablaRecetas.setRowCount(0); // Limpiamos la tabla

                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONArray array = respuesta.getJSONArray("datos");
                        int coincidencias = 0;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject receta = array.getJSONObject(i);
                            String idPaciente = receta.optString("id_paciente", "");
                            String nombrePaciente = receta.optString("nombre_paciente", "").toLowerCase();

                            boolean coincideCedula = filtroCedula.isEmpty() || idPaciente.equals(filtroCedula);
                            boolean coincideNombre = filtroNombre.isEmpty() || nombrePaciente.contains(filtroNombre);

                            if (coincideCedula && coincideNombre) {
                                Object[] fila = {
                                        receta.optInt("id"),
                                        receta.optString("id_paciente", ""),
                                        receta.optString("nombre_paciente", ""),
                                        receta.optString("fecha_retiro", ""),
                                        receta.optString("estado", "CONFECCIONADA")
                                };
                                modeloTablaRecetas.addRow(fila);
                                coincidencias++;
                            }
                        }

                        if (coincidencias == 0) {
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "No se encontraron recetas con los filtros ingresados.",
                                    "Información",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }

                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al buscar recetas: " + respuesta.optString("mensaje"),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(vista.getFrame(),
                        "Error al buscar recetas: " + e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }


    private void actualizarEstadoReceta(String nuevoEstado) {
        int filaSeleccionada = vista.getTblRecetas().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Por favor seleccione una receta de la tabla",
                    "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idReceta = (int) modeloTablaRecetas.getValueAt(filaSeleccionada, 0);
        String estadoActual = (String) modeloTablaRecetas.getValueAt(filaSeleccionada, 4);

        if (!validarTransicionEstado(estadoActual, nuevoEstado)) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "No se puede cambiar de " + estadoActual + " a " + nuevoEstado,
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(vista.getFrame(),
                "¿Está seguro de cambiar el estado de la receta #" + idReceta + " a '" + nuevoEstado + "'?",
                "Confirmar Cambio", JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) return;

        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.actualizarEstadoReceta(idReceta, nuevoEstado, usuarioActual);

                SwingUtilities.invokeLater(() -> {
                    if ("éxito".equals(respuesta.optString("estado"))) {
                        modeloTablaRecetas.setValueAt(nuevoEstado, filaSeleccionada, 4);
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Estado actualizado exitosamente", "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al actualizar estado: " + respuesta.optString("mensaje"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(vista.getFrame(),
                        "Error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    private boolean validarTransicionEstado(String estadoActual, String nuevoEstado) {
        switch (estadoActual) {
            case "CONFECCIONADA": return "PROCESO".equals(nuevoEstado);
            case "PROCESO": return "LISTA".equals(nuevoEstado);
            case "LISTA": return "ENTREGADA".equals(nuevoEstado);
            case "ENTREGADA": return false;
            default: return true;
        }
    }

    private void limpiarCampos() {
        vista.getTfCedulaPaciente().setText("");
        vista.getTfNombrePaciente().setText("");
    }
}
