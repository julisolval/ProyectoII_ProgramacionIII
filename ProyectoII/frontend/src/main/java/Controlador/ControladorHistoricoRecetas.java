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
                    receta.getInt("id"),
                    receta.getString("id_paciente"),
                    receta.getString("nombre_paciente"),
                    receta.getString("id_medico"),
                    receta.getString("nombre_medico"),
                    receta.getString("fecha_confeccion"),
                    receta.getString("fecha_retiro"),
                    receta.getString("estado")
            };
            modeloTablaHistorico.addRow(fila);
        }
    }

    private void buscarHistorico() {
        String filtro = vista.getTxtBusquedaHistorico().getText().trim().toLowerCase();

        if (filtro.isEmpty()) {
            cargarHistoricoDesdeBackend();
            return;
        }

        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerRecetas();
                SwingUtilities.invokeLater(() -> {
                    modeloTablaHistorico.setRowCount(0);

                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONArray array = respuesta.getJSONArray("datos");
                        int resultados = 0;

                        for (int i = 0; i < array.length(); i++) {
                            JSONObject receta = array.getJSONObject(i);
                            boolean coincide =
                                    String.valueOf(receta.getInt("id")).contains(filtro) ||
                                            receta.getString("id_paciente").toLowerCase().contains(filtro) ||
                                            receta.getString("nombre_paciente").toLowerCase().contains(filtro) ||
                                            receta.getString("id_medico").toLowerCase().contains(filtro) ||
                                            receta.getString("nombre_medico").toLowerCase().contains(filtro) ||
                                            receta.getString("estado").toLowerCase().contains(filtro);

                            if (coincide) {
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
                                modeloTablaHistorico.addRow(fila);
                                resultados++;
                            }
                        }

                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Búsqueda completada. " + resultados + " resultados.",
                                "Información",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error en búsqueda: " + respuesta.optString("mensaje"),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vista.getFrame(),
                            "Error en búsqueda: " + e.getMessage(),
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