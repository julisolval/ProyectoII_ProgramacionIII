package server;

import service.Service;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClientHandler implements Runnable {
    private Socket socket;
    private BackendServer server;
    private PrintWriter out;
    private BufferedReader in;
    private Service service;
    private String username;

    public ClientHandler(Socket socket, BackendServer server) {
        this.socket = socket;
        this.server = server;
        this.service = server.getService();
        try {
            this.out = new PrintWriter(socket.getOutputStream(), true);
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            System.err.println("Error creando ClientHandler: " + e.getMessage());
        }
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Mensaje recibido: " + inputLine);
                JSONObject request = new JSONObject(inputLine);
                processRequest(request);
            }
        } catch (IOException e) {
            System.err.println("Error en ClientHandler: " + e.getMessage());
        } finally {
            if (username != null) {
                server.notifyUserLogout(username);
            }
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.removeClient(this);
        }
    }

    private void processRequest(JSONObject request) {
        String tipo = request.getString("tipo");
        JSONObject datos = request.optJSONObject("datos");
        JSONObject response = new JSONObject();

        if (request.has("requestId")) {
            response.put("requestId", request.getString("requestId"));
        }

        try {
            switch (tipo) {
                case "login":
                    handleLogin(datos, response);
                    break;
                case "logout":
                    handleLogout(datos, response);
                    break;
                case "cambiar_clave":
                    handleCambiarClave(datos, response);
                    break;
                case "obtener_medicos":
                    handleObtenerMedicos(response);
                    break;
                case "guardar_medico":
                    handleGuardarMedico(datos, response);
                    break;
                case "actualizar_medico":
                    handleActualizarMedico(datos, response);
                    break;
                case "eliminar_medico":
                    handleEliminarMedico(datos, response);
                    break;
                case "obtener_farmaceuticos":
                    handleObtenerFarmaceuticos(response);
                    break;
                case "guardar_farmaceutico":
                    handleGuardarFarmaceutico(datos, response);
                    break;
                case "actualizar_farmaceutico":
                    handleActualizarFarmaceutico(datos, response);
                    break;
                case "eliminar_farmaceutico":
                    handleEliminarFarmaceutico(datos, response);
                    break;
                case "obtener_pacientes":
                    handleObtenerPacientes(response);
                    break;
                case "guardar_paciente":
                    handleGuardarPaciente(datos, response);
                    break;
                case "actualizar_paciente":
                    handleActualizarPaciente(datos, response);
                    break;
                case "eliminar_paciente":
                    handleEliminarPaciente(datos, response);
                    break;
                case "obtener_medicamentos":
                    handleObtenerMedicamentos(response);
                    break;
                case "guardar_medicamento":
                    handleGuardarMedicamento(datos, response);
                    break;
                case "actualizar_medicamento":
                    handleActualizarMedicamento(datos, response);
                    break;
                case "eliminar_medicamento":
                    handleEliminarMedicamento(datos, response);
                    break;
                case "guardar_receta":
                    handleGuardarReceta(datos, response);
                    break;
                case "obtener_recetas":
                    handleObtenerRecetas(response);
                    break;
                case "actualizar_estado_receta":
                    handleActualizarEstadoReceta(datos, response);
                    break;
                case "obtener_estadisticas":
                    handleObtenerEstadisticas(datos, response);
                    break;
                case "enviar_mensaje":
                    handleEnviarMensaje(datos, response);
                    break;
                case "obtener_mensajes":
                    handleObtenerMensajes(datos, response);
                    break;
                case "obtener_usuarios_conectados":
                    handleObtenerUsuariosConectados(response);
                    break;
                default:
                    response.put("estado", "error");
                    response.put("mensaje", "Operación no soportada: " + tipo);
            }
        } catch (Exception e) {
            response.put("estado", "error");
            response.put("mensaje", e.getMessage());
            e.printStackTrace();
        }

        out.println(response.toString());
    }

    private void handleLogin(JSONObject datos, JSONObject response) {
        String username = datos.getString("username");
        String password = datos.getString("password");

        boolean success = service.login(username, password);
        if (success) {
            this.username = username;
            response.put("estado", "éxito");
            response.put("mensaje", "Login exitoso");

            String nombre = username;
            server.notifyUserLogin(username, nombre);
        } else {
            response.put("estado", "error");
            response.put("mensaje", "Credenciales incorrectas");
        }
    }

    private void handleLogout(JSONObject datos, JSONObject response) {
        String username = datos.getString("username");
        this.username = null;
        response.put("estado", "éxito");
        response.put("mensaje", "Logout exitoso");
        server.notifyUserLogout(username);
    }

    private void handleCambiarClave(JSONObject datos, JSONObject response) {
        String username = datos.getString("username");
        String oldPassword = datos.getString("oldPassword");
        String newPassword = datos.getString("newPassword");

        boolean success = service.cambiarClave(username, oldPassword, newPassword);
        if (success) {
            response.put("estado", "éxito");
            response.put("mensaje", "Clave cambiada exitosamente");
        } else {
            response.put("estado", "error");
            response.put("mensaje", "Error al cambiar la clave");
        }
    }

    private void handleObtenerMedicos(JSONObject response) {
        List<?> medicos = service.obtenerMedicos();
        response.put("estado", "éxito");
        response.put("datos", new JSONArray(medicos));
    }

    private void handleGuardarMedico(JSONObject datos, JSONObject response) {
        Map<String, String> medico = new HashMap<>();
        medico.put("id", datos.getString("id"));
        medico.put("nombre", datos.getString("nombre"));
        medico.put("especialidad", datos.getString("especialidad"));

        service.guardarMedico(medico);
        response.put("estado", "éxito");
        response.put("mensaje", "Médico guardado exitosamente");
    }

    private void handleActualizarMedico(JSONObject datos, JSONObject response) {
        Map<String, String> medico = new HashMap<>();
        medico.put("id", datos.getString("id"));
        medico.put("nombre", datos.getString("nombre"));
        medico.put("especialidad", datos.getString("especialidad"));

        service.actualizarMedico(medico);
        response.put("estado", "éxito");
        response.put("mensaje", "Médico actualizado exitosamente");
    }

    private void handleEliminarMedico(JSONObject datos, JSONObject response) {
        String id = datos.getString("id");
        service.eliminarMedico(id);
        response.put("estado", "éxito");
        response.put("mensaje", "Médico eliminado exitosamente");
    }

    private void handleObtenerFarmaceuticos(JSONObject response) {
        List<?> farmaceuticos = service.obtenerFarmaceuticos();
        response.put("estado", "éxito");
        response.put("datos", new JSONArray(farmaceuticos));
    }

    private void handleGuardarFarmaceutico(JSONObject datos, JSONObject response) {
        Map<String, String> farmaceutico = new HashMap<>();
        farmaceutico.put("id", datos.getString("id"));
        farmaceutico.put("nombre", datos.getString("nombre"));

        service.guardarFarmaceutico(farmaceutico);
        response.put("estado", "éxito");
        response.put("mensaje", "Farmacéutico guardado exitosamente");
    }

    private void handleActualizarFarmaceutico(JSONObject datos, JSONObject response) {
        Map<String, String> farmaceutico = new HashMap<>();
        farmaceutico.put("id", datos.getString("id"));
        farmaceutico.put("nombre", datos.getString("nombre"));

        service.actualizarFarmaceutico(farmaceutico);
        response.put("estado", "éxito");
        response.put("mensaje", "Farmacéutico actualizado exitosamente");
    }

    private void handleEliminarFarmaceutico(JSONObject datos, JSONObject response) {
        String id = datos.getString("id");
        service.eliminarFarmaceutico(id);
        response.put("estado", "éxito");
        response.put("mensaje", "Farmacéutico eliminado exitosamente");
    }

    private void handleObtenerPacientes(JSONObject response) {
        List<?> pacientes = service.obtenerPacientes();
        response.put("estado", "éxito");
        response.put("datos", new JSONArray(pacientes));
    }

    private void handleGuardarPaciente(JSONObject datos, JSONObject response) {
        Map<String, String> paciente = new HashMap<>();
        paciente.put("id", datos.getString("id"));
        paciente.put("nombre", datos.getString("nombre"));
        paciente.put("fecha_nacimiento", datos.getString("fecha_nacimiento"));
        paciente.put("telefono", datos.getString("telefono"));

        service.guardarPaciente(paciente);
        response.put("estado", "éxito");
        response.put("mensaje", "Paciente guardado exitosamente");
    }

    private void handleActualizarPaciente(JSONObject datos, JSONObject response) {
        Map<String, String> paciente = new HashMap<>();
        paciente.put("id", datos.getString("id"));
        paciente.put("nombre", datos.getString("nombre"));
        paciente.put("fecha_nacimiento", datos.getString("fecha_nacimiento"));
        paciente.put("telefono", datos.getString("telefono"));

        service.actualizarPaciente(paciente);
        response.put("estado", "éxito");
        response.put("mensaje", "Paciente actualizado exitosamente");
    }

    private void handleEliminarPaciente(JSONObject datos, JSONObject response) {
        String id = datos.getString("id");
        service.eliminarPaciente(id);
        response.put("estado", "éxito");
        response.put("mensaje", "Paciente eliminado exitosamente");
    }

    private void handleObtenerMedicamentos(JSONObject response) {
        List<?> medicamentos = service.obtenerMedicamentos();
        response.put("estado", "éxito");
        response.put("datos", new JSONArray(medicamentos));
    }

    private void handleGuardarMedicamento(JSONObject datos, JSONObject response) {
        Map<String, String> medicamento = new HashMap<>();
        medicamento.put("codigo", datos.getString("codigo"));
        medicamento.put("nombre", datos.getString("nombre"));
        medicamento.put("presentacion", datos.getString("presentacion"));

        service.guardarMedicamento(medicamento);
        response.put("estado", "éxito");
        response.put("mensaje", "Medicamento guardado exitosamente");
    }

    private void handleActualizarMedicamento(JSONObject datos, JSONObject response) {
        Map<String, String> medicamento = new HashMap<>();
        medicamento.put("codigo", datos.getString("codigo"));
        medicamento.put("nombre", datos.getString("nombre"));
        medicamento.put("presentacion", datos.getString("presentacion"));

        service.actualizarMedicamento(medicamento);
        response.put("estado", "éxito");
        response.put("mensaje", "Medicamento actualizado exitosamente");
    }

    private void handleEliminarMedicamento(JSONObject datos, JSONObject response) {
        String codigo = datos.getString("codigo");
        service.eliminarMedicamento(codigo);
        response.put("estado", "éxito");
        response.put("mensaje", "Medicamento eliminado exitosamente");
    }

    private void handleGuardarReceta(JSONObject datos, JSONObject response) {
        Map<String, Object> receta = new HashMap<>();
        receta.put("idPaciente", datos.getString("idPaciente"));
        receta.put("idMedico", datos.getString("idMedico"));
        receta.put("fechaConfeccion", datos.getString("fechaConfeccion"));
        receta.put("fechaRetiro", datos.getString("fechaRetiro"));

        JSONArray detallesArray = datos.getJSONArray("detalles");
        List<Map<String, Object>> detalles = new ArrayList<>();
        for (int i = 0; i < detallesArray.length(); i++) {
            JSONObject detalleJson = detallesArray.getJSONObject(i);
            Map<String, Object> detalle = new HashMap<>();
            detalle.put("codigoMedicamento", detalleJson.getString("codigoMedicamento"));
            detalle.put("cantidad", detalleJson.getInt("cantidad"));
            detalle.put("indicaciones", detalleJson.getString("indicaciones"));
            detalle.put("duracion", detalleJson.getInt("duracion"));
            detalles.add(detalle);
        }

        service.guardarReceta(receta, detalles);
        response.put("estado", "éxito");
        response.put("mensaje", "Receta guardada exitosamente");
    }

    private void handleObtenerRecetas(JSONObject response) {
        List<?> recetas = service.obtenerRecetas();
        response.put("estado", "éxito");
        response.put("datos", new JSONArray(recetas));
    }

    private void handleActualizarEstadoReceta(JSONObject datos, JSONObject response) {
        int idReceta = datos.getInt("idReceta");
        String nuevoEstado = datos.getString("nuevoEstado");
        String idFarmaceutico = datos.getString("idFarmaceutico");

        service.actualizarEstadoReceta(idReceta, nuevoEstado, idFarmaceutico);
        response.put("estado", "éxito");
        response.put("mensaje", "Estado de receta actualizado exitosamente");
    }

    private void handleObtenerEstadisticas(JSONObject datos, JSONObject response) {
        String desdeStr = datos.getString("desde");
        String hastaStr = datos.getString("hasta");
        String medicamentoFiltro = datos.optString("medicamentoFiltro", null);

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date desde = sdf.parse(desdeStr);
            java.util.Date hasta = sdf.parse(hastaStr);

            Map<String, Object> estadisticas = service.obtenerEstadisticas(desde, hasta, medicamentoFiltro);
            response.put("estado", "éxito");
            response.put("datos", new JSONObject(estadisticas));
        } catch (Exception e) {
            response.put("estado", "error");
            response.put("mensaje", "Error al parsear fechas: " + e.getMessage());
        }
    }

    private void handleEnviarMensaje(JSONObject datos, JSONObject response) {
        String remitente = datos.getString("remitente");
        String destinatario = datos.getString("destinatario");
        String texto = datos.getString("texto");

        service.enviarMensaje(remitente, destinatario, texto);
        response.put("estado", "éxito");
        response.put("mensaje", "Mensaje enviado exitosamente");
    }

    private void handleObtenerMensajes(JSONObject datos, JSONObject response) {
        String usuario = datos.getString("usuario");
        List<?> mensajes = service.obtenerMensajes(usuario);
        response.put("estado", "éxito");
        response.put("datos", new JSONArray(mensajes));
    }

    private void handleObtenerUsuariosConectados(JSONObject response) {
        List<String> usuarios = service.obtenerUsuariosConectados();
        response.put("estado", "éxito");
        response.put("datos", new JSONArray(usuarios));
    }

    public void sendMessage(org.json.JSONObject message) {
        out.println(message.toString());
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }
}