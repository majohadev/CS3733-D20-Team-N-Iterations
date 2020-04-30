package edu.wpi.N.entities.employees;

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
  public String toString() {
    return name + " (" + this.getServiceType() + ")";
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof Employee)) {
      return false;
    }

    Employee other = (Employee) o;

    return id == other.id && name.equals(other.name);
  }
}

/* TODO: Create your employee entity: make sure serviceType is the same as what you insert into service*/
