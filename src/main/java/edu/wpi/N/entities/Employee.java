package edu.wpi.N.entities;

public abstract class Employee {
  private int id;
  private String name;

  public Employee(int id, String name) {
    this.id = id;
    this.name = name;
  }

  public int getID() {
    return id;
  }

  public String getName() {
    return name;
  }

  public abstract String getServiceType();

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Employee)) {
      return false;
    }

    Employee other = (Employee) o;

    return id == other.id && name.equals(other.name);
  }
}
