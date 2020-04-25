package edu.wpi.N.entities.request;

import java.util.GregorianCalendar;

public class TranslatorRequest extends Request {
  private String language;

  public TranslatorRequest(
      int requestID,
      int emp_assigned,
      String reqNotes,
      String compNotes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status,
      String language) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);
    this.language = language;
  }

  public String getAtr1() {
    return language;
  }

  public String getServiceType() {
    return "Translator";
  }
}
