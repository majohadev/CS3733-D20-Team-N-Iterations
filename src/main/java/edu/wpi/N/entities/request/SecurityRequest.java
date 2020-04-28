package edu.wpi.N.entities.request;

import java.util.GregorianCalendar;

public class SecurityRequest extends Request {

  private String isEmergency;

  public SecurityRequest(
      int requestID,
      int emp_assigned,
      String reqNotes,
      String compNotes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status,
      String isEmergency) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);
    this.isEmergency = isEmergency;
  }

  public String getAtr1() {
    return this.isEmergency;
  }

  public String getServiceType() {
    return "Security";
  }
}
