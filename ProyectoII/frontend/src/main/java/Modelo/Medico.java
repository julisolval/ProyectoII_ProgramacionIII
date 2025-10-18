package main.java.Modelo;

public class Medico extends Persona {
    private String clave, especialidad;

    public Medico(String id, String nombre, String especialidad) {
        super(id, nombre);
        this.clave = id;
        this.especialidad = especialidad;
    }

public String getClave() {
        return clave;
    }

    public String getEspecialidad() {
        return especialidad;
    }
    
    public void setClave(String clave) {
        this.clave = clave;
    }
}
