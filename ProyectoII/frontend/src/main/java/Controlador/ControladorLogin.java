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

    private ControladorMedicos ctrlMedicos;
    private ControladorFarmaceuta ctrlFarmas;
    private ControladoraPaciente ctrlPacientes;
    private ControladoraMedicamentos ctrlMeds;
    private ControladorPrescripcion ctrlPresc;
    private ControladorDespacho ctrlDespacho;
    private ControladoraDashboard ctrlDashboard;
    private ControladorHistoricoRecetas ctrlHistorico;

    public enum Rol {ADMIN, MEDICO, FARMACEUTA}

    public ControladorLogin(LoginInterface loginView) {
        this.loginView = loginView;
        this.proxyService = new ProxyService();
        inicializarModelos();
        prepararLogin();
    }

    private void inicializarModelos() {
        this.listaMedicos = new ListaDeMedicos();
        this.listaFarmas = new ListaDeFarmaceutas();
        this.listaPacientes = new ListaDePacientes();
        this.catalogo = new CatalogoDeMedicamentos();
        System.out.println("✅ Modelos inicializados - Datos vendrán del backend");
    }

    private void prepararLogin() {
        // Verificar conexión con el backend
        if (!proxyService.isConectado()) {
            JOptionPane.showMessageDialog(loginView.getFrameLogin(),
                    "No hay conexión con el servidor backend.\nAlgunas funcionalidades no estarán disponibles.",
                    "Advertencia de Conexión",
                    JOptionPane.WARNING_MESSAGE);
        }

        loginView.getTextFieldIDLogin().getDocument().addDocumentListener(new DocumentListener() {
            private void toggle() {
                boolean enabled = !loginView.getTextFieldIDLogin().getText().trim().isEmpty();
                loginView.getButtonCambiarLogin().setEnabled(enabled);
            }

            public void insertUpdate(DocumentEvent e) {
                toggle();
            }

            public void removeUpdate(DocumentEvent e) {
                toggle();
            }

            public void changedUpdate(DocumentEvent e) {
                toggle();
            }
        });
        loginView.getButtonCambiarLogin().setEnabled(false);

        loginView.getButtonAceptarLogin().addActionListener(e -> intentarLogin());
        loginView.getButtonCancelarLogin().addActionListener(e -> {
            loginView.getTextFieldIDLogin().setText("");
            loginView.getPassFieldPasswordLogin().setText("");
        });
        loginView.getButtonSalirLogin().addActionListener(e -> {
            if (proxyService.isConectado()) {
                proxyService.cerrarConexion();
            }
            System.exit(0);
        });
        loginView.getButtonCambiarLogin().addActionListener(e -> mostrarDialogoCambioClave());
    }

    private void intentarLogin() {
        String user = loginView.getTextFieldIDLogin().getText().trim();
        String pass = new String(loginView.getPassFieldPasswordLogin().getPassword());

        if (user.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(loginView.getFrameLogin(),
                    "Debe completar todos los campos", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Mostrar indicador de carga
        loginView.getButtonAceptarLogin().setEnabled(false);
        loginView.getButtonAceptarLogin().setText("Conectando...");

        // Usar SwingWorker para no bloquear la UI
        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {
            private JSONObject respuestaBackend;
            private boolean exitoBackend = false;

            @Override
            protected Boolean doInBackground() throws Exception {
                // PRIMERO intentar login con el backend
                if (proxyService.isConectado()) {
                    respuestaBackend = proxyService.login(user, pass);
                    exitoBackend = "éxito".equals(respuestaBackend.optString("estado"));
                }
                return exitoBackend;
            }

            @Override
            protected void done() {
                loginView.getButtonAceptarLogin().setEnabled(true);
                loginView.getButtonAceptarLogin().setText("Aceptar");

                try {
                    boolean exitoBackend = get();

                    if (exitoBackend) {
                        usuarioActual = user;
                        determinarRolYContinuar(user);
                        return;
                    } else if (proxyService.isConectado()) {
                        // El backend está conectado pero el login falló
                        String mensajeError = respuestaBackend.optString("mensaje", "Credenciales inválidas");
                        JOptionPane.showMessageDialog(loginView.getFrameLogin(),
                                mensajeError, "Error de Login", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (Exception ex) {
                    // Error en la ejecución del worker
                    System.err.println("Error en login: " + ex.getMessage());
                }

                // Si el backend no está disponible o falló, usar lógica local como fallback
                intentarLoginLocal(user, pass);
            }
        };

        worker.execute();
    }

    private void intentarLoginLocal(String user, String pass) {
        // Login local como fallback
        if (user.equals(ADMIN_USER) && pass.equals(ADMIN_PASS)) {
            usuarioActual = user;
            abrirInterfazConRol(Rol.ADMIN, null, null);
            return;
        }

        Medico medico = listaMedicos.buscarPorID(user);
        if (medico != null && pass.equals(medico.getClave())) {
            usuarioActual = user;
            abrirInterfazConRol(Rol.MEDICO, medico, null);
            return;
        }

        Farmaceutas far = listaFarmas.buscarPorID(user);
        if (far != null && pass.equals(far.getClave())) {
            usuarioActual = user;
            abrirInterfazConRol(Rol.FARMACEUTA, null, far);
            return;
        }

        JOptionPane.showMessageDialog(loginView.getFrameLogin(),
                "Credenciales inválidas", "Acceso denegado", JOptionPane.ERROR_MESSAGE);
    }

    private void determinarRolYContinuar(String username) {
        Rol rol;

        if (username.startsWith("ADM-") || username.equals("admi")) {
            rol = Rol.ADMIN;
        } else if (username.startsWith("MED-")) {
            rol = Rol.MEDICO;
        } else if (username.startsWith("FARM-")) {
            rol = Rol.FARMACEUTA;
        } else {
            // Por defecto asumimos administrador
            rol = Rol.ADMIN;
        }

        // No necesitamos crear objetos temporales, los datos vendrán del backend
        abrirInterfazConRol(rol, null, null);
    }

    private void abrirInterfazConRol(Rol rol, Medico medico, Farmaceutas far) {
        loginView.getFrameLogin().setVisible(false);
        vista = new Interfaz();

        String usuarioId = (medico != null) ? medico.getId() :
                (far != null) ? far.getId() : "ADM-111";

        String nombreUsuario = (medico != null) ? medico.getNombre() :
                (far != null) ? far.getNombre() : "Administrador";

        // INICIALIZAR CONTROLADORES CON PROXY SERVICE
        ctrlMedicos = new ControladorMedicos(vista, listaMedicos, proxyService);
        ctrlFarmas = new ControladorFarmaceuta(vista, listaFarmas, proxyService);
        ctrlPacientes = new ControladoraPaciente(vista, listaPacientes, proxyService);
        ctrlMeds = new ControladoraMedicamentos(vista, catalogo, proxyService);
        ctrlPresc = new ControladorPrescripcion(vista, listaPacientes, catalogo, proxyService);
        ctrlDespacho = new ControladorDespacho(vista, proxyService);
        ctrlDashboard = new ControladoraDashboard(vista, proxyService);
        ctrlHistorico = new ControladorHistoricoRecetas(vista, proxyService, listaPacientes, catalogo, listaMedicos);

        // CONTROLADOR DE USUARIOS ACTIVOS
        ControladorUsuariosActivos ctrlUsuarios = new ControladorUsuariosActivos();
        proxyService.setControladorUsuarios(ctrlUsuarios);

        // CORRECCIÓN: Usar el método que SÍ existe en Interfaz
        vista.configurarPestanasPorRol(rol.toString().toLowerCase());

        // Configurar título de la ventana con información del usuario
        vista.getFrame().setTitle("Sistema de Recetas - " + nombreUsuario + " (" + usuarioId + ")");

        vista.getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        vista.getFrame().addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // Notificar logout al backend
                if (proxyService.isConectado() && usuarioActual != null) {
                    proxyService.logout();
                }
                vista.getFrame().setVisible(false);
                loginView.getTextFieldIDLogin().setText("");
                loginView.getPassFieldPasswordLogin().setText("");
                loginView.getFrameLogin().setVisible(true);
                usuarioActual = null;
            }
        });

        vista.mostrar();

        // Cargar datos iniciales según el rol
        cargarDatosIniciales(rol);
    }

    private void cargarDatosIniciales(Rol rol) {
        // Cargar datos necesarios según el rol del usuario
        switch (rol) {
            case ADMIN:
                // El administrador puede necesitar todas las listas
                ctrlMedicos.cargarMedicosDesdeBackend();
                ctrlFarmas.cargarFarmaceutasDesdeBackend();
                ctrlPacientes.cargarPacientesDesdeBackend();
                ctrlMeds.cargarMedicamentosDesdeBackend();
                break;
            case MEDICO:
                // El médico necesita pacientes y medicamentos para prescribir
                ctrlPacientes.cargarPacientesDesdeBackend();
                ctrlMeds.cargarMedicamentosDesdeBackend();
                break;
            case FARMACEUTA:
                // El farmacéutico necesita recetas para despacho
                ctrlDespacho.cargarRecetasDesdeBackend();
                break;
        }
    }

    // CORRECCIÓN: Este método ya no es necesario porque usamos configurarPestanasPorRol de Interfaz
    // private void configurarPestanasPorRol(Rol rol) { ... }

    // CORRECCIÓN: Este método ya no es necesario
    // private void habilitar(JTabbedPane tabs, int idx) { ... }

    private void mostrarDialogoCambioClave() {
        String id = loginView.getTextFieldIDLogin().getText().trim();
        if (id.isEmpty()) {
            JOptionPane.showMessageDialog(loginView.getFrameLogin(),
                    "Escriba su ID para cambiar la clave.", "Atención", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Verificar si el usuario existe
        Medico medico = listaMedicos.buscarPorID(id);
        Farmaceutas far = (medico == null) ? listaFarmas.buscarPorID(id) : null;

        if (medico == null && far == null && !id.equals("admi")) {
            JOptionPane.showMessageDialog(loginView.getFrameLogin(),
                    "No existe un usuario con ese ID.", "Error", JOptionPane.ERROR_MESSAGE);
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

            // Intentar cambiar clave en el BACKEND primero
            if (proxyService.isConectado()) {
                JSONObject respuesta = proxyService.cambiarClave(id, actual, nueva);

                if ("éxito".equals(respuesta.optString("estado"))) {
                    // Actualizar también localmente si existe
                    if (medico != null) {
                        medico.setClave(nueva);
                    } else if (far != null) {
                        far.setClave(nueva);
                    }

                    JOptionPane.showMessageDialog(loginView.getFrameChangePassword(),
                            "Clave actualizada correctamente.", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    loginView.getFrameChangePassword().dispose();
                    loginView.getPassFieldPasswordLogin().setText("");
                    loginView.getFrameLogin().setVisible(true);
                    return;
                } else {
                    String mensajeError = respuesta.optString("mensaje", "Error al cambiar la clave");
                    JOptionPane.showMessageDialog(loginView.getFrameChangePassword(),
                            mensajeError, "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }

            // Fallback: cambiar solo localmente
            String claveActualEnModelo = (medico != null) ? medico.getClave() :
                    (far != null) ? far.getClave() : ADMIN_PASS;

            if (!actual.equals(claveActualEnModelo)) {
                JOptionPane.showMessageDialog(loginView.getFrameChangePassword(),
                        "La clave actual no es correcta.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Actualizar localmente
            if (medico != null) {
                medico.setClave(nueva);
            } else if (far != null) {
                far.setClave(nueva);
            }

            JOptionPane.showMessageDialog(loginView.getFrameChangePassword(),
                    "Clave actualizada localmente (servidor no disponible).",
                    "Advertencia", JOptionPane.WARNING_MESSAGE);

            loginView.getFrameChangePassword().dispose();
            loginView.getPassFieldPasswordLogin().setText("");
            loginView.getFrameLogin().setVisible(true);
        });
    }
}
