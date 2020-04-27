package edu.wpi.N.entities.request;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Set;

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
      LinkedList<String> list) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);
    this.patientName = patient;
    this.visitorName = visitor;
    this.creditNum = num;

    HashMap<String, Integer> acc = new HashMap<>();
    int n;
    for (String f : list) {
      if (acc.containsKey(f)) {
        n = acc.remove(f) + 1;
        acc.put(f, n);
      } else {
        acc.put(f, 1);
      }
    }

    String flowerList = "";
    Set<String> keys = acc.keySet();

    for (String k : keys) {
      if (acc.get(k) > 1) {
        flowerList += "" + acc.get(k) + " " + k + "s, ";
      } else {
        flowerList += "" + acc.get(k) + " " + k + ", ";
      }
    }

    if (flowerList.length() > 0) flowerList = flowerList.substring(0, flowerList.length() - 2);

    this.flowerList = flowerList;
  }

  @Override
  public String getServiceType() {
    return "Flower";
  }

  public String getPatientName() {
    return patientName;
  }

  public String getVisitorName() {
    return visitorName;
  }

  public String getCreditNum() {
    return creditNum;
  }

  public String getFlowerList() {
    return flowerList;
  }

  public void setFlowerList(String flowerList) {
    this.flowerList = flowerList;
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
