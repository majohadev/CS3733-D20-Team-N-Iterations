package edu.wpi.N.entities;

import java.util.GregorianCalendar;

public abstract class Request {
  int requestID;
  int emp_assigned;
  String notes;
  String nodeID;
  GregorianCalendar timeRequested;
  GregorianCalendar timeCompleted;
  String status;

  public String getStatus() {
    return status;
  }

  Request(
      int requestID,
      int emp_assigned,
      String notes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status) {
    this.requestID = requestID;
    this.emp_assigned = emp_assigned;
    this.notes = notes;
    this.nodeID = nodeID;
    this.timeCompleted = timeCompleted;
    this.timeRequested = timeRequested;
    this.status = status;
  }

  public int getRequestID() {
    return requestID;
  }

  public int getEmp_assigned() {
    return emp_assigned;
  }

  public String getNotes() {
    return notes;
  }

  public String getNodeID() {
    return nodeID;
  }

  public abstract String getServiceType();

  public abstract String getLanguage();

  public GregorianCalendar getTimeRequested() {
    return timeRequested;
  }

  public GregorianCalendar getTimeCompleted() {
    return timeCompleted;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Request)) {
      return false;
    }

    Request other = (Request) o;

    return requestID == other.requestID;
  }
}
