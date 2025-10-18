package main.java.Controlador;

import main.java.Modelo.ListaDeMedicos;
import main.java.Modelo.Medico;
import main.java.Vista.Interfaz;
import main.java.proxy.ProxyService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class ControladorMedicos {
    private Interfaz vista;
    private ListaDeMedicos listaMedicos;
    private ProxyService proxyService;
    private DefaultTableModel modeloTablaMedicos;

    public ControladorMedicos(Interfaz vista, ListaDeMedicos listaMedicos, ProxyService proxyService) {
        this.vista = vista;
        this.listaMedicos = listaMedicos;
        this.proxyService = proxyService;
        this.modeloTablaMedicos = (DefaultTableModel) vista.getTablaMedicos().getModel();
        inicializar();
        cargarMedicosDesdeBackend();
    }

    private void inicializar() {
        // CORRECCIÓN: Usar los nombres CORRECTOS de los métodos que SÍ existen en Interfaz
        vista.getBtnGuardar().addActionListener(e -> guardarMedico());
        vista.getBtnBorrar().addActionListener(e -> eliminarMedico());
        vista.getBtnBuscar().addActionListener(e -> buscarMedico());
        vista.getBtnLimpiar().addActionListener(e -> limpiarCamposMedico());

        // Si existe el botón de reporte
        if (vista.getBtnReporte() != null) {
            vista.getBtnReporte().addActionListener(e -> generarReporteMedico());
        }
    }

    public void cargarMedicosDesdeBackend() {
        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerMedicos();
                SwingUtilities.invokeLater(() -> {
                    listaMedicos.getMedicos().clear();
                    modeloTablaMedicos.setRowCount(0);

                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONArray array = respuesta.getJSONArray("datos");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject medicoJson = array.getJSONObject(i);
                            Medico medico = new Medico(
                                    medicoJson.getString("id"),
                                    medicoJson.getString("nombre"),
                                    medicoJson.getString("especialidad")
                            );
                            listaMedicos.getMedicos().add(medico);

                            Object[] fila = {
                                    medico.getId(),
                                    medico.getNombre(),
                                    medico.getEspecialidad()
                            };
                            modeloTablaMedicos.addRow(fila);
                        }

                        System.out.println("✅ Médicos cargados: " + array.length());
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al cargar médicos: " + respuesta.optString("mensaje"),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vista.getFrame(),
                            "Error al cargar médicos: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void guardarMedico() {
        String id = vista.getTxtIdMedico().getText().trim();
        String nombre = vista.getTxtNombreMedico().getText().trim();
        String especialidad = vista.getTxtEspecialidad().getText().trim();

        if (id.isEmpty() || nombre.isEmpty() || especialidad.isEmpty()) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Todos los campos son obligatorios",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.guardarMedico(id, nombre, especialidad);
                SwingUtilities.invokeLater(() -> {
                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Médico guardado exitosamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                        cargarMedicosDesdeBackend();
                        limpiarCamposMedico();
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al guardar médico: " + respuesta.optString("mensaje"),
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

    private void eliminarMedico() {
        String id = vista.getTxtIdMedico().getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Seleccione un médico para eliminar",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(vista.getFrame(),
                "¿Está seguro de eliminar al médico " + id + "?",
                "Confirmar Eliminación",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.eliminarMedico(id);
                SwingUtilities.invokeLater(() -> {
                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Médico eliminado exitosamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                        cargarMedicosDesdeBackend();
                        limpiarCamposMedico();
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al eliminar médico: " + respuesta.optString("mensaje"),
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

    private void buscarMedico() {
        String filtroId = vista.getTxtBusquedaId().getText().trim().toLowerCase();
        String filtroNombre = vista.getTxtBusquedaNombre().getText().trim().toLowerCase();

        if (filtroId.isEmpty() && filtroNombre.isEmpty()) {
            cargarMedicosDesdeBackend();
            return;
        }

        modeloTablaMedicos.setRowCount(0);
        for (Medico medico : listaMedicos.getMedicos()) {
            boolean coincide = true;

            if (!filtroId.isEmpty()) {
                if (!medico.getId().toLowerCase().contains(filtroId)) {
                    coincide = false;
                }
            }

            if (!filtroNombre.isEmpty()) {
                if (!medico.getNombre().toLowerCase().contains(filtroNombre)) {
                    coincide = false;
                }
            }

            if (coincide) {
                Object[] fila = {
                        medico.getId(),
                        medico.getNombre(),
                        medico.getEspecialidad()
                };
                modeloTablaMedicos.addRow(fila);
            }
        }
    }

    private void limpiarCamposMedico() {
        vista.getTxtIdMedico().setText("");
        vista.getTxtNombreMedico().setText("");
        vista.getTxtEspecialidad().setText("");
        vista.getTxtBusquedaId().setText("");
        vista.getTxtBusquedaNombre().setText("");
    }

    private void generarReporteMedico() {
        JOptionPane.showMessageDialog(vista.getFrame(),
                "Generando reporte de médicos...",
                "Reporte",
                JOptionPane.INFORMATION_MESSAGE);
    }
}