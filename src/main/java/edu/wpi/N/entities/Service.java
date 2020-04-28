package edu.wpi.N.entities;

import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;

public class Service {

  private TemporalAccessor startTime;
  private TemporalAccessor endTime;
  private String serviceType;
  private String description;

  public Service(String startTime, String endTime, String serviceType, String description) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
    this.startTime = formatter.parse(startTime);
    this.endTime = formatter.parse(endTime);
    this.serviceType = serviceType;
    this.description = description;
  }

  public TemporalAccessor getStartTime() {
    return startTime;
  }

  public TemporalAccessor getEndTime() {
    return endTime;
  }

  public String getServiceType() {
    return serviceType;
  }

  public String getDescription() {
    return description;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Service)) {
      return false;
    }

    Service other = (Service) o;

    return startTime.toString().equals(other.startTime.toString())
        && endTime.toString().equals(other.endTime.toString())
        && serviceType.equals(other.serviceType)
        && description.equals(other.description);
  }

  @Override
  public String toString() {
    return serviceType;
  }
}
