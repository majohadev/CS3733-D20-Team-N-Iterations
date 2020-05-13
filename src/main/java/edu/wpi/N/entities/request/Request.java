package edu.wpi.N.entities.request;

import edu.wpi.N.database.DBException;
import edu.wpi.N.database.ServiceDB;
import edu.wpi.N.entities.employees.Employee;
import java.util.GregorianCalendar;

public abstract class Request {
  int requestID;
  Employee emp_assigned;
  String reqNotes;
  String compNotes;
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
      String reqNotes,
      String compNotes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status) {
    this.requestID = requestID;
    try {
      this.emp_assigned = ServiceDB.getEmployee(emp_assigned);
    } catch (DBException e) {
      this.emp_assigned = null;
    }
    this.reqNotes = reqNotes;
    this.compNotes = compNotes;
    this.nodeID = nodeID;
    this.timeCompleted = timeCompleted;
    this.timeRequested = timeRequested;
    this.status = status;
  }

  public int getRequestID() {
    return requestID;
  }

  public Employee getEmp_assigned() {
    return emp_assigned;
  }

  public String getReqNotes() {
    return reqNotes;
  }

  public String getCompNotes() {
    return compNotes;
  }

  public String getNodeID() {
    return nodeID;
  }

  public abstract String getServiceType();

  // Attribute getters
  public String getAtr1() {
    return "N/A";
  }

  public String getAtr2() {
    return "N/A";
  }

  public String getAtr3() {
    return "N/A";
  }

  public String getAtr4() {
    return "N/A";
  }

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

    boolean emp_assignedEqual;
    if (emp_assigned == null) {
      emp_assignedEqual = other.emp_assigned == null;
    } else {
      emp_assignedEqual = emp_assigned.equals(other.emp_assigned);
    }

    boolean compNotesEqual;
    if (compNotes == null) {
      compNotesEqual = other.compNotes == null;
    } else {
      compNotesEqual = compNotes.equals(other.compNotes);
    }

    return requestID == other.requestID;
  }
}
