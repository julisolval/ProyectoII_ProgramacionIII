package main.java.Controlador;

import main.java.Modelo.*;
import main.java.Vista.Interfaz;
import main.java.Vista.LoginInterface;
import main.java.proxy.ProxyService;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import org.json.JSONObject;

public class ControladorLogin {

    private static final String ADMIN_USER = "admi";
    private static final String ADMIN_PASS = "1234";

    private final LoginInterface loginView;
    private final ProxyService proxyService;

    private ListaDeMedicos listaMedicos;
    private ListaDeFarmaceutas listaFarmas;
    private ListaDePacientes listaPacientes;
    private CatalogoDeMedicamentos catalogo;

    private Interfaz vista;
    private String usuarioActual;

    // Controladores auxiliares
    private ControladorMedicos ctrlMedicos;
    private ControladorFarmaceuta ctrlFarmas;
    private ControladoraPaciente ctrlPacientes;
    private ControladoraMedicamentos ctrlMeds;
    private ControladorPrescripcion ctrlPresc;
    private ControladorDespacho ctrlDespacho;
    private ControladoraDashboard ctrlDashboard;
    private ControladorHistoricoRecetas ctrlHistorico;

    public enum Rol { ADMIN, MEDICO, FARMACEUTICO }

    // ===============================================================
    // 🔹 CONSTRUCTOR
    // ===============================================================
    public ControladorLogin(LoginInterface loginView) {
        this.loginView = loginView;
        this.proxyService = new ProxyService();
        inicializarModelos();
        prepararLogin();
    }

    // ===============================================================
    // 🔹 INICIALIZACIÓN DE MODELOS
    // ===============================================================
    private void inicializarModelos() {
        this.listaMedicos = new ListaDeMedicos();
        this.listaFarmas = new ListaDeFarmaceutas();
        this.listaPacientes = new ListaDePacientes();
        this.catalogo = new CatalogoDeMedicamentos();
        System.out.println("✅ Modelos inicializados (modo local listo)");
    }

    // ===============================================================
    // 🔹 CONFIGURAR EVENTOS DE LOGIN
    // ===============================================================
    private void prepararLogin() {

        if (!proxyService.isConectado()) {
            JOptionPane.showMessageDialog(loginView.getFrameLogin(),
                    "⚠️ No hay conexión con el servidor backend.\nSe usará modo local.",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);
        }

        // Habilitar botón Cambiar Clave solo si hay ID
        loginView.getTextFieldIDLogin().getDocument().addDocumentListener(new DocumentListener() {
            private void toggle() {
                boolean enabled = !loginView.getTextFieldIDLogin().getText().trim().isEmpty();
                loginView.getButtonCambiarLogin().setEnabled(enabled);
            }
            public void insertUpdate(DocumentEvent e) { toggle(); }
            public void removeUpdate(DocumentEvent e) { toggle(); }
            public void changedUpdate(DocumentEvent e) { toggle(); }
        });
        loginView.getButtonCambiarLogin().setEnabled(false);

        // Botones principales
        loginView.getButtonAceptarLogin().addActionListener(e -> intentarLogin());
        loginView.getButtonCancelarLogin().addActionListener(e -> {
            loginView.getTextFieldIDLogin().setText("");
            loginView.getPassFieldPasswordLogin().setText("");
        });
        loginView.getButtonSalirLogin().addActionListener(e -> {
            proxyService.cerrarConexion();
            System.exit(0);
        });
        loginView.getButtonCambiarLogin().addActionListener(e -> mostrarDialogoCambioClave());
    }

    // ===============================================================
    // 🔹 INTENTAR LOGIN (con backend o local)
    // ===============================================================
    private void intentarLogin() {
        String user = loginView.getTextFieldIDLogin().getText().trim();
        String pass = new String(loginView.getPassFieldPasswordLogin().getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(loginView.getFrameLogin(),
                    "Debe completar todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        loginView.getButtonAceptarLogin().setEnabled(false);
        loginView.getButtonAceptarLogin().setText("Conectando...");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            private JSONObject respuesta;

            @Override
            protected Void doInBackground() {
                if (proxyService.isConectado()) {
                    respuesta = proxyService.login(user, pass);
                } else {
                    respuesta = null;
                }
                return null;
            }

            @Override
            protected void done() {
                loginView.getButtonAceptarLogin().setEnabled(true);
                loginView.getButtonAceptarLogin().setText("Aceptar");

                if (respuesta != null && "éxito".equalsIgnoreCase(respuesta.optString("estado"))) {
                    usuarioActual = proxyService.getUsuarioActual();
                    String tipo = proxyService.getTipoUsuario();
                    abrirInterfazConRol(tipo);
                } else if (respuesta != null) {
                    JOptionPane.showMessageDialog(loginView.getFrameLogin(),
                            respuesta.optString("mensaje", "Credenciales inválidas"),
                            "Error de login", JOptionPane.ERROR_MESSAGE);
                } else {
                    intentarLoginLocal(user, pass);
                }
            }
        };
        worker.execute();
    }

    // ===============================================================
    // 🔹 LOGIN LOCAL (si el backend no está disponible)
    // ===============================================================
    private void intentarLoginLocal(String user, String pass) {
        if (user.equals(ADMIN_USER) && pass.equals(ADMIN_PASS)) {
            abrirInterfazConRol("admin");
            return;
        }

        Medico medico = listaMedicos.buscarPorID(user);
        if (medico != null && pass.equals(medico.getClave())) {
            abrirInterfazConRol("medico");
            return;
        }

        Farmaceutas far = listaFarmas.buscarPorID(user);
        if (far != null && pass.equals(far.getClave())) {
            abrirInterfazConRol("farmaceutico");
            return;
        }

        JOptionPane.showMessageDialog(loginView.getFrameLogin(),
                "Credenciales inválidas o usuario no encontrado", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
    }

    // ===============================================================
    // 🔹 ABRIR INTERFAZ SEGÚN ROL
    // ===============================================================
    private void abrirInterfazConRol(String rolStr) {
        Rol rol;
        switch (rolStr.toLowerCase()) {
            case "medico":
                rol = Rol.MEDICO; break;
            case "farmaceutico":
            case "farmaceuta":
                rol = Rol.FARMACEUTICO; break;
            default:
                rol = Rol.ADMIN;
        }

        loginView.getFrameLogin().setVisible(false);
        vista = new Interfaz();

        // Inicializar controladores
        ctrlMedicos = new ControladorMedicos(vista, listaMedicos, proxyService);
        ctrlFarmas = new ControladorFarmaceuta(vista, listaFarmas, proxyService);
        ctrlPacientes = new ControladoraPaciente(vista, listaPacientes, proxyService);
        ctrlMeds = new ControladoraMedicamentos(vista, catalogo, proxyService);
        ctrlPresc = new ControladorPrescripcion(vista, listaPacientes, catalogo, proxyService);
        ctrlDespacho = new ControladorDespacho(vista, proxyService);
        ctrlDashboard = new ControladoraDashboard(vista, proxyService);
        ctrlHistorico = new ControladorHistoricoRecetas(vista, proxyService, listaPacientes, catalogo, listaMedicos);



        ControladorUsuariosActivos ctrlUsuarios = new ControladorUsuariosActivos(vista, proxyService);
        proxyService.setControladorUsuarios(ctrlUsuarios);
        ctrlUsuarios.actualizarUsuarios(); // Cargar lista inicial


        vista.configurarPestanasPorRol(rol.toString().toLowerCase());
        vista.getFrame().setTitle("Sistema de Recetas - " + proxyService.getNombreUsuario() + " (" + proxyService.getUsuarioActual() + ")");

        vista.getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        vista.getFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                proxyService.logout();
                vista.getFrame().dispose();
                loginView.getFrameLogin().setVisible(true);
            }
        });

        vista.mostrar();
        cargarDatosIniciales(rol);
    }

    // ===============================================================
    // 🔹 CARGA INICIAL SEGÚN ROL
    // ===============================================================
    private void cargarDatosIniciales(Rol rol) {
        switch (rol) {
            case ADMIN:
                ctrlMedicos.cargarMedicosDesdeBackend();
                ctrlFarmas.cargarFarmaceutasDesdeBackend();
                ctrlPacientes.cargarPacientesDesdeBackend();
                ctrlMeds.cargarMedicamentosDesdeBackend();
                break;
            case MEDICO:
                ctrlPacientes.cargarPacientesDesdeBackend();
                ctrlMeds.cargarMedicamentosDesdeBackend();
                break;
            case FARMACEUTICO:
                ctrlDespacho.cargarRecetasDesdeBackend();
                break;
        }
    }

    // ===============================================================
    // 🔹 CAMBIO DE CONTRASEÑA
    // ===============================================================
    private void mostrarDialogoCambioClave() {
        String id = loginView.getTextFieldIDLogin().getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(loginView.getFrameLogin(),
                    "Ingrese su ID para cambiar la clave.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        loginView.mostrarCambiarPassword();
        for (var al : loginView.getAceptarButtonChange().getActionListeners()) {
            loginView.getAceptarButtonChange().removeActionListener(al);
        }

        loginView.getAceptarButtonChange().addActionListener(ev -> {
            String actual = new String(loginView.getCurrentPassField().getPassword()).trim();
            String nueva = new String(loginView.getNewPassField().getPassword()).trim();
            String conf = new String(loginView.getConfirmPassField().getPassword()).trim();

            if (nueva.isEmpty()) {
                JOptionPane.showMessageDialog(loginView.getFrameChangePassword(),
                        "La nueva clave no puede estar vacía.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            if (!nueva.equals(conf)) {
                JOptionPane.showMessageDialog(loginView.getFrameChangePassword(),
                        "Las claves no coinciden.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (proxyService.isConectado()) {
                JSONObject respuesta = proxyService.cambiarClave(id, actual, nueva);
                if ("éxito".equals(respuesta.optString("estado"))) {
                    JOptionPane.showMessageDialog(loginView.getFrameChangePassword(),
                            "Clave actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loginView.getFrameChangePassword().dispose();
                    loginView.getFrameLogin().setVisible(true);
                } else {
                    JOptionPane.showMessageDialog(loginView.getFrameChangePassword(),
                            respuesta.optString("mensaje", "Error al cambiar la clave"),
                            "Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(loginView.getFrameChangePassword(),
                        "No hay conexión con el servidor.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}
