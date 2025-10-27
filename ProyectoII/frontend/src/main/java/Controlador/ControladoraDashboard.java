package main.java.Controlador;

import main.java.Vista.Interfaz;
import main.java.proxy.ProxyService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.Calendar;

import org.jfree.chart.*;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.plot.*;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

public class ControladoraDashboard {
    private final Interfaz vista;
    private final ProxyService proxyService;
    private TableRowSorter<DefaultTableModel> sorter;

    public ControladoraDashboard(Interfaz vista, ProxyService proxyService) {
        this.vista = vista;
        this.proxyService = proxyService;
        inicializar();
    }

    private void inicializar() {
        vista.getBtnGenerarDashboard().addActionListener(e -> generarReporte());

        // 🔹 Cargar medicamentos al abrir dashboard
        cargarMedicamentos();

        // 🔹 Activar buscador dinámico
        vista.getTxtFiltroDash().getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filtrarMedicamentos(); }
            public void removeUpdate(DocumentEvent e) { filtrarMedicamentos(); }
            public void changedUpdate(DocumentEvent e) { filtrarMedicamentos(); }
        });
    }

    // ============================================================
    // 🔹 CARGAR MEDICAMENTOS DESDE LA BASE DE DATOS
    // ============================================================
    private void cargarMedicamentos() {
        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerMedicamentos();

                SwingUtilities.invokeLater(() -> {
                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONArray datos = respuesta.getJSONArray("datos");
                        DefaultTableModel model = (DefaultTableModel) vista.getTblDashMeds().getModel();
                        model.setRowCount(0);

                        for (int i = 0; i < datos.length(); i++) {
                            JSONObject med = datos.getJSONObject(i);
                            model.addRow(new Object[]{
                                    Boolean.FALSE,
                                    med.optString("codigo"),
                                    med.optString("nombre")
                            });
                        }

                        sorter = new TableRowSorter<>(model);
                        vista.getTblDashMeds().setRowSorter(sorter);
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al obtener medicamentos: " + respuesta.optString("mensaje"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(vista.getFrame(),
                        "Error cargando medicamentos: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    // ============================================================
    // 🔹 FILTRO DE MEDICAMENTOS POR NOMBRE O CÓDIGO
    // ============================================================
    private void filtrarMedicamentos() {
        if (sorter == null) return;
        String texto = vista.getTxtFiltroDash().getText().trim();
        if (texto.isEmpty()) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + texto));
        }
    }

    // ============================================================
    // 🔹 GENERAR REPORTE
    // ============================================================
    private void generarReporte() {
        int mesDesde = vista.getCmbMesDesde().getSelectedIndex() + 1;
        int anioDesde = (Integer) vista.getSpAnioDesde().getValue();
        int mesHasta = vista.getCmbMesHasta().getSelectedIndex() + 1;
        int anioHasta = (Integer) vista.getSpAnioHasta().getValue();

        // Obtener medicamentos seleccionados (✔)
        DefaultTableModel model = (DefaultTableModel) vista.getTblDashMeds().getModel();
        StringBuilder filtroMeds = new StringBuilder();
        for (int i = 0; i < model.getRowCount(); i++) {
            Boolean seleccionado = (Boolean) model.getValueAt(i, 0);
            if (Boolean.TRUE.equals(seleccionado)) {
                if (filtroMeds.length() > 0) filtroMeds.append(",");
                filtroMeds.append(model.getValueAt(i, 2)); // nombre
            }
        }

        // Validar fechas
        if (anioDesde > anioHasta || (anioDesde == anioHasta && mesDesde > mesHasta)) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "La fecha desde no puede ser mayor que la fecha hasta",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Convertir fechas a formato
        String desdeStr = String.format("%d-%02d-01", anioDesde, mesDesde);
        Calendar cal = Calendar.getInstance();
        cal.set(anioHasta, mesHasta - 1, 1);
        int ultimoDia = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        String hastaStr = String.format("%d-%02d-%02d", anioHasta, mesHasta, ultimoDia);

        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerEstadisticas(desdeStr, hastaStr,
                        filtroMeds.isEmpty() ? null : filtroMeds.toString());

                SwingUtilities.invokeLater(() -> {
                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONObject datos = respuesta.getJSONObject("datos");
                        actualizarDashboard(datos);
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error: " + respuesta.optString("mensaje"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(vista.getFrame(),
                        "Error generando reporte: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }

    // ============================================================
    // 🔹 ACTUALIZAR DASHBOARD CON DATOS
    // ============================================================
    // ============================================================
// 🔹 ACTUALIZAR DASHBOARD CON DATOS
// ============================================================
    // ============================================================
// 🔹 ACTUALIZAR DASHBOARD CON DATOS
// ============================================================
    private void actualizarDashboard(JSONObject datos) {
        try {
            // --- Pastel (Recetas por estado) ---
            int confeccionadas = datos.optInt("confeccionadas", 0);
            int proceso = datos.optInt("proceso", 0);
            int lista = datos.optInt("lista", 0);
            int entregadas = datos.optInt("entregadas", 0);
            actualizarGraficoPastel(confeccionadas, proceso, lista, entregadas);

            // --- Línea (Medicamentos por mes) ---
            JSONArray etiquetasMes = datos.optJSONArray("etiquetas_meses");
            JSONArray nombres = datos.optJSONArray("nombres_medicamentos");
            JSONArray series = datos.optJSONArray("series_medicamentos");

            if (etiquetasMes != null && nombres != null && series != null) {
                actualizarGraficoLineal(etiquetasMes, nombres, series);
            }

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error al procesar datos del dashboard: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ============================================================
// 🔹 GRÁFICO LINEAL (MEDICAMENTOS POR MES)
// ============================================================
    private void actualizarGraficoLineal(JSONArray etiquetasMes, JSONArray nombres, JSONArray series) {
        vista.getPanelLineaDashboard().removeAll();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < nombres.length(); i++) {
            String med = nombres.getString(i);
            JSONArray valores = series.getJSONArray(i);

            for (int j = 0; j < etiquetasMes.length(); j++) {
                String mes = etiquetasMes.getString(j);
                int cantidad = valores.getInt(j);
                dataset.addValue(cantidad, med, mes);
            }
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Cantidad de Medicamentos Prescritos por Mes",
                "Mes",
                "Cantidad",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        vista.getPanelLineaDashboard().setLayout(new BorderLayout());
        vista.getPanelLineaDashboard().add(new ChartPanel(chart), BorderLayout.CENTER);
        vista.getPanelLineaDashboard().revalidate();
        vista.getPanelLineaDashboard().repaint();
    }

    // ============================================================
    // 🔹 GRÁFICO PASTEL (RECETAS)
    // ============================================================
    private void actualizarGraficoPastel(int confeccionadas, int proceso, int lista, int entregadas) {
        vista.getPanelPastelDashboard().removeAll();

        DefaultPieDataset dataset = new DefaultPieDataset();
        dataset.setValue("Confeccionadas", confeccionadas);
        dataset.setValue("En proceso", proceso);
        dataset.setValue("Listas", lista);
        dataset.setValue("Entregadas", entregadas);

        JFreeChart chart = ChartFactory.createPieChart(
                "Recetas por Estado",
                dataset,
                true, true, false
        );

        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));

        vista.getPanelPastelDashboard().setLayout(new BorderLayout());
        vista.getPanelPastelDashboard().add(new ChartPanel(chart), BorderLayout.CENTER);
        vista.getPanelPastelDashboard().revalidate();
        vista.getPanelPastelDashboard().repaint();
    }

    // ============================================================
    // 🔹 GRÁFICO LINEAL (MEDICAMENTOS)
    // ============================================================
    private void actualizarGraficoLineal(JSONArray nombres, JSONArray cantidades) {
        vista.getPanelLineaDashboard().removeAll();

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        // Cada medicamento será una serie separada
        for (int i = 0; i < nombres.length(); i++) {
            String med = nombres.getString(i);
            int valor = cantidades.getInt(i);
            dataset.addValue(valor, med, ""); // eje X vacío (solo comparativa)
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Comparativa de Medicamentos Seleccionados",
                "Medicamento",
                "Cantidad Prescrita",
                dataset,
                PlotOrientation.VERTICAL,
                true, // Mostrar leyenda
                true,
                false
        );

        CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.WHITE);
        plot.setRangeGridlinePaint(Color.GRAY);

        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);

        vista.getPanelLineaDashboard().setLayout(new BorderLayout());
        vista.getPanelLineaDashboard().add(new ChartPanel(chart), BorderLayout.CENTER);
        vista.getPanelLineaDashboard().revalidate();
        vista.getPanelLineaDashboard().repaint();
    }


    private void actualizarGraficoPastelMedicamentos(JSONArray nombres, JSONArray porcentajes) {
        vista.getPanelPastelDashboard().removeAll();

        DefaultPieDataset dataset = new DefaultPieDataset();
        for (int i = 0; i < nombres.length(); i++) {
            dataset.setValue(nombres.getString(i), porcentajes.getDouble(i));
        }

        JFreeChart chart = ChartFactory.createPieChart(
                "Comparativa de Medicamentos Seleccionados",
                dataset,
                true, true, false
        );

        chart.setBackgroundPaint(Color.WHITE);
        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setLabelFont(new Font("Arial", Font.PLAIN, 12));

        vista.getPanelPastelDashboard().setLayout(new BorderLayout());
        vista.getPanelPastelDashboard().add(new ChartPanel(chart), BorderLayout.CENTER);
        vista.getPanelPastelDashboard().revalidate();
        vista.getPanelPastelDashboard().repaint();
    }



}
