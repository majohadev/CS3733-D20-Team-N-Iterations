package edu.wpi.N.entities.employees;

public class WheelchairEmployee extends Employee {
  public WheelchairEmployee(int id, String name) {
    super(id, name);
  }

  @Override
  public String getServiceType() {
    return "Wheelchair";
  }
}
