package edu.wpi.N.entities;

import java.util.LinkedList;

public class Doctor {
  private String name;
  private LinkedList<DbNode> loc;
  private String field;

  public Doctor(String name, String field, LinkedList<DbNode> loc) {
    this.name = name;
    this.loc = loc;
    this.field = field;
  }

  public String getName() {
    return name;
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

    return this.name.equals(other.getName());
  }
}
