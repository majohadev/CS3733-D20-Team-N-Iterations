package edu.wpi.N.entities.employees;

public class flowerDeliverer extends Employee {
  public flowerDeliverer(int id, String name) {
    super(id, name);
  }

  @Override
  public String getServiceType() {
    return "Flower";
  }
}
