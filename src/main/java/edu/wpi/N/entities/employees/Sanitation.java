package edu.wpi.N.entities.employees;

public class Sanitation extends Employee {
  public Sanitation(int id, String name) {
    super(id, name);
  }

  @Override
  public String getServiceType() {
    return "Sanitation";
  }
}
