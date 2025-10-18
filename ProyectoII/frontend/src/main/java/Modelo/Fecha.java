package main.java.Modelo;

public class Fecha {
    private int dia;
    private int anio;
    private String mes;

    public Fecha(int anio, int mesNum, int dia) {
        this.anio = anio;
        this.mes = nombreMes(mesNum);
        this.dia = dia;
    }

    public Fecha(int anio, String mes, int dia) {
        this.anio = anio;
        this.mes = mes;
        this.dia = dia;
    }

    public static String nombreMes(int mesNum) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        if (mesNum >= 1 && mesNum <= 12) {
            return meses[mesNum - 1];
        }
        return "Enero";
    }

    public static int mesANumero(String mes) {
        String[] meses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        for (int i = 0; i < meses.length; i++) {
            if (meses[i].equalsIgnoreCase(mes)) {
                return i + 1;
            }
        }
        return 1;
    }

    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d", anio, mesANumero(mes), dia);
    }

    public int getDia() {
        return dia;
    }

    public String getMes() {
        return mes;
    }

    public int getAnio() {
        return anio;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Fecha fecha = (Fecha) obj;
        return dia == fecha.dia && anio == fecha.anio && mes.equals(fecha.mes);
    }

    @Override
    public int hashCode() {
        int result = dia;
        result = 31 * result + anio;
        result = 31 * result + mes.hashCode();
        return result;
    }
}
