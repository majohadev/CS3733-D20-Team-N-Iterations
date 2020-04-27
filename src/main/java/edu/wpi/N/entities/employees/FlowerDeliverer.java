package edu.wpi.N.entities.employees;

public class FlowerDeliverer extends Employee {
  public FlowerDeliverer(int id, String name) {
    super(id, name);
  }

  @Override
  public String getServiceType() {
    return "Flower";
  }
}
