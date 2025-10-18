package main.java.Modelo;

import java.util.ArrayList;

public class CatalogoDeMedicamentos {
    private ArrayList<Medicamento> medicamentos;

    public CatalogoDeMedicamentos() {
        this.medicamentos = new ArrayList<>();
    }

    public ArrayList<Medicamento> getMedicamentos() {
        return medicamentos;
    }

    public Medicamento buscarPorCodigo(int codigo){
        if (medicamentos == null) {
            return null;
        }

        for( int i = 0; i < medicamentos.size(); i++){
            if(medicamentos.get(i).getCodigo() == codigo){
                return medicamentos.get(i);
            }
        }
        return null;
    }



    public boolean agregarMedicamento(Medicamento medicamento){
        return medicamentos.add(medicamento);
    }

}