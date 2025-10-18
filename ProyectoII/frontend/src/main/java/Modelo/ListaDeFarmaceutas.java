package main.java.Modelo;

import java.util.ArrayList;

public class ListaDeFarmaceutas {
    private ArrayList<Farmaceutas> farmaceutas;

    public ArrayList<Farmaceutas> getFarmaceutas() {
        return farmaceutas;
    }
    public ListaDeFarmaceutas() {
        this.farmaceutas = new ArrayList<>();
    }
    public Farmaceutas buscarPorID(String id){
        for( int i = 0; i < farmaceutas.size(); i++){
            if(farmaceutas.get(i).getId().equals(id)){
                return farmaceutas.get(i);
            }
        }
        return null;
    }


    public boolean agregarFarmaceuta(Farmaceutas farmaceuta){
        return farmaceutas.add(farmaceuta);
    }

}
