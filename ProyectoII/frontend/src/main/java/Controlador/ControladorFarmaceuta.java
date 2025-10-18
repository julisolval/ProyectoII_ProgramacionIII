package main.java.Controlador;

import main.java.Modelo.Farmaceutas;
import main.java.Modelo.ListaDeFarmaceutas;
import main.java.Vista.Interfaz;
import main.java.proxy.ProxyService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ControladorFarmaceuta {
    private Interfaz vista;
    private ListaDeFarmaceutas modelo;
    private ProxyService proxyService;
    private DefaultTableModel modeloTabla;

    public ControladorFarmaceuta(Interfaz vista, ListaDeFarmaceutas modelo, ProxyService proxyService) {
        this.vista = vista;
        this.modelo = modelo;
        this.proxyService = proxyService;
        this.modeloTabla = (DefaultTableModel) vista.getTablaFarmaceuticos().getModel();

        inicializar();
        cargarFarmaceutasDesdeBackend();
    }

    private void inicializar() {
        configurarListeners();
    }

    private void configurarListeners() {
        vista.getBtnGuardarFarmaceutico().addActionListener(e -> guardarFarmaceuta());
        vista.getBtnLimpiarFarmaceutico().addActionListener(e -> limpiarCamposFarmaceuta());
        vista.getBtnBorrarFarmaceutico().addActionListener(e -> borrarFarmaceuta());
        vista.getBtnBuscarFarmaceutico().addActionListener(e -> buscarFarmaceuta());
        vista.getBtnReporteFarmaceutico().addActionListener(e -> generarReportePDFFarmaceuta());
    }

    public void cargarFarmaceutasDesdeBackend() {
        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerFarmaceuticos();
                SwingUtilities.invokeLater(() -> {
                    modelo.getFarmaceutas().clear();
                    modeloTabla.setRowCount(0);

                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONArray array = respuesta.getJSONArray("datos");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject farmaJson = array.getJSONObject(i);
                            String id = farmaJson.getString("id");
                            String nombre = farmaJson.getString("nombre");

                            Farmaceutas farmaceuta = new Farmaceutas(id, nombre);
                            modelo.agregarFarmaceuta(farmaceuta);

                            Object[] fila = {id, nombre};
                            modeloTabla.addRow(fila);
                        }
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al cargar farmacéuticos: " + respuesta.optString("mensaje"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vista.getFrame(),
                            "Error al cargar farmacéuticos: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void guardarFarmaceuta() {
        try {
            String id = vista.getTxtIdFarmaceutico().getText().trim();
            String nombre = vista.getTxtNombreFarmaceutico().getText().trim();

            if (id.isEmpty() || nombre.isEmpty()) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (modelo.buscarPorID(id) != null) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Ya existe un farmacéutico con este ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            new Thread(() -> {
                try {
                    JSONObject respuesta = proxyService.guardarFarmaceutico(id, nombre);
                    SwingUtilities.invokeLater(() -> {
                        if ("éxito".equals(respuesta.optString("estado"))) {
                            Farmaceutas nuevoFarmaceutico = new Farmaceutas(id, nombre);
                            modelo.agregarFarmaceuta(nuevoFarmaceutico);

                            Object[] fila = {id, nombre};
                            modeloTabla.addRow(fila);

                            limpiarCamposFarmaceuta();
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Farmacéutico guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Error al guardar farmacéutico: " + respuesta.optString("mensaje"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al guardar farmacéutico: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error al guardar farmacéutico: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarFarmaceuta() {
        try {
            String id = vista.getTxtBusquedaIdFarmaceutico().getText().trim().toLowerCase();
            String nombre = vista.getTxtBusquedaNombreFarmaceutico().getText().trim().toLowerCase();

            modeloTabla.setRowCount(0);

            for (Farmaceutas farmaceuta : modelo.getFarmaceutas()) {
                boolean coincide = true;

                if (!id.isEmpty()) {
                    if (!farmaceuta.getId().toLowerCase().contains(id)) {
                        coincide = false;
                    }
                }

                if (!nombre.isEmpty()) {
                    if (!farmaceuta.getNombre().toLowerCase().contains(nombre)) {
                        coincide = false;
                    }
                }

                if (coincide) {
                    Object[] row = {farmaceuta.getId(), farmaceuta.getNombre()};
                    modeloTabla.addRow(row);
                }
            }

            if (modeloTabla.getRowCount() == 0 && (!id.isEmpty() || !nombre.isEmpty())) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "No se encontraron farmacéuticos con los criterios de búsqueda",
                        "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error en búsqueda: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrarFarmaceuta() {
        try {
            String id = vista.getTxtIdFarmaceutico().getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Ingrese un ID para borrar", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Farmaceutas farmaceuta = modelo.buscarPorID(id);
            if (farmaceuta == null) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Farmacéutico no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            new Thread(() -> {
                try {
                    JSONObject respuesta = proxyService.eliminarFarmaceutico(id);
                    SwingUtilities.invokeLater(() -> {
                        if ("éxito".equals(respuesta.optString("estado"))) {
                            modelo.getFarmaceutas().remove(farmaceuta);
                            cargarFarmaceutasDesdeBackend();
                            limpiarCamposFarmaceuta();
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Farmacéutico borrado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Error al borrar farmacéutico: " + respuesta.optString("mensaje"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al borrar farmacéutico: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error al borrar farmacéutico: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCamposFarmaceuta() {
        vista.getTxtIdFarmaceutico().setText("");
        vista.getTxtNombreFarmaceutico().setText("");
        vista.getTxtBusquedaIdFarmaceutico().setText("");
        vista.getTxtBusquedaNombreFarmaceutico().setText("");
    }

    private void generarReportePDFFarmaceuta() {
        // Mantener tu implementación existente de PDF
        JOptionPane.showMessageDialog(vista.getFrame(),
                "Generando reporte PDF de farmacéuticos...",
                "Reporte", JOptionPane.INFORMATION_MESSAGE);
    }
}