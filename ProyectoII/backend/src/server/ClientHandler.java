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
import java.util.*;

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
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Mensaje recibido de " + (username != null ? username : "desconocido") + ": " + inputLine);
                try {
                    JSONObject request = new JSONObject(inputLine);
                    processRequest(request);
                } catch (Exception ex) {
                    System.err.println("Error procesando JSON: " + ex.getMessage());
                    ex.printStackTrace();
                    JSONObject errorResponse = new JSONObject();
                    errorResponse.put("estado", "error");
                    errorResponse.put("mensaje", "JSON inválido o error interno");
                    sendMessage(errorResponse);
                }
            }
        } catch (IOException e) {
            System.err.println("Error en ClientHandler: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (username != null) {
                service.logout(username);
                server.notifyUserLogout(username);
            }
            closeResources();
            server.removeClient(this);
        }
    }

    private void closeResources() {
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processRequest(JSONObject request) {
        String tipo = request.optString("tipo", null);
        JSONObject datos = request.optJSONObject("datos");
        JSONObject response = new JSONObject();
        response.put("estado", "error");
        response.put("mensaje", "Operación no procesada");

        if (request.has("requestId")) {
            response.put("requestId", request.getString("requestId"));
        }

        if (tipo == null) {
            response.put("mensaje", "Falta el campo 'tipo'");
            sendMessage(response);
            return;
        }

        try {
            switch (tipo) {
                case "login": handleLogin(datos, response); break;
                case "logout": handleLogout(datos, response); break;
                case "cambiar_clave": handleCambiarClave(datos, response); break;
                case "obtener_medicos": handleObtenerMedicos(response); break;
                case "guardar_medico": handleGuardarMedico(datos, response); break;
                case "actualizar_medico": handleActualizarMedico(datos, response); break;
                case "eliminar_medico": handleEliminarMedico(datos, response); break;
                case "obtener_farmaceuticos": handleObtenerFarmaceuticos(response); break;
                case "guardar_farmaceutico": handleGuardarFarmaceutico(datos, response); break;
                case "actualizar_farmaceutico": handleActualizarFarmaceutico(datos, response); break;
                case "eliminar_farmaceutico": handleEliminarFarmaceutico(datos, response); break;
                case "obtener_pacientes": handleObtenerPacientes(response); break;
                case "guardar_paciente": handleGuardarPaciente(datos, response); break;
                case "actualizar_paciente": handleActualizarPaciente(datos, response); break;
                case "eliminar_paciente": handleEliminarPaciente(datos, response); break;
                case "obtener_medicamentos": handleObtenerMedicamentos(response); break;
                case "guardar_medicamento": handleGuardarMedicamento(datos, response); break;
                case "actualizar_medicamento": handleActualizarMedicamento(datos, response); break;
                case "eliminar_medicamento": handleEliminarMedicamento(datos, response); break;
                case "guardar_receta": handleGuardarReceta(datos, response); break;
                case "obtener_recetas": handleObtenerRecetas(response); break;
                case "actualizar_estado_receta": handleActualizarEstadoReceta(datos, response); break;
                case "obtener_estadisticas": handleObtenerEstadisticas(datos, response); break;
                case "enviar_mensaje": handleEnviarMensaje(datos, response); break;
                case "obtener_mensajes": handleObtenerMensajes(datos, response); break;
                case "obtener_usuarios_conectados": handleObtenerUsuariosConectados(response); break;
                default:
                    response.put("mensaje", "Operación no soportada: " + tipo);
            }
        } catch (Exception e) {
            response.put("estado", "error");
            response.put("mensaje", e.getMessage());
            e.printStackTrace();
        }

        sendMessage(response);
    }

    private String getStringVariant(JSONObject obj, String... posiblesNombres) {
        for (String nombre : posiblesNombres) {
            if (obj.has(nombre) && !obj.isNull(nombre)) {
                return obj.getString(nombre);
            }
        }
        return null;
    }

    private void respondExito(JSONObject response, String mensaje) {
        response.put("estado", "éxito");
        response.put("mensaje", mensaje);
    }

    private void handleLogin(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        String username = getStringVariant(datos, "username", "user");
        String password = getStringVariant(datos, "password", "pass");
        if (username == null || password == null) { response.put("mensaje", "Usuario o contraseña faltantes"); sendMessage(response); return; }

        Map<String, Object> resultado = service.login(username, password);
        response.put("estado", resultado.get("estado"));
        response.put("mensaje", resultado.get("mensaje"));

        if ("éxito".equals(resultado.get("estado"))) {
            this.username = username;
            if (resultado.containsKey("tipo")) response.put("tipo", resultado.get("tipo"));
            if (resultado.containsKey("nombre")) response.put("nombre", resultado.get("nombre"));

            String nombreMostrar = resultado.containsKey("nombre") ? resultado.get("nombre").toString() : username;
            server.notifyUserLogin(username, nombreMostrar);
        }
    }

    private void handleLogout(JSONObject datos, JSONObject response) {
        if (datos == null) {
            response.put("mensaje", "Datos faltantes");
            sendMessage(response);
            return;
        }

        String username = getStringVariant(datos, "username", "user");
        if (username == null) {
            response.put("mensaje", "Usuario faltante");
            sendMessage(response);
            return;
        }

        service.logout(username);

        this.username = null;
        respondExito(response, "Logout exitoso");
        server.notifyUserLogout(username);
    }

    private void handleCambiarClave(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        String username = getStringVariant(datos, "username", "user");
        String oldPassword = getStringVariant(datos, "oldPassword", "old_pass");
        String newPassword = getStringVariant(datos, "newPassword", "new_pass");
        if (username == null || oldPassword == null || newPassword == null) {
            response.put("mensaje", "Campos incompletos para cambiar clave");
            sendMessage(response);
            return;
        }

        boolean success = service.cambiarClave(username, oldPassword, newPassword);
        if (success) respondExito(response, "Clave cambiada exitosamente");
        else { response.put("estado", "error"); response.put("mensaje", "Error al cambiar la clave"); }
    }

    private void handleObtenerMedicos(JSONObject response) {
        List<?> medicos = service.obtenerMedicos();
        respondExito(response, "Medicos obtenidos");
        response.put("datos", new JSONArray(medicos));
    }

    private void handleGuardarMedico(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        Map<String, String> medico = new HashMap<>();
        medico.put("id", getStringVariant(datos, "id"));
        medico.put("nombre", getStringVariant(datos, "nombre"));
        medico.put("especialidad", getStringVariant(datos, "especialidad"));
        service.guardarMedico(medico);
        respondExito(response, "Médico guardado exitosamente");
    }

    private void handleActualizarMedico(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        Map<String, String> medico = new HashMap<>();
        medico.put("id", getStringVariant(datos, "id"));
        medico.put("nombre", getStringVariant(datos, "nombre"));
        medico.put("especialidad", getStringVariant(datos, "especialidad"));
        service.actualizarMedico(medico);
        respondExito(response, "Médico actualizado exitosamente");
    }

    private void handleEliminarMedico(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        String id = getStringVariant(datos, "id");
        if (id == null) { response.put("mensaje", "ID faltante"); sendMessage(response); return; }
        service.eliminarMedico(id);
        respondExito(response, "Médico eliminado exitosamente");
    }

    private void handleObtenerFarmaceuticos(JSONObject response) {
        List<?> farmaceuticos = service.obtenerFarmaceuticos();
        respondExito(response, "Farmacéuticos obtenidos");
        response.put("datos", new JSONArray(farmaceuticos));
    }

    private void handleGuardarFarmaceutico(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        Map<String, String> farmaceutico = new HashMap<>();
        farmaceutico.put("id", getStringVariant(datos, "id"));
        farmaceutico.put("nombre", getStringVariant(datos, "nombre"));
        service.guardarFarmaceutico(farmaceutico);
        respondExito(response, "Farmacéutico guardado exitosamente");
    }

    private void handleActualizarFarmaceutico(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        Map<String, String> farmaceutico = new HashMap<>();
        farmaceutico.put("id", getStringVariant(datos, "id"));
        farmaceutico.put("nombre", getStringVariant(datos, "nombre"));
        service.actualizarFarmaceutico(farmaceutico);
        respondExito(response, "Farmacéutico actualizado exitosamente");
    }

    private void handleEliminarFarmaceutico(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        String id = getStringVariant(datos, "id");
        if (id == null) { response.put("mensaje", "ID faltante"); sendMessage(response); return; }
        service.eliminarFarmaceutico(id);
        respondExito(response, "Farmacéutico eliminado exitosamente");
    }

    private void handleObtenerPacientes(JSONObject response) {
        List<?> pacientes = service.obtenerPacientes();
        respondExito(response, "Pacientes obtenidos");
        response.put("datos", new JSONArray(pacientes));
    }

    private void handleGuardarPaciente(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        Map<String, String> paciente = new HashMap<>();
        paciente.put("id", getStringVariant(datos, "id"));
        paciente.put("nombre", getStringVariant(datos, "nombre"));
        paciente.put("fecha_nacimiento", getStringVariant(datos, "fecha_nacimiento", "fechaNacimiento"));
        paciente.put("telefono", getStringVariant(datos, "telefono", "tel"));
        service.guardarPaciente(paciente);
        respondExito(response, "Paciente guardado exitosamente");
    }

    private void handleActualizarPaciente(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        Map<String, String> paciente = new HashMap<>();
        paciente.put("id", getStringVariant(datos, "id"));
        paciente.put("nombre", getStringVariant(datos, "nombre"));
        paciente.put("fecha_nacimiento", getStringVariant(datos, "fecha_nacimiento", "fechaNacimiento"));
        paciente.put("telefono", getStringVariant(datos, "telefono", "tel"));
        service.actualizarPaciente(paciente);
        respondExito(response, "Paciente actualizado exitosamente");
    }

    private void handleEliminarPaciente(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        String id = getStringVariant(datos, "id");
        if (id == null) { response.put("mensaje", "ID faltante"); sendMessage(response); return; }
        service.eliminarPaciente(id);
        respondExito(response, "Paciente eliminado exitosamente");
    }

    private void handleObtenerMedicamentos(JSONObject response) {
        List<?> medicamentos = service.obtenerMedicamentos();
        respondExito(response, "Medicamentos obtenidos");
        response.put("datos", new JSONArray(medicamentos));
    }

    private void handleGuardarMedicamento(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        Map<String, String> medicamento = new HashMap<>();
        medicamento.put("codigo", getStringVariant(datos, "codigo"));
        medicamento.put("nombre", getStringVariant(datos, "nombre"));
        medicamento.put("presentacion", getStringVariant(datos, "presentacion"));
        service.guardarMedicamento(medicamento);
        respondExito(response, "Medicamento guardado exitosamente");
    }

    private void handleActualizarMedicamento(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        Map<String, String> medicamento = new HashMap<>();
        medicamento.put("codigo", getStringVariant(datos, "codigo"));
        medicamento.put("nombre", getStringVariant(datos, "nombre"));
        medicamento.put("presentacion", getStringVariant(datos, "presentacion"));
        service.actualizarMedicamento(medicamento);
        respondExito(response, "Medicamento actualizado exitosamente");
    }

    private void handleEliminarMedicamento(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        String codigo = getStringVariant(datos, "codigo");
        if (codigo == null) { response.put("mensaje", "Código faltante"); sendMessage(response); return; }
        service.eliminarMedicamento(codigo);
        respondExito(response, "Medicamento eliminado exitosamente");
    }


    private void handleGuardarReceta(JSONObject datos, JSONObject response) {
        try {
            if (datos == null) {
                response.put("estado", "error");
                response.put("mensaje", "Datos faltantes");
                sendMessage(response);
                return;
            }
            String idPaciente = datos.optString("idPaciente", datos.optString("id_paciente", null));
            String idMedico = datos.optString("idMedico", datos.optString("id_medico", null));

            if (idPaciente == null || idMedico == null) {
                response.put("estado", "error");
                response.put("mensaje", "Campos obligatorios faltantes: idPaciente o idMedico");
                sendMessage(response);
                return;
            }

            String fechaConfeccion = datos.optString("fechaConfeccion", datos.optString("fecha_confeccion", null));
            if (fechaConfeccion == null || fechaConfeccion.isEmpty()) {
                fechaConfeccion = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
            }

            String fechaRetiro = datos.optString("fechaRetiro", datos.optString("fecha_retiro", null));
            if (fechaRetiro != null && fechaRetiro.isEmpty()) {
                fechaRetiro = null;
            }

            Map<String, Object> receta = new HashMap<>();
            receta.put("id_paciente", idPaciente);
            receta.put("id_medico", idMedico);
            receta.put("fechaConfeccion", fechaConfeccion);
            receta.put("fechaRetiro", fechaRetiro);

            JSONArray detallesArray = datos.optJSONArray("detalles");
            if (detallesArray == null || detallesArray.isEmpty()) {
                response.put("estado", "error");
                response.put("mensaje", "No hay detalles de medicamentos");
                sendMessage(response);
                return;
            }

            List<Map<String, Object>> detalles = new ArrayList<>();
            for (int i = 0; i < detallesArray.length(); i++) {
                JSONObject detalleJson = detallesArray.getJSONObject(i);
                Map<String, Object> detalle = new HashMap<>();

                String codigoMedicamento = detalleJson.optString("codigoMedicamento", null);
                int cantidad = detalleJson.optInt("cantidad", -1);
                String indicaciones = detalleJson.optString("indicaciones", null);
                int duracion = detalleJson.optInt("duracion", -1);

                if (codigoMedicamento == null || cantidad < 0 || indicaciones == null || duracion < 0) {
                    response.put("estado", "error");
                    response.put("mensaje", "Detalle de medicamento incompleto en posición " + i);
                    sendMessage(response);
                    return;
                }

                detalle.put("codigoMedicamento", codigoMedicamento);
                detalle.put("cantidad", cantidad);
                detalle.put("indicaciones", indicaciones);
                detalle.put("duracion", duracion);

                detalles.add(detalle);
            }

            System.out.println("Receta a guardar: " + receta);
            System.out.println("Detalles: " + detalles);

            service.guardarReceta(receta, detalles);

            response.put("estado", "éxito");
            response.put("mensaje", "Receta guardada exitosamente");

        } catch (Exception e) {
            response.put("estado", "error");
            response.put("mensaje", "Error al guardar receta: " + e.getMessage());
            e.printStackTrace();
        }

        sendMessage(response);
    }




    private void handleObtenerRecetas(JSONObject response) {
        List<?> recetas = service.obtenerRecetas();
        respondExito(response, "Recetas obtenidas");
        response.put("datos", new JSONArray(recetas));
    }

    private void handleActualizarEstadoReceta(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        int idReceta = datos.optInt("idReceta", -1);
        String nuevoEstado = getStringVariant(datos, "nuevoEstado", "nuevo_estado");
        String idFarmaceutico = getStringVariant(datos, "idFarmaceutico", "id_farmaceutico");
        if (idReceta == -1 || nuevoEstado == null || idFarmaceutico == null) {
            response.put("mensaje", "Campos incompletos para actualizar estado de receta");
            sendMessage(response);
            return;
        }
        service.actualizarEstadoReceta(idReceta, nuevoEstado, idFarmaceutico);
        respondExito(response, "Estado de receta actualizado exitosamente");
    }

    private void handleObtenerEstadisticas(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        String desdeStr = getStringVariant(datos, "desde");
        String hastaStr = getStringVariant(datos, "hasta");
        String medicamentoFiltro = getStringVariant(datos, "medicamentoFiltro", "medicamento_filtro");

        if (desdeStr == null || hastaStr == null) {
            response.put("mensaje", "Fechas incompletas");
            sendMessage(response);
            return;
        }

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date desde = sdf.parse(desdeStr);
            Date hasta = sdf.parse(hastaStr);

            Map<String, Object> estadisticas = service.obtenerEstadisticas(desde, hasta, medicamentoFiltro);
            respondExito(response, "Estadísticas obtenidas");
            response.put("datos", new JSONObject(estadisticas));
        } catch (Exception e) {
            response.put("estado", "error");
            response.put("mensaje", "Error al parsear fechas: " + e.getMessage());
        }
    }

    private void handleEnviarMensaje(JSONObject datos, JSONObject response) {
        if (datos == null) {
            response.put("mensaje", "Datos faltantes");
            sendMessage(response);
            return;
        }

        String remitente = getStringVariant(datos, "remitente");
        String destinatario = getStringVariant(datos, "destinatario");
        String texto = getStringVariant(datos, "texto");

        if (remitente == null || destinatario == null || texto == null) {
            response.put("mensaje", "Campos incompletos para enviar mensaje");
            sendMessage(response);
            return;
        }

        service.enviarMensaje(remitente, destinatario, texto);
        respondExito(response, "Mensaje enviado exitosamente");

        server.notifyNewMessage(remitente, destinatario, texto);
    }

    private void handleObtenerMensajes(JSONObject datos, JSONObject response) {
        if (datos == null) { response.put("mensaje", "Datos faltantes"); sendMessage(response); return; }
        String usuario = getStringVariant(datos, "usuario");
        if (usuario == null) { response.put("mensaje", "Usuario faltante"); sendMessage(response); return; }
        List<?> mensajes = service.obtenerMensajes(usuario);
        respondExito(response, "Mensajes obtenidos");
        response.put("datos", new JSONArray(mensajes));
    }

    private void handleObtenerUsuariosConectados(JSONObject response) {
        List<String> usuarios = service.obtenerUsuariosConectados();
        respondExito(response, "Usuarios conectados obtenidos");
        response.put("datos", new JSONArray(usuarios));
    }

    public void sendMessage(JSONObject message) {
        if (isConnected() && out != null) out.println(message.toString());
    }

    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    public String getUsername() {
        return username;
    }
}
