package main.java.Modelo;

import java.util.ArrayList;

public class ListaDePacientes {
    private ArrayList<Paciente> pacientes;

    public ListaDePacientes() {
        this.pacientes = new ArrayList<>();
    }
    public ArrayList<Paciente> getPacientes() {
        return pacientes;
    }
    public Paciente buscarPorID(String id){
        for( int i = 0; i < pacientes.size(); i++){
            if(pacientes.get(i).getId().equals(id)){
                return pacientes.get(i);
            }
        }
        return null;
    }

    public boolean agregarPaciente(Paciente paciente){
        return pacientes.add(paciente);
    }
}



