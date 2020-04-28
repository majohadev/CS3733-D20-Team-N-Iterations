package edu.wpi.N.entities.request;

import java.util.GregorianCalendar;

public class EmotionalRequest extends Request {
  // Available types of support: Individual, Family, Couple, Group
  private String supportType;

  public EmotionalRequest(
      int requestID,
      int emp_assigned,
      String reqNotes,
      String compNotes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status,
      String supportType) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);
    this.supportType = supportType;
  }

  @Override
  public String getAtr1() {
    return supportType;
  }

  @Override
  public String getServiceType() {
    return "Emotional Support";
  }

  public String getSupportType() {
    return this.supportType;
  }
}
