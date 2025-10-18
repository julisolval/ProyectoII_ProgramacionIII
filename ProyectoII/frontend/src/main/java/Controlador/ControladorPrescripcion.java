package main.java.Controlador;

import main.java.Modelo.CatalogoDeMedicamentos;
import main.java.Modelo.ListaDePacientes;
import main.java.Vista.Interfaz;
import main.java.proxy.ProxyService;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ControladorPrescripcion {
    private Interfaz vista;
    private ListaDePacientes listaPacientes;
    private CatalogoDeMedicamentos catalogo;
    private ProxyService proxyService;
    private DefaultTableModel modeloDetalles;
    private List<JSONObject> detallesTemporales;
    private String medicoActual;

    public ControladorPrescripcion(Interfaz vista, ListaDePacientes listaPacientes,
                                   CatalogoDeMedicamentos catalogo, ProxyService proxyService) {
        this.vista = vista;
        this.listaPacientes = listaPacientes;
        this.catalogo = catalogo;
        this.proxyService = proxyService;
        this.modeloDetalles = (DefaultTableModel) vista.getTablaDetallesPrescripcion().getModel();
        this.detallesTemporales = new ArrayList<>();
        this.medicoActual = "MED-001"; // Esto debería venir del login
        inicializar();
    }

    private void inicializar() {
        // CORRECCIÓN: Usar los nombres CORRECTOS de los métodos que SÍ existen en Interfaz
        vista.getBtnBuscarPacientePrescripcion().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                buscarPaciente();
            }
        });

        vista.getBtnAgregarMedicamentoPrescripcion().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                agregarMedicamento();
            }
        });

        vista.getBtnGuardarPrescripcion().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                guardarPrescripcion();
            }
        });

        vista.getBtnLimpiarPrescripcion().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                limpiarPrescripcion();
            }
        });

        vista.getBtnDescartarMedicamentoPrescripcion().addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                eliminarMedicamento();
            }
        });

        // Establecer fecha de retiro por defecto (3 días desde hoy)
        establecerFechaRetiroPorDefecto();
    }

    private void establecerFechaRetiroPorDefecto() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // Fecha de retiro en 3 días
        long tresDias = 3 * 24 * 60 * 60 * 1000L;
        Date fechaRetiro = new Date(System.currentTimeMillis() + tresDias);
        vista.getTxtFechaRetiroPrescricion().setText(sdf.format(fechaRetiro));
    }

    private void buscarPaciente() {
        String idPaciente = JOptionPane.showInputDialog(vista.getFrame(),
                "Ingrese el ID del paciente:",
                "Buscar Paciente",
                JOptionPane.QUESTION_MESSAGE);

        if (idPaciente != null && !idPaciente.trim().isEmpty()) {
            buscarPacienteEnBackend(idPaciente.trim());
        }
    }

    private void buscarPacienteEnBackend(String idPaciente) {
        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerPacientes();
                SwingUtilities.invokeLater(() -> {
                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONArray pacientes = respuesta.getJSONArray("datos");
                        boolean encontrado = false;

                        for (int i = 0; i < pacientes.length(); i++) {
                            JSONObject paciente = pacientes.getJSONObject(i);
                            if (paciente.getString("id").equals(idPaciente)) {
                                vista.getTxtBusquedaPacientePrescripcion().setText(
                                        paciente.getString("nombre") + " (" + idPaciente + ")"
                                );
                                encontrado = true;
                                break;
                            }
                        }

                        if (!encontrado) {
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Paciente no encontrado",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
                    }
                });
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(vista.getFrame(),
                            "Error buscando paciente: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                });
            }
        }).start();
    }

    private void agregarMedicamento() {
        String codigoMedicamento = JOptionPane.showInputDialog(vista.getFrame(),
                "Ingrese código del medicamento:",
                "Agregar Medicamento",
                JOptionPane.QUESTION_MESSAGE);

        if (codigoMedicamento != null && !codigoMedicamento.trim().isEmpty()) {
            buscarYAgregarMedicamento(codigoMedicamento.trim());
        }
    }

    private void buscarYAgregarMedicamento(String codigoMedicamento) {
        new Thread(() -> {
            try {
                JSONObject respuesta = proxyService.obtenerMedicamentos();
                SwingUtilities.invokeLater(() -> {
                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JSONArray medicamentos = respuesta.getJSONArray("datos");
                        boolean encontrado = false;

                        for (int i = 0; i < medicamentos.length(); i++) {
                            JSONObject med = medicamentos.getJSONObject(i);
                            if (med.getString("codigo").equals(codigoMedicamento)) {
                                // Pedir cantidad e indicaciones
                                String cantidadStr = JOptionPane.showInputDialog(vista.getFrame(),
                                        "Cantidad:", "30");
                                String indicaciones = JOptionPane.showInputDialog(vista.getFrame(),
                                        "Indicaciones:", "Tomar cada 8 horas");
                                String duracionStr = JOptionPane.showInputDialog(vista.getFrame(),
                                        "Duración (días):", "7");

                                if (cantidadStr != null && duracionStr != null) {
                                    try {
                                        int cantidad = Integer.parseInt(cantidadStr);
                                        int duracion = Integer.parseInt(duracionStr);

                                        // Agregar a la tabla temporal
                                        JSONObject detalle = new JSONObject();
                                        detalle.put("codigoMedicamento", codigoMedicamento);
                                        detalle.put("nombre", med.getString("nombre"));
                                        detalle.put("presentacion", med.getString("presentacion"));
                                        detalle.put("cantidad", cantidad);
                                        detalle.put("indicaciones", indicaciones != null ? indicaciones : "");
                                        detalle.put("duracion", duracion);

                                        detallesTemporales.add(detalle);

                                        // Agregar a la tabla visual
                                        Object[] fila = {
                                                med.getString("nombre"),
                                                med.getString("presentacion"),
                                                cantidad,
                                                indicaciones != null ? indicaciones : "",
                                                duracion
                                        };
                                        modeloDetalles.addRow(fila);
                                        encontrado = true;
                                    } catch (NumberFormatException ex) {
                                        JOptionPane.showMessageDialog(vista.getFrame(),
                                                "Cantidad y duración deben ser números válidos",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE);
                                    }
                                }
                                break;
                            }
                        }

                        if (!encontrado) {
                            JOptionPane.showMessageDialog(vista.getFrame(),
                                    "Medicamento no encontrado",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE);
                        }
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

    private void eliminarMedicamento() {
        int filaSeleccionada = vista.getTablaDetallesPrescripcion().getSelectedRow();
        if (filaSeleccionada != -1) {
            modeloDetalles.removeRow(filaSeleccionada);
            if (filaSeleccionada < detallesTemporales.size()) {
                detallesTemporales.remove(filaSeleccionada);
            }
        }
    }

    private void guardarPrescripcion() {
        // Obtener ID del paciente del campo de búsqueda
        String textoPaciente = vista.getTxtBusquedaPacientePrescripcion().getText().trim();
        if (textoPaciente.isEmpty()) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Debe buscar y seleccionar un paciente primero",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Extraer ID del paciente del texto (formato: "Nombre (ID)")
        String idPaciente = "";
        if (textoPaciente.contains("(") && textoPaciente.contains(")")) {
            int start = textoPaciente.lastIndexOf("(") + 1;
            int end = textoPaciente.lastIndexOf(")");
            idPaciente = textoPaciente.substring(start, end);
        }

        String fechaRetiroStr = vista.getTxtFechaRetiroPrescricion().getText().trim();

        if (idPaciente.isEmpty() || fechaRetiroStr.isEmpty()) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Complete todos los campos obligatorios",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (detallesTemporales.isEmpty()) {
            JOptionPane.showMessageDialog(vista.getFrame(),
                    "Agregue al menos un medicamento a la receta",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        // Usar fecha actual para confección
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaConfeccionStr = sdf.format(new Date());

        final String idPacienteFinal = idPaciente;
        final String medicoActualFinal = medicoActual;
        final String fechaConfeccionStrFinal = fechaConfeccionStr;
        final String fechaRetiroStrFinal = fechaRetiroStr;
        final List<JSONObject> detallesTemporalesFinal = new ArrayList<>(detallesTemporales);

        new Thread(() -> {
            try {
                // USAR LAS VARIABLES FINALES EN EL LAMBDA
                JSONObject respuesta = proxyService.guardarReceta(idPacienteFinal, medicoActualFinal,
                        fechaConfeccionStrFinal, fechaRetiroStrFinal, detallesTemporalesFinal);

                SwingUtilities.invokeLater(() -> {
                    if ("éxito".equals(respuesta.optString("estado"))) {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Receta guardada exitosamente",
                                "Éxito",
                                JOptionPane.INFORMATION_MESSAGE);
                        limpiarPrescripcion();
                    } else {
                        JOptionPane.showMessageDialog(vista.getFrame(),
                                "Error al guardar receta: " + respuesta.optString("mensaje"),
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

    private void limpiarPrescripcion() {
        vista.getTxtBusquedaPacientePrescripcion().setText("");
        vista.getTxtFechaRetiroPrescricion().setText("");
        establecerFechaRetiroPorDefecto();
        modeloDetalles.setRowCount(0);
        detallesTemporales.clear();
    }
}