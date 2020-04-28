package edu.wpi.N.entities.request;

import java.util.GregorianCalendar;

public class InternalTransportationRequest extends Request {
  private String transportationType;
  private String scheduledTransportTime;
  private String destinationLocation;

  public InternalTransportationRequest(
      int requestID,
      int emp_assigned,
      String reqNotes,
      String compNotes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status,
      String transportationType,
      String scheduledTransportTime,
      String destinationLocation) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);

    this.transportationType = transportationType;

    this.scheduledTransportTime = scheduledTransportTime;

    this.destinationLocation = destinationLocation;
  }

  public String getAtr1() {
    return transportationType;
  }

  public String getAtr2() {
    return scheduledTransportTime;
  }

  public String getAtr3() {
    return destinationLocation;
  }

  @Override
  public String getServiceType() {
    return "Internal Transportation";
  }
}
