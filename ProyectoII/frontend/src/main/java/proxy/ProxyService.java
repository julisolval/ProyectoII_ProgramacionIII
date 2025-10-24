package main.java.proxy;

import main.java.Controlador.ControladorUsuariosActivos;
import main.java.config.Config;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * ============================================================
 * 🧩 PROXY SERVICE
 * ------------------------------------------------------------
 * Clase intermedia entre el frontend (controladoras y vistas)
 * y el backend (ServerSocket - ClientHandler).
 * Envía solicitudes JSON y recibe respuestas JSON.
 * ============================================================
 */
public class ProxyService {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ControladorUsuariosActivos controladorUsuarios;
    private boolean conectado = false;

    private String usuarioActual;
    private String tipoUsuario;
    private String nombreUsuario;

    private final ConcurrentHashMap<String, CompletableFuture<JSONObject>> pendingRequests = new ConcurrentHashMap<>();

    // ==========================================================
    // 🔹 CONSTRUCTOR Y CONEXIÓN AL BACKEND
    // ==========================================================
    public ProxyService() {
        this(Config.BACKEND_HOST, Config.BACKEND_PORT);
    }

    public ProxyService(String host, int port) {
        connectToBackend(host, port);
    }

    private void connectToBackend(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            conectado = true;
            System.out.println("✅ Conectado al backend en " + host + ":" + port);
            startNotificationListener();
        } catch (IOException e) {
            conectado = false;
            System.err.println("❌ Error conectando al backend: " + e.getMessage());
        }
    }

    // ==========================================================
    // 🔹 ESCUCHAR NOTIFICACIONES DEL SERVIDOR
    // ==========================================================
    private void startNotificationListener() {
        Thread listenerThread = new Thread(() -> {
            try {
                String inputLine;
                while (conectado && (inputLine = in.readLine()) != null) {
                    System.out.println("📨 Mensaje recibido: " + inputLine);
                    try {
                        JSONObject message = new JSONObject(inputLine);
                        if (message.has("requestId")) {
                            // Respuesta a una solicitud
                            String requestId = message.getString("requestId");
                            CompletableFuture<JSONObject> future = pendingRequests.remove(requestId);
                            if (future != null) future.complete(message);
                        } else {
                            // Notificación en tiempo real
                            processAsyncNotification(message);
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Error procesando mensaje JSON: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("⚠️ Conexión perdida: " + e.getMessage());
                conectado = false;
                attemptReconnection();
            }
        });
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    private void processAsyncNotification(JSONObject notification) {
        String tipo = notification.optString("tipo", "");
        SwingUtilities.invokeLater(() -> {
            if (controladorUsuarios != null && "notificacion".equals(tipo)) {
                controladorUsuarios.manejarNotificacion(notification);
            }
        });
    }

    private void attemptReconnection() {
        System.out.println("🔄 Intentando reconectar...");
        try {
            Thread.sleep(5000);
            connectToBackend("localhost", Config.BACKEND_PORT);
        } catch (Exception e) {
            System.err.println("❌ Error en reconexión: " + e.getMessage());
        }
    }

    // ==========================================================
    // 🔹 MÉTODO GENERAL PARA ENVIAR SOLICITUDES
    // ==========================================================
    private JSONObject enviarSolicitud(JSONObject solicitud) {
        if (!conectado) {
            return crearError("No hay conexión con el servidor");
        }

        String requestId = UUID.randomUUID().toString();
        solicitud.put("requestId", requestId);

        CompletableFuture<JSONObject> future = new CompletableFuture<>();
        pendingRequests.put(requestId, future);

        try {
            out.println(solicitud.toString());
            System.out.println("📤 Solicitud enviada: " + solicitud.toString());
            return future.get(30, TimeUnit.SECONDS);
        } catch (Exception e) {
            pendingRequests.remove(requestId);
            return crearError("Error de comunicación: " + e.getMessage());
        }
    }

    private JSONObject crearError(String mensaje) {
        JSONObject error = new JSONObject();
        error.put("estado", "error");
        error.put("mensaje", mensaje);
        return error;
    }

    // ==========================================================
    // 🔐 AUTENTICACIÓN (Login, Logout, Cambio de Clave)
    // ==========================================================
    public JSONObject login(String username, String password) {
        JSONObject request = new JSONObject();
        request.put("tipo", "login");

        JSONObject datos = new JSONObject();
        datos.put("username", username);
        datos.put("password", password);
        request.put("datos", datos);

        JSONObject response = enviarSolicitud(request);

        if ("éxito".equalsIgnoreCase(response.optString("estado"))) {
            this.usuarioActual = response.optString("id", username);
            this.nombreUsuario = response.optString("nombre", username);
            this.tipoUsuario = response.optString("tipo", "admin").toLowerCase();
            System.out.printf("🔐 Sesión iniciada: [%s] %s (%s)%n", tipoUsuario, nombreUsuario, usuarioActual);
        } else {
            System.err.println("⚠️ Error en login: " + response.optString("mensaje"));
        }
        return response;
    }

    public JSONObject cambiarClave(String username, String oldPassword, String newPassword) {
        JSONObject request = new JSONObject();
        request.put("tipo", "cambiar_clave");

        JSONObject datos = new JSONObject();
        datos.put("username", username);
        datos.put("oldPassword", oldPassword);
        datos.put("newPassword", newPassword);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    public JSONObject logout() {
        if (usuarioActual == null) {
            JSONObject resp = new JSONObject();
            resp.put("estado", "éxito");
            resp.put("mensaje", "Sesión finalizada");
            return resp;
        }

        JSONObject request = new JSONObject();
        request.put("tipo", "logout");

        JSONObject datos = new JSONObject();
        datos.put("username", usuarioActual);
        request.put("datos", datos);

        JSONObject response = enviarSolicitud(request);
        usuarioActual = null;
        tipoUsuario = null;
        nombreUsuario = null;
        return response;
    }

    // ==========================================================
    // 🧑‍⚕️ MÉDICOS
    // ==========================================================
    public JSONObject obtenerMedicos() {
        return enviarSolicitud(new JSONObject().put("tipo", "obtener_medicos"));
    }

    public JSONObject guardarMedico(String id, String nombre, String especialidad) {
        JSONObject datos = new JSONObject();
        datos.put("id", id);
        datos.put("nombre", nombre);
        datos.put("especialidad", especialidad);
        return enviarSolicitud(new JSONObject().put("tipo", "guardar_medico").put("datos", datos));
    }

    public JSONObject actualizarMedico(String id, String nombre, String especialidad) {
        JSONObject datos = new JSONObject();
        datos.put("id", id);
        datos.put("nombre", nombre);
        datos.put("especialidad", especialidad);
        return enviarSolicitud(new JSONObject().put("tipo", "actualizar_medico").put("datos", datos));
    }

    public JSONObject eliminarMedico(String id) {
        JSONObject datos = new JSONObject();
        datos.put("id", id);
        return enviarSolicitud(new JSONObject().put("tipo", "eliminar_medico").put("datos", datos));
    }

    // ==========================================================
    // 💊 FARMACÉUTICOS
    // ==========================================================
    public JSONObject obtenerFarmaceuticos() {
        return enviarSolicitud(new JSONObject().put("tipo", "obtener_farmaceuticos"));
    }

    public JSONObject guardarFarmaceutico(String id, String nombre) {
        JSONObject datos = new JSONObject();
        datos.put("id", id);
        datos.put("nombre", nombre);
        return enviarSolicitud(new JSONObject().put("tipo", "guardar_farmaceutico").put("datos", datos));
    }

    public JSONObject actualizarFarmaceutico(String id, String nombre) {
        JSONObject datos = new JSONObject();
        datos.put("id", id);
        datos.put("nombre", nombre);
        return enviarSolicitud(new JSONObject().put("tipo", "actualizar_farmaceutico").put("datos", datos));
    }

    public JSONObject eliminarFarmaceutico(String id) {
        JSONObject datos = new JSONObject();
        datos.put("id", id);
        return enviarSolicitud(new JSONObject().put("tipo", "eliminar_farmaceutico").put("datos", datos));
    }

    // ==========================================================
    // 🧍 PACIENTES
    // ==========================================================
    public JSONObject obtenerPacientes() {
        return enviarSolicitud(new JSONObject().put("tipo", "obtener_pacientes"));
    }

    public JSONObject guardarPaciente(String id, String nombre, String fechaNacimiento, String telefono) {
        JSONObject datos = new JSONObject();
        datos.put("id", id);
        datos.put("nombre", nombre);
        datos.put("fecha_nacimiento", fechaNacimiento);
        datos.put("telefono", telefono);
        return enviarSolicitud(new JSONObject().put("tipo", "guardar_paciente").put("datos", datos));
    }

    public JSONObject actualizarPaciente(String id, String nombre, String fechaNacimiento, String telefono) {
        JSONObject datos = new JSONObject();
        datos.put("id", id);
        datos.put("nombre", nombre);
        datos.put("fecha_nacimiento", fechaNacimiento);
        datos.put("telefono", telefono);
        return enviarSolicitud(new JSONObject().put("tipo", "actualizar_paciente").put("datos", datos));
    }

    public JSONObject eliminarPaciente(String id) {
        JSONObject datos = new JSONObject();
        datos.put("id", id);
        return enviarSolicitud(new JSONObject().put("tipo", "eliminar_paciente").put("datos", datos));
    }

    // ==========================================================
    // 💉 MEDICAMENTOS
    // ==========================================================
    public JSONObject obtenerMedicamentos() {
        return enviarSolicitud(new JSONObject().put("tipo", "obtener_medicamentos"));
    }

    public JSONObject guardarMedicamento(String codigo, String nombre, String presentacion) {
        JSONObject datos = new JSONObject();
        datos.put("codigo", codigo);
        datos.put("nombre", nombre);
        datos.put("presentacion", presentacion);
        return enviarSolicitud(new JSONObject().put("tipo", "guardar_medicamento").put("datos", datos));
    }

    public JSONObject actualizarMedicamento(String codigo, String nombre, String presentacion) {
        JSONObject datos = new JSONObject();
        datos.put("codigo", codigo);
        datos.put("nombre", nombre);
        datos.put("presentacion", presentacion);
        return enviarSolicitud(new JSONObject().put("tipo", "actualizar_medicamento").put("datos", datos));
    }

    public JSONObject eliminarMedicamento(String codigo) {
        JSONObject datos = new JSONObject();
        datos.put("codigo", codigo);
        return enviarSolicitud(new JSONObject().put("tipo", "eliminar_medicamento").put("datos", datos));
    }

    // ==========================================================
    // 📜 RECETAS MÉDICAS
    // ==========================================================
    public JSONObject obtenerRecetas() {
        return enviarSolicitud(new JSONObject().put("tipo", "obtener_recetas"));
    }

    public JSONObject guardarReceta(String idPaciente, String idMedico,
                                    String fechaConfeccion, String fechaRetiro,
                                    List<?> detalles) {
        JSONObject datos = new JSONObject();
        // 🔹 Usamos nombres exactos de columnas en la base de datos
        datos.put("id_paciente", idPaciente);
        datos.put("id_medico", idMedico);
        datos.put("fecha_confeccion", fechaConfeccion);
        datos.put("fecha_retiro", fechaRetiro);
        datos.put("detalles", new JSONArray(detalles));

        return enviarSolicitud(new JSONObject()
                .put("tipo", "guardar_receta")
                .put("datos", datos));
    }

    public JSONObject actualizarEstadoReceta(int idReceta, String nuevoEstado, String idFarmaceutico) {
        JSONObject datos = new JSONObject();
        datos.put("idReceta", idReceta);
        datos.put("nuevoEstado", nuevoEstado);
        datos.put("idFarmaceutico", idFarmaceutico);
        return enviarSolicitud(new JSONObject().put("tipo", "actualizar_estado_receta").put("datos", datos));
    }

    // ==========================================================
    // 📊 DASHBOARD / ESTADÍSTICAS
    // ==========================================================
    public JSONObject obtenerEstadisticas(String desde, String hasta, String medicamentoFiltro) {
        JSONObject datos = new JSONObject();
        datos.put("desde", desde);
        datos.put("hasta", hasta);
        if (medicamentoFiltro != null && !medicamentoFiltro.isEmpty())
            datos.put("medicamentoFiltro", medicamentoFiltro);

        return enviarSolicitud(new JSONObject().put("tipo", "obtener_estadisticas").put("datos", datos));
    }

    // ==========================================================
    // 📨 USUARIOS CONECTADOS Y CHAT
    // ==========================================================
    public JSONObject obtenerUsuariosConectados() {
        return enviarSolicitud(new JSONObject().put("tipo", "obtener_usuarios_conectados"));
    }

    public JSONObject enviarMensaje(String destinatario, String mensaje) {
        JSONObject datos = new JSONObject();
        datos.put("remitente", this.usuarioActual);
        datos.put("destinatario", destinatario);
        datos.put("texto", mensaje);
        return enviarSolicitud(new JSONObject().put("tipo", "enviar_mensaje").put("datos", datos));
    }

    public JSONObject obtenerMensajes(String usuario) {
        JSONObject datos = new JSONObject();
        datos.put("usuario", usuario);
        return enviarSolicitud(new JSONObject().put("tipo", "obtener_mensajes").put("datos", datos));
    }

    // ==========================================================
    // ⚙️ UTILIDADES
    // ==========================================================
    public void cerrarConexion() {
        conectado = false;
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
        }
    }

    public boolean isConectado() { return conectado; }
    public String getUsuarioActual() { return usuarioActual; }
    public String getTipoUsuario() { return tipoUsuario; }
    public String getNombreUsuario() { return nombreUsuario; }
    public void setControladorUsuarios(ControladorUsuariosActivos controlador) { this.controladorUsuarios = controlador; }
}
