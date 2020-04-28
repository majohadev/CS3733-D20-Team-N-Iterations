package edu.wpi.N.entities.request;

import java.util.GregorianCalendar;

public class MedicineRequest extends Request {
  private String medicineName;
  private double dosage;
  private String units;
  private String patient;

  public MedicineRequest(
      int requestID,
      int emp_assigned,
      String reqNotes,
      String compNotes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status,
      String medicineName,
      double dosage,
      String units,
      String patient) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);
    this.medicineName = medicineName;
    this.dosage = dosage;
    this.units = units;
    this.patient = patient;
  }

  @Override
  public String getServiceType() {
    return "Medicine";
  }

  public String getAtr1() {
    return medicineName;
  }

  public void setMedicineName(String medicineName) {
    this.medicineName = medicineName;
  }

  public double getDosage() {
    return dosage;
  }

  public void setDosage(double dosage) {
    this.dosage = dosage;
  }

  public String getAtr2() {
    return units;
  }

  public void setUnits(String units) {
    this.units = units;
  }

  public String getAtr3() {
    return patient;
  }

  public void setPatient(String patient) {
    this.patient = patient;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof MedicineRequest)) {
      return false;
    }

    MedicineRequest other = (MedicineRequest) o;

    return super.equals(o)
        && medicineName.equals(other.medicineName)
        && dosage == other.dosage
        && units.equals(other.units)
        && patient.equals(other.patient);
  }
}
