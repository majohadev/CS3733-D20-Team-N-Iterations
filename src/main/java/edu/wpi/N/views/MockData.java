package edu.wpi.N.views;

import java.util.GregorianCalendar;

public class MockData {

  int requestID;
  int emp_assigned;
  String notes;
  String nodeID;
  GregorianCalendar timeRequested;
  GregorianCalendar timeCompleted;
  String status;
  String language;

  public MockData(
      int requestID,
      int emp_assigned,
      String notes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status,
      String language) {
    this.requestID = requestID;
    this.emp_assigned = emp_assigned;
    this.notes = notes;
    this.nodeID = nodeID;
    this.timeRequested = timeRequested;
    this.timeCompleted = timeCompleted;
    this.status = status;
    this.language = language;
  }

  public int getRequestID() {
    return requestID;
  }

  public void setRequestID(int requestID) {
    this.requestID = requestID;
  }

  public int getEmp_assigned() {
    return emp_assigned;
  }

  public void setEmp_assigned(int emp_assigned) {
    this.emp_assigned = emp_assigned;
  }

  public String getNotes() {
    return notes;
  }

  public void setNotes(String notes) {
    this.notes = notes;
  }

  public String getNodeID() {
    return nodeID;
  }

  public void setNodeID(String nodeID) {
    this.nodeID = nodeID;
  }

  public GregorianCalendar getTimeRequested() {
    return timeRequested;
  }

  public void setTimeRequested(GregorianCalendar timeRequested) {
    this.timeRequested = timeRequested;
  }

  public GregorianCalendar getTimeCompleted() {
    return timeCompleted;
  }

  public void setTimeCompleted(GregorianCalendar timeCompleted) {
    this.timeCompleted = timeCompleted;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getLanguage() {
    return language;
  }

  public void setLanguage(String language) {
    this.language = language;
  }
}
