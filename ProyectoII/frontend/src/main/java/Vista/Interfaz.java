package main.java.Vista;
import main.java.Controlador.ControladorUsuariosActivos;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.table.TableColumnModel;


public class Interfaz {
    private JFrame frame;
    private JPanel panelPrincipal;
    private JTabbedPane tabbedPane;

    private JPanel usersPanel;
    private DefaultListModel<String> usersListModel;
    private JList<String> usersList;
    private JButton btnSendMessage;
    private JButton btnReceiveMessages;
    private JLabel lblActiveUsers;

    private JPanel panelMedicos;
    private JTextField txtIdMedico, txtNombreMedico, txtEspecialidad, txtBusquedaNombre, txtBusquedaId;
    private JButton btnGuardar, btnLimpiar, btnBorrar, btnBuscar, btnReporte, btnCalendario;
    private JTable tablaMedicos;

    private JPanel panelFarmaceuticos;
    private JTextField txtIdFarmaceutico, txtBusquedaIdFarmaceutico, txtNombreFarmaceutico, txtBusquedaNombreFarmaceutico;
    private JButton btnGuardarFarmaceutico, btnLimpiarFarmaceutico, btnBorrarFarmaceutico, btnBuscarFarmaceutico, btnReporteFarmaceutico;
    private JTable tablaFarmaceuticos;

    private JPanel panelPacientes;
    private JTextField txtIdPaciente, txtNombrePaciente, txtFechaNacimiento, txtTelefono, txtBusquedaIdPaciente, txtBusquedaNombrePaciente;
    private JButton btnGuardarPaciente, btnLimpiarPaciente, btnBorrarPaciente, btnBuscarPaciente, btnReportePaciente;
    private JTable tablaPacientes;

    private JPanel panelMedicamentos;
    private JTextField txtCodigoMedicamento, txtNombreMedicamento, txtPresentacion, txtBusquedaNombreMedicamento, txtBusquedaCodigo;
    private JButton btnGuardarMedicamento, btnLimpiarMedicamento, btnBorrarMedicamento, btnBuscarMedicamento, btnReporteMedicamento;
    private JTable tablaMedicamentos;

    private JPanel panelPrescripcion;
    private JTextField txtBusquedaPacientePrescripcion, txtFechaRetiroPrescricion;
    private JTable tablaDetallesPrescripcion;
    private JButton btnBuscarPacientePrescripcion, btnAgregarMedicamentoPrescripcion, btnDescartarMedicamentoPrescripcion,
            btnDetallesMedicamentoPrescripcion, btnGuardarPrescripcion, btnLimpiarPrescripcion;

    private JPanel panelDashboard;
    private JButton btnGenerarDashboard;
    private JPanel panelPastelDashboard, panelLineaDashboard;
    private JComboBox<String> cmbMesDesde, cmbMesHasta;
    private JSpinner spAnioDesde, spAnioHasta;
    private JTextField txtFiltroDash;
    private JTable tblDashMeds;

    private JPanel panelHistorico;
    private JTextField txtBusquedaHistorico;
    private JButton btnBuscarHistorico, btnLimpiarHistorico;
    private JTable tblHistoricoRecetas;
    private JTextArea txtDetallesHistorico;

    private JPanel panelAcercaDe;

    private JPanel panelDespachar;
    private JTextField tfCedulaPaciente, tfNombrePaciente;
    private JButton btnBuscarRecetas, btnLimpiarRecetas;
    private JTable tblRecetas, tblDetalles;
    private JButton btnPonerProceso, btnMarcarLista, btnEntregar;
    private JLabel lblEstadoSeleccionado, lblFechaRetiro;
    private ControladorUsuariosActivos controladorUsuarios;

    public void setControladorUsuarios(ControladorUsuariosActivos controlador) {
        this.controladorUsuarios = controlador;
    }

    public Interfaz() { crearInterfaz(); }


    public JList<String> getUsersList() {
        return usersList;
    }

    public JButton getBtnSendMessage() {
        return btnSendMessage;
    }

    public JButton getBtnReceiveMessages() {
        return btnReceiveMessages;
    }


    public JFrame getFrame() { return frame; }

    public JButton getBtnReporte() {return btnReporte;}
    public JTextField getTxtIdMedico() { return txtIdMedico; }
    public JTextField getTxtNombreMedico() { return txtNombreMedico; }
    public JTextField getTxtEspecialidad() { return txtEspecialidad; }
    public JTextField getTxtBusquedaNombre() { return txtBusquedaNombre; }
    public JTextField getTxtBusquedaId() { return txtBusquedaId; }
    public JButton getBtnGuardar() { return btnGuardar; }
    public JButton getBtnLimpiar() { return btnLimpiar; }
    public JButton getBtnBorrar() { return btnBorrar; }
    public JButton getBtnBuscar() { return btnBuscar; }
    public JTable getTablaMedicos() { return tablaMedicos; }

    public JTextField getTxtIdFarmaceutico() { return txtIdFarmaceutico; }
    public JTextField getTxtNombreFarmaceutico(){ return txtNombreFarmaceutico; }
    public JTextField getTxtBusquedaNombreFarmaceutico(){ return txtBusquedaNombreFarmaceutico; }
    public JTable getTablaFarmaceuticos(){ return tablaFarmaceuticos; }
    public JButton getBtnReporteFarmaceutico(){ return btnReporteFarmaceutico; }
    public JButton getBtnGuardarFarmaceutico(){ return btnGuardarFarmaceutico; }
    public JButton getBtnLimpiarFarmaceutico(){ return btnLimpiarFarmaceutico; }
    public JButton getBtnBorrarFarmaceutico(){ return btnBorrarFarmaceutico;}
    public JButton getBtnBuscarFarmaceutico(){ return btnBuscarFarmaceutico; }
    public JTextField getTxtBusquedaIdFarmaceutico() { return txtBusquedaIdFarmaceutico; }

    public JTextField getTxtIdPaciente() { return txtIdPaciente; }
    public JTextField getTxtNombrePaciente() { return txtNombrePaciente; }
    public JTextField getTxtFechaNacimiento() { return txtFechaNacimiento; }
    public JTextField getTxtTelefono() { return txtTelefono; }
    public JTextField getTxtBusquedaIdPaciente() { return txtBusquedaIdPaciente; }
    public JTextField getTxtBusquedaNombrePaciente() { return txtBusquedaNombrePaciente; }
    public JButton getBtnGuardarPaciente() { return btnGuardarPaciente; }
    public JButton getBtnLimpiarPaciente() { return btnLimpiarPaciente; }
    public JButton getBtnBorrarPaciente() { return btnBorrarPaciente; }
    public JButton getBtnBuscarPaciente() { return btnBuscarPaciente; }
    public JButton getBtnCalendario() { return btnCalendario;}
    public JButton getBtnReportePaciente() { return btnReportePaciente; }
    public JTable getTablaPacientes() { return tablaPacientes; }

    public JTextField getTxtCodigoMedicamento() { return txtCodigoMedicamento; }
    public JTextField getTxtNombreMedicamento() { return txtNombreMedicamento; }
    public JTextField getTxtPresentacion() { return txtPresentacion; }
    public JTextField getTxtBusquedaCodigo() { return txtBusquedaCodigo; }
    public JTextField getTxtBusquedaNombreMedicamento() { return txtBusquedaNombreMedicamento; }
    public JButton getBtnGuardarMedicamento() { return btnGuardarMedicamento; }
    public JButton getBtnLimpiarMedicamento() { return btnLimpiarMedicamento; }
    public JButton getBtnBorrarMedicamento() { return btnBorrarMedicamento; }
    public JButton getBtnBuscarMedicamento() { return btnBuscarMedicamento; }
    public JButton getBtnReporteMedicamento() { return btnReporteMedicamento; }
    public JTable getTablaMedicamentos() { return tablaMedicamentos; }

    public AbstractButton getBtnBuscarPacientePrescripcion() { return btnBuscarPacientePrescripcion; }
    public AbstractButton getBtnAgregarMedicamentoPrescripcion() { return btnAgregarMedicamentoPrescripcion; }
    public AbstractButton getBtnGuardarPrescripcion() { return btnGuardarPrescripcion; }
    public AbstractButton getBtnLimpiarPrescripcion() { return btnLimpiarPrescripcion; }
    public AbstractButton getBtnDetallesMedicamentoPrescripcion(){ return btnDetallesMedicamentoPrescripcion; }
    public JTable getTablaDetallesPrescripcion() {return tablaDetallesPrescripcion;}
    public JTextField getTxtBusquedaPacientePrescripcion() { return txtBusquedaPacientePrescripcion;}
    public JTextField getTxtFechaRetiroPrescricion() { return txtFechaRetiroPrescricion; }
    public JButton getBtnDescartarMedicamentoPrescripcion() { return btnDescartarMedicamentoPrescripcion; }

    public JButton getBtnGenerarDashboard() { return btnGenerarDashboard; }
    public JComboBox<String> getCmbMesDesde() { return cmbMesDesde; }
    public JComboBox<String> getCmbMesHasta() { return cmbMesHasta; }
    public JSpinner getSpAnioDesde() { return spAnioDesde; }
    public JSpinner getSpAnioHasta() { return spAnioHasta; }
    public JTextField getTxtFiltroDash() { return txtFiltroDash; }

    public JTextField getTxtBusquedaHistorico() { return txtBusquedaHistorico; }
    public JButton getBtnBuscarHistorico() { return btnBuscarHistorico; }
    public JButton getBtnLimpiarHistorico() { return btnLimpiarHistorico; }
    public JTable getTblHistoricoRecetas() { return tblHistoricoRecetas; }

    public JTextField getTfCedulaPaciente() { return tfCedulaPaciente; }
    public JTextField getTfNombrePaciente() { return tfNombrePaciente; }
    public JButton getBtnBuscarRecetas() { return btnBuscarRecetas; }
    public JButton getBtnLimpiarRecetas() { return btnLimpiarRecetas; }
    public JTable getTblRecetas() { return tblRecetas; }
    public JButton getBtnPonerProceso() { return btnPonerProceso; }
    public JButton getBtnMarcarLista() { return btnMarcarLista; }
    public JButton getBtnEntregar() { return btnEntregar; }

    public void mostrar() { frame.setVisible(true); }


    private void crearInterfaz() {
        frame = new JFrame("Recetas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        frame.setSize(1100, 680);
        frame.setLocationRelativeTo(null);
        panelPrincipal = new JPanel(new BorderLayout(10, 10));
        panelPrincipal.setBorder(new EmptyBorder(8, 8, 8, 8));
        panelPrincipal.setBackground(new Color(250, 250, 255));

        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Arial", Font.PLAIN, 14));

        panelMedicos       = crearPanelMedicos();
        panelFarmaceuticos = crearPanelFarmaceuticos();
        panelPacientes     = crearPanelPacientes();
        panelMedicamentos  = crearPanelMedicamentos();
        panelPrescripcion  = crearPanelPrescripcion();
        panelDashboard     = crearPanelDashboard();
        panelAcercaDe      = crearPanelAcercaDe();
        JPanel panelHistorico = crearPanelHistorico();
        construirPestanaDespachar();

        tabbedPane.addTab("Medicos", panelMedicos);
        tabbedPane.addTab("Farmacéutas", panelFarmaceuticos);
        tabbedPane.addTab("Pacientes", panelPacientes);
        tabbedPane.addTab("Medicamentos", panelMedicamentos);
        tabbedPane.addTab("Prescripción", panelPrescripcion);
        tabbedPane.addTab("Dashboard", panelDashboard);
        tabbedPane.addTab("Historico", panelHistorico);
        tabbedPane.addTab("Acerca de...", panelAcercaDe);
        tabbedPane.addTab("Despachar", panelDespachar);

        usersPanel = crearPanelUsuariosActivos();

        panelPrincipal.add(usersPanel, BorderLayout.EAST);
        panelPrincipal.add(tabbedPane, BorderLayout.CENTER);

        frame.getContentPane().setBackground(new Color(245, 248, 255));
        frame.add(panelPrincipal);



    }

    private JPanel crearPanelUsuariosActivos() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1, true),
                "Usuarios Conectados",
                TitledBorder.CENTER,
                TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 13)
        ));
        panel.setPreferredSize(new Dimension(250, 0));
        panel.setBackground(new Color(245, 248, 255));

        usersListModel = new DefaultListModel<>();
        usersList = new JList<>(usersListModel);
        usersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        usersList.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        usersList.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 230)));
        JScrollPane scrollUsuarios = new JScrollPane(usersList);

        lblActiveUsers = new JLabel("0 usuarios conectados", SwingConstants.CENTER);
        lblActiveUsers.setFont(new Font("Segoe UI", Font.BOLD, 12));
        lblActiveUsers.setForeground(new Color(60, 60, 60));
        lblActiveUsers.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 8));
        btnSendMessage = new JButton("Enviar");
        btnReceiveMessages = new JButton("Recibir");

        btnSendMessage.setBackground(new Color(70, 130, 180));
        btnSendMessage.setForeground(Color.WHITE);
        btnSendMessage.setFocusPainted(false);
        btnReceiveMessages.setBackground(new Color(34, 139, 34));
        btnReceiveMessages.setForeground(Color.WHITE);
        btnReceiveMessages.setFocusPainted(false);

        botones.add(btnSendMessage);
        botones.add(btnReceiveMessages);

        panel.add(lblActiveUsers, BorderLayout.NORTH);
        panel.add(scrollUsuarios, BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }


    private JPanel crearPanelMedicos() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel panelDatos = new JPanel(new BorderLayout(10, 10));
        panelDatos.setBorder(BorderFactory.createTitledBorder(null, "Medico", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12)));

        JPanel sup = new JPanel(new GridBagLayout());
        sup.setBorder(new EmptyBorder(5, 5, 5, 5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel lId = new JLabel("Id");
        gbc.gridx=0; gbc.gridy=0;
        sup.add(lId, gbc);
        txtIdMedico = new JTextField();
        gbc.gridx=1; gbc.gridy=0; gbc.weightx=1;
        sup.add(txtIdMedico, gbc);

        JLabel lNom = new JLabel("Nombre");
        gbc.gridx=2; gbc.gridy=0; gbc.weightx=0;
        sup.add(lNom, gbc);
        txtNombreMedico = new JTextField();
        gbc.gridx=3; gbc.gridy=0; gbc.weightx=1;
        sup.add(txtNombreMedico, gbc);

        JLabel lEsp = new JLabel("Especialidad");
        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0;
        sup.add(lEsp, gbc);
        txtEspecialidad = new JTextField();
        gbc.gridx=1; gbc.gridy=1; gbc.weightx=1;
        sup.add(txtEspecialidad, gbc);

        JPanel btns = new JPanel(new GridLayout(2,2,5,10));
        btnGuardar = new JButton("Guardar");
        btnLimpiar  = new JButton("Limpiar");
        btnBorrar   = new JButton("Borrar");
        btns.add(btnGuardar); btns.add(btnLimpiar); btns.add(btnBorrar);

        gbc.gridx=4; gbc.gridy=0; gbc.gridheight=2; gbc.weightx=0;
        sup.add(btns, gbc);

        panelDatos.add(sup, BorderLayout.CENTER);

        JPanel panelBusqueda = new JPanel(new BorderLayout(10,5));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder(null, "Búsqueda", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12)));

        JPanel contenido = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5);
        g.fill = GridBagConstraints.HORIZONTAL;

        JLabel lBid = new JLabel("Id");
        g.gridx=0; g.gridy=0; contenido.add(lBid, g);
        txtBusquedaId = new JTextField();
        g.gridx=1; g.gridy=0; g.weightx=1; contenido.add(txtBusquedaId, g);

        JLabel lBnom = new JLabel("Nombre");
        g.gridx=2; g.gridy=0; g.weightx=0; contenido.add(lBnom, g);
        txtBusquedaNombre = new JTextField();
        g.gridx=3; g.gridy=0; g.weightx=1; contenido.add(txtBusquedaNombre, g);

        JPanel btnsBus = new JPanel(new GridLayout(1,2,5,5));
        btnBuscar = new JButton("Buscar");
        btnReporte = new JButton("Reporte");
        btnsBus.add(btnBuscar); btnsBus.add(btnReporte);

        g.gridx=4; g.gridy=0; g.weightx=0; contenido.add(btnsBus, g);
        panelBusqueda.add(contenido, BorderLayout.CENTER);

        JPanel listado = new JPanel(new BorderLayout());
        listado.setBorder(BorderFactory.createTitledBorder(null, "Listado", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12)));
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Id","Nombre","Especialidad"},0);
        tablaMedicos = new JTable(model);
        tablaMedicos.setRowHeight(25);
        tablaMedicos.setDefaultEditor(Object.class, null);
        listado.add(new JScrollPane(tablaMedicos), BorderLayout.CENTER);

        panel.add(panelDatos);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(panelBusqueda);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(listado);

        return panel;
    }

    private JPanel crearPanelFarmaceuticos() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel datos = new JPanel(new BorderLayout(10,10));
        datos.setBorder(BorderFactory.createTitledBorder(null, "Farmacéutico", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12)));

        JPanel sup = new JPanel(new GridBagLayout());
        sup.setBorder(new EmptyBorder(5,5,5,5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; sup.add(new JLabel("Id"), gbc);
        txtIdFarmaceutico = new JTextField();
        gbc.gridx=1; gbc.gridy=0; gbc.weightx=1; sup.add(txtIdFarmaceutico, gbc);

        gbc.gridx=2; gbc.gridy=0; gbc.weightx=0; sup.add(new JLabel("Nombre"), gbc);
        txtNombreFarmaceutico = new JTextField();
        gbc.gridx=3; gbc.gridy=0; gbc.weightx=1; sup.add(txtNombreFarmaceutico, gbc);

        JPanel btns = new JPanel(new GridLayout(2,2,5,10));
        btnGuardarFarmaceutico = new JButton("Guardar");
        btnLimpiarFarmaceutico = new JButton("Limpiar");
        btnBorrarFarmaceutico  = new JButton("Borrar");
        btns.add(btnGuardarFarmaceutico); btns.add(btnLimpiarFarmaceutico); btns.add(btnBorrarFarmaceutico);

        gbc.gridx=4; gbc.gridy=0; gbc.gridheight=1; gbc.weightx=0;
        sup.add(btns, gbc);
        datos.add(sup, BorderLayout.CENTER);

        JPanel panelBusqueda = new JPanel(new BorderLayout(10,5));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder(null, "Búsqueda", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12)));

        JPanel cont = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx=0; g.gridy=0; cont.add(new JLabel("Id"), g);
        txtBusquedaIdFarmaceutico = new JTextField();
        g.gridx=1; g.gridy=0; g.weightx=1; cont.add(txtBusquedaIdFarmaceutico, g);

        g.gridx=2; g.gridy=0; g.weightx=0; cont.add(new JLabel("Nombre"), g);
        txtBusquedaNombreFarmaceutico = new JTextField();
        g.gridx=3; g.gridy=0; g.weightx=1; cont.add(txtBusquedaNombreFarmaceutico, g);

        JPanel btnsBus = new JPanel(new GridLayout(1,2,5,5));
        btnBuscarFarmaceutico = new JButton("Buscar");
        btnReporteFarmaceutico = new JButton("Reporte");
        btnsBus.add(btnBuscarFarmaceutico); btnsBus.add(btnReporteFarmaceutico);

        g.gridx=4; g.gridy=0; g.weightx=0; cont.add(btnsBus, g);
        panelBusqueda.add(cont, BorderLayout.CENTER);

        JPanel listado = new JPanel(new BorderLayout());
        listado.setBorder(BorderFactory.createTitledBorder(null, "Listado", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12)));
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Id","Nombre"},0);
        tablaFarmaceuticos = new JTable(model);
        tablaFarmaceuticos.setRowHeight(25);
        tablaFarmaceuticos.setDefaultEditor(Object.class, null);
        listado.add(new JScrollPane(tablaFarmaceuticos), BorderLayout.CENTER);

        panel.add(datos);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(panelBusqueda);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(listado);
        return panel;
    }

    private JPanel crearPanelPacientes() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10,10,10,10));

        JPanel datos = new JPanel(new BorderLayout(10,10));
        datos.setBorder(BorderFactory.createTitledBorder(null, "Paciente", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12)));

        JPanel sup = new JPanel(new GridBagLayout());
        sup.setBorder(new EmptyBorder(5,5,5,5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; sup.add(new JLabel("Id"), gbc);
        txtIdPaciente = new JTextField();
        gbc.gridx=1; gbc.gridy=0; gbc.weightx=1; sup.add(txtIdPaciente, gbc);

        gbc.gridx=2; gbc.gridy=0; gbc.weightx=0; sup.add(new JLabel("Nombre"), gbc);
        txtNombrePaciente = new JTextField();
        gbc.gridx=3; gbc.gridy=0; gbc.weightx=1; sup.add(txtNombrePaciente, gbc);

        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0; sup.add(new JLabel("Fecha Nacimiento"), gbc);
        JPanel fechaPanel = new JPanel(new BorderLayout());
        txtFechaNacimiento = new JTextField();
        fechaPanel.add(txtFechaNacimiento, BorderLayout.CENTER);
        btnCalendario = new JButton("...");
        btnCalendario.setPreferredSize(new Dimension(40,25));
        fechaPanel.add(btnCalendario, BorderLayout.EAST);
        gbc.gridx=1; gbc.gridy=1; gbc.weightx=1; sup.add(fechaPanel, gbc);

        btnCalendario.addActionListener(e -> {
            Calendario calendario = new Calendario(frame);
            calendario.setVisible(true);
            Date fechaSel = calendario.getSelectedDate();
            if (fechaSel != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                txtFechaNacimiento.setText(sdf.format(fechaSel));
            }
        });

        gbc.gridx=2; gbc.gridy=1; gbc.weightx=0; sup.add(new JLabel("Teléfono"), gbc);
        txtTelefono = new JTextField();
        gbc.gridx=3; gbc.gridy=1; gbc.weightx=1; sup.add(txtTelefono, gbc);

        JPanel btns = new JPanel(new GridLayout(2,2,5,10));
        btnGuardarPaciente = new JButton("Guardar");
        btnLimpiarPaciente = new JButton("Limpiar");
        btnBorrarPaciente  = new JButton("Borrar");
        btns.add(btnGuardarPaciente); btns.add(btnLimpiarPaciente); btns.add(btnBorrarPaciente);
        gbc.gridx=4; gbc.gridy=0; gbc.gridheight=2;
        sup.add(btns, gbc);

        datos.add(sup, BorderLayout.CENTER);

        JPanel panelBusqueda = new JPanel(new BorderLayout(10,5));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder(null, "Búsqueda", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12)));

        JPanel cont = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx=0; g.gridy=0; cont.add(new JLabel("Id"), g);
        txtBusquedaIdPaciente = new JTextField();
        g.gridx=1; g.gridy=0; g.weightx=1; cont.add(txtBusquedaIdPaciente, g);

        g.gridx=2; g.gridy=0; g.weightx=0; cont.add(new JLabel("Nombre"), g);
        txtBusquedaNombrePaciente = new JTextField();
        g.gridx=3; g.gridy=0; g.weightx=1; cont.add(txtBusquedaNombrePaciente, g);

        JPanel btnsBus = new JPanel(new GridLayout(1,2,5,5));
        btnBuscarPaciente = new JButton("Buscar");
        btnReportePaciente = new JButton("Reporte");
        btnsBus.add(btnBuscarPaciente); btnsBus.add(btnReportePaciente);
        g.gridx=4; g.gridy=0; cont.add(btnsBus, g);
        panelBusqueda.add(cont, BorderLayout.CENTER);

        JPanel listado = new JPanel(new BorderLayout());
        listado.setBorder(BorderFactory.createTitledBorder(null, "Listado", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12)));
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Id","Nombre","Fecha Nacimiento","Teléfono"},0);
        tablaPacientes = new JTable(model);
        tablaPacientes.setRowHeight(25);
        tablaPacientes.setDefaultEditor(Object.class, null);
        listado.add(new JScrollPane(tablaPacientes), BorderLayout.CENTER);

        panel.add(datos);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(panelBusqueda);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(listado);
        return panel;
    }

    private JPanel crearPanelMedicamentos() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel datos = new JPanel(new BorderLayout(10,10));
        datos.setBorder(BorderFactory.createTitledBorder(null, "Medicamento", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12)));

        JPanel sup = new JPanel(new GridBagLayout());
        sup.setBorder(new EmptyBorder(5,5,5,5));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,5,5,5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx=0; gbc.gridy=0; sup.add(new JLabel("Código"), gbc);
        txtCodigoMedicamento = new JTextField();
        gbc.gridx=1; gbc.gridy=0; gbc.weightx=1; sup.add(txtCodigoMedicamento, gbc);

        gbc.gridx=2; gbc.gridy=0; gbc.weightx=0; sup.add(new JLabel("Nombre"), gbc);
        txtNombreMedicamento = new JTextField();
        gbc.gridx=3; gbc.gridy=0; gbc.weightx=1; sup.add(txtNombreMedicamento, gbc);

        gbc.gridx=0; gbc.gridy=1; gbc.weightx=0; sup.add(new JLabel("Presentación"), gbc);
        txtPresentacion = new JTextField();
        gbc.gridx=1; gbc.gridy=1; gbc.weightx=1; sup.add(txtPresentacion, gbc);

        JPanel btns = new JPanel(new GridLayout(2,2,5,10));
        btnGuardarMedicamento = new JButton("Guardar");
        btnLimpiarMedicamento  = new JButton("Limpiar");
        btnBorrarMedicamento   = new JButton("Borrar");
        btns.add(btnGuardarMedicamento); btns.add(btnLimpiarMedicamento); btns.add(btnBorrarMedicamento);

        gbc.gridx=4; gbc.gridy=0; gbc.gridheight=2; sup.add(btns, gbc);
        datos.add(sup, BorderLayout.CENTER);

        JPanel panelBusqueda = new JPanel(new BorderLayout(10,5));
        panelBusqueda.setBorder(BorderFactory.createTitledBorder(null, "Búsqueda", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12)));
        JPanel cont = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx=0; g.gridy=0; cont.add(new JLabel("Código"), g);
        txtBusquedaCodigo = new JTextField();
        g.gridx=1; g.gridy=0; g.weightx=1; cont.add(txtBusquedaCodigo, g);

        g.gridx=2; g.gridy=0; g.weightx=0; cont.add(new JLabel("Descripción"), g);
        txtBusquedaNombreMedicamento = new JTextField();
        g.gridx=3; g.gridy=0; g.weightx=1; cont.add(txtBusquedaNombreMedicamento, g);

        JPanel btnsBus = new JPanel(new GridLayout(1,2,5,5));
        btnBuscarMedicamento = new JButton("Buscar");
        btnReporteMedicamento = new JButton("Reporte");
        btnsBus.add(btnBuscarMedicamento); btnsBus.add(btnReporteMedicamento);
        g.gridx=4; g.gridy=0; cont.add(btnsBus, g);

        panelBusqueda.add(cont, BorderLayout.CENTER);

        JPanel listado = new JPanel(new BorderLayout());
        listado.setBorder(BorderFactory.createTitledBorder(null, "Listado", TitledBorder.DEFAULT_JUSTIFICATION,
                TitledBorder.DEFAULT_POSITION, new Font("Arial", Font.BOLD, 12)));
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Codigo","Nombre","Presentacion"},0);
        tablaMedicamentos = new JTable(model);
        tablaMedicamentos.setRowHeight(25);
        tablaMedicamentos.setDefaultEditor(Object.class, null);
        listado.add(new JScrollPane(tablaMedicamentos), BorderLayout.CENTER);

        panel.add(datos);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(panelBusqueda);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(listado);
        return panel;
    }

    private JPanel crearPanelPrescripcion() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(10,10,10,10));

        JPanel panelControl = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelControl.setBorder(BorderFactory.createTitledBorder("Control"));
        btnBuscarPacientePrescripcion = new JButton("Buscar Paciente");
        panelControl.add(btnBuscarPacientePrescripcion);
        btnAgregarMedicamentoPrescripcion = new JButton("Agregar Medicamento");
        panelControl.add(btnAgregarMedicamentoPrescripcion);

        JPanel panelReceta = new JPanel();
        panelReceta.setLayout(new BoxLayout(panelReceta, BoxLayout.Y_AXIS));
        panelReceta.setBorder(BorderFactory.createTitledBorder("Receta Médica"));

        JPanel panelFecha = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelFecha.add(new JLabel("Fecha de Retiro:"));
        txtFechaRetiroPrescricion = new JTextField(10);
        panelFecha.add(txtFechaRetiroPrescricion);
        JButton btnCal = new JButton("...");
        btnCal.setPreferredSize(new Dimension(40,25));
        btnCal.addActionListener(e -> {
            Calendario cal = new Calendario(frame);
            cal.setVisible(true);
            Date f = cal.getSelectedDate();
            if (f != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                txtFechaRetiroPrescricion.setText(sdf.format(f));
            }
        });
        panelFecha.add(btnCal);
        panelReceta.add(panelFecha);

        txtBusquedaPacientePrescripcion = new JTextField(1);
        txtBusquedaPacientePrescripcion.setEditable(false);
        panelReceta.add(txtBusquedaPacientePrescripcion);

        DefaultTableModel modelDet = new DefaultTableModel();
        modelDet.addColumn("Medicamento");
        modelDet.addColumn("Presentación");
        modelDet.addColumn("Cantidad");
        modelDet.addColumn("Indicaciones");
        modelDet.addColumn("Duración (días)");
        tablaDetallesPrescripcion = new JTable(modelDet);
        tablaDetallesPrescripcion.setRowHeight(25);
        tablaDetallesPrescripcion.setDefaultEditor(Object.class, null);
        panelReceta.add(new JScrollPane(tablaDetallesPrescripcion));

        JPanel panelAjuste = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAjuste.setBorder(BorderFactory.createTitledBorder("Ajustar Prescripción"));
        btnGuardarPrescripcion = new JButton("Guardar");
        btnLimpiarPrescripcion = new JButton("Limpiar");
        btnDescartarMedicamentoPrescripcion = new JButton("Descartar Medicamento");
        btnDetallesMedicamentoPrescripcion = new JButton("Detalles");
        panelAjuste.add(btnGuardarPrescripcion);
        panelAjuste.add(btnLimpiarPrescripcion);
        panelAjuste.add(btnDescartarMedicamentoPrescripcion);
        panelAjuste.add(btnDetallesMedicamentoPrescripcion);

        panel.add(panelControl);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(panelReceta);
        panel.add(Box.createRigidArea(new Dimension(0,10)));
        panel.add(panelAjuste);
        return panel;
    }

    private JPanel crearPanelDashboard() {
        JPanel root = new JPanel(new BorderLayout(10,10));
        root.setBorder(new EmptyBorder(10,10,10,10));

        JPanel filtros = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,8,5,8);
        g.anchor = GridBagConstraints.WEST;

        String[] meses = {"Enero","Febrero","Marzo","Abril","Mayo","Junio",
                "Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre"};
        int anioActual = java.time.LocalDate.now().getYear();

        cmbMesDesde = new JComboBox<>(meses);
        cmbMesHasta = new JComboBox<>(meses);
        spAnioDesde = new JSpinner(new SpinnerNumberModel(anioActual, 2000, 2100, 1));
        spAnioHasta = new JSpinner(new SpinnerNumberModel(anioActual, 2000, 2100, 1));

        g.gridx=0; g.gridy=0; filtros.add(new JLabel("Desde (mes/año):"), g);
        g.gridx=1; filtros.add(cmbMesDesde, g);
        g.gridx=2; filtros.add(spAnioDesde, g);

        g.gridx=0; g.gridy=1; filtros.add(new JLabel("Hasta (mes/año):"), g);
        g.gridx=1; filtros.add(cmbMesHasta, g);
        g.gridx=2; filtros.add(spAnioHasta, g);

        btnGenerarDashboard = new JButton("Generar Gráficos");
        g.gridx=3; g.gridy=0; g.gridheight=2; g.fill = GridBagConstraints.VERTICAL;
        filtros.add(btnGenerarDashboard, g);
        g.gridheight=1; g.fill = GridBagConstraints.NONE;

        root.add(filtros, BorderLayout.NORTH);

        JPanel panelPastelWrap = new JPanel(new BorderLayout());
        panelPastelWrap.setBorder(BorderFactory.createTitledBorder("Recetas por estado"));
        panelPastelDashboard = new JPanel(new BorderLayout());
        panelPastelWrap.add(panelPastelDashboard, BorderLayout.CENTER);

        JPanel panelLineaWrap = new JPanel(new BorderLayout());
        panelLineaWrap.setBorder(BorderFactory.createTitledBorder("Medicamentos por mes"));
        panelLineaDashboard = new JPanel(new BorderLayout());
        panelLineaWrap.add(panelLineaDashboard, BorderLayout.CENTER);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelPastelWrap, panelLineaWrap);
        split.setResizeWeight(0.35);
        split.setContinuousLayout(true);
        split.setOneTouchExpandable(true);

        root.add(split, BorderLayout.CENTER);

        JPanel derecha = new JPanel(new BorderLayout(8,8));
        derecha.setBorder(BorderFactory.createTitledBorder("Medicamentos (seleccione con ✔)"));
        derecha.setPreferredSize(new Dimension(260, 10));

        JPanel buscador = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        buscador.add(new JLabel("Buscar:"));
        txtFiltroDash = new JTextField(18);
        buscador.add(txtFiltroDash);
        derecha.add(buscador, BorderLayout.NORTH);

        DefaultTableModel modelM = new DefaultTableModel(new Object[]{"Sel", "Código", "Nombre"}, 0) {
            @Override public Class<?> getColumnClass(int columnIndex) {
                return (columnIndex == 0) ? Boolean.class : String.class;
            }
            @Override public boolean isCellEditable(int row, int column) { return column == 0; }
        };
        tblDashMeds = new JTable(modelM);
        tblDashMeds.setFont(new Font("Arial", Font.PLAIN, 11));
        tblDashMeds.getTableHeader().setFont(new Font("Arial", Font.BOLD, 11));
        tblDashMeds.setRowHeight(22);
        tblDashMeds.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

        TableColumnModel cm = tblDashMeds.getColumnModel();
        cm.getColumn(0).setPreferredWidth(40);
        cm.getColumn(0).setMaxWidth(50);
        cm.getColumn(1).setPreferredWidth(70);
        cm.getColumn(1).setMaxWidth(90);
        derecha.add(new JScrollPane(tblDashMeds), BorderLayout.CENTER);

        root.add(derecha, BorderLayout.EAST);

        return root;
    }


    private JPanel crearPanelHistorico() {
        panelHistorico = new JPanel(new BorderLayout(10,10));
        panelHistorico.setBorder(new EmptyBorder(10,10,10,10));

        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelBusqueda.add(new JLabel("Buscar:"));
        txtBusquedaHistorico = new JTextField(20);
        panelBusqueda.add(txtBusquedaHistorico);
        btnBuscarHistorico = new JButton("Buscar");
        panelBusqueda.add(btnBuscarHistorico);
        btnLimpiarHistorico = new JButton("Limpiar");
        panelBusqueda.add(btnLimpiarHistorico);
        panelHistorico.add(panelBusqueda, BorderLayout.NORTH);

        DefaultTableModel model = new DefaultTableModel(new Object[]{"ID Receta","Paciente","Fecha Creación","Estado"},0) {
            @Override public boolean isCellEditable(int r,int c){ return false; }
        };
        tblHistoricoRecetas = new JTable(model);
        panelHistorico.add(new JScrollPane(tblHistoricoRecetas), BorderLayout.CENTER);

        JPanel panelDetalles = new JPanel(new BorderLayout());
        panelDetalles.setBorder(BorderFactory.createTitledBorder("Detalles de la Receta"));
        txtDetallesHistorico = new JTextArea(10,30);
        txtDetallesHistorico.setEditable(false);
        panelDetalles.add(new JScrollPane(txtDetallesHistorico), BorderLayout.CENTER);
        panelHistorico.add(panelDetalles, BorderLayout.SOUTH);
        return panelHistorico;
    }

    private JPanel crearPanelAcercaDe() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20,20,20,20));

        JPanel cont = new JPanel();
        cont.setLayout(new BoxLayout(cont, BoxLayout.Y_AXIS));
        cont.setBackground(Color.WHITE);

        JLabel lblTitulo = new JLabel("Prescripción y Despacho de Recetas", JLabel.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 40));
        lblTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel lblImagen = new JLabel("HOSPITAL");
        lblImagen.setFont(new Font("Arial", Font.BOLD, 24));
        lblImagen.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        lblImagen.setPreferredSize(new Dimension(300,200));
        lblImagen.setHorizontalAlignment(JLabel.CENTER);
        lblImagen.setAlignmentX(Component.CENTER_ALIGNMENT);

        cont.add(Box.createVerticalGlue());
        cont.add(lblTitulo);
        cont.add(Box.createRigidArea(new Dimension(0,20)));
        cont.add(lblImagen);
        cont.add(Box.createVerticalGlue());

        panel.add(cont, BorderLayout.CENTER);
        return panel;
    }

    private void construirPestanaDespachar() {
        panelDespachar = new JPanel(new BorderLayout(10,10));
        panelDespachar.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));

        JPanel north = new JPanel(new GridBagLayout());
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5,5,5,5);
        g.anchor = GridBagConstraints.WEST;

        g.gridx=0; g.gridy=0; north.add(new JLabel("Cédula/ID Paciente:"), g);
        g.gridx=1; tfCedulaPaciente = new JTextField(16); north.add(tfCedulaPaciente, g);

        g.gridx=0; g.gridy=1; north.add(new JLabel("Nombre Paciente:"), g);
        g.gridx=1; tfNombrePaciente = new JTextField(16); north.add(tfNombrePaciente, g);

        g.gridx=2; g.gridy=0; g.gridheight=2;
        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnBuscarRecetas  = new JButton("Buscar");
        btnLimpiarRecetas = new JButton("Limpiar");
        rightBtns.add(btnBuscarRecetas);
        rightBtns.add(btnLimpiarRecetas);
        north.add(rightBtns, g);
        g.gridheight=1;

        g.gridx=0; g.gridy=2; g.gridwidth=3;
        lblFechaRetiro = new JLabel("Fecha retiro: (seleccione receta)");
        north.add(lblFechaRetiro, g);

        g.gridy=3;
        lblEstadoSeleccionado = new JLabel("Estado: (seleccione receta)");
        north.add(lblEstadoSeleccionado, g);

        panelDespachar.add(north, BorderLayout.NORTH);

        JPanel center = new JPanel(new GridLayout(2,1,10,10));
        tblRecetas = new JTable(new DefaultTableModel(new Object[]{"ID Receta","ID Paciente","Nombre Paciente","Fecha Retiro","Estado"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        });
        center.add(new JScrollPane(tblRecetas));

        tblDetalles = new JTable(new DefaultTableModel(new Object[]{"Código","Medicamento","Dosis","Cantidad"},0){
            @Override public boolean isCellEditable(int r,int c){return false;}
        });
        center.add(new JScrollPane(tblDetalles));
        panelDespachar.add(center, BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 8));
        btnPonerProceso = new JButton("Poner en PROCESO");
        btnMarcarLista  = new JButton("Marcar como LISTA");
        btnEntregar     = new JButton("ENTREGAR");
        south.add(btnPonerProceso);
        south.add(btnMarcarLista);
        south.add(btnEntregar);
        panelDespachar.add(south, BorderLayout.SOUTH);
    }

    public void configurarPestanasPorRol(String rol) {
        String r = (rol == null) ? "" : rol.trim().toLowerCase();

        tabbedPane.removeAll();

        switch (r) {
            case "admin":
                tabbedPane.addTab("Medicos", panelMedicos);
                tabbedPane.addTab("Farmacéutas", panelFarmaceuticos);
                tabbedPane.addTab("Pacientes", panelPacientes);
                tabbedPane.addTab("Medicamentos", panelMedicamentos);
                tabbedPane.addTab("Dashboard", panelDashboard);
                tabbedPane.addTab("Historico", panelHistorico);
                tabbedPane.addTab("Acerca de...", panelAcercaDe);
                break;

            case "farmaceutico":
            case "farmaceuta":
                tabbedPane.addTab("Despachar", panelDespachar);
                tabbedPane.addTab("Historico", panelHistorico);
                tabbedPane.addTab("Dashboard", panelDashboard);
                tabbedPane.addTab("Acerca de...", panelAcercaDe);
                break;

            case "medico":
                tabbedPane.addTab("Prescripción", panelPrescripcion);
                tabbedPane.addTab("Dashboard", panelDashboard);
                tabbedPane.addTab("Historico", panelHistorico);
                tabbedPane.addTab("Acerca de...", panelAcercaDe);
                break;

            default:
                tabbedPane.addTab("Acerca de...", panelAcercaDe);
                break;
        }

        if (tabbedPane.getTabCount() > 0) {
            tabbedPane.setSelectedIndex(0);
        }
    }

    public void actualizarUsuariosConectados(java.util.List<String> usuarios) {
        SwingUtilities.invokeLater(() -> {
            usersListModel.clear();

            int usuariosFiltrados = 0;
            String usuarioActual = "";

            try {
                if (controladorUsuarios != null) {
                    usuarioActual = controladorUsuarios.getUsuarioActual();
                    System.out.println("Usuario actual para filtrar: " + usuarioActual);
                }
            } catch (Exception e) {
                System.err.println("Error obteniendo usuario actual: " + e.getMessage());
            }

            for (String user : usuarios) {
                if (user != null && !user.trim().isEmpty() &&
                        !"null".equals(user) && !user.equals(usuarioActual) &&
                        !user.contains(usuarioActual + " -")) {

                    usersListModel.addElement(user);
                    usuariosFiltrados++;
                    System.out.println("Agregando usuario conectado: " + user);
                } else {
                    System.out.println("Filtrando usuario: " + user);
                }
            }

            lblActiveUsers.setText(usuariosFiltrados + " usuarios conectados");
            System.out.println("Total usuarios conectados mostrados: " + usuariosFiltrados);
        });
    }

    public JPanel getPanelPastelDashboard() {
        return panelPastelDashboard;
    }

    public JPanel getPanelLineaDashboard() {
        return panelLineaDashboard;
    }

    public JTable getTblDashMeds() {
        return tblDashMeds;
    }



}
