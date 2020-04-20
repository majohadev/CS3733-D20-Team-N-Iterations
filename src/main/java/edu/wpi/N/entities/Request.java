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

  public GregorianCalendar getTimeRequested() {
    return timeRequested;
  }

  public GregorianCalendar getTimeCompleted() {
    return timeCompleted;
  }

  @Override
  public boolean equals(Object o) {
    if(!(o instanceof Request)){
      return false;
    }

    Request other = (Request)o;

    return requestID == other.requestID &&
            emp_assigned == other.emp_assigned &&
            notes.equals(other.notes) &&
            nodeID.equals(other.nodeID) &&
            timeRequested.toString().equals(other.timeRequested.toString()) &&
            timeCompleted.toString().equals(other.timeCompleted,toString());
  }
}
