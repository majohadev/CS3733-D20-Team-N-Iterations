package edu.wpi.N.entities.request;

import edu.wpi.N.entities.Patient;
import java.util.GregorianCalendar;

public class MedicineRequest extends Request {
  private String medicineType;
  private double dosage;
  private String units;
  private Patient patient;

  public MedicineRequest(
      int requestID,
      int emp_assigned,
      String reqNotes,
      String compNotes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status,
      String type,
      double dosage,
      String units,
      Patient patient) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);
    this.medicineType = type;
    this.dosage = dosage;
    this.units = units;
    this.patient = patient;
  }

  @Override
  public String getServiceType() {
    return "Medicine";
  }

  public String getMedicineType() {
    return medicineType;
  }

  public void setMedicineType(String medicineType) {
    this.medicineType = medicineType;
  }

  public double getDosage() {
    return dosage;
  }

  public void setDosage(double dosage) {
    this.dosage = dosage;
  }

  public String getUnits() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public Patient getPatient() {
    return patient;
  }

  public void setPatient(Patient patient) {
    this.patient = patient;
  }
}
