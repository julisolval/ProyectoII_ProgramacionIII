package main.java.Modelo;

public class Farmaceutas extends Persona {
    private String clave;

    public Farmaceutas(String id, String nombre) {
        super(id, nombre);
        this.clave = id;
    }

    public String getClave() {
        return clave;
    }

    public void setClave(String clave) {
        this.clave = clave;
    }

}
