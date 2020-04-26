package edu.wpi.N.entities.request;

import java.util.GregorianCalendar;

public class ITRequest extends Request {
  private String device;
  private String problem;

  public ITRequest(
      int requestID,
      int emp_assigned,
      String reqNotes,
      String compNotes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status,
      String device,
      String problem) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);
    this.device = device;
    this.problem = problem;
  }

  public String getAtr1() {
    return device;
  }

  public String getAtr2() {
    return problem;
  }

  public String getServiceType() {
    return "IT";
  }
}
