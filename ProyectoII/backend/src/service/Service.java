package service;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Service {

    // 🔹 Ahora devuelve un Map con los datos del usuario
    Map<String, Object> login(String username, String password);

    boolean cambiarClave(String username, String oldPassword, String newPassword);

    List<?> obtenerMedicos();
    void guardarMedico(Object medico);
    void actualizarMedico(Object medico);
    void eliminarMedico(String id);

    List<?> obtenerFarmaceuticos();
    void guardarFarmaceutico(Object farmaceutico);
    void actualizarFarmaceutico(Object farmaceutico);
    void eliminarFarmaceutico(String id);

    List<?> obtenerPacientes();
    void guardarPaciente(Object paciente);
    void actualizarPaciente(Object paciente);
    void eliminarPaciente(String id);

    List<?> obtenerMedicamentos();
    void guardarMedicamento(Object medicamento);
    void actualizarMedicamento(Object medicamento);
    void eliminarMedicamento(String codigo);

    void guardarReceta(Object receta, List<?> detalles);
    List<?> obtenerRecetas();
    void actualizarEstadoReceta(int idReceta, String nuevoEstado, String idFarmaceutico);

    Map<String, Object> obtenerEstadisticas(Date desde, Date hasta, String medicamentoFiltro);

    void enviarMensaje(String remitente, String destinatario, String texto);
    List<?> obtenerMensajes(String usuario);
    List<String> obtenerUsuariosConectados();
}
