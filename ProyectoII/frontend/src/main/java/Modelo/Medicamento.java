package main.java.Modelo;

public class Medicamento {
    private int codigo;
    private String nombre;
    private double presentacion;

    public Medicamento(int codigo, String nombre, double presentacion) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.presentacion = presentacion;
    }

    public double getPresentacion() {
        return presentacion;
    }

    public int getCodigo(){
        return codigo;
    }
    public String getNombre(){
        return nombre;
    }
}

