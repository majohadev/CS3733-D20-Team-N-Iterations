package edu.wpi.N.entities.employees;

public class IT extends Employee {

  public IT(int id, String name) {
    super(id, name);
  }

  @Override
  public String getServiceType() {
    return "IT";
  }
}
