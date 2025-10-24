package service;

import dao.DatabaseConnection;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class ServiceImpl implements Service {

    // ==========================================================
    // 🔹 LOGIN CORREGIDO - Devuelve un Map con los datos del usuario
    // ==========================================================
    @Override
    public Map<String, Object> login(String username, String password) {
        String sql = "SELECT id, nombre, tipo, especialidad FROM Usuario WHERE id = ? AND clave = ?";
        Map<String, Object> usuario = new HashMap<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                usuario.put("estado", "éxito");
                usuario.put("id", rs.getString("id"));
                usuario.put("nombre", rs.getString("nombre"));
                usuario.put("tipo", rs.getString("tipo"));
                usuario.put("especialidad", rs.getString("especialidad"));

                // Marcar como activo
                String updateSql = "UPDATE Usuario SET activo = TRUE WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, username);
                    updateStmt.executeUpdate();
                }
            } else {
                usuario.put("estado", "error");
                usuario.put("mensaje", "Credenciales inválidas");
            }

        } catch (SQLException e) {
            usuario.put("estado", "error");
            usuario.put("mensaje", "Error en la base de datos: " + e.getMessage());
            System.err.println("Error en login BD: " + e.getMessage());
        }

        return usuario;
    }

    // ==========================================================
    @Override
    public boolean cambiarClave(String username, String oldPassword, String newPassword) {
        String sql = "UPDATE Usuario SET clave = ? WHERE id = ? AND clave = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, newPassword);
            stmt.setString(2, username);
            stmt.setString(3, oldPassword);
            return stmt.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("Error cambiando clave: " + e.getMessage());
            return false;
        }
    }

    // ==========================================================
    // 🔹 MÉDICOS
    // ==========================================================
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
        }
    }

    // ==========================================================
    // 🔹 FARMACÉUTICOS
    // ==========================================================
    @Override
    public List<Map<String, Object>> obtenerFarmaceuticos() {
        List<Map<String, Object>> farmaceuticos = new ArrayList<>();
        String sql = "SELECT id, nombre FROM Usuario WHERE tipo = 'FARMACEUTA'";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> f = new HashMap<>();
                f.put("id", rs.getString("id"));
                f.put("nombre", rs.getString("nombre"));
                farmaceuticos.add(f);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo farmacéuticos: " + e.getMessage());
        }

        return farmaceuticos;
    }

    @Override
    public void guardarFarmaceutico(Object farmaceuticoObj) {
        @SuppressWarnings("unchecked")
        Map<String, String> f = (Map<String, String>) farmaceuticoObj;
        String sql = "INSERT INTO Usuario (id, clave, nombre, tipo) VALUES (?, ?, ?, 'FARMACEUTA')";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f.get("id"));
            stmt.setString(2, f.get("id"));
            stmt.setString(3, f.get("nombre"));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error guardando farmacéutico: " + e.getMessage());
        }
    }

    @Override
    public void actualizarFarmaceutico(Object farmaceuticoObj) {
        Map<String, String> f = (Map<String, String>) farmaceuticoObj;
        String sql = "UPDATE Usuario SET nombre = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, f.get("nombre"));
            stmt.setString(2, f.get("id"));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error actualizando farmacéutico: " + e.getMessage());
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
        }
    }

    // ==========================================================
    // 🔹 PACIENTES
    // ==========================================================
    @Override
    public List<Map<String, Object>> obtenerPacientes() {
        List<Map<String, Object>> pacientes = new ArrayList<>();
        String sql = "SELECT id, nombre, fecha_nacimiento, telefono FROM Paciente";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Map<String, Object> p = new HashMap<>();
                p.put("id", rs.getString("id"));
                p.put("nombre", rs.getString("nombre"));
                p.put("fecha_nacimiento", rs.getString("fecha_nacimiento"));
                p.put("telefono", rs.getString("telefono"));
                pacientes.add(p);
            }

        } catch (SQLException e) {
            System.err.println("Error obteniendo pacientes: " + e.getMessage());
        }

        return pacientes;
    }

    @Override
    public void guardarPaciente(Object pacienteObj) {
        @SuppressWarnings("unchecked")
        Map<String, String> p = (Map<String, String>) pacienteObj;
        String sql = "INSERT INTO Paciente (id, nombre, fecha_nacimiento, telefono) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, p.get("id"));
            stmt.setString(2, p.get("nombre"));
            stmt.setString(3, p.get("fecha_nacimiento"));
            stmt.setString(4, p.get("telefono"));
            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error guardando paciente: " + e.getMessage());
        }
    }

    // ==========================================================
    // 🔹 MÉTODOS RESTANTES (Medicamentos, Recetas, Mensajes, etc.)
    // ==========================================================
    // ✅ Estos no cambian y puedes mantenerlos igual que los tienes ahora.

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
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> detalles = (List<Map<String, Object>>) detallesObj;

        String sqlReceta = "INSERT INTO Receta (id_paciente, id_medico, fecha_confeccion, fecha_retiro, estado) "
                + "VALUES (?, ?, ?, ?, 'CONFECCIONADA')";
        String sqlDetalle = "INSERT INTO DetalleReceta (id_receta, codigo_medicamento, cantidad, indicaciones, duracion) "
                + "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int idReceta;

            // 📌 Insertar receta
            try (PreparedStatement stmtReceta = conn.prepareStatement(sqlReceta, Statement.RETURN_GENERATED_KEYS)) {

                String idPaciente = (String) receta.get("id_paciente");
                //String idMedico = (String) receta.get("id_medico");
                String idMedico = "2222";

                String fechaConfeccion = (String) receta.get("fechaConfeccion");
                String fechaRetiro = (String) receta.get("fechaRetiro");

                // Si no viene fecha de confección, usar la actual
                if (fechaConfeccion == null || fechaConfeccion.isEmpty()) {
                    fechaConfeccion = new java.sql.Date(System.currentTimeMillis()).toString();
                }

                // Si fecha_retiro está vacía, se guarda como NULL
                stmtReceta.setString(1, idPaciente);
                stmtReceta.setString(2, idMedico);
                stmtReceta.setString(3, fechaConfeccion);
                if (fechaRetiro == null || fechaRetiro.isEmpty()) {
                    stmtReceta.setNull(4, java.sql.Types.DATE);
                } else {
                    stmtReceta.setString(4, fechaRetiro);
                }

                int filas = stmtReceta.executeUpdate();
                if (filas == 0) {
                    throw new SQLException("No se pudo insertar la receta");
                }

                try (ResultSet generatedKeys = stmtReceta.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        idReceta = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("No se pudo obtener el ID de la receta insertada");
                    }
                }
            }

            // 📋 Insertar detalles
            try (PreparedStatement stmtDetalle = conn.prepareStatement(sqlDetalle)) {
                for (Map<String, Object> detalle : detalles) {
                    stmtDetalle.setInt(1, idReceta);
                    stmtDetalle.setString(2, (String) detalle.get("codigoMedicamento"));
                    stmtDetalle.setInt(3, Integer.parseInt(detalle.get("cantidad").toString()));
                    stmtDetalle.setString(4, (String) detalle.get("indicaciones"));
                    stmtDetalle.setInt(5, Integer.parseInt(detalle.get("duracion").toString()));
                    stmtDetalle.addBatch();
                }
                stmtDetalle.executeBatch();
            }

            conn.commit();
            System.out.println("✅ Receta y detalles guardados correctamente. ID receta: " + idReceta);

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al guardar receta: " + e.getMessage());
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
        String sql = "INSERT INTO mensaje (remitente, destinatario, texto, fecha_envio) VALUES (?, ?, ?, NOW())";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, remitente);
            stmt.setString(2, destinatario);
            stmt.setString(3, texto);
            stmt.executeUpdate();

            System.out.println("💬 Mensaje guardado de " + remitente + " para " + destinatario);
        } catch (SQLException e) {
            System.err.println("Error al guardar mensaje: " + e.getMessage());
        }
    }


    @Override
    public List<Map<String, Object>> obtenerMensajes(String usuario) {
        List<Map<String, Object>> mensajes = new ArrayList<>();

        String sql = "SELECT m.id, m.remitente, m.destinatario, m.texto, m.fecha_envio, " +
                "u.nombre AS nombre_remitente " +
                "FROM mensaje m " +
                "LEFT JOIN usuario u ON m.remitente = u.id " +
                "WHERE m.destinatario = ? OR m.remitente = ? " +
                "ORDER BY m.fecha_envio DESC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, usuario);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Map<String, Object> mensaje = new HashMap<>();
                mensaje.put("id", rs.getInt("id"));
                mensaje.put("remitente", rs.getString("remitente"));
                mensaje.put("nombre_remitente", rs.getString("nombre_remitente"));
                mensaje.put("destinatario", rs.getString("destinatario"));
                mensaje.put("texto", rs.getString("texto"));
                mensaje.put("fecha_envio", rs.getString("fecha_envio"));
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