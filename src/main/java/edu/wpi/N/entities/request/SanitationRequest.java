package edu.wpi.N.entities.request;

import java.util.Arrays;
import java.util.GregorianCalendar;

public class SanitationRequest extends Request {

  private String spillType, size, danger;

  /**
   * Note: size must be one of {"small", "medium", "large", "unknown"} (case insensitive) Note:
   * danger must be one of {"low", "medium", "high", "unknown"} (case insensitive)
   */
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
      String size,
      String danger) {
    super(
        requestID, emp_assigned, reqNotes, compNotes, nodeID, timeRequested, timeCompleted, status);

    String[] sizeArray = new String[] {"small", "medium", "large", "unknown"};
    String[] dangerArray = new String[] {"low", "medium", "high", "unknown"};

    if (!Arrays.asList(sizeArray).contains(size.toLowerCase())) {
      throw new IllegalArgumentException("SanitationRequest: \"" + size + "\" is not a valid size");
    }

    if (!Arrays.asList(dangerArray).contains(danger.toLowerCase())) {
      throw new IllegalArgumentException(
          "SanitationRequest: \"" + danger + "\" is not a valid danger level");
    }

    this.spillType = spillType;
    this.size = size;
    this.danger = danger;
  }

  @Override
  public String getServiceType() {
    return "Sanitation";
  }

  public String getAtr1() {
    return spillType;
  }

  public String getAtr2() {
    return size;
  }

  public String getAtr3() {
    return danger;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof SanitationRequest)) return false;

    SanitationRequest other = (SanitationRequest) o;

    return super.equals(o)
        && spillType.equals(other.spillType)
        && size.equals(other.size)
        && danger.equals(other.danger);
  }
}
