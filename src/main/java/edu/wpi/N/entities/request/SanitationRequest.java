package edu.wpi.N.entities.request;

import java.util.GregorianCalendar;

public class SanitationRequest extends Request {

  private String spillType, amount, danger;

  public SanitationRequest(
      int requestID,
      int emp_assigned,
      String reqNotes,
      String compNotes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status,
      String spillType,
      String amount,
      String danger) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);
    this.spillType = spillType;
    this.amount = amount;
    this.danger = danger;
  }

  @Override
  public String getServiceType() {
    return "sanitation";
  }

  public String getSpillType() {
    return spillType;
  }

  public String getAmount() {
    return amount;
  }

  public String getDanger() {
    return danger;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SanitationRequest)) return false;

    SanitationRequest other = (SanitationRequest) o;

    return super.equals(o)
        && spillType.equals(other.spillType)
        && amount.equals(other.amount)
        && danger.equals(other.danger);
  }
}
