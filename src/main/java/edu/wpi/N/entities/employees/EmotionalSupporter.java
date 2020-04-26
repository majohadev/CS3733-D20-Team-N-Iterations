package edu.wpi.N.entities.employees;

public class EmotionalSupporter extends Employee {

  public EmotionalSupporter(int id, String name, String serviceType) {
    super(id, name);
  }

  @Override
  public String getServiceType() {
    return "Emotional Support";
  }
}
