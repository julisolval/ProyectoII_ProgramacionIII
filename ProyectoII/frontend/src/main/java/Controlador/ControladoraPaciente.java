package main.java.Controlador;

import main.java.Modelo.ListaDePacientes;
import main.java.Modelo.Paciente;
import main.java.Modelo.Fecha;
import main.java.Vista.Calendario;
import main.java.Vista.Interfaz;
import main.java.proxy.ProxyService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ControladoraPaciente {
    private Interfaz vista;
    private ListaDePacientes modelo;
    private ProxyService proxyService;
    private DefaultTableModel modeloTabla;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

    public ControladoraPaciente(Interfaz vista, ListaDePacientes modelo, ProxyService proxyService) {
        this.vista = vista;
        this.modelo = modelo;
        this.proxyService = proxyService;
        this.modeloTabla = (DefaultTableModel) vista.getTablaPacientes().getModel();

        inicializar();
        cargarPacientesDesdeBackend();
    }

    private void inicializar() {
        configurarListeners();
    }

    private void configurarListeners() {
        vista.getBtnGuardarPaciente().addActionListener(e -> guardarPaciente());
        vista.getBtnBuscarPaciente().addActionListener(e -> buscarPaciente());
        vista.getBtnBorrarPaciente().addActionListener(e -> borrarPaciente());
        vista.getBtnLimpiarPaciente().addActionListener(e -> limpiarCamposPaciente());
        vista.getBtnCalendario().addActionListener(e -> abrirCalendario());

        if (vista.getBtnReportePaciente() != null) {
            vista.getBtnReportePaciente().addActionListener(e -> generarReportePDFPaciente());
        }
    }

    public void cargarPacientesDesdeBackend() {
        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerPacientes();
                SwingUtilities.invokeLater(() -> {
                    modelo.getPacientes().clear();
                    modeloTabla.setRowCount(0);

                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONArray array = respuesta.getJSONArray("datos");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject pacJson = array.getJSONObject(i);
                            String id = pacJson.getString("id");
                            String nombre = pacJson.getString("nombre");
                            String telefono = pacJson.getString("telefono");
                            String fechaNacimientoStr = pacJson.getString("fecha_nacimiento");

                            String[] partes = fechaNacimientoStr.split("-");
                            int anio = Integer.parseInt(partes[0]);
                            int mes = Integer.parseInt(partes[1]);
                            int dia = Integer.parseInt(partes[2]);

                            Fecha fechaNacimiento = new Fecha(anio, mes, dia);
                            Paciente paciente = new Paciente(id, nombre, telefono, fechaNacimiento);
                            modelo.agregarPaciente(paciente);

                            Object[] fila = {id, nombre, fechaNacimiento, telefono};
                            modeloTabla.addRow(fila);
                        }
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al cargar pacientes: " + respuesta.optString("mensaje"),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vista.getFrame(),
                            "Error al cargar pacientes: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void guardarPaciente() {
        try {
            String id = vista.getTxtIdPaciente().getText().trim();
            String nombre = vista.getTxtNombrePaciente().getText().trim();
            String fechaNacimientoStr = vista.getTxtFechaNacimiento().getText().trim();
            String telefono = vista.getTxtTelefono().getText().trim();

            if (id.isEmpty() || nombre.isEmpty() || fechaNacimientoStr.isEmpty() || telefono.isEmpty()) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Todos los campos son obligatorios", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (modelo.buscarPorID(id) != null) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Ya existe un paciente con este ID", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Guardar en backend
            new Thread(() -> {
                try {
                    JSONObject respuesta = proxyService.guardarPaciente(id, nombre, fechaNacimientoStr, telefono);
                    SwingUtilities.invokeLater(() -> {
                        if ("éxito".equals(respuesta.optString("estado"))) {
                            // Actualizar modelo local
                            String[] partes = fechaNacimientoStr.split("-");
                            int anio = Integer.parseInt(partes[0]);
                            int mes = Integer.parseInt(partes[1]);
                            int dia = Integer.parseInt(partes[2]);

                            Fecha fechaNacimiento = new Fecha(anio, mes, dia);
                            Paciente paciente = new Paciente(id, nombre, telefono, fechaNacimiento);
                            modelo.agregarPaciente(paciente);

                            // Actualizar tabla
                            Object[] fila = {id, nombre, fechaNacimiento, telefono};
                            modeloTabla.addRow(fila);

                            limpiarCamposPaciente();
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Paciente guardado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Error al guardar paciente: " + respuesta.optString("mensaje"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al guardar paciente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error al guardar paciente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void buscarPaciente() {
        try {
            String id = vista.getTxtBusquedaIdPaciente().getText().trim().toLowerCase();
            String nombre = vista.getTxtBusquedaNombrePaciente().getText().trim().toLowerCase();

            // Búsqueda local en datos cargados
            modeloTabla.setRowCount(0);

            for (Paciente paciente : modelo.getPacientes()) {
                boolean coincide = true;

                if (!id.isEmpty()) {
                    if (!paciente.getId().toLowerCase().contains(id)) {
                        coincide = false;
                    }
                }

                if (!nombre.isEmpty()) {
                    if (!paciente.getNombre().toLowerCase().contains(nombre)) {
                        coincide = false;
                    }
                }

                if (coincide) {
                    Object[] row = {
                            paciente.getId(),
                            paciente.getNombre(),
                            paciente.getFechaNacimiento(),
                            paciente.getNumeroTelefonico()
                    };
                    modeloTabla.addRow(row);
                }
            }

            if (modeloTabla.getRowCount() == 0 && (!id.isEmpty() || !nombre.isEmpty())) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "No se encontraron pacientes con los criterios de búsqueda",
                        "Búsqueda", JOptionPane.INFORMATION_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error en búsqueda: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void borrarPaciente() {
        try {
            String id = vista.getTxtIdPaciente().getText().trim();
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Ingrese un ID para borrar", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Paciente paciente = modelo.buscarPorID(id);
            if (paciente == null) {
                JOptionPane.showMessageDialog(vista.getFrame(),
                        "Paciente no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Eliminar del backend
            new Thread(() -> {
                try {
                    JSONObject respuesta = proxyService.eliminarPaciente(id);
                    SwingUtilities.invokeLater(() -> {
                        if ("éxito".equals(respuesta.optString("estado"))) {
                            modelo.getPacientes().remove(paciente);
                            cargarPacientesDesdeBackend();
                            limpiarCamposPaciente();
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Paciente borrado exitosamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Error al borrar paciente: " + respuesta.optString("mensaje"),
                                    "Error", JOptionPane.ERROR_MESSAGE);
                        }
                    });
                } catch (Exception ex) {
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al borrar paciente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
            }).start();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error al borrar paciente: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limpiarCamposPaciente() {
        vista.getTxtIdPaciente().setText("");
        vista.getTxtNombrePaciente().setText("");
        vista.getTxtFechaNacimiento().setText("");
        vista.getTxtTelefono().setText("");
        vista.getTxtBusquedaIdPaciente().setText("");
        vista.getTxtBusquedaNombrePaciente().setText("");
    }

    private void abrirCalendario() {
        try {
            Calendario calendario = new Calendario(vista.getFrame());
            calendario.setVisible(true);
            Date fechaSeleccionada = calendario.getSelectedDate();
            if (fechaSeleccionada != null) {
                vista.getTxtFechaNacimiento().setText(dateFormat.format(fechaSeleccionada));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Error al abrir el calendario: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void generarReportePDFPaciente() {
        // Mantener tu implementación existente de PDF
        JOptionPane.showMessageDialog(vista.getFrame(),
                "Generando reporte PDF de pacientes...",
                "Reporte", JOptionPane.INFORMATION_MESSAGE);
    }
}