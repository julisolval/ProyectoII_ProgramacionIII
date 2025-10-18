package main.java.Modelo;

public class DetallesPrescripcion {
    private String medicationCode;
    private int quantity;
    private String instructions;
    private int durationDays;
    private String medicationName;

    public String getMedicationCode() { return medicationCode; }
    public void setMedicationCode(String medicationCode) { this.medicationCode = medicationCode; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getInstructions() { return instructions; }
    public void setInstructions(String instructions) { this.instructions = instructions; }

    public int getDurationDays() { return durationDays; }
    public void setDurationDays(int durationDays) { this.durationDays = durationDays; }

    public Object getMedicationName() {
        return medicationName;
    }
}