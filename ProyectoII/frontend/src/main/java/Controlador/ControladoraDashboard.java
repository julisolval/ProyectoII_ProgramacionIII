package main.java.Controlador;

import main.java.Vista.Interfaz;
import main.java.proxy.ProxyService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ControladoraDashboard {
    private Interfaz vista;
    private ProxyService proxyService;

    public ControladoraDashboard(Interfaz vista, ProxyService proxyService) {
        this.vista = vista;
        this.proxyService = proxyService;
        inicializar();
    }

    private void inicializar() {
        // Usar el botón que SÍ existe en tu Interfaz
        vista.getBtnGenerarDashboard().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generarReporte();
            }
        });
    }

    private void generarReporte() {
        // Obtener fechas de los componentes que SÍ existen
        int mesDesde = vista.getCmbMesDesde().getSelectedIndex() + 1;
        int anioDesde = (Integer) vista.getSpAnioDesde().getValue();
        int mesHasta = vista.getCmbMesHasta().getSelectedIndex() + 1;
        int anioHasta = (Integer) vista.getSpAnioHasta().getValue();

        String medicamentoFiltro = vista.getTxtFiltroDash().getText().trim();

        // Validar fechas
        if (anioDesde > anioHasta || (anioDesde == anioHasta && mesDesde > mesHasta)) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "La fecha desde no puede ser mayor que la fecha hasta",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Convertir a formato String para el backend
        String desdeStr = String.format("%d-%02d-01", anioDesde, mesDesde);

        Calendar cal = Calendar.getInstance();
        cal.set(anioHasta, mesHasta - 1, 1);
        int ultimoDia = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        String hastaStr = String.format("%d-%02d-%02d", anioHasta, mesHasta, ultimoDia);

        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerEstadisticas(desdeStr, hastaStr,
                        medicamentoFiltro.isEmpty() ? null : medicamentoFiltro);

                SwingUtilities.invokeLater(() -> {
                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONObject datos = respuesta.getJSONObject("datos");
                        actualizarDashboard(datos);
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al generar reporte: " + respuesta.optString("mensaje"),
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

    private void actualizarDashboard(JSONObject datos) {
        try {
            int confeccionadas = datos.getInt("confeccionadas");
            int proceso = datos.getInt("proceso");
            int lista = datos.getInt("lista");
            int entregadas = datos.getInt("entregadas");
            int total = datos.getInt("total");

            // Mostrar en consola por ahora (luego puedes agregar labels reales)
            System.out.println("📊 Estadísticas del Dashboard:");
            System.out.println("   - Confeccionadas: " + confeccionadas);
            System.out.println("   - En proceso: " + proceso);
            System.out.println("   - Listas: " + lista);
            System.out.println("   - Entregadas: " + entregadas);
            System.out.println("   - Total: " + total);

            actualizarGraficoPastel(confeccionadas, proceso, lista, entregadas);

            JSONArray nombresMedicamentos = datos.getJSONArray("nombres_medicamentos");
            JSONArray cantidadesMedicamentos = datos.getJSONArray("cantidades_medicamentos");
            actualizarGraficoBarras(nombresMedicamentos, cantidadesMedicamentos);

            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Dashboard actualizado correctamente\n" +
                            "Confeccionadas: " + confeccionadas + "\n" +
                            "En proceso: " + proceso + "\n" +
                            "Listas: " + lista + "\n" +
                            "Entregadas: " + entregadas + "\n" +
                            "Total: " + total,
                    "Dashboard Actualizado",
                    JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error al procesar datos del dashboard: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void actualizarGraficoPastel(int confeccionadas, int proceso, int lista, int entregadas) {
        System.out.println("🎨 Actualizando gráfico de pastel:");
        System.out.println("   - Confeccionadas: " + confeccionadas);
        System.out.println("   - Proceso: " + proceso);
        System.out.println("   - Listas: " + lista);
        System.out.println("   - Entregadas: " + entregadas);
    }

    private void actualizarGraficoBarras(JSONArray nombres, JSONArray cantidades) {
        System.out.println("📈 Actualizando gráfico de barras - Top medicamentos:");
        for (int i = 0; i < nombres.length(); i++) {
            System.out.println("   - " + nombres.getString(i) + ": " + cantidades.getInt(i));
        }
    }
}