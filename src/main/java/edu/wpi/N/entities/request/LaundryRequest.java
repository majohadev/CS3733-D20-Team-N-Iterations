package edu.wpi.N.entities.request;

import java.util.GregorianCalendar;

public class LaundryRequest extends Request {
  public LaundryRequest(
      int requestID,
      int emp_assigned,
      String notes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status) {
    super(requestID, emp_assigned, notes, nodeID, timeRequested, timeCompleted, status);
  }

  public String getServiceType() {
    return "Laundry";
  }

  public String getLanguage() {
    return "N/A";
  }
}
