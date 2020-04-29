package edu.wpi.N.entities.employees;

public class SecurityOfficer extends Employee {

  public SecurityOfficer(int id, String name) {
    super(id, name);
  }

  @Override
  public String getServiceType() {
    return "Security";
  }
}
