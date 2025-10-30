package main.java.Controlador;

import main.java.Modelo.CatalogoDeMedicamentos;
import main.java.Modelo.ListaDeMedicos;
import main.java.Modelo.ListaDePacientes;
import main.java.Vista.Interfaz;
import main.java.proxy.ProxyService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ControladorHistoricoRecetas {
    private Interfaz vista;
    private ProxyService proxyService;
    private ListaDePacientes listaPacientes;
    private CatalogoDeMedicamentos catalogo;
    private ListaDeMedicos listaMedicos;
    private DefaultTableModel modeloTablaHistorico;

    public ControladorHistoricoRecetas(Interfaz vista, ProxyService proxyService,
                                       ListaDePacientes listaPacientes, CatalogoDeMedicamentos catalogo,
                                       ListaDeMedicos listaMedicos) {
        this.vista = vista;
        this.proxyService = proxyService;
        this.listaPacientes = listaPacientes;
        this.catalogo = catalogo;
        this.listaMedicos = listaMedicos;
        this.modeloTablaHistorico = (DefaultTableModel) vista.getTblHistoricoRecetas().getModel();
        inicializar();
    }

    private void inicializar() {
        cargarHistoricoDesdeBackend();

        vista.getBtnBuscarHistorico().addActionListener(e -> buscarHistorico());
        vista.getBtnLimpiarHistorico().addActionListener(e -> limpiarFiltro()); // Cambiado a getBtnLimpiarHistorico
        // getBtnVerDetalles no existe - lo omitimos por ahora
    }

    private void cargarHistoricoDesdeBackend() {
        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerRecetas();
                SwingUtilities.invokeLater(() -> {
                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONArray array = respuesta.getJSONArray("datos");
                        actualizarTablaHistorico(array);
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Histórico cargado: " + array.length() + " recetas",
                                "Información",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al cargar histórico: " + respuesta.optString("mensaje"),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vista.getFrame(),
                            "Error al cargar histórico: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void actualizarTablaHistorico(JSONArray recetas) {
        modeloTablaHistorico.setRowCount(0);

        for (int i = 0; i < recetas.length(); i++) {
            JSONObject receta = recetas.getJSONObject(i);
            Object[] fila = {
                    receta.getInt("id"),                 // ID Receta
                    receta.getString("nombre_paciente"), // Paciente
                    receta.getString("fecha_confeccion"),// Fecha Creación
                    receta.getString("estado")           // Estado
            };
            modeloTablaHistorico.addRow(fila);
        }
    }

    private void buscarHistorico() {
        String filtro = vista.getTxtBusquedaHistorico().getText().trim();

        if (filtro.isEmpty()) {
            // Si no hay filtro, cargamos todo el histórico
            cargarHistoricoDesdeBackend();
            return;
        }

        int idBuscado;
        try {
            idBuscado = Integer.parseInt(filtro); // Convertimos a entero
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Ingrese un ID de receta válido (solo números).",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerRecetas();
                SwingUtilities.invokeLater(() -> {
                    modeloTablaHistorico.setRowCount(0); // Limpiamos la tabla

                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONArray array = respuesta.getJSONArray("datos");
                        boolean encontrado = false;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject receta = array.getJSONObject(i);
                            if (receta.getInt("id") == idBuscado) {

                                // 🔹 Mostramos la receta en la tabla
                                Object[] fila = {
                                        receta.getInt("id"),                 // ID Receta
                                        receta.getString("nombre_paciente"), // Paciente
                                        receta.getString("fecha_confeccion"),// Fecha Creación
                                        receta.getString("estado")           // Estado
                                };
                                modeloTablaHistorico.addRow(fila);
                                encontrado = true;

                                // 🔹 Mostramos los detalles en el área de texto
                                StringBuilder detalles = new StringBuilder();
                                detalles.append("ID Receta: ").append(receta.getInt("id")).append("\n");
                                detalles.append("Paciente: ").append(receta.optString("nombre_paciente", "No especificado")).append("\n");
                                detalles.append("Médico: ").append(receta.optString("nombre_medico", "No especificado")).append("\n");
                                detalles.append("Fecha de creación: ").append(receta.optString("fecha_confeccion", "N/A")).append("\n");
                                detalles.append("Estado: ").append(receta.optString("estado", "N/A")).append("\n\n");

                                // 🔹 Si tiene medicamentos asociados
                                if (receta.has("medicamentos")) {
                                    JSONArray meds = receta.getJSONArray("medicamentos");
                                    detalles.append("Medicamentos:\n");
                                    for (int j = 0; j < meds.length(); j++) {
                                        JSONObject med = meds.getJSONObject(j);
                                        detalles.append("- ").append(med.optString("nombre", "Desconocido"));
                                        if (med.has("dosis"))
                                            detalles.append(" (").append(med.optString("dosis")).append(")");
                                        detalles.append("\n");
                                    }
                                    detalles.append("\n");
                                }

                                // 🔹 Si tiene indicaciones
                                if (receta.has("indicaciones")) {
                                    detalles.append("Indicaciones: ").append(receta.optString("indicaciones", "Ninguna")).append("\n");
                                }

                                vista.getTxtDetallesHistorico().setText(detalles.toString());
                                break; // Ya encontramos la receta
                            }
                        }

                        if (encontrado) {
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Receta encontrada y mostrada con detalles.",
                                    "Información",
                                    JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            vista.getTxtDetallesHistorico().setText(""); // Limpiamos detalles si no se encontró
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "No se encontró ninguna receta con ese ID.",
                                    "Información",
                                    JOptionPane.INFORMATION_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al buscar histórico: " + respuesta.optString("mensaje"),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vista.getFrame(),
                            "Error al buscar histórico: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }


    private void limpiarFiltro() {
        vista.getTxtBusquedaHistorico().setText("");
        cargarHistoricoDesdeBackend();
    }
}