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

public class ProxyService {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private ControladorUsuariosActivos controladorUsuarios;
    private boolean conectado = false;
    private String usuarioActual;

    private final ConcurrentHashMap<String, CompletableFuture<JSONObject>> pendingRequests = new ConcurrentHashMap<>();

    public ProxyService() {
        this(Config.BACKEND_HOST, Config.BACKEND_PORT);
    }

    public ProxyService(String host, int port) {
        connectToBackend(host, port);
    }

    public void setControladorUsuarios(ControladorUsuariosActivos controlador) {
        this.controladorUsuarios = controlador;
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

    private void startNotificationListener() {
        Thread listenerThread = new Thread(() -> {
            try {
                String inputLine;
                while (conectado && (inputLine = in.readLine()) != null) {
                    System.out.println("📨 Mensaje recibido: " + inputLine);
                    try {
                        JSONObject message = new JSONObject(inputLine);
                        if (message.has("requestId")) {
                            String requestId = message.getString("requestId");
                            CompletableFuture<JSONObject> future = pendingRequests.remove(requestId);
                            if (future != null) {
                                future.complete(message);
                            }
                        } else {
                            processAsyncNotification(message);
                        }
                    } catch (Exception e) {
                        System.err.println("❌ Error procesando mensaje JSON: " + e.getMessage());
                    }
                }
            } catch (IOException e) {
                System.err.println("❌ Conexión con backend perdida: " + e.getMessage());
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
        // Lógica de reconexión
        System.out.println("🔄 Intentando reconectar...");
        try {
            Thread.sleep(5000);
            connectToBackend("localhost", 12345);
        } catch (Exception e) {
            System.err.println("❌ Error en reconexión: " + e.getMessage());
        }
    }

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

    // MÉTODOS DE AUTENTICACIÓN
    public JSONObject login(String username, String password) {
        JSONObject request = new JSONObject();
        request.put("tipo", "login");

        JSONObject datos = new JSONObject();
        datos.put("username", username);
        datos.put("password", password);
        request.put("datos", datos);

        JSONObject response = enviarSolicitud(request);
        if ("éxito".equals(response.optString("estado"))) {
            this.usuarioActual = username;
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

    public JSONObject logout(String username) {
        JSONObject request = new JSONObject();
        request.put("tipo", "logout");

        JSONObject datos = new JSONObject();
        datos.put("username", username);
        request.put("datos", datos);

        JSONObject response = enviarSolicitud(request);
        this.usuarioActual = null;
        return response;
    }

    // MÉTODOS DE MÉDICOS
    public JSONObject obtenerMedicos() {
        JSONObject request = new JSONObject();
        request.put("tipo", "obtener_medicos");
        return enviarSolicitud(request);
    }

    public JSONObject guardarMedico(String id, String nombre, String especialidad) {
        JSONObject request = new JSONObject();
        request.put("tipo", "guardar_medico");

        JSONObject datos = new JSONObject();
        datos.put("id", id);
        datos.put("nombre", nombre);
        datos.put("especialidad", especialidad);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    public JSONObject actualizarMedico(String id, String nombre, String especialidad) {
        JSONObject request = new JSONObject();
        request.put("tipo", "actualizar_medico");

        JSONObject datos = new JSONObject();
        datos.put("id", id);
        datos.put("nombre", nombre);
        datos.put("especialidad", especialidad);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    public JSONObject eliminarMedico(String id) {
        JSONObject request = new JSONObject();
        request.put("tipo", "eliminar_medico");

        JSONObject datos = new JSONObject();
        datos.put("id", id);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    // MÉTODOS DE FARMACÉUTICOS
    public JSONObject obtenerFarmaceuticos() {
        JSONObject request = new JSONObject();
        request.put("tipo", "obtener_farmaceuticos");
        return enviarSolicitud(request);
    }

    public JSONObject guardarFarmaceutico(String id, String nombre) {
        JSONObject request = new JSONObject();
        request.put("tipo", "guardar_farmaceutico");

        JSONObject datos = new JSONObject();
        datos.put("id", id);
        datos.put("nombre", nombre);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    public JSONObject actualizarFarmaceutico(String id, String nombre) {
        JSONObject request = new JSONObject();
        request.put("tipo", "actualizar_farmaceutico");

        JSONObject datos = new JSONObject();
        datos.put("id", id);
        datos.put("nombre", nombre);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    public JSONObject eliminarFarmaceutico(String id) {
        JSONObject request = new JSONObject();
        request.put("tipo", "eliminar_farmaceutico");

        JSONObject datos = new JSONObject();
        datos.put("id", id);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    // MÉTODOS DE PACIENTES
    public JSONObject obtenerPacientes() {
        JSONObject request = new JSONObject();
        request.put("tipo", "obtener_pacientes");
        return enviarSolicitud(request);
    }

    public JSONObject guardarPaciente(String id, String nombre, String fechaNacimiento, String telefono) {
        JSONObject request = new JSONObject();
        request.put("tipo", "guardar_paciente");

        JSONObject datos = new JSONObject();
        datos.put("id", id);
        datos.put("nombre", nombre);
        datos.put("fecha_nacimiento", fechaNacimiento);
        datos.put("telefono", telefono);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    public JSONObject actualizarPaciente(String id, String nombre, String fechaNacimiento, String telefono) {
        JSONObject request = new JSONObject();
        request.put("tipo", "actualizar_paciente");

        JSONObject datos = new JSONObject();
        datos.put("id", id);
        datos.put("nombre", nombre);
        datos.put("fecha_nacimiento", fechaNacimiento);
        datos.put("telefono", telefono);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    public JSONObject eliminarPaciente(String id) {
        JSONObject request = new JSONObject();
        request.put("tipo", "eliminar_paciente");

        JSONObject datos = new JSONObject();
        datos.put("id", id);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    // MÉTODOS DE MEDICAMENTOS
    public JSONObject obtenerMedicamentos() {
        JSONObject request = new JSONObject();
        request.put("tipo", "obtener_medicamentos");
        return enviarSolicitud(request);
    }

    public JSONObject guardarMedicamento(String codigo, String nombre, String presentacion) {
        JSONObject request = new JSONObject();
        request.put("tipo", "guardar_medicamento");

        JSONObject datos = new JSONObject();
        datos.put("codigo", codigo);
        datos.put("nombre", nombre);
        datos.put("presentacion", presentacion);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    public JSONObject actualizarMedicamento(String codigo, String nombre, String presentacion) {
        JSONObject request = new JSONObject();
        request.put("tipo", "actualizar_medicamento");

        JSONObject datos = new JSONObject();
        datos.put("codigo", codigo);
        datos.put("nombre", nombre);
        datos.put("presentacion", presentacion);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    public JSONObject eliminarMedicamento(String codigo) {
        JSONObject request = new JSONObject();
        request.put("tipo", "eliminar_medicamento");

        JSONObject datos = new JSONObject();
        datos.put("codigo", codigo);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    // MÉTODOS DE RECETAS - COMPLETAMENTE CORREGIDOS
    public JSONObject obtenerRecetas() {
        JSONObject request = new JSONObject();
        request.put("tipo", "obtener_recetas");
        return enviarSolicitud(request);
    }

    public JSONObject guardarReceta(String idPaciente, String idMedico, String fechaConfeccion,
                                    String fechaRetiro, List<JSONObject> detalles) {
        JSONObject request = new JSONObject();
        request.put("tipo", "guardar_receta");

        JSONObject datos = new JSONObject();
        datos.put("idPaciente", idPaciente);
        datos.put("idMedico", idMedico);
        datos.put("fechaConfeccion", fechaConfeccion);
        datos.put("fechaRetiro", fechaRetiro);
        datos.put("detalles", new JSONArray(detalles));
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    public JSONObject actualizarEstadoReceta(int idReceta, String nuevoEstado, String idFarmaceutico) {
        JSONObject request = new JSONObject();
        request.put("tipo", "actualizar_estado_receta");

        JSONObject datos = new JSONObject();
        datos.put("idReceta", idReceta);
        datos.put("nuevoEstado", nuevoEstado);
        datos.put("idFarmaceutico", idFarmaceutico);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    // MÉTODOS DE DASHBOARD - CORREGIDOS
    public JSONObject obtenerEstadisticas(String desde, String hasta, String medicamentoFiltro) {
        JSONObject request = new JSONObject();
        request.put("tipo", "obtener_estadisticas");

        JSONObject datos = new JSONObject();
        datos.put("desde", desde);
        datos.put("hasta", hasta);
        if (medicamentoFiltro != null && !medicamentoFiltro.isEmpty()) {
            datos.put("medicamentoFiltro", medicamentoFiltro);
        }
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    // MÉTODOS DE USUARIOS Y MENSAJERÍA
    public JSONObject obtenerUsuariosConectados() {
        JSONObject request = new JSONObject();
        request.put("tipo", "obtener_usuarios_conectados");
        return enviarSolicitud(request);
    }

    public JSONObject enviarMensaje(String destinatario, String mensaje) {
        JSONObject request = new JSONObject();
        request.put("tipo", "enviar_mensaje");

        JSONObject datos = new JSONObject();
        datos.put("remitente", this.usuarioActual);
        datos.put("destinatario", destinatario);
        datos.put("texto", mensaje);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    public JSONObject obtenerMensajes(String usuario) {
        JSONObject request = new JSONObject();
        request.put("tipo", "obtener_mensajes");

        JSONObject datos = new JSONObject();
        datos.put("usuario", usuario);
        request.put("datos", datos);

        return enviarSolicitud(request);
    }

    // MÉTODOS DE UTILIDAD
    public void cerrarConexion() {
        conectado = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error cerrando conexión: " + e.getMessage());
        }
    }

    public boolean isConectado() {
        return conectado;
    }

    public String getUsuarioActual() {
        return usuarioActual;
    }

    // En ProxyService.java - AGREGAR MÉTODO
    public JSONObject logout() {
        if (this.usuarioActual != null) {
            JSONObject response = logout(this.usuarioActual);
            this.usuarioActual = null;
            return response;
        }
        JSONObject response = new JSONObject();
        response.put("estado", "éxito");
        response.put("mensaje", "Sesión cerrada");
        return response;
    }
}