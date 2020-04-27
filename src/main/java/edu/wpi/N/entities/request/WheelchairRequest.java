package edu.wpi.N.entities.request;

import java.util.GregorianCalendar;

public class WheelchairRequest extends Request {
  String needsAssistance;

  public WheelchairRequest(
      int requestID,
      int emp_assigned,
      String reqNotes,
      String compNotes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status,
      String needsAssistance) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);
    this.needsAssistance = needsAssistance;
  }

  public String getServiceType() {
    return "Wheelchair";
  }

  @Override
  public String getAtr1() {
    return this.needsAssistance;
  }
}
