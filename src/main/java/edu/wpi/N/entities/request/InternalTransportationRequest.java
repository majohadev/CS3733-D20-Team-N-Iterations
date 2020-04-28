package edu.wpi.N.entities.request;

import edu.wpi.N.entities.DbNode;
import java.util.GregorianCalendar;

public class InternalTransportationRequest extends Request {
  private String transportationType;
  private GregorianCalendar scheduledTransportTime;
  private DbNode destinationLocation;

  InternalTransportationRequest(
      int requestID,
      int emp_assigned,
      String reqNotes,
      String compNotes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);
  }

  @Override
  public String getServiceType() {
    return "Internal Transportation";
  }
}
