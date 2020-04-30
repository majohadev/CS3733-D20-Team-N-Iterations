package edu.wpi.N.entities.request;

import java.util.GregorianCalendar;

public class MedicineRequest extends Request {
  private String medicineName;
  private String dosage;
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
    this.dosage = dosage + units;
    this.patient = patient;
  }

  @Override
  public String getServiceType() {
    return "Medicine";
  }

  public String getAtr1() {
    return medicineName;
  }

  public String getAtr2() {
    return dosage;
  }

  public String getAtr3() {
    return patient;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof MedicineRequest)) {
      return false;
    }

    MedicineRequest other = (MedicineRequest) o;

    return super.equals(other);
  }
}
