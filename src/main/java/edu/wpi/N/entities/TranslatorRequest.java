package edu.wpi.N.entities;

import java.util.GregorianCalendar;

public class TranslatorRequest extends Request {
  private String language;

  public TranslatorRequest(
      int requestID,
      int emp_assigned,
      String notes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status,
      String language) {
    super(requestID, emp_assigned, notes, nodeID, timeRequested, timeCompleted, status);
    this.language = language;
  }

  public String getLanguage() {
    return language;
  }

  public String getServiceType() {
    return "Translator";
  }
}
