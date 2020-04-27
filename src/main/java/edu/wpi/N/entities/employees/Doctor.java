package edu.wpi.N.entities.employees;

import edu.wpi.N.entities.DbNode;
import java.util.LinkedList;

public class Doctor extends Employee {
  private LinkedList<DbNode> loc;
  private String field;
  private String username;

  public Doctor(int id, String name, String field, String username, LinkedList<DbNode> loc) {
    super(id, name);
    this.loc = loc;
    this.username = username;
    this.field = field;
  }

  public String getUsername() {
    return this.username;
  }

  @Override
  public String getServiceType() {
    return "Medicine";
  }

  public LinkedList<DbNode> getLoc() {
    return loc;
  }

  public String getField() {
    return field;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Doctor)) {
      return false;
    }

    Doctor other = (Doctor) o;

    return this.getID() == other.getID();
  }

  @Override
  public String toString() {
    return this.getName();
  }
}
