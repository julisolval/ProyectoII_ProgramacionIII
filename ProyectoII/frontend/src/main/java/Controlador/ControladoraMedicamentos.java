package main.java.Controlador;

import main.java.Modelo.CatalogoDeMedicamentos;
import main.java.Modelo.Medicamento;
import main.java.Vista.Interfaz;
import main.java.proxy.ProxyService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.util.HashMap;
import java.util.Map;

public class ControladoraMedicamentos {
    private Interfaz vista;
    private CatalogoDeMedicamentos modelo;
    private ProxyService proxyService;
    private DefaultTableModel modeloTabla;

    public ControladoraMedicamentos(Interfaz vista, CatalogoDeMedicamentos modelo, ProxyService proxyService) {
        this.vista = vista;
        this.modelo = modelo;
        this.proxyService = proxyService;
        this.modeloTabla = (DefaultTableModel) vista.getTablaMedicamentos().getModel();

        inicializar();
        cargarMedicamentosDesdeBackend();
    }

    private void inicializar() {
        configurarListeners();
    }

    private void configurarListeners() {
        vista.getBtnGuardarMedicamento().addActionListener(e -> guardarMedicamento());
        vista.getBtnBuscarMedicamento().addActionListener(e -> buscarMedicamento());
        vista.getBtnBorrarMedicamento().addActionListener(e -> borrarMedicamento());
        vista.getBtnLimpiarMedicamento().addActionListener(e -> limpiarCamposMedicamento());

        if (vista.getBtnReporteMedicamento() != null) {
            vista.getBtnReporteMedicamento().addActionListener(e -> generarReportePDFMedicamento());
        }
    }

    public void cargarMedicamentosDesdeBackend() {
        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerMedicamentos();
                SwingUtilities.invokeLater(() -> {
                    modelo.getMedicamentos().clear();
                    modeloTabla.setRowCount(0);

                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONArray array = respuesta.getJSONArray("datos");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject medJson = array.getJSONObject(i);
                            String codigo = medJson.getString("codigo");
                            String nombre = medJson.getString("nombre");
                            String presentacion = medJson.getString("presentacion");

                            // ✅ SOLUCIÓN: Usar los datos directamente como vienen del backend
                            // NO intentar convertir a int/double

                            // Para la tabla, usar los Strings directamente
                            Object[] fila = {codigo, nombre, presentacion};
                            modeloTabla.addRow(fila);

                            // Si necesitas guardar en el modelo local, adapta el Medicamento
                            // o usa un Map temporal
                            Map<String, String> medicamentoMap = new HashMap<>();
                            medicamentoMap.put("codigo", codigo);
                            medicamentoMap.put("nombre", nombre);
                            medicamentoMap.put("presentacion", presentacion);
                            // modelo.agregarMedicamento(medicamentoMap); // Si tu modelo lo permite
                        }

                        System.out.println("✅ Medicamentos cargados: " + array.length());
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al cargar medicamentos: " + respuesta.optString("mensaje"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vista.getFrame(),
                            "Error al cargar medicamentos: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void guardarMedicamento() {
        try {
            String codigoStr = vista.getTxtCodigoMedicamento().getText().trim();
            String nombre = vista.getTxtNombreMedicamento().getText().trim();
            String presentacionStr = vista.getTxtPresentacion().getText().trim();

            if (codigoStr.isEmpty() || nombre.isEmpty() || presentacionStr.isEmpty()) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ✅ SOLUCIÓN: Verificar si ya existe (usando búsqueda en tabla)
            boolean existe = false;
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String codigoEnTabla = (String) modeloTabla.getValueAt(i, 0);
                if (codigoEnTabla.equals(codigoStr)) {
                    existe = true;
                    break;
                }
            }

            if (existe) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Ya existe un medicamento con este código", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ✅ SOLUCIÓN: Guardar en backend (usa Strings directamente)
            new Thread(() -> {
                try {
                    JSONObject respuesta = proxyService.guardarMedicamento(codigoStr, nombre, presentacionStr);
                    SwingUtilities.invokeLater(() -> {
                        if ("éxito".equals(respuesta.optString("estado"))) {
                            // ✅ Agregar directamente a la tabla
                            Object[] fila = {codigoStr, nombre, presentacionStr};
                            modeloTabla.addRow(fila);

                            limpiarCamposMedicamento();
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Medicamento guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Error al guardar medicamento: " + respuesta.optString("mensaje"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al guardar medicamento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error al guardar medicamento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrarMedicamento() {
        try {
            String codigoStr = vista.getTxtCodigoMedicamento().getText().trim();
            if (codigoStr.isEmpty()) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Ingrese el código del medicamento a borrar", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ✅ SOLUCIÓN: Buscar en la tabla directamente
            boolean encontrado = false;
            for (int i = 0; i < modeloTabla.getRowCount(); i++) {
                String codigoEnTabla = (String) modeloTabla.getValueAt(i, 0);
                if (codigoEnTabla.equals(codigoStr)) {
                    encontrado = true;
                    break;
                }
            }

            if (!encontrado) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "No se encontró un medicamento con ese código", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // ✅ Eliminar del backend
            new Thread(() -> {
                try {
                    JSONObject respuesta = proxyService.eliminarMedicamento(codigoStr);
                    SwingUtilities.invokeLater(() -> {
                        if ("éxito".equals(respuesta.optString("estado"))) {
                            // Recargar toda la tabla desde el backend
                            cargarMedicamentosDesdeBackend();
                            limpiarCamposMedicamento();
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Medicamento borrado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Error al borrar medicamento: " + respuesta.optString("mensaje"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al borrar medicamento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error al borrar medicamento: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarMedicamento() {
        try {
            String codigoStr = vista.getTxtBusquedaCodigo().getText().trim();
            String nombreStr = vista.getTxtBusquedaNombreMedicamento().getText().trim();

            DefaultTableModel model = (DefaultTableModel) vista.getTablaMedicamentos().getModel();
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            vista.getTablaMedicamentos().setRowSorter(sorter);

            java.util.List<RowFilter<Object, Object>> filtros = new java.util.ArrayList<>();

            if (!codigoStr.isEmpty()) {
                // Filtra por columna 0 = Código
                filtros.add(RowFilter.regexFilter("(?i)" + codigoStr, 0));
            }

            if (!nombreStr.isEmpty()) {
                // Filtra por columna 1 = Nombre
                filtros.add(RowFilter.regexFilter("(?i)" + nombreStr, 1));
            }

            RowFilter<Object, Object> rf = filtros.isEmpty() ? null : RowFilter.andFilter(filtros);
            sorter.setRowFilter(rf);

            // Mensaje si no hay coincidencias
            if (sorter.getViewRowCount() == 0 && (!codigoStr.isEmpty() || !nombreStr.isEmpty())) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "No se encontraron medicamentos con los criterios de búsqueda",
                        "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error en búsqueda: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCamposMedicamento() {
        vista.getTxtCodigoMedicamento().setText("");
        vista.getTxtNombreMedicamento().setText("");
        vista.getTxtPresentacion().setText("");
        vista.getTxtBusquedaCodigo().setText("");
        vista.getTxtBusquedaNombreMedicamento().setText("");
    }

    private void generarReportePDFMedicamento() {
        // Implementación de generación de PDF (mantener tu código existente)
        JOptionPane.showMessageDialog(vista.getFrame(),
                "Generando reporte PDF de medicamentos...",
                "Reporte", JOptionPane.INFORMATION_MESSAGE);
    }
}