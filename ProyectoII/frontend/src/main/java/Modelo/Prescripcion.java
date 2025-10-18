package main.java.Modelo;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Prescripcion {
    private String id;
    private LocalDate creationDate;
    private LocalDate withdrawalDate;
    private String status;
    private String patientId;
    private String doctorId;
    private List<DetallesPrescripcion> details;

    public Prescripcion() {
        this.creationDate = LocalDate.now();
        this.details = new ArrayList<>();
        this.status = "confeccionada";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public LocalDate getWithdrawalDate() { return withdrawalDate; }
    public void setWithdrawalDate(LocalDate withdrawalDate) { this.withdrawalDate = withdrawalDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getDoctorId() { return doctorId; }
    public void setDoctorId(String doctorId) { this.doctorId = doctorId; }

    public List<DetallesPrescripcion> getDetails() { return details; }
    public void setDetails(List<DetallesPrescripcion> details) { this.details = details; }

    public void addDetail(DetallesPrescripcion detail) {
        this.details.add(detail);
    }
}