package edu.wpi.N.entities;

import java.util.LinkedList;

public class Doctor {
  private int id;
  private String name;
  private LinkedList<DbNode> loc;
  private String field;

  public Doctor(int id, String name, String field, LinkedList<DbNode> loc) {
    this.id = id;
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

  public int getID() {
    return id;
  }

  public boolean equals(Object o) {
    if (!(o instanceof Doctor)) {
      return false;
    }

    Doctor other = (Doctor) o;

    return this.name.equals(other.getName());
  }
}
