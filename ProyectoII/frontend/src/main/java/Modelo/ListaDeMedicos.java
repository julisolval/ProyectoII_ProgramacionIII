package main.java.Modelo;

import java.util.ArrayList;

public class ListaDeMedicos {
    private ArrayList<Medico> medicos;

    public ListaDeMedicos() {
        this.medicos = new ArrayList<>();
    }

    public ArrayList<Medico> getMedicos() {
        return medicos;
    }
    
    public Medico buscarPorID(String id){
        for( int i = 0; i < medicos.size(); i++){
            if(medicos.get(i).getId().equals(id)){
                return medicos.get(i);
            }
        }
        return null;
    }

    public boolean agregarMedico(Medico medico){
        return medicos.add(medico);
    }

}
