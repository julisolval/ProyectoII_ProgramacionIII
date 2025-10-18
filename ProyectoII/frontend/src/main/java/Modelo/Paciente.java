package main.java.Modelo;

public class Paciente extends Persona {
    private String numeroTelefonico;
    private Fecha fechaNacimiento;

    public Paciente(String id, String nombre, String numeroTelefonico, Fecha fechaNacimiento) {
        super(id, nombre);
        this.numeroTelefonico = numeroTelefonico;
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNumeroTelefonico() {
        return numeroTelefonico;
    }

    public Fecha getFechaNacimiento() {
        return fechaNacimiento;
    }
}
