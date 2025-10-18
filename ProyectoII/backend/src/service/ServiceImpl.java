package service;

import dao.DatabaseConnection;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class ServiceImpl implements Service {
    @Override
    public boolean login(String username, String password) {
        String sql = "SELECT id, nombre, tipo FROM Usuario WHERE id = ? AND clave = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String updateSql = "UPDATE Usuario SET activo = TRUE WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, username);
                    updateStmt.executeUpdate();
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            System.err.println("Error en login BD: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean cambiarClave(String username, String oldPassword, String newPassword) {
        String sql = "UPDATE Usuario SET clave = ? WHERE id = ? AND clave = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.setString(3, oldPassword);

            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;

        } catch (SQLException e) {
            System.err.println("Error cambiando clave: " + e.getMessage());
            return false;
        }
    }

    @Override
    public List<Map<String, Object>> obtenerMedicos() {
        List<Map<String, Object>> medicos = new ArrayList<>();
        String sql = "SELECT id, nombre, especialidad FROM Usuario WHERE tipo = 'MEDICO'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> medico = new HashMap<>();
                medico.put("id", rs.getString("id"));
                medico.put("nombre", rs.getString("nombre"));
                medico.put("especialidad", rs.getString("especialidad"));
                medicos.add(medico);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo médicos: " + e.getMessage());
        }

        return medicos;
    }

    @Override
    public void guardarMedico(Object medicoObj) {
        if (!(medicoObj instanceof Map)) {
            throw new IllegalArgumentException("Parámetro médico debe ser un Map");
        }
        @SuppressWarnings("unchecked")
        Map<String, String> medico = (Map<String, String>) medicoObj;

        String sql = "INSERT INTO Usuario (id, clave, nombre, tipo, especialidad) VALUES (?, ?, ?, 'MEDICO', ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medico.get("id"));
            stmt.setString(2, medico.get("id"));
            stmt.setString(3, medico.get("nombre"));
            stmt.setString(4, medico.get("especialidad"));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error guardando médico: " + e.getMessage());
            throw new RuntimeException("Error al guardar médico: " + e.getMessage());
        }
    }

    @Override
    public void actualizarMedico(Object medicoObj) {
        Map<String, String> medico = (Map<String, String>) medicoObj;
        String sql = "UPDATE Usuario SET nombre = ?, especialidad = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medico.get("nombre"));
            stmt.setString(2, medico.get("especialidad"));
            stmt.setString(3, medico.get("id"));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error actualizando médico: " + e.getMessage());
            throw new RuntimeException("Error al actualizar médico: " + e.getMessage());
        }
    }

    @Override
    public void eliminarMedico(String id) {
        String sql = "DELETE FROM Usuario WHERE id = ? AND tipo = 'MEDICO'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error eliminando médico: " + e.getMessage());
            throw new RuntimeException("Error al eliminar médico: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> obtenerFarmaceuticos() {
        List<Map<String, Object>> farmaceuticos = new ArrayList<>();
        String sql = "SELECT id, nombre FROM Usuario WHERE tipo = 'FARMACEUTA'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> farmaceutico = new HashMap<>();
                farmaceutico.put("id", rs.getString("id"));
                farmaceutico.put("nombre", rs.getString("nombre"));
                farmaceuticos.add(farmaceutico);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo farmacéuticos: " + e.getMessage());
        }

        return farmaceuticos;
    }

    @Override
    public void guardarFarmaceutico(Object farmaceuticoObj) {
        if (!(farmaceuticoObj instanceof Map)) {
            throw new IllegalArgumentException("Parámetro farmaceutico debe ser un Map");
        }
        @SuppressWarnings("unchecked")
        Map<String, String> farmaceutico = (Map<String, String>) farmaceuticoObj;
        String sql = "INSERT INTO Usuario (id, clave, nombre, tipo) VALUES (?, ?, ?, 'FARMACEUTA')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, farmaceutico.get("id"));
            stmt.setString(2, farmaceutico.get("id"));
            stmt.setString(3, farmaceutico.get("nombre"));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error guardando farmacéutico: " + e.getMessage());
            throw new RuntimeException("Error al guardar farmacéutico: " + e.getMessage());
        }
    }

    @Override
    public void actualizarFarmaceutico(Object farmaceuticoObj) {
        Map<String, String> farmaceutico = (Map<String, String>) farmaceuticoObj;
        String sql = "UPDATE Usuario SET nombre = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, farmaceutico.get("nombre"));
            stmt.setString(2, farmaceutico.get("id"));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error actualizando farmacéutico: " + e.getMessage());
            throw new RuntimeException("Error al actualizar farmacéutico: " + e.getMessage());
        }
    }

    @Override
    public void eliminarFarmaceutico(String id) {
        String sql = "DELETE FROM Usuario WHERE id = ? AND tipo = 'FARMACEUTA'";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error eliminando farmacéutico: " + e.getMessage());
            throw new RuntimeException("Error al eliminar farmacéutico: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> obtenerPacientes() {
        List<Map<String, Object>> pacientes = new ArrayList<>();
        String sql = "SELECT id, nombre, fecha_nacimiento, telefono FROM Paciente";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> paciente = new HashMap<>();
                paciente.put("id", rs.getString("id"));
                paciente.put("nombre", rs.getString("nombre"));
                paciente.put("fecha_nacimiento", rs.getString("fecha_nacimiento"));
                paciente.put("telefono", rs.getString("telefono"));
                pacientes.add(paciente);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo pacientes: " + e.getMessage());
        }

        return pacientes;
    }

    @Override
    public void guardarPaciente(Object pacienteObj) {
        if (!(pacienteObj instanceof Map)) {
            throw new IllegalArgumentException("Parámetro paciente debe ser un Map");
        }

        @SuppressWarnings("unchecked")
        Map<String, String> paciente = (Map<String, String>) pacienteObj;

        String sql = "INSERT INTO Paciente (id, nombre, fecha_nacimiento, telefono) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paciente.get("id"));
            stmt.setString(2, paciente.get("nombre"));
            stmt.setString(3, paciente.get("fecha_nacimiento"));
            stmt.setString(4, paciente.get("telefono"));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error guardando paciente: " + e.getMessage());
            throw new RuntimeException("Error al guardar paciente: " + e.getMessage());
        }
    }

    @Override
    public void actualizarPaciente(Object pacienteObj) {
        Map<String, String> paciente = (Map<String, String>) pacienteObj;
        String sql = "UPDATE Paciente SET nombre = ?, fecha_nacimiento = ?, telefono = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, paciente.get("nombre"));
            stmt.setString(2, paciente.get("fecha_nacimiento"));
            stmt.setString(3, paciente.get("telefono"));
            stmt.setString(4, paciente.get("id"));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error actualizando paciente: " + e.getMessage());
            throw new RuntimeException("Error al actualizar paciente: " + e.getMessage());
        }
    }

    @Override
    public void eliminarPaciente(String id) {
        String sql = "DELETE FROM Paciente WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, id);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error eliminando paciente: " + e.getMessage());
            throw new RuntimeException("Error al eliminar paciente: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> obtenerMedicamentos() {
        List<Map<String, Object>> medicamentos = new ArrayList<>();
        String sql = "SELECT codigo, nombre, presentacion FROM Medicamento";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> medicamento = new HashMap<>();
                medicamento.put("codigo", rs.getString("codigo"));
                medicamento.put("nombre", rs.getString("nombre"));
                medicamento.put("presentacion", rs.getString("presentacion"));
                medicamentos.add(medicamento);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo medicamentos: " + e.getMessage());
        }

        return medicamentos;
    }

    @Override
    public void guardarMedicamento(Object medicamentoObj) {
        if (!(medicamentoObj instanceof Map)) {
            throw new IllegalArgumentException("Parámetro medicamento debe ser un Map");
        }
        @SuppressWarnings("unchecked")
        Map<String, String> medicamento = (Map<String, String>) medicamentoObj;
        String sql = "INSERT INTO Medicamento (codigo, nombre, presentacion) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicamento.get("codigo"));
            stmt.setString(2, medicamento.get("nombre"));
            stmt.setString(3, medicamento.get("presentacion"));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error guardando medicamento: " + e.getMessage());
            throw new RuntimeException("Error al guardar medicamento: " + e.getMessage());
        }
    }

    @Override
    public void actualizarMedicamento(Object medicamentoObj) {
        Map<String, String> medicamento = (Map<String, String>) medicamentoObj;
        String sql = "UPDATE Medicamento SET nombre = ?, presentacion = ? WHERE codigo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, medicamento.get("nombre"));
            stmt.setString(2, medicamento.get("presentacion"));
            stmt.setString(3, medicamento.get("codigo"));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error actualizando medicamento: " + e.getMessage());
            throw new RuntimeException("Error al actualizar medicamento: " + e.getMessage());
        }
    }

    @Override
    public void eliminarMedicamento(String codigo) {
        String sql = "DELETE FROM Medicamento WHERE codigo = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, codigo);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error eliminando medicamento: " + e.getMessage());
            throw new RuntimeException("Error al eliminar medicamento: " + e.getMessage());
        }
    }

    @Override
    public void guardarReceta(Object recetaObj, List<?> detallesObj) {
        if (!(recetaObj instanceof Map)) {
            throw new IllegalArgumentException("Parámetro receta debe ser un Map");
        }
        @SuppressWarnings("unchecked")
        Map<String, Object> receta = (Map<String, Object>) recetaObj;
        List<Map<String, Object>> detalles = (List<Map<String, Object>>) detallesObj;

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String sqlReceta = "INSERT INTO Receta (id_paciente, id_medico, fecha_confeccion, fecha_retiro, estado) VALUES (?, ?, ?, ?, 'CONFECCIONADA')";
            PreparedStatement stmtReceta = conn.prepareStatement(sqlReceta, Statement.RETURN_GENERATED_KEYS);
            stmtReceta.setString(1, (String) receta.get("idPaciente"));
            stmtReceta.setString(2, (String) receta.get("idMedico"));
            stmtReceta.setString(3, (String) receta.get("fechaConfeccion"));
            stmtReceta.setString(4, (String) receta.get("fechaRetiro"));
            stmtReceta.executeUpdate();

            ResultSet generatedKeys = stmtReceta.getGeneratedKeys();
            int idReceta = -1;
            if (generatedKeys.next()) {
                idReceta = generatedKeys.getInt(1);
            }

            String sqlDetalle = "INSERT INTO DetalleReceta (id_receta, codigo_medicamento, cantidad, indicaciones, duracion) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmtDetalle = conn.prepareStatement(sqlDetalle);

            for (Map<String, Object> detalle : detalles) {
                stmtDetalle.setInt(1, idReceta);
                stmtDetalle.setString(2, (String) detalle.get("codigoMedicamento"));
                stmtDetalle.setInt(3, Integer.parseInt(detalle.get("cantidad").toString()));
                stmtDetalle.setString(4, (String) detalle.get("indicaciones"));
                stmtDetalle.setInt(5, Integer.parseInt(detalle.get("duracion").toString()));
                stmtDetalle.addBatch();
            }

            stmtDetalle.executeBatch();
            conn.commit();

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            System.err.println("Error guardando receta: " + e.getMessage());
            throw new RuntimeException("Error al guardar receta: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<Map<String, Object>> obtenerRecetas() {
        List<Map<String, Object>> recetas = new ArrayList<>();
        String sql = "SELECT r.*, p.nombre as nombre_paciente, u.nombre as nombre_medico " +
                "FROM Receta r " +
                "JOIN Paciente p ON r.id_paciente = p.id " +
                "JOIN Usuario u ON r.id_medico = u.id " +
                "ORDER BY r.fecha_confeccion DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> receta = new HashMap<>();
                receta.put("id", rs.getInt("id"));
                receta.put("id_paciente", rs.getString("id_paciente"));
                receta.put("nombre_paciente", rs.getString("nombre_paciente"));
                receta.put("id_medico", rs.getString("id_medico"));
                receta.put("nombre_medico", rs.getString("nombre_medico"));
                receta.put("fecha_confeccion", rs.getString("fecha_confeccion"));
                receta.put("fecha_retiro", rs.getString("fecha_retiro"));
                receta.put("estado", rs.getString("estado"));
                recetas.add(receta);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo recetas: " + e.getMessage());
        }

        return recetas;
    }

    @Override
    public void actualizarEstadoReceta(int idReceta, String nuevoEstado, String idFarmaceutico) {
        String sql = "UPDATE Receta SET estado = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nuevoEstado);
            stmt.setInt(2, idReceta);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error actualizando estado receta: " + e.getMessage());
            throw new RuntimeException("Error al actualizar estado de receta: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> obtenerEstadisticas(Date desde, Date hasta, String medicamentoFiltro) {
        Map<String, Object> stats = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        try (Connection conn = DatabaseConnection.getConnection()) {
            String sqlRecetas = "SELECT estado, COUNT(*) as cantidad FROM Receta WHERE fecha_confeccion BETWEEN ? AND ? GROUP BY estado";
            PreparedStatement stmtRecetas = conn.prepareStatement(sqlRecetas);
            stmtRecetas.setString(1, sdf.format(desde));
            stmtRecetas.setString(2, sdf.format(hasta));

            ResultSet rsRecetas = stmtRecetas.executeQuery();
            int confeccionadas = 0, proceso = 0, lista = 0, entregadas = 0;

            while (rsRecetas.next()) {
                String estado = rsRecetas.getString("estado");
                int cantidad = rsRecetas.getInt("cantidad");
                switch (estado) {
                    case "CONFECCIONADA": confeccionadas = cantidad; break;
                    case "PROCESO": proceso = cantidad; break;
                    case "LISTA": lista = cantidad; break;
                    case "ENTREGADA": entregadas = cantidad; break;
                }
            }

            stats.put("confeccionadas", confeccionadas);
            stats.put("proceso", proceso);
            stats.put("lista", lista);
            stats.put("entregadas", entregadas);
            stats.put("total", confeccionadas + proceso + lista + entregadas);

            String sqlMedicamentos = "SELECT m.nombre, SUM(d.cantidad) as total_prescrito " +
                    "FROM DetalleReceta d " +
                    "JOIN Medicamento m ON d.codigo_medicamento = m.codigo " +
                    "JOIN Receta r ON d.id_receta = r.id " +
                    "WHERE r.fecha_confeccion BETWEEN ? AND ? " +
                    (medicamentoFiltro != null && !medicamentoFiltro.isEmpty() ?
                            " AND m.nombre LIKE ? " : "") +
                    "GROUP BY m.nombre ORDER BY total_prescrito DESC LIMIT 10";

            PreparedStatement stmtMedicamentos = conn.prepareStatement(sqlMedicamentos);
            stmtMedicamentos.setString(1, sdf.format(desde));
            stmtMedicamentos.setString(2, sdf.format(hasta));

            if (medicamentoFiltro != null && !medicamentoFiltro.isEmpty()) {
                stmtMedicamentos.setString(3, "%" + medicamentoFiltro + "%");
            }

            ResultSet rsMedicamentos = stmtMedicamentos.executeQuery();
            List<String> nombresMedicamentos = new ArrayList<>();
            List<Integer> cantidadesMedicamentos = new ArrayList<>();

            while (rsMedicamentos.next()) {
                nombresMedicamentos.add(rsMedicamentos.getString("nombre"));
                cantidadesMedicamentos.add(rsMedicamentos.getInt("total_prescrito"));
            }

            stats.put("nombres_medicamentos", nombresMedicamentos);
            stats.put("cantidades_medicamentos", cantidadesMedicamentos);

        } catch (SQLException e) {
            System.err.println("Error obteniendo estadísticas: " + e.getMessage());
        }

        return stats;
    }

    @Override
    public void enviarMensaje(String remitente, String destinatario, String texto) {
        String sql = "INSERT INTO Mensaje (id_remitente, id_destinatario, mensaje) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, remitente);
            stmt.setString(2, destinatario);
            stmt.setString(3, texto);
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error enviando mensaje: " + e.getMessage());
            throw new RuntimeException("Error al enviar mensaje: " + e.getMessage());
        }
    }

    @Override
    public List<Map<String, Object>> obtenerMensajes(String usuario) {
        List<Map<String, Object>> mensajes = new ArrayList<>();
        String sql = "SELECT m.*, u.nombre as nombre_remitente " +
                "FROM Mensaje m " +
                "JOIN Usuario u ON m.id_remitente = u.id " +
                "WHERE m.id_destinatario = ? OR m.id_remitente = ? " +
                "ORDER BY m.fecha_envio DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, usuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> mensaje = new HashMap<>();
                mensaje.put("id", rs.getInt("id"));
                mensaje.put("remitente", rs.getString("id_remitente"));
                mensaje.put("nombre_remitente", rs.getString("nombre_remitente"));
                mensaje.put("destinatario", rs.getString("id_destinatario"));
                mensaje.put("mensaje", rs.getString("mensaje"));
                mensaje.put("fecha_envio", rs.getString("fecha_envio"));
                mensaje.put("leido", rs.getBoolean("leido"));
                mensajes.add(mensaje);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo mensajes: " + e.getMessage());
        }

        return mensajes;
    }

    @Override
    public List<String> obtenerUsuariosConectados() {
        List<String> usuarios = new ArrayList<>();
        String sql = "SELECT id, nombre FROM Usuario WHERE activo = TRUE";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(rs.getString("id") + " - " + rs.getString("nombre"));
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo usuarios conectados: " + e.getMessage());
        }

        return usuarios;
    }
}