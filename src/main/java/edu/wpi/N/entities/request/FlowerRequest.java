package edu.wpi.N.entities.request;

import java.util.GregorianCalendar;

public class FlowerRequest extends Request {

  private String patientName, visitorName, creditNum;
  private String flowerList;

  public FlowerRequest(
      int requestID,
      int emp_assigned,
      String reqNotes,
      String compNotes,
      String nodeID,
      GregorianCalendar timeRequested,
      GregorianCalendar timeCompleted,
      String status,
      String patient,
      String visitor,
      String num,
      String flowers) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);
    this.patientName = patient;
    this.visitorName = visitor;
    this.creditNum = num;
    this.flowerList = flowers;
  }

  @Override
  public String getServiceType() {
    return "Flower";
  }

  public String getAtr1() {
    return patientName;
  }

  public String getAtr2() {
    return visitorName;
  }

  public String getAtr3() {
    return creditNum;
  }

  public String getAtr4() {
    return flowerList;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof FlowerRequest)) {
      return false;
    }

    FlowerRequest other = (FlowerRequest) o;

    return super.equals(o)
        && patientName.equals(other.patientName)
        && visitorName.equals(other.visitorName)
        && creditNum.equals(other.creditNum)
        && flowerList.equals(other.flowerList);
  }
}
