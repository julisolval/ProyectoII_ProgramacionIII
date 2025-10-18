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
        this.usuarioActual = "FARM-111";
        inicializar();
        cargarRecetasDesdeBackend();
    }

    private void inicializar() {
        // Configurar listeners para los botones de despacho
        vista.getBtnPonerProceso().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarEstadoReceta("PROCESO");
            }
        });

        vista.getBtnMarcarLista().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarEstadoReceta("LISTA");
            }
        });

        vista.getBtnEntregar().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                actualizarEstadoReceta("ENTREGADA");
            }
        });

        // Botón para refrescar
        vista.getBtnBuscarRecetas().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cargarRecetasDesdeBackend();
            }
        });

        vista.getBtnLimpiarRecetas().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarCampos();
            }
        });
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
                                    receta.getInt("id"),
                                    receta.getString("id_paciente"),
                                    receta.getString("nombre_paciente"),
                                    receta.getString("id_medico"),
                                    receta.getString("nombre_medico"),
                                    receta.getString("fecha_confeccion"),
                                    receta.getString("fecha_retiro"),
                                    receta.getString("estado")
                            };
                            modeloTablaRecetas.addRow(fila);
                        }

                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Recetas cargadas: " + array.length(),
                                "Información",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al cargar recetas: " + respuesta.optString("mensaje"),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vista.getFrame(),
                            "Error al cargar recetas: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void actualizarEstadoReceta(String nuevoEstado) {
        int filaSeleccionada = vista.getTblRecetas().getSelectedRow();

        if (filaSeleccionada == -1) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Por favor seleccione una receta de la tabla",
                    "Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int idReceta = (int) modeloTablaRecetas.getValueAt(filaSeleccionada, 0);
        String estadoActual = (String) modeloTablaRecetas.getValueAt(filaSeleccionada, 7);

        // Validar transición de estado
        if (!validarTransicionEstado(estadoActual, nuevoEstado)) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "No se puede cambiar de " + estadoActual + " a " + nuevoEstado,
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirmacion = JOptionPane.showConfirmDialog(vista.getFrame(),
                "¿Está seguro de cambiar el estado de la receta #" + idReceta + " a '" + nuevoEstado + "'?",
                "Confirmar Cambio",
                JOptionPane.YES_NO_OPTION);

        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }

        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.actualizarEstadoReceta(idReceta, nuevoEstado, usuarioActual);

                SwingUtilities.invokeLater(() -> {
                    if ("éxito".equals(respuesta.optString("estado"))) {
                        // Actualizar la tabla localmente
                        modeloTablaRecetas.setValueAt(nuevoEstado, filaSeleccionada, 7);
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Estado actualizado exitosamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al actualizar el estado: " + respuesta.optString("mensaje"),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vista.getFrame(),
                            "Error: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private boolean validarTransicionEstado(String estadoActual, String nuevoEstado) {
        switch (estadoActual) {
            case "CONFECCIONADA":
                return "PROCESO".equals(nuevoEstado);
            case "PROCESO":
                return "LISTA".equals(nuevoEstado);
            case "LISTA":
                return "ENTREGADA".equals(nuevoEstado);
            case "ENTREGADA":
                return false;
            default:
                return true;
        }
    }

    private void limpiarCampos() {
        vista.getTfCedulaPaciente().setText("");
        vista.getTfNombrePaciente().setText("");
    }
}