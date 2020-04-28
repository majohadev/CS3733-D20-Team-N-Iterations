package edu.wpi.N.entities.employees;

public class InternalTransportationEmployee extends Employee {
  public InternalTransportationEmployee(int id, String name) {
    super(id, name);
  }

  @Override
  public String getServiceType() {
    return "Internal Transportation";
  }
}
